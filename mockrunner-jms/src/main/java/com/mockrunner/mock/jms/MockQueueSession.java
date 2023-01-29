package com.mockrunner.mock.jms;

import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.QueueReceiver;
import jakarta.jms.QueueSender;
import jakarta.jms.QueueSession;
import jakarta.jms.Session;

/**
 * Mock implementation of JMS <code>QueueSession</code>.
 */
public class MockQueueSession extends MockSession implements QueueSession
{
    public MockQueueSession(MockQueueConnection connection)
    {
        this(connection, false, Session.AUTO_ACKNOWLEDGE);
    }
    
    public MockQueueSession(MockQueueConnection connection, boolean transacted, int acknowledgeMode)
    {
        super(connection, transacted, acknowledgeMode);
    }

    public QueueReceiver createReceiver(Queue queue) throws JMSException
    {
        return (QueueReceiver)createConsumer(queue);
    }

    public QueueReceiver createReceiver(Queue queue, String messageSelector) throws JMSException
    { 
        return (QueueReceiver)createConsumer(queue, messageSelector);
    }

    public QueueSender createSender(Queue queue) throws JMSException
    {
        return (QueueSender)createProducer(queue);
    }
    
    protected MessageProducer createProducerForNullDestination()
    {
        return getGenericTransmissionManager().createQueueSender();
    }
}
