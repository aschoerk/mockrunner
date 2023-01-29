package com.mockrunner.mock.jms.jms2_compat;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import jakarta.jms.BytesMessage;
import jakarta.jms.CompletionListener;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.JMSProducer;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.MessageProducer;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

public final class Jms2Producer implements JMSProducer {

    private final Session session;

    private boolean disableMessageID;

    private boolean disableMessageTimestamp;

    private Destination replyTo;

    private String jmsType;

    private Integer deliveryMode;

    private Long timeToLive;

    private Integer priourity;

    private String jmsCorrelationID;

    private Long deliveryDelay;

    private final Jms2Properties properties = new Jms2Properties();

    private CompletionListener completionListener;

    Jms2Producer(Session session) {
        this.session = java.util.Objects.requireNonNull(session);
    }

    @Override
    public JMSProducer send(final Destination destination, final Message message) {

        Jms2Util.execute(new Callback<Void>() {

            @Override
            public Void execute() throws JMSException {
                MessageProducer messageProducer = session.createProducer(destination);

                messageProducer.setDisableMessageID(disableMessageID);
                messageProducer.setDisableMessageTimestamp(disableMessageTimestamp);

                if (ObjectsUtil.nonNull(deliveryMode)) {
                    messageProducer.setDeliveryMode(deliveryMode);
                }
                if (ObjectsUtil.nonNull(timeToLive)) {
                    messageProducer.setTimeToLive(timeToLive);
                }

                if (ObjectsUtil.nonNull(priourity)) {
                    messageProducer.setPriority(priourity);
                }

                if (ObjectsUtil.nonNull(replyTo)) {
                    message.setJMSReplyTo(replyTo);
                }

                if(ObjectsUtil.nonNull(jmsCorrelationID)) {
                    message.setJMSCorrelationID(jmsCorrelationID);
                }
                
                if(ObjectsUtil.nonNull(jmsType)) {
                    message.setJMSType(jmsType);
                }
                
                properties.applyToMessage(message);

                
                Message messageToSend;
                if(Jms2Message.class.isInstance(message)) {
                    messageToSend = Jms2Message.class.cast(message).getDelegate();
                } else {
                    messageToSend = message;
                }
                
                messageProducer.send(messageToSend);

                return null;
            }
        });

        return this;
    }
    
    
    
    @Override
    public JMSProducer send(final Destination destination, final String body) {
        TextMessage message = Jms2MessageFactory.createTextMessage(session, body);

        return send(destination, message);
    }

    @Override
    public JMSProducer send(final Destination destination, final Map<String, Object> body) {
        MapMessage message = Jms2MessageFactory.createMapMessage(session, body);
        return send(destination, message);
    }

    @Override
    public JMSProducer send(Destination destination, final byte[] body) {
        BytesMessage message = Jms2MessageFactory.createBytesMessage(session, body);
        return send(destination, message);
    }

    @Override
    public JMSProducer send(Destination destination, final Serializable body) {
        ObjectMessage message = Jms2MessageFactory.createObjectMessage(session, body);
        return send(destination, message);
    }

    @Override
    public JMSProducer setDisableMessageID(boolean disableMessageID) {
        this.disableMessageID = disableMessageID;
        return this;
    }

    @Override
    public boolean getDisableMessageID() {
        return disableMessageID;
    }

    @Override
    public JMSProducer setDisableMessageTimestamp(boolean disableMessageTimestamp) {
        this.disableMessageTimestamp = disableMessageTimestamp;
        return this;
    }

    @Override
    public boolean getDisableMessageTimestamp() {
        return disableMessageTimestamp;
    }

    @Override
    public JMSProducer setDeliveryMode(int deliveryMode) {
        this.deliveryMode = deliveryMode;
        return this;
    }

    @Override
    public int getDeliveryMode() {
        return deliveryMode != null ? deliveryMode : 0;
    }

    @Override
    public JMSProducer setPriority(int priority) {
        this.priourity = priority;
        return this;
    }

