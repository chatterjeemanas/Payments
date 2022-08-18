package org.payments;

import javax.money.MonetaryAmount;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class PaymentStoreMapImpl implements PaymentStoreIF<MonetaryAmount,MonetaryAmount,List<MonetaryAmount>> {
    private final Map<String,MonetaryAmount> payStore = new ConcurrentHashMap<>();

    @Override
    public MonetaryAmount calculateAndUpdatePayment(final MonetaryAmount amount){

         return Optional.ofNullable(amount).map(amt -> payStore.compute(amt.getCurrency()
                 .getCurrencyCode(),(k,v) -> v == null? amt : v.add(amt))).orElse(null);

    }

    @Override
    public List<MonetaryAmount> processOutput(){
        return payStore.entrySet().parallelStream()
                .map(Map.Entry::getValue).filter(v -> !v.isZero()).collect(Collectors.toList());
    }

    @Override
    public List<MonetaryAmount> viewStoreContent(){
        return payStore.entrySet().parallelStream()
                .map(Map.Entry::getValue).collect(Collectors.toList());
    }

}
