package com.mockrunner.mock.jms;

import com.mockrunner.base.NestedApplicationException;
import org.activemq.filter.mockrunner.Filter;

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Mock implementation of JMS <code>Destination</code>.
 */
public abstract class MockDestination implements Destination, Serializable
{
    private Set sessions;
    private List currentMessages;
    private List receivedMessages;

    public MockDestination()
    {
        sessions = new CopyOnWriteArraySet();
        currentMessages = new CopyOnWriteArrayList();
        receivedMessages = new CopyOnWriteArrayList();
    }
    
    /**
     * Adds a message and delivers it to the corresponding consumers. 
     * Implemented by {@link MockQueue} and {@link MockTopic}.
     * @param message the message
     * @throws JMSException os case the message can not be added
     */
    public abstract void addMessage(Message message) throws JMSException;
 
    /**
     * Adds a message to the list of current messages in this
     * destination. The message is not delivered to registered
     * consumers. Can be used to preload destinations with
     * test messages.
     * @param message the message
     */
    public void loadMessage(Message message)
    {
        addCurrentMessage(message);
    }
    
    /**
     * Returns if this destination contains messages.
     * @return <code>false</code> if there's at least one message,
     *         <code>true</code> otherwise
     */
    public boolean isEmpty()
    {
        return currentMessages.size() <= 0;
    }

    /**
     * Clears all current messages.
     */
    public void clear()
    {
        currentMessages.clear();
    }

    /**
     * Clears all current messages and resets the list of received messages.
     */
    public void reset()
    {
        currentMessages.clear();
        receivedMessages.clear();
    }

    /**
     * Returns the next message. The message will be removed from the list
     * of current messages. 
     * If there's no message, <code>null</code> will be returned.
     * @return the <code>Message</code>
     */
    public Message getMessage()
    {
        if(currentMessages.size() <= 0) return null;
        return (Message)currentMessages.remove(0);
    }
    
    /**
     * Returns the next message that matches the filter. 
     * The message will be removed from the list of current messages. 
     * If there's no matching message, <code>null</code> will be returned.
     * @param filter the message filter
     * @return the <code>Message</code>
     */
    public Message getMatchingMessage(Filter filter)
    {
        for(int ii = 0; ii < currentMessages.size(); ii++)
        {
            Message currentMessage = (Message)currentMessages.get(ii);
            try
            {
                if(filter.matches(currentMessage))
                {
                    currentMessages.remove(ii);
                    return currentMessage;
                }
            }
            catch(JMSException exc)
            {
                throw new NestedApplicationException(exc);
            }
        }
        return null;
    }

    /**
     * Returns a <code>List</code> of all current messages.
     * @return the <code>List</code> of messages
     */
    public List getCurrentMessageList()
    {
        return Collections.unmodifiableList(currentMessages);
    }

    /**
     * Returns a <code>List</code> of all received messages.
     * @return the <code>List</code> of messages
     */
    public List getReceivedMessageList()
    {
        return Collections.unmodifiableList(receivedMessages);
    }

    /**
     * Adds a <code>Session</code>.
     * @param session the session
     */
    public void addSession(Session session)
    {
        sessions.add(session);
    }
    
    /**
     * Removes a <code>Session</code>.
     * @param session the session
     */
    public void removeSession(Session session)
    {
        sessions.remove(session);
    }
    
    /**
     * Return a <code>Set</code> of all sessions.
     * @return a <code>Set</code> of all sessions
     */
    public Set sessionSet()
    {
        return Collections.unmodifiableSet(sessions);
    }
    
    protected void addReceivedMessage(Message message)
    {
        receivedMessages.add(message);
    }
    
    protected void addCurrentMessage(Message message)
    {
        currentMessages.add(message);
    }
    
    protected void acknowledgeMessage(Message message, MockSession session) throws JMSException
    {
        if(session.isAutoAcknowledge())
        {
            message.acknowledge();
        }
    }
}
