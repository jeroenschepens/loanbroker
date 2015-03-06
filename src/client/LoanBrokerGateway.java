package client;

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

    ClientSerializer serializer;
    MessagingGateway messagingGateway;

    public LoanBrokerGateway(String producer, String consumer) throws JMSException, NamingException {
        serializer = new ClientSerializer();
        messagingGateway = new MessagingGateway(producer, consumer);
        messagingGateway.setReceivedMessageListener(new MessageListener() {

            public void onMessage(Message msg) {
                try {
                    TextMessage m = (TextMessage) msg;
                    ClientReply reply = serializer.replyFromString(m.getText());
                    loanOfferReceived(reply);
                } catch (JMSException ex) {
                    Logger.getLogger(LoanBrokerGateway.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });

        messagingGateway.openConnection();
    }

    public abstract void loanOfferReceived(ClientReply reply);

    public boolean applyForLoan(ClientRequest request) {
        try {
            Message m = messagingGateway.createTextMessage(serializer.requestToString(request));
            return messagingGateway.sendMessage((TextMessage) m);
        } catch (JMSException ex) {
            Logger.getLogger(LoanBrokerGateway.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
