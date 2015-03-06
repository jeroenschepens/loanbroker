/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loanbroker;

import creditbureau.CreditReply;
import creditbureau.CreditRequest;
import creditbureau.CreditSerializer;
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
public abstract class CreditGateway {

    CreditSerializer serializer;
    MessagingGateway messagingGateway;

    public CreditGateway(String senderName, String receiverName) throws NamingException, JMSException {

        serializer = new CreditSerializer();
        messagingGateway = new MessagingGateway(senderName, receiverName);
        messagingGateway.setReceivedMessageListener(new MessageListener() {

            public void onMessage(Message msg) {
                try {
                    TextMessage m = (TextMessage) msg;
                    CreditReply reply = serializer.replyFromString(m.getText());
                    receivedCreditRequest(reply);
                } catch (JMSException ex) {
                    //Logger.getLogger(loanBroakerGateway.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });
        messagingGateway.openConnection();
    }

    public abstract void receivedCreditRequest(CreditReply reply);

    public boolean getCreditHistory(CreditRequest request) {
        try {
            Message m = messagingGateway.createTextMessage(serializer.requestToString(request));
            return messagingGateway.sendMessage((TextMessage) m);
        } catch (JMSException ex) {
            Logger.getLogger(CreditGateway.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
