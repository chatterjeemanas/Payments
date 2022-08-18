package org.payments;

/**
 * Entry class
 */
public class PaymentApp {


    private final PaymentController controller = new PaymentController();

    public static void main(String[] args) {
        PaymentApp app = new PaymentApp();

        //read from File
        if (args.length > 0) {
            app.controller.generateFileAndProcess(args[0]);
        }
        //Start the Timer display
        app.controller.processOutput();
        System.out.println("Enter currency code and amount:");
        //Input on the console
        app.controller.getConsoleInputAndProcess(System.in);
        //shutdown
        app.controller.shutdown();

    }
















































}

