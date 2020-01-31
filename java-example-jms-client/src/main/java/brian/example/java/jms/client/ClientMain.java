package brian.example.java.jms.client;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ClientMain {
    public static void main(String[] args){

        Connection connection = null;
        try {
            InitialContext ctx = new InitialContext();
            ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://192.168.99.100:61616");
            connection = cf.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("inbound.queue");
            MessageProducer messageProducer = session.createProducer(destination);

            MapMessage message = session.createMapMessage();
            message.setString("Name","Tim");
            message.setString("Role", "Developer");

            messageProducer.send(message);

        } catch (NamingException e) {
            e.printStackTrace();
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            if ( connection != null ){
                try{connection.close();}catch(Exception e){}
            }
        }
    }
}