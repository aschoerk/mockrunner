package com.mockrunner.mock.jms;

import jakarta.jms.JMSException;
import jakarta.jms.ServerSession;
import jakarta.jms.ServerSessionPool;

/**
 * Mock implementation of JMS <code>ServerSessionPool</code>.
 */
public class MockServerSessionPool implements ServerSessionPool
{
    private MockConnection connection;
    private ServerSession session;
    
    public MockServerSessionPool(MockConnection connection)
    {
        this.connection = connection;
        session = new MockServerSession(connection);
    }
    
    public void setServerSession(ServerSession session)
    {
        this.session = session;
    }
    
    public ServerSession getServerSession() throws JMSException
    {
        connection.throwJMSException();
        return session;
    }
}
