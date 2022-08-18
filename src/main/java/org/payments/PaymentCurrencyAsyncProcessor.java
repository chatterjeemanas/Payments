package org.payments;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRateProvider;

import java.math.BigDecimal;

import java.util.List;
import java.util.Optional;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PaymentCurrencyAsyncProcessor
        implements PaymentCurrencyProcessorIF<String, CompletableFuture<MonetaryAmount>,
        BigDecimal, List<MonetaryAmount>, MonetaryAmount> {
    private final PaymentStoreIF<MonetaryAmount, MonetaryAmount, List<MonetaryAmount>> store;
    private final ExchangeRateProvider exchangeRateProvider;
    private ScheduledExecutorService executor = null;

    public PaymentCurrencyAsyncProcessor(PaymentStoreIF<MonetaryAmount, MonetaryAmount, List<MonetaryAmount>> store, ExchangeRateProvider exchangeRateProvider) {
        this.store = store;
        this.exchangeRateProvider = exchangeRateProvider;
    }

    @Override
    public boolean isCurrencyValid(String currencyCode) {
        return Optional.ofNullable(currencyCode).map(curr -> Monetary.isCurrencyAvailable(curr)).orElse(false);
    }

    @Override
    public CompletableFuture<MonetaryAmount> processCalculation(String currencyCode, BigDecimal amt) {
        CompletableFuture<MonetaryAmount> amtFromOptional = Optional.ofNullable(currencyCode)
                .map(curr -> createMonetaryAmount(curr, amt)).orElse(null);


        if (amtFromOptional != null) {
            return amtFromOptional.thenCompose(value -> CompletableFuture
                    .supplyAsync(() -> store.calculateAndUpdatePayment(value)))
                    .exceptionally(ex -> {
                        System.out.println("Exception: " + ex.getMessage());
                        return null;
                    });
        }

        return null;
    }


    private CompletableFuture<MonetaryAmount> createMonetaryAmount(String currencyCode, BigDecimal amt) {
        return CompletableFuture.supplyAsync(() -> Monetary.getDefaultAmountFactory()
                .setCurrency(currencyCode).setNumber(amt).create());
    }

    @Override
    public List<MonetaryAmount> processOutput() {
        TimerTask outputTask = new TimerTask() {
            public void run() {
                store.processOutput().forEach(amt -> {
                    System.out.print(amt.getCurrency().getCurrencyCode() + " " + amt.getNumber());
                    if (!amt.getCurrency().getCurrencyCode().equals("USD")) {
                        System.out.print("(USD " + convertCurrency(amt, "USD").getNumber() + ")\n");
                    } else {
                        System.out.println();
                    }

                });

            }
        };
        this.executor = Executors.newSingleThreadScheduledExecutor();
        long delay = 60L;
        long period = 60L;
        executor.scheduleAtFixedRate(outputTask, delay, period, TimeUnit.SECONDS);


        return store.processOutput();


    }

    @Override
    public List<MonetaryAmount> viewStoreContent(){
        return store.viewStoreContent();

    }

    @Override
    public void shutdown() {
        if(executor !=null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }


    @Override
    public MonetaryAmount convertCurrency(MonetaryAmount fromCurr, String baseCurrencyCode) {

        CurrencyConversion conversion = this.exchangeRateProvider.getCurrencyConversion(baseCurrencyCode);
        return fromCurr.with(conversion);

    }


}
