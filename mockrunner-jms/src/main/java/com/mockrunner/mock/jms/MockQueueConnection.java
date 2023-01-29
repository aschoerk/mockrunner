package com.mockrunner.mock.jms;

import java.util.List;

import jakarta.jms.ConnectionConsumer;
import jakarta.jms.JMSException;
import jakarta.jms.Queue;
import jakarta.jms.QueueConnection;
import jakarta.jms.QueueSession;
import jakarta.jms.ServerSessionPool;
import jakarta.jms.Session;

import com.mockrunner.jms.ConfigurationManager;
import com.mockrunner.jms.DestinationManager;

/**
 * Mock implementation of JMS <code>QueueConnection</code>.
 * Please note: The interfaces <code>ConnectionConsumer</code>,
 * <code>ServerSessionPool</code> and <code>ServerSession</code>
 * are not meant for application use. Mockrunner provides very
 * simple mock implementations but usually you won't need them.
 */
public class MockQueueConnection extends MockConnection implements QueueConnection
{
    public MockQueueConnection(DestinationManager destinationManager, ConfigurationManager configurationManager)
    {
        super(destinationManager, configurationManager);
    }
    
    public MockQueueConnection(DestinationManager destinationManager, ConfigurationManager configurationManager, String userName, String password)
    {
        super(destinationManager, configurationManager, userName, password);
    }

    /**
     * Returns the list of {@link MockQueueSession} objects that were created 
     * with {@link #createQueueSession}.
     * @return the list
     */
    public List getQueueSessionList()
    {
        return super.getSessionList();
    }

    /**
     * Returns a {@link MockQueueSession} that was created with
     * {@link #createQueueSession}. If there's no such
     * {@link MockQueueSession}, <code>null</code> is returned.
     * @param index the index of the session object
     * @return the session object
     */
    public MockQueueSession getQueueSession(int index)
    {
        return (MockQueueSession)super.getSession(index);
    }
    
    public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException
    {
        return createQueueSession(transacted, acknowledgeMode);
    }
    
    public QueueSession createQueueSession(boolean transacted, int acknowledgeMode) throws JMSException
    {
        throwJMSException();
        MockQueueSession session = new MockQueueSession(this, transacted, acknowledgeMode);
        sessions().add(session);
        return session;
    }

    public ConnectionConsumer createConnectionConsumer(Queue queue, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException
    {
        return super.createConnectionConsumer(queue, messageSelector, sessionPool, maxMessages);
    }
}
