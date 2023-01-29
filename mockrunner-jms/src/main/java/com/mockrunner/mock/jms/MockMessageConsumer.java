package com.mockrunner.mock.jms;

import java.io.Serializable;
import java.util.Date;

import jakarta.jms.InvalidSelectorException;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageListener;

import org.activemq.filter.mockrunner.Filter;
import org.activemq.selector.mockrunner.SelectorParser;

/**
 * Mock implementation of JMS <code>MessageConsumer</code>.
 */
public abstract class MockMessageConsumer implements MessageConsumer, Serializable
{
    private MockConnection connection;
    private String messageSelector;
    private Filter messageSelectorFilter;
    private boolean closed;
    private MessageListener messageListener;
        
    public MockMessageConsumer(MockConnection connection, String messageSelector)
    {
        this.connection = connection;
        this.messageSelector = messageSelector;
        parseMessageSelector();
        closed = false;
        messageListener = null;
    }

    private void parseMessageSelector()
    {
        if(null == messageSelector || messageSelector.length() == 0)
        {
            this.messageSelectorFilter = null;
        }
        else
        {
            try
            {
                this.messageSelectorFilter = new SelectorParser().parse(messageSelector);
            }
            catch(InvalidSelectorException exc)
            {
                throw new RuntimeException("Error parsing message selector: " + exc.getMessage());
            }
        }
    }

    /**
     * Returns if this consumer was closed.
     * @return <code>true</code> if this consumer is closed
     */
    public boolean isClosed()
    {
        return closed;
    }
    
    /**
     * Returns if this consumer can consume an incoming message,
     * i.e. if a <code>MessageListener</code> is registered,
     * the receiver isn't closed and has an appropriate selector.
     * @param message the message to consume
     * @return <code>true</code> if this receiver can consume the message
     */
    public boolean canConsume(Message message)
    {
        if(messageListener == null) return false;
        if(isClosed()) return false;
        return matchesMessageSelector(message);
    }
    
    /**
     * Adds a message that is immediately propagated to the
     * message listener. If there's no message listener,
     * nothing happens.
     * @param message the message
     */
    public void receiveMessage(Message message)
    {
        if(null == messageListener) return;
        messageListener.onMessage(message);
    }

    public String getMessageSelector() throws JMSException
    {
        connection.throwJMSException();
        return messageSelector;
    }

    public MessageListener getMessageListener() throws JMSException
    {
        connection.throwJMSException();
        return messageListener;
    }

    public void setMessageListener(MessageListener messageListener) throws JMSException
    {
        connection.throwJMSException();
        this.messageListener = messageListener;
    }

    /** 
     * wait until: 
     * 
     * * a message is ready
     * * the timeout elapses
     * * some other event happens (@see Object.wait)
     * 
     * if the timeout is 0, the function will not timeout.
     * 
     * @param timeout - max milliseconds to wait. 
     */
    protected abstract void waitOnMessage(long timeout);

    
    public Message receive(long timeout) throws JMSException
    {
    	if(timeout == 0) return receive();
    	getConnection().throwJMSException();
    	Message message=null; 
    
    	Date waitTill = new Date((new Date()).getTime()+timeout);
    	
   		for(;;){
    		message = receiveNoWait();
   			if(message!=null) return message;
   			timeout= waitTill.getTime()-(new Date()).getTime();
   			if(timeout>0)
   			{
				waitOnMessage(timeout);
   			}
   			else
   			{
   				return null;
   			}
    	}	
	}

    public Message receive() throws JMSException
    {
    	getConnection().throwJMSException();
    	
    	Message message = null;
   		for(;;){
    		message = receiveNoWait();
   			if(message!=null) return message;   
   			waitOnMessage(0);
    	}	
    }
    public void close() throws JMSException
    {
        connection.throwJMSException();
        closed = true;
    }
    
    private boolean matchesMessageSelector(Message message)
    {
        if(!connection.getConfigurationManager().getUseMessageSelectors()) return true;
        if(messageSelectorFilter == null) return true;
        try
        {
            return messageSelectorFilter.matches(message);
        }
        catch(JMSException exc)
        {
            throw new RuntimeException(exc.getMessage());
        }
    }
    
    protected Filter getMessageFilter()
    {
        return messageSelectorFilter;
    }
    
    protected MockConnection getConnection()
    {
        return connection;
    }
}