    @Override
    public int getPriority() {
        return priourity != null ? priourity : 0;
    }

    @Override
    public JMSProducer setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
        return this;
    }

    @Override
    public long getTimeToLive() {
        return timeToLive != null ? timeToLive : 0L;
    }

    @Override
    public JMSProducer setDeliveryDelay(long deliveryDelay) {
        this.deliveryDelay = deliveryDelay;
        return this;
    }

    @Override
    public long getDeliveryDelay() {
        return deliveryDelay != null ? deliveryDelay : 0L;
    }

    @Override
    public JMSProducer setAsync(CompletionListener completionListener) {
        this.completionListener = completionListener;
        return this;
    }

    @Override
    public CompletionListener getAsync() {
        return completionListener;
    }

    @Override
    public JMSProducer setProperty(String name, boolean value) {
        properties.setProperty(name, value);
        return this;
    }

    @Override
    public JMSProducer setProperty(String name, byte value) {
        properties.setProperty(name, value);
        return this;
    }

    @Override
    public JMSProducer setProperty(String name, short value) {
        properties.setProperty(name, value);
        return this;
    }

    @Override
    public JMSProducer setProperty(String name, int value) {
        properties.setProperty(name, value);
        return this;
    }

    @Override
    public JMSProducer setProperty(String name, long value) {
        properties.setProperty(name, value);
        return this;
    }

    @Override
    public JMSProducer setProperty(String name, float value) {
        properties.setProperty(name, value);
        return this;
    }

    @Override
    public JMSProducer setProperty(String name, double value) {
        properties.setProperty(name, value);
        return this;
    }

    @Override
    public JMSProducer setProperty(String name, String value) {
        properties.setProperty(name, value);
        return this;
    }

    @Override
    public JMSProducer setProperty(String name, Object value) {
        properties.setProperty(name, value);
        return this;
    }

    @Override
    public JMSProducer clearProperties() {
        properties.clear();
        return this;
    }

    @Override
    public boolean propertyExists(String name) {
        return properties.propertyExists(name);
    }

    @Override
    public boolean getBooleanProperty(String name) {
        return properties.getProperty(name);
    }

    @Override
    public byte getByteProperty(String name) {
        return properties.getProperty(name);
    }

    @Override
    public short getShortProperty(String name) {
        return properties.getProperty(name);
    }

    @Override
    public int getIntProperty(String name) {
        return properties.getProperty(name);
    }

    @Override
    public long getLongProperty(String name) {
        return properties.getProperty(name);
    }

    @Override
    public float getFloatProperty(String name) {
        return properties.getProperty(name);
    }

    @Override
    public double getDoubleProperty(String name) {
        return properties.getProperty(name);
    }

    @Override
    public String getStringProperty(String name) {
        return properties.getProperty(name);
    }

    @Override
    public Object getObjectProperty(String name) {
        return properties.getProperty(name);
    }

    @Override
    public Set<String> getPropertyNames() {
        return properties.getPropertyNames();
    }

    @Override
    public JMSProducer setJMSCorrelationIDAsBytes(byte[] correlationID) {
        this.jmsCorrelationID = new String(correlationID);
        return this;
    }

    @Override
    public byte[] getJMSCorrelationIDAsBytes() {
        return jmsCorrelationID.getBytes();
    }

    @Override
    public JMSProducer setJMSCorrelationID(String jmsCorrelationID) {
        this.jmsCorrelationID = jmsCorrelationID;
        return this;
    }

    @Override
    public String getJMSCorrelationID() {
        return jmsCorrelationID;
    }

    @Override
    public JMSProducer setJMSType(String jmsType) {
        this.jmsType = jmsType;
        return this;
    }

    @Override
    public String getJMSType() {
        return jmsType;
    }

    @Override
    public JMSProducer setJMSReplyTo(Destination replyTo) {
        this.replyTo = replyTo;
        return this;
    }

    @Override
    public Destination getJMSReplyTo() {
        return replyTo;
    }

}
