package org.payments;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.UnknownCurrencyException;
import javax.money.convert.ExchangeRateProvider;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;


public class PaymentCurrencyAsyncProcessorTest extends TestCase {

    private PaymentCurrencyAsyncProcessor processor = null;

    @Before
    public void setUp() throws Exception {
        ExchangeRateProvider exRateProvider = new TestExchangeRateProvider();
        processor = new PaymentCurrencyAsyncProcessor(new PaymentStoreMapImpl(),exRateProvider);
       // super.setUp();

    }


    public void tearDown() throws Exception {
    }

    @Test
    public void testIsCurrencyValidWithNull() {

        Assert.assertFalse(processor.isCurrencyValid(null));

    }
    @Test
    public void testIsCurrencyValidWithValidCurrency() {

        Assert.assertTrue(processor.isCurrencyValid("USD"));

    }
    @Test
    public void testIsCurrencyValidWithEmptySpace() {

        Assert.assertFalse(processor.isCurrencyValid(""));

    }
    @Test
    public void testIsCurrencyValidWithSpecialChars() {

        Assert.assertFalse(processor.isCurrencyValid("$&*"));

    }

    @Test
    public void testProcessCalculationWithNullCurrency() {

        CompletableFuture<MonetaryAmount> amt =
                processor.processCalculation(null,new BigDecimal("0"));
        Assert.assertTrue(amt == null);


    }
    @Test(expected = UnknownCurrencyException.class)
    public void testProcessCalculationWithInvalidCurrency() throws Exception{

        CompletableFuture<MonetaryAmount> amt =
                processor.processCalculation("ABC",new BigDecimal("0"));
        amt.get();

    }
    @Test(expected = UnknownCurrencyException.class)
    public void testProcessCalculationWithInvalidCurrencyEmptySpace() throws Exception{

        CompletableFuture<MonetaryAmount> amt =
                processor.processCalculation("",new BigDecimal("0"));
        amt.get();

    }

    @Test
    public void testProcessCalculationWithValidCurrency() throws Exception{

        CompletableFuture<MonetaryAmount> amt =
                processor.processCalculation("USD",new BigDecimal("100"));
        MonetaryAmount expectedAmt = Monetary.getDefaultAmountFactory()
                .setCurrency("USD").setNumber(100).create();
        Assert.assertTrue(amt.get().isEqualTo(expectedAmt));

    }

    @Test
    public void testProcessCalculationWithValidCurrencyAdd() throws Exception{

        CompletableFuture<MonetaryAmount> amt =
                processor.processCalculation("USD",new BigDecimal("100"));
        amt.get();
        CompletableFuture<MonetaryAmount> addAmt =
                processor.processCalculation("USD",new BigDecimal("50"));
        MonetaryAmount expectedAmt = Monetary.getDefaultAmountFactory()
                .setCurrency("USD").setNumber(150).create();
        Assert.assertTrue(addAmt.get().isEqualTo(expectedAmt));

    }

    @Test
    public void testConvertCurrencyCode(){
        MonetaryAmount amt = Monetary.getDefaultAmountFactory()
                .setCurrency("GBP").setNumber(150).create();
        MonetaryAmount output = processor.convertCurrency(amt,"USD");
        Assert.assertTrue(output.getCurrency().getCurrencyCode().equals("USD"));
    }

    @Test
    public void testConvertCurrencyAmt(){
        MonetaryAmount amt = Monetary.getDefaultAmountFactory()
                .setCurrency("GBP").setNumber(150).create();
        MonetaryAmount expectedAmt = Monetary.getDefaultAmountFactory()
                .setCurrency("USD").setNumber(205.5).create();
        MonetaryAmount output = processor.convertCurrency(amt,"USD");
       Assert.assertTrue(output.getNumber().toString().equals(expectedAmt.getNumber().toString()));
    }


}