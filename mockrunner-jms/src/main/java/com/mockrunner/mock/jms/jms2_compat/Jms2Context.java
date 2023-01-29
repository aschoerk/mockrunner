package com.mockrunner.mock.jms.jms2_compat;

import static com.mockrunner.mock.jms.jms2_compat.Jms2Util.execute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.jms.BytesMessage;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionMetaData;
import jakarta.jms.Destination;
import jakarta.jms.ExceptionListener;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.JMSProducer;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Queue;
import jakarta.jms.QueueBrowser;
import jakarta.jms.Session;
import jakarta.jms.StreamMessage;
import jakarta.jms.TemporaryQueue;
import jakarta.jms.TemporaryTopic;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;

public final class Jms2Context implements JMSContext {

    private boolean autoStart;

    private boolean started;
    
    private final Session session;

    private final Connection connection;

    private final List<Jms2Consumer> consumers = new ArrayList<>();
    
    public Jms2Context(Connection connection, Session session) {
        this.connection = Objects.requireNonNull(connection);
        this.session = Objects.requireNonNull(session);
    }

    @Override
    public JMSContext createContext(final int sessionMode) {
        return execute(new Callback<JMSContext>() {

            @Override
            public JMSContext execute() throws JMSException {
                Session newSession = connection.createSession(sessionMode == Session.SESSION_TRANSACTED, sessionMode);
                return new Jms2Context(connection, newSession);
            }
        });

    }

    @Override
    public JMSProducer createProducer() {
        return new Jms2Producer(session);
    }

    @Override
    public String getClientID() {
        return Jms2Util.getClientID(connection);
    }

    @Override
    public void setClientID(final String clientID) {
        Jms2Util.setClientID(connection, clientID);
    }

    @Override
    public ConnectionMetaData getMetaData() {
        return Jms2Util.getMetaData(connection);
    }

    @Override
    public ExceptionListener getExceptionListener() {
        return Jms2Util.getExceptionListener(connection);
    }

    @Override
    public void setExceptionListener(ExceptionListener listener) {
        Jms2Util.setExceptionListener(connection, listener);
    }

    @Override
    public void start() {
        if(started) {
            this.stop();
        }
        Jms2Util.start(connection);
        this.started = true;
        
    }

    @Override
    public void stop() {
        Jms2Util.stop(connection);
        this.started = false;
    }

    @Override
    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    @Override
    public boolean getAutoStart() {
        return autoStart;
    }

    @Override
    public void close() {
        Jms2Util.close(connection, session);
    }


    
    @Override
    public BytesMessage createBytesMessage() {
        return Jms2MessageFactory.createBytesMessage(session);
    }

    @Override
    public MapMessage createMapMessage() {
        return Jms2MessageFactory.createMapMessage(session);
    }

    @Override
    public Message createMessage() {
        return Jms2MessageFactory.createMessage(session);
    }

    @Override
    public ObjectMessage createObjectMessage() {
        return Jms2MessageFactory.createObjectMessage(session);
    }

    @Override
    public ObjectMessage createObjectMessage(Serializable object) {
        return Jms2MessageFactory.createObjectMessage(session, object);
    }

    @Override
    public StreamMessage createStreamMessage() {
        return Jms2MessageFactory.createStreamMessage(session);
    }

    @Override
    public TextMessage createTextMessage() {

        return Jms2MessageFactory.createTextMessage(session);
    }

    @Override
    public TextMessage createTextMessage(String text) {
        return Jms2MessageFactory.createTextMessage(session, text);
    }

    @Override
    public boolean getTransacted() {
        return Jms2Util.getTransacted(session);
    }

    @Override
    public int getSessionMode() {
        return Jms2Util.getSessionMode(session);
    }

    @Override
    public void commit() {
        Jms2Util.commit(session);
    }

    @Override
    public void rollback() {
        Jms2Util.rollback(session);
    }

    @Override
    public void recover() {
        Jms2Util.recover(session);
    }

    @Override
    public JMSConsumer createConsumer(Destination destination) {
        return Jms2Util.createConsumer(this, destination);
    }

    @Override
    public JMSConsumer createConsumer(Destination destination, String selector) {
        return Jms2Util.createConsumer(this, destination, selector);
    }

    @Override
    public JMSConsumer createConsumer(Destination destination, String selector, boolean arg2) {

        return Jms2Util.createConsumer(this, destination, selector, arg2);
    }

    @Override
    public Queue createQueue(String queueName) {
        return Jms2Util.createQueue(session, queueName);
    }

    @Override
    public Topic createTopic(String topicName) {
        return Jms2Util.createTopic(session, topicName);
    }

    @Override
    public JMSConsumer createDurableConsumer(Topic topic, String name) {
        return Jms2Util.createDurableConsumer(this, topic, name);
    }

    @Override
    public JMSConsumer createDurableConsumer(Topic topic, String name, String selector, boolean noLocal) {
        return Jms2Util.createDurableConsumer(this, topic, name, selector, noLocal);
    }

    @Override
    public JMSConsumer createSharedDurableConsumer(Topic topic, String name) {
        return Jms2Util.createDurableConsumer(this, topic, name);
    }

    @Override
    public JMSConsumer createSharedDurableConsumer(Topic topic, String name, String selector) {
        return Jms2Util.createSharedDurableConsumer(this, topic, name, selector);
    }

    @Override
    public JMSConsumer createSharedConsumer(Topic topic, String name) {
        return Jms2Util.createSharedConsumer(this, topic, name);
    }

    @Override
    public JMSConsumer createSharedConsumer(Topic topic, String name, String selector) {
        return Jms2Util.createSharedConsumer(this, topic, name, selector);
    }

    @Override
    public QueueBrowser createBrowser(Queue queue) {
        return Jms2Util.createBrowser(session, queue);
    }

    @Override
    public QueueBrowser createBrowser(Queue queue, String selector) {
        return Jms2Util.createBrowser(session, queue, selector);
    }

    @Override
    public TemporaryQueue createTemporaryQueue() {
        return Jms2Util.createTemporaryQueue(session);
    }

    @Override
    public TemporaryTopic createTemporaryTopic() {
        return Jms2Util.createTemporaryTopic(session);
    }

    @Override
    public void unsubscribe(String name) {
        Jms2Util.unsubscribe(session, name);
    }

    @Override
    public void acknowledge() {
        for(Jms2Consumer consumer : consumers) {
            consumer.acknowledge();
        }
    }

    public Session getSession() {
        return session;
    }

    Jms2Consumer addConsumer(Jms2Consumer consumer) {
        this.consumers.add(consumer);
        return consumer;
    }
    
    

}
