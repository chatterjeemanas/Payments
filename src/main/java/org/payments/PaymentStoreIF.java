package org.payments;

import javax.money.MonetaryAmount;
import java.util.List;

public interface PaymentStoreIF <T,R,L>{
    public R calculateAndUpdatePayment(T payAmount);
    public L processOutput();
    public L viewStoreContent();
}
