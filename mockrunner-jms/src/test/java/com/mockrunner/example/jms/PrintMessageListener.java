package com.mockrunner.example.jms;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

/**
 * Listener for {@link com.mockrunner.example.jms.PrintMessageServlet}.
 * If the message is a text message, the customer data will be printed
 * and the message will be acknowledged. The print code is omitted for 
 * simplicity.
 */
public class PrintMessageListener implements MessageListener
{
    public void onMessage(Message message)
    {
        if(message instanceof TextMessage)
        {
            //do print
        }
        try
        {
            message.acknowledge();
        }
        catch(JMSException exc)
        {
            exc.printStackTrace();
        }
    }
}
