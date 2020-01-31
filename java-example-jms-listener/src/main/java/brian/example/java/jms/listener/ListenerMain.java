package brian.example.java.jms.listener;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ListenerMain {

    public ListenerMain(){
        try {
            InitialContext ctx = new InitialContext();
            ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://192.168.99.100:61616");
            Connection connection = cf.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("inbound.queue");

            // To send the message back to sender ----//
            MessageProducer replyProducer = session.createProducer(null);
            replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            // ---------------------------------------//

            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(new MapMessageListener(session, replyProducer));

            connection.start();

        } catch (NamingException e) {
            e.printStackTrace();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        new ListenerMain();
    }
}
