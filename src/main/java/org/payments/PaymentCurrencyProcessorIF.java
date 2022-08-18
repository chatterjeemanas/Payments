package org.payments;


public interface PaymentCurrencyProcessorIF <T,R,D,L,M>{
    public boolean isCurrencyValid(T currencyCode);
    public R processCalculation(T currencyCode, D amt);
    public L processOutput();
    public M convertCurrency(M inputCurr, T outputCurrCode);
    public void shutdown();
    public L viewStoreContent();
}


