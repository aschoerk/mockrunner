package com.mockrunner.mock.jms.jms2_compat;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mockrunner.mock.jms.JMSMockObjectFactory;
import com.mockrunner.mock.jms.MockBytesMessage;
import com.mockrunner.mock.jms.MockConnectionFactory;
import com.mockrunner.mock.jms.MockMessage;
import com.mockrunner.mock.jms.MockQueue;
import com.mockrunner.mock.jms.MockTextMessage;
import com.mockrunner.mock.jms.MockTopic;

import jakarta.jms.BytesMessage;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;
import jakarta.jms.TextMessage;

public class Jms2CompatIT {

    JMSMockObjectFactory mockObjectFactory = new JMSMockObjectFactory();

    MockConnectionFactory mockConnectionFactory;

    MockQueue mockQueue;

    MockTopic mockTopic;

    public Jms2CompatIT() {
    }

    @Before
    public void setUp() {

        mockQueue = mockObjectFactory.getDestinationManager().createQueue("JUNIT_QUEUE");
        mockTopic = mockObjectFactory.getDestinationManager().createTopic("JUNIT_TOPIC");
        mockConnectionFactory = mockObjectFactory.createMockConnectionFactory();
    }

    @After
    public void tearDown() {
        mockConnectionFactory.clearConnections();
        mockQueue.clear();
        mockQueue.reset();
        mockTopic.clear();
        mockTopic.reset();
    }

    @Test
    public void sendStringBody() throws Exception {

        try (JMSContext context = mockConnectionFactory.createContext(JMSContext.SESSION_TRANSACTED)) {
            context.createProducer()
                    .send(mockQueue, "HELLOW");
        }
        assertTrue(mockConnectionFactory.getLatestConnection().isClosed());
        assertEquals(1, mockQueue.getReceivedMessageList().size());
        assertEquals("HELLOW", TextMessage.class.cast(mockQueue.getMessage()).getText());

    }

