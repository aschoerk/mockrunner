package com.mockrunner.mock.jms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionConsumer;
import jakarta.jms.ConnectionMetaData;
import jakarta.jms.Destination;
import jakarta.jms.ExceptionListener;
import jakarta.jms.JMSException;
import jakarta.jms.ServerSessionPool;
import jakarta.jms.Session;
import jakarta.jms.Topic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mockrunner.jms.ConfigurationManager;
import com.mockrunner.jms.DestinationManager;

/**
 * Mock implementation of JMS <code>Connection</code>.
 * Please note: The interfaces <code>ConnectionConsumer</code>,
 * <code>ServerSessionPool</code> and <code>ServerSession</code>
 * are not meant for application use. Mockrunner provides very
 * simple mock implementations but usually you won't need them.
 */
public class MockConnection implements Connection, Serializable
{
	
	private static final Log logger = LogFactory.getLog(MockConnection.class);
	
    private ConnectionMetaData metaData;
    private List sessions;
    private String clientId;
    private boolean started;
    private boolean closed;
    private ExceptionListener listener;
    private JMSException exception;
    private DestinationManager destinationManager;
    private ConfigurationManager configurationManager;
    private String userName;
    private String password;
    
    public MockConnection(DestinationManager destinationManager, ConfigurationManager configurationManager)
    { 
        this(destinationManager, configurationManager, null, null);
    }
    
    public MockConnection(DestinationManager destinationManager, ConfigurationManager configurationManager, String userName, String password)
    { 
        metaData = new MockConnectionMetaData();
        started = false;
        closed = false;
        exception = null;
        this.destinationManager = destinationManager;
        this.configurationManager = configurationManager;
        sessions = new ArrayList();
        this.userName = userName;
        this.password = password;
        if(logger.isDebugEnabled())
        	logger.debug("Created new mock connection");
    }
    
    /**
     * Returns the user name.
     * @return the user name
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     * Returns the password.
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Returns the {@link com.mockrunner.jms.DestinationManager}.
     * @return the {@link com.mockrunner.jms.DestinationManager}
     */
    public DestinationManager getDestinationManager()
    {
        return destinationManager;
    }
    
    /**
     * Returns the {@link com.mockrunner.jms.ConfigurationManager}.
     * @return the {@link com.mockrunner.jms.ConfigurationManager}
     */
    public ConfigurationManager getConfigurationManager()
    {
        return configurationManager;
    }
    
    /**
     * Returns the list of {@link MockSession} objects.
     * @return the list
     */
    public List getSessionList()
    {
        return Collections.unmodifiableList(sessions);
    }

    /**
     * Returns a {@link MockSession}. If there's no such
     * {@link MockSession}, <code>null</code> is returned.
     * @param index the index of the session object
     * @return the session object
     */
    public MockSession getSession(int index)
    {
        if(sessions.size() <= index || index < 0) return null;
        return (MockSession)sessions.get(index);
    }
    
    /**
     * Set an exception that will be thrown when calling one
     * of the interface methods. Since the mock implementation
     * cannot fail like a full blown message server you can use
     * this method to simulate server errors. After the exception
     * was thrown it will be deleted.
     * @param exception the exception to throw
     */
    public void setJMSException(JMSException exception)
    {
        this.exception = exception;
    }

    /**
     * Throws a <code>JMSException</code> if one is set with
     * {@link #setJMSException}. Deletes the exception.
     * @throws JMSException thrown of JMSException has been set.
     */
    public void throwJMSException() throws JMSException
    {
        if(null == exception) return;
        JMSException tempException = exception;
        exception = null;
        throw tempException;
    }
    
    /**
     * Calls the <code>ExceptionListener</code>
     * if an exception is set {@link #setJMSException}.
     * Deletes the exception after calling the <code>ExceptionListener</code>.
     */
    public void callExceptionListener()
    {
        JMSException tempException = exception;
        exception = null;
        callExceptionListener(tempException);
    }
    
    /**
     * Calls the <code>ExceptionListener</code>
     * using the specified exception.
     * @param exception the exception
     */
    public void callExceptionListener(JMSException exception)
    {
        if(listener != null && exception != null)
        {
            listener.onException(exception);
        }
    }
    
    /**
     * You can use this to set the <code>ConnectionMetaData</code>.
     * Usually this should not be necessary. Per default an instance
     * of {@link MockConnectionMetaData} is returned when calling
     * {@link #getMetaData}.
     * @param metaData the meta data
     */
    public void setMetaData(ConnectionMetaData metaData)
    {
        this.metaData = metaData;
    }
    
    public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException
    {
        throwJMSException();
        MockSession session = new MockSession(this, transacted, acknowledgeMode);
        sessions().add(session);
        return session;
    }

    @Override
    public Session createSession(int sessionMode) throws JMSException {
        return createSession(sessionMode == Session.SESSION_TRANSACTED, sessionMode);
    }

    @Override
    public Session createSession() throws JMSException {
        return createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    public ConnectionConsumer createConnectionConsumer(Destination destination, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException
    {
        throwJMSException();
        return new MockConnectionConsumer(this, sessionPool);
    }

    @Override
    public ConnectionConsumer createSharedConnectionConsumer(Topic topic, String subscriptionName,
                                                             String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        return createDurableConnectionConsumer(topic, subscriptionName, messageSelector, sessionPool,maxMessages);
    }

    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException
    {
        return createConnectionConsumer(topic, messageSelector, sessionPool, maxMessages);
    }

    @Override
    public ConnectionConsumer createSharedDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector,
                                                                    ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        return createDurableConnectionConsumer(topic, subscriptionName, messageSelector, sessionPool, maxMessages);
    }

    public ConnectionMetaData getMetaData() throws JMSException
    {
        throwJMSException();
        return metaData;
    }
        
    public String getClientID() throws JMSException
    {
        throwJMSException();
        return clientId;
    }

    public void setClientID(String clientId) throws JMSException
    {
        throwJMSException();
        this.clientId = clientId;
    }  

    public ExceptionListener getExceptionListener() throws JMSException
    {
        throwJMSException();
        return listener;
    }

    public void setExceptionListener(ExceptionListener listener) throws JMSException
    {
        throwJMSException();
        this.listener = listener;
    }

    public void start() throws JMSException
    {
        throwJMSException();
        started = true;
    }

    public void stop() throws JMSException
    {
        throwJMSException();
        started = false;
    }

    public void close() throws JMSException
    {
        throwJMSException();
        for (Object session1 : sessions) {
            Session session = (Session) session1;
            session.close();
        }
        closed = true;
        if(logger.isDebugEnabled())
        	logger.debug("Closed mock connection");
    }
    
    public boolean isStarted()
    {
        return started;
    }
    
    public boolean isStopped()
    {
        return !isStarted();
    }
    
    public boolean isClosed()
    {
        return closed;
    }
    
    protected List sessions()
    {
        return sessions;
    }
}
