package org.payments;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

public class PaymentStoreMapImplTest extends TestCase {


    @Test
    public void testCalculateAndUpdatePaymentNullCurrency() {
        PaymentStoreMapImpl store = new PaymentStoreMapImpl();
        MonetaryAmount amt = null;
        MonetaryAmount returnedAmt = store.calculateAndUpdatePayment(amt);
        Assert.assertTrue(returnedAmt == null);
        store = null;

    }
    @Test
    public void testCalculateAndUpdatePaymentNewCurrency() {
        PaymentStoreMapImpl store = new PaymentStoreMapImpl();
        MonetaryAmount amt = Monetary.getDefaultAmountFactory()
                .setCurrency("USD").setNumber(100.00).create();
        MonetaryAmount returnedAmt = store.calculateAndUpdatePayment(amt);
        Assert.assertTrue(returnedAmt.isEqualTo(amt));
        store = null;

    }

    @Test
    public void testCalculateAndUpdatePaymentExistingCurrencyAdd() {
        PaymentStoreMapImpl store = new PaymentStoreMapImpl();
        MonetaryAmount amt = Monetary.getDefaultAmountFactory()
                .setCurrency("USD").setNumber(100.00).create();
        store.calculateAndUpdatePayment(amt);
        MonetaryAmount addAmt = Monetary.getDefaultAmountFactory()
                .setCurrency("USD").setNumber(200.00).create();
        MonetaryAmount returnedAmt = store.calculateAndUpdatePayment(addAmt);
        MonetaryAmount expectedAmt = Monetary.getDefaultAmountFactory()
                .setCurrency("USD").setNumber(300.00).create();
        Assert.assertTrue(returnedAmt.isEqualTo(expectedAmt));
        store = null;
    }

    @Test
    public void testCalculateAndUpdatePaymentExistingCurrencySubstractPositiveBalance() {
        PaymentStoreMapImpl store = new PaymentStoreMapImpl();
        MonetaryAmount amt = Monetary.getDefaultAmountFactory()
                .setCurrency("USD").setNumber(100.00).create();
        store.calculateAndUpdatePayment(amt);
        MonetaryAmount subAmt = Monetary.getDefaultAmountFactory()
                .setCurrency("USD").setNumber(-50.00).create();
        MonetaryAmount returnedAmt = store.calculateAndUpdatePayment(subAmt);
        MonetaryAmount expectedAmt = Monetary.getDefaultAmountFactory()
                .setCurrency("USD").setNumber(50.00).create();
        Assert.assertTrue(returnedAmt.isEqualTo(expectedAmt));
        store = null;
    }

    @Test
    public void testCalculateAndUpdatePaymentExistingCurrencySubstractZeroBalance() {
        PaymentStoreMapImpl store = new PaymentStoreMapImpl();
        MonetaryAmount amt = Monetary.getDefaultAmountFactory()
                .setCurrency("USD").setNumber(50.00).create();
        store.calculateAndUpdatePayment(amt);
        MonetaryAmount subAmt = Monetary.getDefaultAmountFactory()
                .setCurrency("USD").setNumber(-50.00).create();
        MonetaryAmount returnedAmt = store.calculateAndUpdatePayment(subAmt);
        MonetaryAmount expectedAmt = Monetary.getDefaultAmountFactory()
                .setCurrency("USD").setNumber(0.00).create();
        Assert.assertTrue(returnedAmt.isEqualTo(expectedAmt));
        store = null;
    }

    @Test
    public void testCalculateAndUpdatePaymentExistingCurrencySubstractNegativeBalance() {
        PaymentStoreMapImpl store = new PaymentStoreMapImpl();
        MonetaryAmount amt = Monetary.getDefaultAmountFactory()
                .setCurrency("USD").setNumber(50.00).create();
        store.calculateAndUpdatePayment(amt);
        MonetaryAmount subAmt = Monetary.getDefaultAmountFactory()
                .setCurrency("USD").setNumber(-70.00).create();
        MonetaryAmount returnedAmt = store.calculateAndUpdatePayment(subAmt);
        MonetaryAmount expectedAmt = Monetary.getDefaultAmountFactory()
                .setCurrency("USD").setNumber(-20.00).create();
        Assert.assertTrue(returnedAmt.isEqualTo(expectedAmt));
        store = null;
    }

    @Test
    public void testProcessOutputWithNonZeroPositiveBalance() throws Exception{
        PaymentStoreMapImpl store = new PaymentStoreMapImpl();

        MonetaryAmount amt = Monetary.getDefaultAmountFactory()
                .setCurrency("USD").setNumber(50.00).create();
        store.calculateAndUpdatePayment(amt);
        Assert.assertTrue(store.processOutput().contains(amt));
        store = null;
    }
    @Test
    public void testProcessOutputWithZeroBalance() throws Exception{
        PaymentStoreMapImpl store = new PaymentStoreMapImpl();

        MonetaryAmount amt = Monetary.getDefaultAmountFactory()
                .setCurrency("USD").setNumber(0).create();
        store.calculateAndUpdatePayment(amt);
        Assert.assertFalse(store.processOutput().contains(amt));
        store = null;
    }
    @Test
    public void testProcessOutputWithNonZeroNegativeBalance() throws Exception{
        PaymentStoreMapImpl store = new PaymentStoreMapImpl();
        MonetaryAmount negativeAmt = Monetary.getDefaultAmountFactory()
                .setCurrency("GBP").setNumber(-30).create();
        store.calculateAndUpdatePayment(negativeAmt);
        Assert.assertTrue(store.processOutput().contains(negativeAmt));
        store = null;
    }



}