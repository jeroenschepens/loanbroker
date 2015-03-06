package client;

import client.gui.ClientFrame;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * This class represents one Client Application. It: 1. Creates a ClientRequest
 * for a loan. 2. Sends it to the LoanBroker Messaging-Oriented Middleware
 * (MOM). 3. Receives the reply from the LoanBroker MOM.
 *
 */
public class LoanTestClient {

    private final LoanBrokerGateway loanGateway;
    private ClientFrame frame; // GUI

    public LoanTestClient(String name, String requestQueue, String replyQueue) throws Exception {
        super();

        loanGateway = new LoanBrokerGateway(requestQueue, replyQueue) {

            @Override
            public void loanOfferReceived(ClientReply reply) {
                processLoanOffer(reply);
            }
        };

        // create the GUI
        frame = new ClientFrame(name) {

            @Override
            public void send(ClientRequest request) {
                sendRequest(request);
            }
        };

        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {

                frame.setVisible(true);
            }
        });
    }

    /**
     * Sends new loan request to the LoanBroker.
     *
     * @param request
     */
    public void sendRequest(ClientRequest request) {
        if (loanGateway.applyForLoan(request)) {
            frame.addRequest(request);
        }
    }

    /**
     * This message is called whenever a new client reply message arrives. The
     * message is de-serialized into a ClientReply, and the reply is shown in
     * the GUI.
     *
     * @param message
     */
    private void processLoanOffer(ClientReply reply) {
        frame.addReply(null, reply);
    }
}
