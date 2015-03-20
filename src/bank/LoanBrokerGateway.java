/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank;

import jms.AsynchronousReplier;
import jms.RequestListener;

/**
 *
 * @author Jeroen
 */
public abstract class LoanBrokerGateway extends AsynchronousReplier<BankQuoteRequest, BankQuoteReply> {

    public LoanBrokerGateway(String requestQueue)
            throws Exception {
        super(requestQueue, new BankSerializer());
        super.setRequestListener(new RequestListener<BankQuoteRequest>() {

            @Override
            public void receivedRequest(BankQuoteRequest request) {
                onBankQuoteRequestReceived(request);
            }
        });
    }

    public abstract void onBankQuoteRequestReceived(BankQuoteRequest request);
}
