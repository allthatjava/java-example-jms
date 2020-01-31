package brian.example.java.jms.client;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.UUID;

public class ClientMain implements MessageListener {

    boolean waitForReturn = true;

    public ClientMain(){
        Connection connection = null;
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://192.168.99.100:61616");

        try {
            InitialContext ctx = new InitialContext();
            connection = cf.createConnection();
            connection.start();    //---> To get the message back from the MQ Server

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("inbound.queue");

            MessageProducer messageProducer = session.createProducer(destination);
            messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT); //--->

            //--> To receive the returned message ---//
            Destination tempDest = session.createTemporaryQueue();
            MessageConsumer responseConsumer = session.createConsumer(tempDest);
            responseConsumer.setMessageListener(this);
            //<--------------------------------------//

            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setString("Name","Tim");
            mapMessage.setString("Role", "Developer");

            //--> Message will have indicator how to get the message back  ---//
            mapMessage.setJMSReplyTo(tempDest); //---> Indicate to where to send the message back
            mapMessage.setJMSCorrelationID( UUID.randomUUID().toString() ); //---> Indicate to sender
            //<---------------------------------------------------------------//

            messageProducer.send(mapMessage);

            // Waiting for the message delay...
            while( waitForReturn ){
                Thread.yield();
            }

        } catch (JMSException e) {
            e.printStackTrace();
        } catch (NamingException e) {
            e.printStackTrace();
        } finally {
            if ( connection != null ){
                try{connection.close();}catch(Exception e){}
            }
        }
    }

    // Added to get the message back from MQ Server
    public void onMessage(Message message) {
        String messageText = null;
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                messageText = textMessage.getText();
                System.out.println("Returned message = " + messageText);

                waitForReturn = false;
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        new ClientMain();
    }
}