    @Test
    public void sendMapBody() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("greeting", "HELLOW");
        try (JMSContext context = mockConnectionFactory.createContext(JMSContext.SESSION_TRANSACTED)) {
            context.createProducer()
                    .send(mockQueue, map);
        }
        assertTrue(mockConnectionFactory.getLatestConnection().isClosed());
        assertEquals(1, mockQueue.getReceivedMessageList().size());
        assertEquals("HELLOW", MapMessage.class.cast(mockQueue.getMessage()).getString("greeting"));
    }

    static class DummyPayload implements Serializable {
    }

    @Test
    public void sendObjectBody() throws Exception {
        DummyPayload payload = new DummyPayload();

        try (JMSContext context = mockConnectionFactory.createContext(JMSContext.SESSION_TRANSACTED)) {
            context.createProducer()
                    .send(mockQueue, payload);
        }
        assertTrue(mockConnectionFactory.getLatestConnection().isClosed());
        assertEquals(1, mockQueue.getReceivedMessageList().size());
        assertEquals(DummyPayload.class, ObjectMessage.class.cast(mockQueue.getMessage()).getObject().getClass());
    }

    @Test
    public void sendBytesBody() throws Exception {
        byte[] payload = new byte[100];

        try (JMSContext context = mockConnectionFactory.createContext(JMSContext.SESSION_TRANSACTED)) {
            context.createProducer()
                    .send(mockQueue, payload);
        }
        assertTrue(mockConnectionFactory.getLatestConnection().isClosed());
        assertEquals(1, mockQueue.getReceivedMessageList().size());
        assertEquals(100L, BytesMessage.class.cast(mockQueue.getMessage()).getBodyLength());
    }

    @Test
    public void receiveTextMessageSynchronous() throws Exception {
        mockQueue.addMessage(new MockTextMessage("HELLOW"));

        try (JMSContext context = mockConnectionFactory.createContext()) {
            TextMessage result = (TextMessage) context.createConsumer(mockQueue).receive();
            assertEquals("HELLOW", result.getText());
        }

        assertTrue(mockQueue.getCurrentMessageList().isEmpty());
    }

    @Test
    public void receiveTextMessageAsynchronous() throws Exception {

        try (JMSContext jmsContext = mockConnectionFactory.createContext(JMSContext.SESSION_TRANSACTED)) {
            final CountDownLatch latch = new CountDownLatch(1);
            final List<MockTextMessage> result = new ArrayList<>();
            JMSConsumer consumer = jmsContext.createConsumer(mockQueue);

            consumer.setMessageListener(new MessageListener() {

                @Override
                public void onMessage(Message message) {
                    result.add((MockTextMessage) message);
                    latch.countDown();
                }
            });
            jmsContext.start();
            mockQueue.addMessage(new MockTextMessage("SOME MESSAGE"));

            assertTrue(latch.await(10, TimeUnit.SECONDS));

            assertEquals("SOME MESSAGE", result.get(0).getText());
            assertEquals("SOME MESSAGE", result.get(0).getBody(String.class));
            assertEquals(com.mockrunner.mock.jms.jms2_compat.Jms2MessageListener.class, consumer.getMessageListener().getClass());
        }

    }

    @Test
    public void receiveStringBodySynchonously() throws Exception {
        mockQueue.addMessage(new MockTextMessage("SOMETHING ELSE"));

        try (JMSContext jmsContext = mockConnectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {

            String outcome = jmsContext.createConsumer(mockQueue).receiveBody(String.class);

            assertEquals("SOMETHING ELSE", outcome);
        }

    }


    @Test
    public void receiveBytesBodySynchonously() throws Exception {

        byte[] data = "SOMETHING ELSE".getBytes();
        MockBytesMessage msg = new MockBytesMessage();
        msg.writeBytes(data, 0, data.length);
        msg.reset();

        mockQueue.addMessage(msg);

        try (JMSContext jmsContext = mockConnectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)) {

            byte[] outcome = jmsContext.createConsumer(mockQueue).receiveBody(byte[].class);

            assertArrayEquals(data, outcome);
        }
    }

    @Test
    public void createDurableConsumer() throws Exception {
        String message = "Some message";
        mockTopic.addMessage(new MockTextMessage(message));

        try (JMSContext jmsContext = mockConnectionFactory.createContext()) {

            JMSConsumer consumer = jmsContext.createDurableConsumer(mockTopic, "MYSUB");

            assertEquals(message, consumer.receiveBodyNoWait(String.class));
            assertNull(consumer.receiveBodyNoWait(String.class));
        }

    }

    @Test
    public void sendMessageWithProperties() throws Exception {

        try (JMSContext context = mockConnectionFactory.createContext()) {
            context.createProducer()
                    .setProperty("MYSTR", "MYSTRVAL")
                    .setJMSType("Bogus")
                    .setJMSCorrelationID("JMSCID")
                    .send(mockQueue, "HELLOW");
        }

        MockTextMessage result = (MockTextMessage) mockQueue.getMessage();
     
        assertEquals("HELLOW", result.getText());
        assertEquals("JMSCID", result.getJMSCorrelationID());
        assertEquals("Bogus", result.getJMSType());
        assertEquals("MYSTRVAL", result.getStringProperty("MYSTR"));

    }
    
    @Test
    public void recieveAyncMessagesClientAck() throws Exception {
        MockMessage mockMessage = new MockMessage();
        
        mockQueue.addMessage(mockMessage);
        
        assertFalse(mockMessage.isAcknowledged());

        try (JMSContext context = mockConnectionFactory.createContext(JMSContext.CLIENT_ACKNOWLEDGE)) {
            Message m = context.createConsumer(mockQueue).receive();
            assertNotNull(m);
            assertFalse(mockMessage.isAcknowledged());
            context.acknowledge();
        }
        
        assertTrue(mockMessage.isAcknowledged());
        
    }
    

}
