package com.ag.transactiongatewayexchange.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class CurrencyConverterService {

    private final LoadingCache<String, Map<LocalDate, BigDecimal>> conversionCache;

    @Value("${currency-conversion.api.url}")
    private String currencyConversionApiUrl;

    @Value("${currency-conversion.cache.expire-after-write}")
    private int expireAfterWrite;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private static final int SIX_MONTHS_IN_DAYS = 6 * 30;

    public CurrencyConverterService() {
        this.conversionCache = CacheBuilder.newBuilder()
                .expireAfterWrite(SIX_MONTHS_IN_DAYS, TimeUnit.DAYS)
                .build(new CacheLoader<String, Map<LocalDate, BigDecimal>>() {
                    @Override
                    public Map<LocalDate, BigDecimal> load(String currency) throws Exception {
                        return loadCurrencyConversionRates(currency, null, null);
                    }
                });
    }

    private Map<LocalDate, BigDecimal> loadCurrencyConversionRates(String targetCurrency, LocalDate startDate, LocalDate endDate) {
        try {
            LocalDate sixMonthsAgo = LocalDate.now().minusDays(SIX_MONTHS_IN_DAYS);
            startDate = (startDate != null && startDate.isAfter(sixMonthsAgo)) ? startDate : sixMonthsAgo;

            String apiUrl = buildApiUrl(targetCurrency, startDate, endDate);
            String jsonApiResponse = restTemplate.getForObject(apiUrl, String.class);
            return parseJsonData(jsonApiResponse, targetCurrency);
        } catch (Exception e) {
            throw new RuntimeException("Error loading currency conversion rates", e);
        }
    }

    private String buildApiUrl(String targetCurrency, LocalDate startDate, LocalDate endDate) {
        StringBuilder apiUrlBuilder = new StringBuilder(currencyConversionApiUrl);

        if (startDate != null || endDate != null) {
            apiUrlBuilder.append("?filter=record_date");

            if (startDate != null) {
                apiUrlBuilder.append(":gte:").append(startDate);
            }

            if (endDate != null) {
                apiUrlBuilder.append(":lte:").append(endDate);
            }
        }

        apiUrlBuilder.append("&fields=record_date,country,currency,country_currency_desc,exchange_rate");

        return apiUrlBuilder.toString();
    }

    private Map<LocalDate, BigDecimal> parseJsonData(String jsonApiResponse, String targetCurrency) throws IOException {
        Map<LocalDate, BigDecimal> conversionRates = new HashMap<>();

        JsonNode rootNode = objectMapper.readTree(jsonApiResponse);
        JsonNode dataNode = rootNode.get("data");

        if (dataNode != null && dataNode.isArray()) {
            for (JsonNode entryNode : dataNode) {
                LocalDate recordDate = LocalDate.parse(entryNode.get("record_date").asText());
                BigDecimal exchangeRate = new BigDecimal(entryNode.get("exchange_rate").asText());

                conversionRates.put(recordDate, exchangeRate);
            }
        }

        return conversionRates;
    }

    public BigDecimal convertToCurrency(BigDecimal amount, String targetCurrency, LocalDate purchaseDate) {
        try {
            // Load or retrieve conversion rates for the specified target currency
            Map<LocalDate, BigDecimal> currencyRates = conversionCache.get(targetCurrency);

            // Find the nearest exchange rate based on the purchase date
            BigDecimal exchangeRate = findNearestExchangeRate(currencyRates, purchaseDate);

            // Perform the currency conversion and round to two decimal places
            return amount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            System.out.println("Conversion of currency failed; defaulting to self");
            return amount;
        }
    }


    private BigDecimal findNearestExchangeRate(Map<LocalDate, BigDecimal> conversionRates, LocalDate purchaseDate) {
        return conversionRates.entrySet()
                .stream()
                .filter(entry -> !entry.getKey().isAfter(purchaseDate))
                .max(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new RuntimeException("No applicable exchange rate found for the given purchase date"));
    }
}