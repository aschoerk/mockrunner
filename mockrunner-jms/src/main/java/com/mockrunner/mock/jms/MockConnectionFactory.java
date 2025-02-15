package com.mockrunner.mock.jms;

import com.mockrunner.jms.ConfigurationManager;
import com.mockrunner.jms.DestinationManager;
import com.mockrunner.mock.jms.jms2_compat.Callback;
import com.mockrunner.mock.jms.jms2_compat.Jms2Context;
import com.mockrunner.mock.jms.jms2_compat.Jms2Util;

import jakarta.jms.Connection;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.QueueConnection;
import jakarta.jms.QueueConnectionFactory;
import jakarta.jms.Session;
import jakarta.jms.TopicConnection;
import jakarta.jms.TopicConnectionFactory;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Mock implementation of JMS <code>ConnectionFactory</code>.
 * Can be used as generic factory for JMS 1.1.
 * Also implements <code>QueueConnectionFactory</code> and
 * <code>TopicConnectionFactory</code> and can be used to 
 * create queue and topic connections as well as generic 
 * JMS 1.1 connections. It is recommended to use
 * {@link com.mockrunner.mock.jms.MockQueueConnectionFactory}
 * if you only use queues and 
 * {@link com.mockrunner.mock.jms.MockTopicConnectionFactory}
 * if you only use topics.
 * This implementation is primary for generic JMS 1.1 connections
 * but can also be used, if a server provides one implementation
 * for both domains (which is not portable).
 */
public class MockConnectionFactory implements QueueConnectionFactory, TopicConnectionFactory, Serializable
{
    private DestinationManager destinationManager;
    private ConfigurationManager configurationManager;
    private CopyOnWriteArrayList connections;
    private JMSException exception;

    public MockConnectionFactory(DestinationManager destinationManager, ConfigurationManager configurationManager)
    {
        connections = new CopyOnWriteArrayList();
        this.destinationManager = destinationManager;
        this.configurationManager = configurationManager;
        exception = null;
    }
    
    public Connection createConnection() throws JMSException
    {
        return createConnection(null, null);
    }

    public Connection createConnection(String name, String password) throws JMSException
    {
        MockConnection connection = new MockConnection(destinationManager, configurationManager, name, password);
        connection.setJMSException(exception);
        connections.add(connection);
        return connection;
    }

    @Override
    public JMSContext createContext() {
        return Jms2Util.execute(new Callback<JMSContext>() {

            @Override
            public JMSContext execute() throws JMSException {
                Connection connection = createConnection();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                return new Jms2Context(connection, session);
            }
        });
    }

    @Override
    public JMSContext createContext(final String arg0, final String arg1) {
        return Jms2Util.execute(new Callback<Jms2Context>() {

            @Override
            public Jms2Context execute() throws JMSException {
                Connection connection = createConnection(arg0, arg1);
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                return new Jms2Context(connection, session);
            }
        });
    }

    @Override
    public JMSContext createContext(final String arg0, final String arg1, final int arg2) {
        return Jms2Util.execute(new Callback<Jms2Context>() {

            @Override
            public Jms2Context execute() throws JMSException {
                Connection connection = createConnection(arg0, arg1);
                Session session = connection.createSession(arg2 == Session.SESSION_TRANSACTED, arg2);
                return new Jms2Context(connection, session);
            }
        });

    }

    @Override
    public JMSContext createContext(final int sessionMode) {

        return Jms2Util.execute(new Callback<Jms2Context>() {

            @Override
            public Jms2Context execute() throws JMSException {
                Connection connection = createConnection();
                Session session = connection.createSession(Session.SESSION_TRANSACTED == sessionMode, sessionMode);
                return new Jms2Context(connection, session);
            }
        });

    }

    public QueueConnection createQueueConnection() throws JMSException
    {
        return createQueueConnection(null, null);
    }

    public QueueConnection createQueueConnection(String name, String password) throws JMSException
    {
        MockQueueConnection connection = new MockQueueConnection(destinationManager(), configurationManager(), name, password);
        connection.setJMSException(exception());
        connections().add(connection);
        return connection;
    }
    
    public TopicConnection createTopicConnection() throws JMSException
    {
        return createTopicConnection(null, null);
    }

    public TopicConnection createTopicConnection(String name, String password) throws JMSException
    {
        MockTopicConnection connection = new MockTopicConnection(destinationManager(), configurationManager(), name, password);
        connection.setJMSException(exception());
        connections().add(connection);
        return connection;
    }
    
    /**
     * Set an exception that will be passed to all
     * created connections. This can be used to
     * simulate server errors. Check out
     * {@link MockConnection#setJMSException}
     * for details.
     * @param exception the exception
     */
    public void setJMSException(JMSException exception)
    {
        this.exception = exception;
    }

    /**
     * Clears the list of connections
     */
    public void clearConnections()
    {
        connections.clear();
    }

    /**
     * Returns the connection with the specified index
     * or <code>null</code> if no such connection
     * exists.
     * @param index the index
     * @return the connection
     */
    public MockConnection getConnection(int index)
    {
        if(connections.size() <= index) return null;
        return (MockConnection)connections.get(index);
    }

    /**
     * Returns the latest created connection
     * or <code>null</code> if no such connection
     * exists.
     * @return the connection
     */
    public MockConnection getLatestConnection()
    {
        if(connections.size() == 0) return null;
        return (MockConnection)connections.get(connections.size() - 1);
    }
    
    protected DestinationManager destinationManager()
    {
        return destinationManager;
    }
    
    protected ConfigurationManager configurationManager()
    {
        return configurationManager;
    }
    
    protected List connections()
    {
        return connections;
    }
    
    protected JMSException exception()
    {
        return exception;
    }
}
