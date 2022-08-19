# Payments

1) The main class in the file PaymentApp.java
2) Build the project using the command : mvn clean compile assembly:single
3) The jar is within the target folder 
4) The project can be run using java -jar from the commandline and optionally passing the filepath - there is already a  sample file in the resources directory
5) The application accepts the entry from the console as well as on the file in the format e.g. "GBP 50". Any other format would be rejected and any invalid entries would also be rejected.
6) Please ignore the java Money API log info intially appear  when first time an entry is made from the console
7) The display of balance runs every 1 min with conversion currency in USD for the non USD currencies
8) A custom written ExchangeRateProvider is used as it takes less time. However the real ExchangeRateProvider can also be used when it's uncommented (followed by commenting custom Exchange Provider) within the PaymentController constructor. When run it would take longer to intialise and lot of logging info with few errors  on the console while it connects to external providers. The errors can be ignored as some of the providers can't be connected.
9) The application stops on typing "quit" and pressing enter. 
