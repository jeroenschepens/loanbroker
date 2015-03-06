package jms;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.naming.NamingException;

public class MessagingGateway {

    private final Connection connection; // connection to the JMS server
    protected Session session; // JMS session for creating producers, consumers and messages
    private final Properties props = new Properties();
    private final MessageProducer messageProducer;
    private final MessageConsumer messageConsumer;

    public MessagingGateway(String producer, String consumer) throws NamingException, JMSException {

        // connect to JMS
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
        props.put(("queue." + producer), producer);
        props.put(("queue." + consumer), consumer);

        // init connection
        Context jndiContext = new InitialContext(props);
        ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // connect to the sender channel
        Destination senderDestination = (Destination) jndiContext.lookup(producer);
        messageProducer = session.createProducer(senderDestination);

        // connect to the receiver channel and register as a listener on it
        Destination receiverDestination = (Destination) jndiContext.lookup(consumer);
        messageConsumer = session.createConsumer(receiverDestination);

    }

    public TextMessage createTextMessage(String text) {
        try {
            return session.createTextMessage(text);
        } catch (JMSException ex) {
            Logger.getLogger(MessagingGateway.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public boolean sendMessage(Message message) throws JMSException {
        messageProducer.send(message);
        return true;
    }

    public void setReceivedMessageListener(MessageListener ml) throws JMSException {
        messageConsumer.setMessageListener(ml);
    }

    public void openConnection() throws NamingException, JMSException {
        connection.start();
    }
}
