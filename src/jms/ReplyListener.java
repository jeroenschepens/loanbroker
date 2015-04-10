package jms;

/**
 *
 * @author Maja Pesic
 */
public interface ReplyListener<REQUEST, REPLY> {

    public void onReply(REQUEST request, REPLY reply);
}
