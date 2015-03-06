/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loanbroker;

import bank.BankQuoteReply;
import bank.BankQuoteRequest;
import bank.BankSerializer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.naming.NamingException;
import jms.MessagingGateway;

/**
 *
 * @author Jeroen
 */
public abstract class BankGateway {
    
    BankSerializer serializer;
    MessagingGateway messagingGateway;

    public BankGateway(String senderName, String receiverName) throws NamingException, JMSException {

        serializer = new BankSerializer();
        messagingGateway = new MessagingGateway(senderName, receiverName);
        messagingGateway.setReceivedMessageListener(new MessageListener() {

            public void onMessage(Message msg) {
                try {
                    TextMessage m = (TextMessage) msg;
                    BankQuoteReply reply = serializer.replyFromString(m.getText());
                    receivedQuoteRequest(reply);
                } catch (JMSException ex) {
                    
                }
            }
            
        });
        messagingGateway.openConnection();
    }

    public abstract void receivedQuoteRequest(BankQuoteReply Reply);

    public void getBankQuote(BankQuoteRequest request) {
        try {
            Message m = messagingGateway.createTextMessage(serializer.requestToString(request));
            messagingGateway.sendMessage((TextMessage) m);
        } catch (JMSException ex) {
            Logger.getLogger(BankGateway.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
