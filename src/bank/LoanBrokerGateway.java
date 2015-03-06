/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank;

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
public abstract class LoanBrokerGateway {

    BankSerializer serializer;
    MessagingGateway messagingGateway;

    public LoanBrokerGateway(String producer, String consumer) throws JMSException, NamingException {
        serializer = new BankSerializer();
        messagingGateway = new MessagingGateway(producer, consumer);
        messagingGateway.setReceivedMessageListener(new MessageListener() {

            public void onMessage(Message msg) {
                try {
                    TextMessage m = (TextMessage) msg;
                    BankQuoteRequest request = serializer.requestFromString(m.getText());
                    receivedQuoteRequest(request);
                } catch (JMSException ex) {
                    Logger.getLogger(LoanBrokerGateway.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });
        messagingGateway.openConnection();
    }

    public abstract void receivedQuoteRequest(BankQuoteRequest request);

    void sendQuoteOffer(BankQuoteRequest request, BankQuoteReply reply) {
        try {
            Message m = messagingGateway.createTextMessage(serializer.replyToString(reply));
            messagingGateway.sendMessage((TextMessage) m);
        } catch (JMSException ex) {
            Logger.getLogger(LoanBrokerGateway.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
