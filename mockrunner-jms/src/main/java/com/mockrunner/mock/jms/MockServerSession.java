package com.mockrunner.mock.jms;

import java.io.Serializable;

import jakarta.jms.JMSException;
import jakarta.jms.QueueSession;
import jakarta.jms.ServerSession;
import jakarta.jms.Session;

/**
 * Mock implementation of JMS <code>ServerSession</code>.
 * The <code>ServerSession</code> is not meant for application
 * use.
 */
public class MockServerSession implements ServerSession, Serializable
{
    private MockConnection connection;
    private Session session;
    private boolean started;
    
    public MockServerSession(MockConnection connection)
    {
        this.connection = connection;
        session = new MockSession(connection, false, QueueSession.AUTO_ACKNOWLEDGE);
        started = false;
    }
    
    /**
     * Returns if this server session was started.
     * @return <code>true</code> if this server session is started
     */
    public boolean isStarted()
    {
        return started;
    }
    
    public void setSession(Session session)
    {
        this.session = session;
    }
    
    public Session getSession() throws JMSException
    {
        connection.throwJMSException();
        return session;
    }

    public void start() throws JMSException
    {
        connection.throwJMSException();
        started = true;
    }
}
