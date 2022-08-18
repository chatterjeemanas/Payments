package org.payments;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.util.List;

public class PaymentControllerTest extends TestCase {

    @Test
    public void testGenerateFileAndProcess() throws Exception{

        PaymentController controller= new PaymentController();
        controller.generateFileAndProcess("/Users/manaschatterjee/workspace/Payments/src/main/resources/payments.txt");
        Thread.sleep(300);
        List<MonetaryAmount> list = controller.getProcessor().viewStoreContent();
        MonetaryAmount expectedMonetaryAmount = Monetary.getDefaultAmountFactory()
                .setCurrency("USD").setNumber(100).create();
        Assert.assertTrue(list.contains(expectedMonetaryAmount));
    }
}