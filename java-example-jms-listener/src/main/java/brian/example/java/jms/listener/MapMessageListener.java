package brian.example.java.jms.listener;

import javax.jms.*;

public class MapMessageListener implements MessageListener {

    private Session session;
    private MessageProducer replyProducer;

    public MapMessageListener(Session session, MessageProducer replyProducer){
        this.session = session;
        this.replyProducer = replyProducer;
    }

    public void onMessage(Message message) {

        try {
            TextMessage response = this.session.createTextMessage();

            if( message instanceof MapMessage){
                MapMessage mapMessage = (MapMessage) message;

                try{
                    String name = mapMessage.getString("Name");
                    System.out.println("Name:"+name);

                    // To send the message back to sender
                    response.setText("The name "+name+" has been received");
                    response.setJMSCorrelationID(message.getJMSCorrelationID());
                    replyProducer.send( message.getJMSReplyTo(), response);

                    System.out.println("MESSAGE SENT BACK : "+response.getText());

                }catch(JMSException e){
                    throw new RuntimeException(e);
                }
            } else{
                System.out.println("Invalid message received.");
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
