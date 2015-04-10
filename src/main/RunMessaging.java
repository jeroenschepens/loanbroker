package main;

import bank.Bank;
import client.ClientRequest;
import client.LoanTestClient;
import creditbureau.CreditBureau;
import java.util.logging.Level;
import java.util.logging.Logger;
import loanbroker.LoanBroker;

/**
 * This application tests the LoanBroker system.
 *
 */
public class RunMessaging {

    public static void main(String[] args) {
        try {
            // read the queue names from file "MESSAGING.ini"
            JMSSettings.init("MESSAGING_CHANNELS.ini");
            JMSSettings.setRunMode(JMSSettings.RunMode.AUTOMATICALLY); // this means that the Bank will automatically generate an Interest rate and return it!
            final String clientRequestQueue = JMSSettings.get(JMSSettings.LOAN_REQUEST);
            final String clientReplyQueue = JMSSettings.get(JMSSettings.LOAN_REPLY);
            final String clientReplyQueue2 = JMSSettings.get(JMSSettings.LOAN_REPLY_2);
            final String creditRequestQueue = JMSSettings.get(JMSSettings.CREDIT_REQUEST);
            final String creditReplyQueue = JMSSettings.get(JMSSettings.CREDIT_REPLY);
            final String jeroenRequestQueue = JMSSettings.get(JMSSettings.BANK_1);
            final String patrickRequestQueue = JMSSettings.get(JMSSettings.BANK_2);
            final String alexanderRequestQueue = JMSSettings.get(JMSSettings.BANK_3);
            final String broumelsRequestQueue = JMSSettings.get(JMSSettings.BANK_4);
            final String bankReplyQueue = JMSSettings.get(JMSSettings.BANK_REPLY);

            // create a LoanBroker middleware
            LoanBroker broker = new LoanBroker(clientRequestQueue, creditRequestQueue, creditReplyQueue, bankReplyQueue);
            broker.addBank(jeroenRequestQueue, "#{amount} > 75000 && #{credit} > 600 && #{history} > 8");
            broker.addBank(patrickRequestQueue, "#{amount} > 10000 && #{amount} < 75000 && #{credit} > 400 && #{history} > 3");
            broker.addBank(alexanderRequestQueue, "#{amount} > 70000 && #{credit} > 500 && #{history} > 5");
            broker.addBank(broumelsRequestQueue, "#{amount} > 1000000");

            // create a Client Application
            LoanTestClient client = new LoanTestClient("Bom Troumels Loan Shark", clientRequestQueue, clientReplyQueue);
            LoanTestClient client2 = new LoanTestClient("SNS Poverty Finance", clientRequestQueue, clientReplyQueue2);

            // create the CreditBureau Application
            CreditBureau creditBureau = new CreditBureau(creditRequestQueue);

            // create one Bank application
            Bank broumels = new Bank("BroumelBank", broumelsRequestQueue);
            Bank jeroen = new Bank("Jeroen Bank", jeroenRequestQueue);
            Bank patrick = new Bank("Patrick Bank", patrickRequestQueue);
            Bank alexander = new Bank("AlexanderBank", alexanderRequestQueue);

            // open all connections in the broker, client and credit applications
            broker.start();

            creditBureau.start();

            broumels.start();
            jeroen.start();
            patrick.start();
            alexander.start();

            client.start();
            client2.start();
        } catch (Exception ex) {
            Logger.getLogger(RunMessaging.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
