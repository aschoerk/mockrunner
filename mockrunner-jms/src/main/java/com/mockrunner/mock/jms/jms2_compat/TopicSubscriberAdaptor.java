package com.mockrunner.mock.jms.jms2_compat;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageListener;
import jakarta.jms.Topic;
import jakarta.jms.TopicSubscriber;

/**
 * @author aschoerk
 */
public class TopicSubscriberAdaptor implements MessageConsumer, TopicSubscriber {

    private final TopicSubscriber topicSubscriber;

    public TopicSubscriberAdaptor(TopicSubscriber topicSubscriber) {
        this.topicSubscriber = java.util.Objects.requireNonNull(topicSubscriber);
    }

    @Override
    public String getMessageSelector() throws JMSException {
        return topicSubscriber.getMessageSelector();
    }

    @Override
    public MessageListener getMessageListener() throws JMSException {
        return topicSubscriber.getMessageListener();
    }

    @Override
    public void setMessageListener(MessageListener listener) throws JMSException {
        topicSubscriber.setMessageListener(listener);
    }

    @Override
    public Message receive() throws JMSException {
        return topicSubscriber.receive();
    }

    @Override
    public Message receive(long timeout) throws JMSException {
        return topicSubscriber.receive(timeout);
    }

    @Override
    public Message receiveNoWait() throws JMSException {
        return topicSubscriber.receiveNoWait();
    }

    @Override
    public void close() throws JMSException {
        topicSubscriber.close();
    }

    @Override
    public Topic getTopic() throws JMSException {
        return topicSubscriber.getTopic();
    }

    @Override
    public boolean getNoLocal() throws JMSException {
        return topicSubscriber.getNoLocal();
    }





}