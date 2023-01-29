package com.mockrunner.mock.jms;

import java.io.Serializable;

import jakarta.jms.BytesMessage;
import jakarta.jms.CompletionListener;
import jakarta.jms.DeliveryMode;
import jakarta.jms.Destination;
import jakarta.jms.InvalidDestinationException;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageProducer;
import jakarta.jms.StreamMessage;

/**
 * Mock implementation of JMS <code>MessageProducer</code>.
 */
public class MockMessageProducer implements MessageProducer, Serializable
{
    private MockConnection connection;
    private MockDestination destination;
    private MockSession session;
    private boolean closed;
    private boolean disableMessageId;
    private boolean disableTimestamp;
    private int deliveryMode;
    private int priority;
    private long timeToLive;
    
    public MockMessageProducer(MockConnection connection, MockSession session, MockDestination destination)
    {
        this.connection = connection;
        this.destination = destination;
        this.session = session;
        closed = false;
        disableMessageId = false;
        disableTimestamp = false;
        deliveryMode = DeliveryMode.PERSISTENT;
        priority = 4;
        timeToLive = 0;
    }
    
    /**
     * Returns if this producer was closed.
     * @return <code>true</code> if this sender is closed
     */
    public boolean isClosed()
    {
        return closed;
    }
    
    public void send(Message message) throws JMSException
    {
        send(destination, message, deliveryMode, priority, timeToLive);
    }
    
    public void send(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException
    {
        send(destination, message, deliveryMode, priority, timeToLive);
    }
    
    public void send(Destination destination, Message message) throws JMSException
    {
        send(destination, message, deliveryMode, priority, timeToLive);
    }
    
    public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException
    {
        connection.throwJMSException();
        if(isClosed())
        {
            throw new JMSException("Producer is closed");
        }
        if(null == destination)
        {
            throw new InvalidDestinationException("destination must not be null");
        }
        if((message instanceof MockMessage) && connection.getConfigurationManager().getDoCloneOnSend())
        {
            message = (MockMessage)((MockMessage)message).clone();
        }
        if(destination instanceof MockQueue)
        {
            setJMSMessageHeaders(message, destination, deliveryMode, priority, timeToLive);
            session.addSessionToQueue((MockQueue)destination);
            ((MockQueue)destination).addMessage(message);
        }
        else if(destination instanceof MockTopic)
        {
            setJMSMessageHeaders(message, destination, deliveryMode, priority, timeToLive);
            session.addSessionToTopic((MockTopic)destination);
            ((MockTopic)destination).addMessage(message);
        }
        else
        {
            throw new InvalidDestinationException("destination must be an instance of MockQueue or MockTopic");
        }
    }

    @Override
    public void send(final Message message, final CompletionListener completionListener) throws JMSException {
        // TODO: jakarta

    }

    @Override
    public void send(final Message message, final int i, final int i1, final long l, final CompletionListener completionListener) throws JMSException {
        // TODO: jakarta

    }

    @Override
    public void send(final Destination destination, final Message message, final CompletionListener completionListener) throws JMSException {
        // TODO: jakarta

    }

    @Override
    public void send(final Destination destination, final Message message, final int i, final int i1, final long l, final CompletionListener completionListener) throws JMSException {
        // TODO: jakarta

    }

    public Destination getDestination() throws JMSException
    {
        connection.throwJMSException();
        return destination;
    }

    public void close() throws JMSException
    {
        connection.throwJMSException();
        closed = true;
    }
    
    public void setDisableMessageID(boolean disableMessageId) throws JMSException
    {
        connection.throwJMSException();
        this.disableMessageId = disableMessageId;
    }

    public boolean getDisableMessageID() throws JMSException
    {
        connection.throwJMSException();
        return disableMessageId;
    }

    public void setDisableMessageTimestamp(boolean disableTimestamp) throws JMSException
    {
        connection.throwJMSException();
        this.disableTimestamp = disableTimestamp;
    }

    public boolean getDisableMessageTimestamp() throws JMSException
    {
        connection.throwJMSException();
        return disableTimestamp;
    }

    public void setDeliveryMode(int deliveryMode) throws JMSException
    {
        connection.throwJMSException();
        this.deliveryMode = deliveryMode;
    }

    public int getDeliveryMode() throws JMSException
    {
        connection.throwJMSException();
        return deliveryMode;
    }

    public void setPriority(int priority) throws JMSException
    {
        connection.throwJMSException();
        this.priority = priority;
    }

    public int getPriority() throws JMSException
    {
        connection.throwJMSException();
        return priority;
    }

    public void setTimeToLive(long timeToLive) throws JMSException
    {
        connection.throwJMSException();
        this.timeToLive = timeToLive;
    }

    public long getTimeToLive() throws JMSException
    {
        connection.throwJMSException();
        return timeToLive;
    }

    @Override
    public void setDeliveryDelay(final long l) throws JMSException {
        // TODO: jakarta

    }

    @Override
    public long getDeliveryDelay() throws JMSException {
        // TODO: jakarta
        return 0;
    }

    private void setJMSMessageHeaders(Message message, Destination destination, int deliveryMode, int priority, long timeToLive) throws JMSException
    {
        message.setJMSDeliveryMode(deliveryMode);
        message.setJMSPriority(priority);
        message.setJMSDestination(destination);
        long currentTime = System.currentTimeMillis();
        if(!disableTimestamp)
        {
            message.setJMSTimestamp(currentTime);
        }
        if(0 == timeToLive)
        {
            message.setJMSExpiration(0);
        }
        else
        {
            message.setJMSExpiration(currentTime + timeToLive);
        }
        if(!disableMessageId)
        {
            message.setJMSMessageID("ID:" + String.valueOf(Math.random()));
        }
        if(message instanceof MockMessage)
        {
            ((MockMessage)message).setReadOnly(true);
            ((MockMessage)message).setReadOnlyProperties(true);
        }
        if(message instanceof BytesMessage)
        {
            ((BytesMessage)message).reset();
        }
        if(message instanceof StreamMessage)
        {
            ((StreamMessage)message).reset();
        }
    }
}
