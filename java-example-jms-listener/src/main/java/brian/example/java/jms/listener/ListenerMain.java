package brian.example.java.jms.listener;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ListenerMain {

    public static void main(String[] args){


        try {
            InitialContext ctx = new InitialContext();
            ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61616");
            Connection connection = cf.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("inbound.queue");

            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(new MapMessageListener());

            connection.start();

        } catch (NamingException e) {
            e.printStackTrace();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
