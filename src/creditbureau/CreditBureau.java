package creditbureau;

import creditbureau.gui.CreditFrame;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents one Credit Agency Application. It should: 1. Receive
 * CreditRequest-s for a loan from the LoanBroker Messaging-Oriented Middleware
 * (MOM). 2. Randomly create CreditReply for each request (use method
 * "getReply"). 3. Send the CreditReply from the LoanBroker MOM.
 */
public class CreditBureau {

    private final Random random = new Random(); // for random generation of replies
    private CreditFrame frame; // GUI
    private final LoanBrokerGateway loanGateway;

    public CreditBureau(String creditRequestQueue, String creditReplyQueue) throws Exception {
        super();

        loanGateway = new LoanBrokerGateway(creditReplyQueue, creditRequestQueue) {

            @Override
            public void receivedCreditRequest(CreditRequest request) {
                onCreditRequest(request);
            }
        };

        // create GUI
        frame = new CreditFrame();
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {

                frame.setVisible(true);
            }
        });
    }

    /**
     * Processes a new request message by randomly generating a reply and
     * sending it back.
     *
     * @param message the credit request message
     */
    private void onCreditRequest(CreditRequest request) {
        try {
            frame.addRequest(request);

            CreditReply reply = computeReply(request);
            loanGateway.sendCreditHistory(reply);

            frame.addReply(request, reply);
        } catch (Exception ex) {
            Logger.getLogger(CreditBureau.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Randomly generates a CreditReply given the request.
     *
     * @param request is the CreditRequest for which the reply must be generated
     * @return a credit reply
     */
    private CreditReply computeReply(CreditRequest request) {
        int ssn = request.getSSN();

        int score = (int) (random.nextInt(600) + 300);
        int history = (int) (random.nextInt(19) + 1);

        return new CreditReply(ssn, score, history);
    }

}
