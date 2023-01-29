package com.mockrunner.mock.jms;

import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import jakarta.jms.Topic;
import jakarta.jms.TopicPublisher;
import jakarta.jms.TopicSession;
import jakarta.jms.TopicSubscriber;

/**
 * Mock implementation of JMS <code>TopicSession</code>.
 */
public class MockTopicSession extends MockSession implements TopicSession
{
    public MockTopicSession(MockTopicConnection connection)
    {
        this(connection, false, Session.AUTO_ACKNOWLEDGE);
    }
    
    public MockTopicSession(MockTopicConnection connection, boolean transacted, int acknowledgeMode)
    {
        super(connection, transacted, acknowledgeMode);
    }

    public TopicPublisher createPublisher(Topic topic) throws JMSException
    {
        return (TopicPublisher)createProducer(topic);
    }

    public TopicSubscriber createSubscriber(Topic topic) throws JMSException
    {
        return (TopicSubscriber)createConsumer(topic);
    }

    public TopicSubscriber createSubscriber(Topic topic, String messageSelector, boolean noLocal) throws JMSException
    {
        return (TopicSubscriber)createConsumer(topic, messageSelector, noLocal);
    }
    
    protected MessageProducer createProducerForNullDestination()
    {
        return getGenericTransmissionManager().createTopicPublisher();
    }
}
