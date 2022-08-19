package org.payments;

import javax.money.MonetaryAmount;
import javax.money.convert.ExchangeRateProvider;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class PaymentController {


    private final PaymentCurrencyProcessorIF<String, CompletableFuture<MonetaryAmount>,
            BigDecimal, List<MonetaryAmount>, MonetaryAmount> processor;

    public PaymentController() {
        //ExchangeRateProvider exchangeRateProvider  = MonetaryConversions.getExchangeRateProvider();
        ExchangeRateProvider exchangeRateProvider = new TestExchangeRateProvider();
        this.processor = new PaymentCurrencyAsyncProcessor(new PaymentStoreMapImpl(), exchangeRateProvider);
    }


    public PaymentCurrencyProcessorIF<String, CompletableFuture<MonetaryAmount>, BigDecimal, List<MonetaryAmount>, MonetaryAmount> getProcessor() {
        return processor;
    }

    public void generateFileAndProcess(String filePath) {

        try(BufferedInputStream bufferStream = new BufferedInputStream(new FileInputStream(new File(filePath)))) {
            getFileInputAndProcess(bufferStream);
        }
        catch (Exception ex) {
            System.out.println("Exception inside generateFile:" + ex.toString());
        }



    }

    public void getConsoleInputAndProcess(InputStream source) {
        Scanner in = new Scanner(source);
        while (!in.hasNext("quit")) {
            processScan(in);
        }
    }


    private void getFileInputAndProcess(InputStream source) {
        Scanner in = new Scanner(source);
        while (in.hasNextLine()) {
            processScan(in);
        }

        in.close();
    }

    private void processScan(Scanner in) {

        String currency = in.next();

        if (processor.isCurrencyValid(currency) && in.hasNextBigDecimal()) {

            BigDecimal pay = in.nextBigDecimal();
            processCurrency(currency, pay);

        } else {
            //clear the remaining input and advance to the next line
            in.nextLine();
            System.out.println("Invalid Entry");
        }
    }


    public void processCurrency(String currencyCode, BigDecimal pay) {
        processor.processCalculation(currencyCode, pay);

    }


    public void processOutput() {
        processor.processOutput();
    }

    public void shutdown() {
        processor.shutdown();
    }


}
