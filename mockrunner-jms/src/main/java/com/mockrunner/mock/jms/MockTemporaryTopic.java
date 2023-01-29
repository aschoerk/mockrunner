package com.mockrunner.mock.jms;

import jakarta.jms.JMSException;
import jakarta.jms.TemporaryTopic;

/**
 * Mock implementation of JMS <code>TemporaryTopic</code>.
 */
public class MockTemporaryTopic extends MockTopic implements TemporaryTopic
{
    private boolean deleted;
    
    public MockTemporaryTopic()
    {
        super("TemporaryTopic");
        deleted = false;
    }
    
    /**
     * Returns if this temporary topic is deleted.
     * @return <code>true</code> if this topic is deleted 
     */
    public boolean isDeleted()
    {
        return deleted;
    }

    public void delete() throws JMSException
    {
        deleted = true;
    }  
}
