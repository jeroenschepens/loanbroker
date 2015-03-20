/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creditbureau;

import jms.AsynchronousReplier;
import jms.RequestListener;

/**
 *
 * @author Jeroen
 */
public abstract class LoanBrokerGateway extends AsynchronousReplier<CreditRequest, CreditReply> {

    public LoanBrokerGateway(String requestQueue)
            throws Exception {
        super(requestQueue, new CreditSerializer());
        super.setRequestListener(new RequestListener<CreditRequest>() {

            @Override
            public void receivedRequest(CreditRequest request) {
                onCreditRequestReceived(request);
            }
        });
    }

    public abstract void onCreditRequestReceived(CreditRequest request);
}
