package jms;

/**
 *
 * @author Maja Pesic
 */
public interface RequestListener<REQUEST> {

    public void receivedRequest(REQUEST request);
}
