package com.ag.transactiongatewayexchange.service;

import com.ag.transactiongatewayexchange.dto.PurchaseWithExchangeRateDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class CurrencyConverterService {

    @Value("${currency-conversion.api.url}")
    private String currencyConversionApiUrl;

    @Value("${currency-conversion.cache.expire-after-write}")
    private int expireAfterWrite;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final LoadingCache<String, Map<LocalDate, BigDecimal>> currencyExchangeRatesCache = CacheBuilder.newBuilder()
            .expireAfterWrite(expireAfterWrite, TimeUnit.DAYS) // Cache expires after 24 hours
            .build(new CacheLoader<String, Map<LocalDate, BigDecimal>>() {
                @Override
                public Map<LocalDate, BigDecimal> load(String key) throws Exception {
                    return fetchAndStoreExchangeRates(key);
                }
            });

    private Map<LocalDate, BigDecimal> fetchAndStoreExchangeRates(String currencyKey) {
        String apiUrlWithParams = currencyConversionApiUrl + "?fields=currency,exchange_rate,record_date" +
                "&filter=currency:in:(" + currencyKey + ")" +
                "&filter=record_date:gte:2023-12-31";

        RestTemplate restTemplate = new RestTemplate();
        String jsonApiResponse = restTemplate.getForObject(apiUrlWithParams, String.class);
        return parseJsonData(jsonApiResponse);
    }

    private Map<LocalDate, BigDecimal> parseJsonData(String jsonApiResponse) {
        Map<LocalDate, BigDecimal> exchangeRatesMap = new HashMap<>();

        try {
            JsonNode rootNode = objectMapper.readTree(jsonApiResponse);
            JsonNode dataNode = rootNode.get("data");

            if (dataNode != null && dataNode.isArray()) {
                Iterator<JsonNode> iterator = dataNode.elements();

                while (iterator.hasNext()) {
                    JsonNode entryNode = iterator.next();
                    LocalDate recordDate = LocalDate.parse(entryNode.get("record_date").asText());
                    BigDecimal exchangeRate = new BigDecimal(entryNode.get("exchange_rate").asText());

                    exchangeRatesMap.put(recordDate, exchangeRate);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error parsing JSON data", e);
        }

        return exchangeRatesMap;
    }

    public PurchaseWithExchangeRateDTO convertToCurrency(PurchaseWithExchangeRateDTO purchaseWithExchangeRateDTO, String targetCurrency) {
        try {
            Map<LocalDate, BigDecimal> exchangeRates = currencyExchangeRatesCache.get(targetCurrency);
            BigDecimal closestExchangeRate = findNearestExchangeRate(exchangeRates, purchaseWithExchangeRateDTO.getTransactionDate());
            purchaseWithExchangeRateDTO.setExchangeRateUsed(closestExchangeRate);
            purchaseWithExchangeRateDTO.setConvertedAmount(purchaseWithExchangeRateDTO.getAmount().multiply(closestExchangeRate));
        } catch (Exception e) {
            System.out.println("Error converting currency; defaulting to self " + e.getMessage());
        }

        return purchaseWithExchangeRateDTO;
    }

    private BigDecimal findNearestExchangeRate(Map<LocalDate, BigDecimal> exchangeRates, LocalDate purchaseDate) {
        return exchangeRates.entrySet()
                .stream()
                .filter(entry -> !entry.getKey().isAfter(purchaseDate))
                .max(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new RuntimeException("No applicable exchange rate found for the given purchase date"));
    }
}