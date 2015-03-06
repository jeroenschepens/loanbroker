/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package loanbroker;

import bank.BankQuoteReply;
import bank.BankQuoteRequest;
import client.*;
import creditbureau.CreditReply;
import creditbureau.CreditRequest;
import loanbroker.gui.LoanBrokerFrame;

/**
 *
 * @author Maja Pesic
 */
public class LoanBroker {

    private final ClientGateway clientG;
    private final BankGateway bankG;
    private final CreditGateway creditG;
    private LoanBrokerFrame frame; // GUI

    /**
     * Initializes attributes, and registers itself (method onClinetRequest) as
     * the listener for new client requests
     *
     * @param clientRequestQueue
     * @param clientReplyQueue
     * @param creditRequestQueue
     * @param creditReplyQueue
     * @param bankRequestQueue
     * @param bankReplyQueue
     * @throws java.lang.Exception
     */
    public LoanBroker(String clientRequestQueue, String clientReplyQueue, String creditRequestQueue, String creditReplyQueue, String bankRequestQueue, String bankReplyQueue) throws Exception {
        super();

        clientG = new ClientGateway(clientReplyQueue, clientRequestQueue) {
            @Override
            public void receivedLoanRequest(ClientRequest request) {
                onClientRequest(request);
            }

        };

        creditG = new CreditGateway(creditRequestQueue, creditReplyQueue) {

            @Override
            public void receivedCreditRequest(CreditReply reply) {
                onCreditReply(reply);
            }
        };

        bankG = new BankGateway(bankRequestQueue, bankReplyQueue) {

            @Override
            public void receivedQuoteRequest(BankQuoteReply Reply) {
                onBankReply(Reply);
            }
        };

        /*
         * Make the GUI
         */
        frame = new LoanBrokerFrame();
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {

                frame.setVisible(true);
            }
        });
    }

    /**
     * This method is called when a new client request arrives. It generates a
     * CreditRequest and sends it to the CreditBureau.
     *
     * @param message the incomming message containng the ClientRequest
     */
    private void onClientRequest(ClientRequest request) {
        try {
            //ClientRequest request = clientSerializer.requestFromString(message.getText()); // de-serialize ClientRequest from the message
            frame.addObject(null, request); // add the request to the GUI

            CreditRequest credit = createCreditRequest(request); // generate CreditRequest

            creditG.getCreditHistory(credit);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * This method is called when a new credit reply arrives. It generates a
     * BankQuoteRequest and sends it to the Bank.
     *
     * @param message the incomming message containng the CreditReply
     */
    private void onCreditReply(CreditReply reply) {
        try {
            frame.addObject(null, reply); // add the reply to the GUI
            BankQuoteRequest bank = createBankRequest(null, reply); // generate BankQuoteRequest
            bankG.getBankQuote(bank);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * This method is called when a new bank quote reply arrives. It generates a
     * ClientReply and sends it to the LoanTestClient.
     *
     * @param message the incomming message containng the BankQuoteReply
     */
    private void onBankReply(BankQuoteReply reply) {
        try {
            frame.addObject(null, reply); // add the reply to the GUI
            ClientReply client = createClientReply(reply); // generate ClientReply
            clientG.offerLoan(client);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Generates a credit request based on the given client request.
     *
     * @param clientRequest
     * @return
     */
    private CreditRequest createCreditRequest(ClientRequest clientRequest) {
        return new CreditRequest(clientRequest.getSSN());
    }

    /**
     * Generates a bank quote request based on the given client request and
     * credit reply.
     *
     * @param creditReply
     * @return
     */
    private BankQuoteRequest createBankRequest(ClientRequest clientRequest, CreditReply creditReply) {
        int ssn = creditReply.getSSN();
        int score = creditReply.getCreditScore();
        int history = creditReply.getHistory();
        int amount = 100; // this must be hard coded because we don't know to which clientRequest this creditReply belongs to!!! 
        int time = 24;   // this must be hard coded because we don't know to which clientRequest this creditReply belongs to!!! 
        if (clientRequest != null) {
            amount = clientRequest.getAmount();
            time = clientRequest.getTime();
        }
        return new BankQuoteRequest(ssn, score, history, amount, time);
    }

    /**
     * Generates a client reply based on the given bank quote reply.
     *
     * @param creditReply
     * @return
     */
    private ClientReply createClientReply(BankQuoteReply reply) {
        return new ClientReply(reply.getInterest(), reply.getQuoteId());
    }
}
