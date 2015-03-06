/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loanbroker;

import client.ClientReply;
import client.ClientRequest;
import client.ClientSerializer;
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
public abstract class ClientGateway {
    
    ClientSerializer serializer;
    MessagingGateway messagingGateway;

    public ClientGateway(String senderName, String receiverName) throws NamingException, JMSException {

        serializer = new ClientSerializer();
        messagingGateway = new MessagingGateway(senderName, receiverName);
        messagingGateway.setReceivedMessageListener(new MessageListener() {

            public void onMessage(Message msg) {
                try {
                    TextMessage m = (TextMessage) msg;
                    ClientRequest request = serializer.requestFromString(m.getText());
                    receivedLoanRequest(request);
                } catch (JMSException ex) {
                    //Logger.getLogger(loanBroakerGateway.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        });
        messagingGateway.openConnection();
    }

    public abstract void receivedLoanRequest(ClientRequest request);

    public boolean offerLoan(ClientReply reply) {
        try {
            Message m = messagingGateway.createTextMessage(serializer.replyToString(reply));
            return  messagingGateway.sendMessage((TextMessage) m);
        } catch (JMSException ex) {
            Logger.getLogger(ClientGateway.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
