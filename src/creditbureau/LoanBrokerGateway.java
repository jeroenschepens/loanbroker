/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creditbureau;

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

    CreditSerializer serializer;
    MessagingGateway messagingGateway;

    public LoanBrokerGateway(String producer, String consumer) throws NamingException, JMSException {

        serializer = new CreditSerializer();
        messagingGateway = new MessagingGateway(producer, consumer);
        messagingGateway.setReceivedMessageListener(new MessageListener() {

            public void onMessage(Message msg) {
                try {
                    TextMessage m = (TextMessage) msg;
                    CreditRequest request = serializer.requestFromString(m.getText());
                    receivedCreditRequest(request);
                } catch (JMSException ex) {
                    Logger.getLogger(LoanBrokerGateway.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });
        messagingGateway.openConnection();
    }

    public abstract void receivedCreditRequest(CreditRequest request);

    public boolean sendCreditHistory(CreditReply reply) {
        try {
            Message m = messagingGateway.createTextMessage(serializer.replyToString(reply));
            return messagingGateway.sendMessage((TextMessage) m);
        } catch (JMSException ex) {
            Logger.getLogger(LoanBrokerGateway.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
