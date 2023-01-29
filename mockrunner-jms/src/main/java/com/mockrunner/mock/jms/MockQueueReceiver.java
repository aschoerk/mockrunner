package com.mockrunner.mock.jms;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Queue;
import jakarta.jms.QueueReceiver;

/**
 * Mock implementation of JMS <code>QueueReceiver</code>.
 */
public class MockQueueReceiver extends MockMessageConsumer implements QueueReceiver
{
    private MockSession session;
    private MockQueue queue;
    
    public MockQueueReceiver(MockConnection connection, MockQueueSession session, MockQueue queue)
    {
        this(connection, session, queue, null); 
    }

    public MockQueueReceiver(MockConnection connection, MockSession session, MockQueue queue, String messageSelector)
    {
        super(connection, messageSelector);
        this.session = session;
        this.queue = queue;
    }

    public Queue getQueue() throws JMSException
    {
        getConnection().throwJMSException();
        return queue;
    }
 
    public Message receiveNoWait() throws JMSException
    {
        getConnection().throwJMSException();
        if(isClosed())
        {
            throw new JMSException("Receiver is closed");
        }
        if(queue.isEmpty()) return null;
        Message message;
        if((!getConnection().getConfigurationManager().getUseMessageSelectors()) || (null == getMessageFilter()))
        {
            message = queue.getMessage();
        }
        else
        {
            message = queue.getMatchingMessage(getMessageFilter());
        }
        if(null == message) return null;
        if(session.isAutoAcknowledge()) message.acknowledge();
        return message;
    }
    
    protected void waitOnMessage(long timeout)
    {
    	try {
    		//todo, notify on topic. 
    		synchronized(queue) {
    			queue.wait(timeout);
    		}
		} catch (InterruptedException ignored) {
		}
    }
}
