package com.mockrunner.mock.jms.jms2_compat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mockrunner.mock.jms.MockBytesMessage;
import com.mockrunner.mock.jms.MockConnection;
import com.mockrunner.mock.jms.MockMapMessage;
import com.mockrunner.mock.jms.MockMessage;
import com.mockrunner.mock.jms.MockObjectMessage;
import com.mockrunner.mock.jms.MockStreamMessage;
import com.mockrunner.mock.jms.MockTextMessage;

import jakarta.jms.BytesMessage;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionMetaData;
import jakarta.jms.Destination;
import jakarta.jms.ExceptionListener;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSProducer;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.StreamMessage;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;
import jakarta.jms.TopicSubscriber;

public class Jms2ContextTest {

    Session mockSession;

    Connection mockConnection;

    Jms2Context jmsContext;

    public Jms2ContextTest() {
    }

    @Before
    public void setUp() throws Exception {
        mockConnection = mock(Connection.class);
        mockSession = mock(Session.class);

        jmsContext = new Jms2Context(mockConnection, mockSession);
        when(mockConnection.createSession(anyBoolean(), anyInt())).thenReturn(mockSession);

        MessageConsumer mockMessageConsumer = mock(MessageConsumer.class);

        when(mockSession.createConsumer(any(Destination.class))).thenReturn(mockMessageConsumer);
        when(mockSession.createConsumer(any(Destination.class), anyString())).thenReturn(mockMessageConsumer);
    }

    @After
    public void tearDown() throws Exception {

        mockConnection = null;
        mockSession = null;
        jmsContext = null;
    }

    @Test
    public void testCreateContext() throws Exception {
        Session createdSession = mock(Session.class);

        when(mockConnection.createSession(false, JMSContext.AUTO_ACKNOWLEDGE)).thenReturn(createdSession);

        JMSContext outcome = jmsContext.createContext(JMSContext.AUTO_ACKNOWLEDGE);
        assertNotNull(outcome);

        verify(mockConnection, times(1)).createSession(false, JMSContext.AUTO_ACKNOWLEDGE);
        verifyZeroInteractions(createdSession, mockSession);
        verifyNoMoreInteractions(mockConnection);

    }

    @Test
    public void testCreateProducer() {
        JMSProducer producer = jmsContext.createProducer();
        assertNotNull(producer);
        assertEquals(Jms2Producer.class, producer.getClass());

        verifyZeroInteractions(mockConnection, mockSession);

    }

    @Test
    public void testGetClientID() throws Exception {

        when(mockConnection.getClientID()).thenReturn("JUNIT");

        assertEquals("JUNIT", jmsContext.getClientID());

        verify(mockConnection, times(1)).getClientID();

        verifyNoMoreInteractions(mockConnection);
        verifyZeroInteractions(mockSession);
    }

    @Test
    public void testSetClientID() throws Exception {
        jmsContext.setClientID("BOGUSCLIENT");
        verify(mockConnection, times(1)).setClientID("BOGUSCLIENT");
        verifyNoMoreInteractions(mockConnection);
        verifyZeroInteractions(mockSession);
    }

    @Test
    public void testGetMetaData() throws Exception {
        ConnectionMetaData metaData = mock(ConnectionMetaData.class);
        when(mockConnection.getMetaData()).thenReturn(metaData);

        ConnectionMetaData result = jmsContext.getMetaData();

        assertSame(metaData, result);
        verify(mockConnection, times(1)).getMetaData();
        verifyNoMoreInteractions(mockConnection);
        verifyZeroInteractions(mockSession);
    }

    @Test
    public void testGetExceptionListener() throws Exception {
        ExceptionListener l = mock(ExceptionListener.class);
        when(mockConnection.getExceptionListener()).thenReturn(l);

        ExceptionListener result = jmsContext.getExceptionListener();
        assertSame(l, result);

        verify(mockConnection, times(1)).getExceptionListener();
        verifyNoMoreInteractions(mockConnection);
        verifyZeroInteractions(mockSession);
    }

    @Test
    public void testSetExceptionListener() throws Exception {
        ExceptionListener l = mock(ExceptionListener.class);
        jmsContext.setExceptionListener(l);
        verify(mockConnection, times(1)).setExceptionListener(l);
        verifyNoMoreInteractions(mockConnection);
        verifyZeroInteractions(mockSession);

    }

    @Test
    public void testStart() throws Exception {
        jmsContext.start();
        verify(mockConnection, times(1)).start();
        verifyNoMoreInteractions(mockConnection);
        verifyZeroInteractions(mockSession);

        reset(mockConnection);

        jmsContext.start();
        verify(mockConnection, times(1)).stop();
        verify(mockConnection, times(1)).start();
        verifyNoMoreInteractions(mockConnection);
        verifyZeroInteractions(mockSession);

    }

    @Test
    public void testStop() throws Exception {
        jmsContext.stop();
        verify(mockConnection, times(1)).stop();
        verifyNoMoreInteractions(mockConnection);
        verifyZeroInteractions(mockSession);

    }

    @Test
    public void testSetAutoStart() throws Exception {
        jmsContext.setAutoStart(true);
        assertTrue(jmsContext.getAutoStart());
        verifyZeroInteractions(mockConnection, mockSession);
    }

    @Test
    public void testGetAutoStart() {
        jmsContext.setAutoStart(false);
        assertFalse(jmsContext.getAutoStart());
        verifyZeroInteractions(mockConnection, mockSession);
    }

    @Test
    public void testClose() throws Exception {
        jmsContext.close();

        verify(mockConnection, times(1)).close();
        verify(mockSession, times(1)).close();

    }

    @Test
    public void testCreateBytesMessage() throws Exception {
        BytesMessage mockMessage = mock(BytesMessage.class);

        when(mockSession.createBytesMessage()).thenReturn(mockMessage);
        BytesMessage result = jmsContext.createBytesMessage();
        assertNotNull(result);

        verify(mockSession, times(1)).createBytesMessage();
        verifyNoMoreInteractions(mockSession);
        verifyZeroInteractions(mockConnection, mockMessage);

    }

    @Test
    public void testCreateMapMessage() throws Exception {
        MapMessage mockMessage = mock(MapMessage.class);

        when(mockSession.createMapMessage()).thenReturn(mockMessage);

        MapMessage result = (MapMessage) jmsContext.createMapMessage();

        verify(mockSession, times(1)).createMapMessage();
        verifyNoMoreInteractions(mockSession);
        verifyZeroInteractions(mockConnection, mockMessage);
    }

    @Test
    public void testCreateMessage() throws Exception {
        Message mockMessage = mock(Message.class);
        when(mockSession.createMessage()).thenReturn(mockMessage);

        Message result = (Message) jmsContext.createMessage();
        verify(mockSession, times(1)).createMessage();
        verifyNoMoreInteractions(mockSession);
        verifyZeroInteractions(mockConnection, mockMessage);
    }

    @Test
    public void testCreateObjectMessage() throws Exception {
        ObjectMessage mockMessage = mock(ObjectMessage.class);
        when(mockSession.createObjectMessage()).thenReturn(mockMessage);

        ObjectMessage result = (ObjectMessage) jmsContext.createObjectMessage();
        verify(mockSession, times(1)).createObjectMessage();
        verifyNoMoreInteractions(mockSession);
        verifyZeroInteractions(mockConnection, mockMessage);
    }

    @Test
    public void testCreateObjectMessageWithPayload() throws Exception {
        Serializable payload = "PAYLOAD";

        ObjectMessage mockMessage = mock(ObjectMessage.class);
        when(mockSession.createObjectMessage()).thenReturn(mockMessage);

        ObjectMessage result = (ObjectMessage) jmsContext.createObjectMessage(payload);

        verify(mockSession, times(1)).createObjectMessage();
        verify(mockMessage, times(1)).setObject(payload);
        //verify(mockSession, times(1)).getAcknowledgeMode();
        verifyNoMoreInteractions(mockSession, mockMessage);

        verifyZeroInteractions(mockConnection);
    }

    @Test
    public void testCreateStreamMessage() throws Exception {
        StreamMessage mockMessage = mock(StreamMessage.class);
        when(mockSession.createStreamMessage()).thenReturn(mockMessage);
        StreamMessage result = (StreamMessage) jmsContext.createStreamMessage();

        verify(mockSession, times(1)).createStreamMessage();
        //verify(mockSession, times(1)).getAcknowledgeMode();
        verifyNoMoreInteractions(mockSession);
        verifyZeroInteractions(mockConnection);
    }

    @Test
    public void testCreateTextMessage() throws Exception {
        TextMessage mockMessage = mock(TextMessage.class);
        when(mockSession.createTextMessage()).thenReturn(mockMessage);

        TextMessage result = (TextMessage) jmsContext.createTextMessage();
        verify(mockSession, times(1)).createTextMessage();
        // verify(mockSession, times(1)).getAcknowledgeMode();
        verifyNoMoreInteractions(mockSession);
        verifyZeroInteractions(mockConnection);
    }

    @Test
    public void testCreateTextMessageWithPayload() throws Exception {
        TextMessage mockMessage = mock(TextMessage.class);
        when(mockSession.createTextMessage()).thenReturn(mockMessage);

        TextMessage result = (TextMessage) jmsContext.createTextMessage("payload");
        verify(mockSession, times(1)).createTextMessage();
        verify(mockMessage, times(1)).setText("payload");
        //verify(mockSession, times(1)).getAcknowledgeMode();
        verifyNoMoreInteractions(mockSession, mockMessage);
        verifyZeroInteractions(mockConnection);
    }

    @Test
    public void testGetTransacted() throws Exception {
        when(mockSession.getTransacted()).thenReturn(true);
        assertTrue(jmsContext.getTransacted());
        verify(mockSession, times(1)).getTransacted();
        verifyNoMoreInteractions(mockSession);
        verifyZeroInteractions(mockConnection);
    }

    @Test
    public void testGetSessionMode() throws Exception {
        when(mockSession.getAcknowledgeMode()).thenReturn(Session.DUPS_OK_ACKNOWLEDGE);

        assertEquals(JMSContext.DUPS_OK_ACKNOWLEDGE, jmsContext.getSessionMode());
        verify(mockSession, times(1)).getAcknowledgeMode();
        verifyNoMoreInteractions(mockSession);
        verifyZeroInteractions(mockConnection);
    }

    @Test
    public void testCommit() throws Exception {
        jmsContext.commit();
        verify(mockSession, times(1)).commit();
        verifyNoMoreInteractions(mockSession);
        verifyZeroInteractions(mockConnection);

    }

    @Test
    public void testRollback() throws Exception {
        jmsContext.rollback();
        verify(mockSession, times(1)).rollback();
        verifyNoMoreInteractions(mockSession);
        verifyZeroInteractions(mockConnection);
    }

    @Test
    public void testRecover() throws Exception {
        jmsContext.recover();
        verify(mockSession, times(1)).recover();
        verifyNoMoreInteractions(mockSession);
        verifyZeroInteractions(mockConnection);
    }

    @Test
    public void testCreateConsumer_Destination() throws Exception {
        Destination mockDestination = mock(Destination.class);
        JMSConsumer consumer = jmsContext.createConsumer(mockDestination);
        verify(mockSession, times(1)).createConsumer(mockDestination);
        verifyNoMoreInteractions(mockSession);
        verifyZeroInteractions(mockConnection, mockDestination);

    }

    @Test
    public void testCreateConsumer_Destination_String() throws Exception {
        Destination mockDestination = mock(Destination.class);
        JMSConsumer consumer = jmsContext.createConsumer(mockDestination, "SELECTOR");
        verify(mockSession, times(1)).createConsumer(mockDestination, "SELECTOR");
        verifyNoMoreInteractions(mockSession);
        verifyZeroInteractions(mockConnection, mockDestination);

    }

    @Test
    public void testCreateConsumerWithSlectorAndNoLocalFlag() throws Exception {
        Destination mockDestination = mock(Destination.class);
        JMSConsumer consumer = jmsContext.createConsumer(mockDestination, "SELECTOR", false);
        verify(mockSession, times(1)).createConsumer(mockDestination, "SELECTOR");
        verifyNoMoreInteractions(mockSession);
        verifyZeroInteractions(mockConnection, mockDestination);

    }

    @Test
    public void testCreateQueue() throws Exception {
        jmsContext.createQueue("MYQ");
        verify(mockSession, times(1)).createQueue("MYQ");
        verifyZeroInteractions(mockConnection);
    }

    @Test
    public void testCreateTopic() throws Exception {
        jmsContext.createTopic("MYT");
        verify(mockSession, times(1)).createTopic("MYT");
        verifyZeroInteractions(mockConnection);
    }

    @Test
    public void testCreateDurableConsumer() throws Exception {
        String subscriptionName = "MYSUB";
        Topic mockTopic = mock(Topic.class);
        TopicSubscriber mockConsumer = mock(TopicSubscriber.class);

        when(mockSession.createDurableSubscriber(mockTopic, subscriptionName)).thenReturn(mockConsumer);
        JMSConsumer consumer = jmsContext.createDurableConsumer(mockTopic, subscriptionName);

        verify(mockSession, times(1)).createDurableSubscriber(mockTopic, subscriptionName);

        consumer.receiveNoWait();

        verify(mockConsumer, times(1)).receiveNoWait();
        verifyNoMoreInteractions(mockConsumer);
        verifyZeroInteractions(mockConnection);

    }

    @Test
    public void testCreateDurableConsumerWithSelectorAndNoLocal() throws Exception {
        String selector = "FOO = 'BAR'";
        String subscriptionName = "MYSUB";
        Topic mockTopic = mock(Topic.class);
        TopicSubscriber mockConsumer = mock(TopicSubscriber.class);

        when(mockSession.createDurableSubscriber(mockTopic, subscriptionName, selector, true)).thenReturn(mockConsumer);
        JMSConsumer consumer = jmsContext.createDurableConsumer(mockTopic, subscriptionName, selector, true);

        verify(mockSession, times(1)).createDurableSubscriber(mockTopic, subscriptionName, selector, true);

        consumer.receiveNoWait();

        verify(mockConsumer, times(1)).receiveNoWait();
        verifyNoMoreInteractions(mockConsumer);
        verifyZeroInteractions(mockConnection);
    }

    @Test
    public void testCreateSharedDurableConsumer() throws Exception {

        String subscriptionName = "MYSUB";
        Topic mockTopic = mock(Topic.class);
        TopicSubscriber mockConsumer = mock(TopicSubscriber.class);

        when(mockSession.createDurableSubscriber(mockTopic, subscriptionName)).thenReturn(mockConsumer);

        JMSConsumer consumer = jmsContext.createSharedDurableConsumer(mockTopic, subscriptionName);

        verify(mockSession, times(1)).createDurableSubscriber(mockTopic, subscriptionName);

        consumer.receiveNoWait();

        verify(mockConsumer, times(1)).receiveNoWait();
        verifyNoMoreInteractions(mockConsumer);
        verifyZeroInteractions(mockConnection);
    }

    @Test
    public void testCreateSharedDurableConsumer_3args() throws Exception {
        String selector = "SELECTOR";
        String subscriptionName = "MYSUB";
        Topic mockTopic = mock(Topic.class);
        TopicSubscriber mockConsumer = mock(TopicSubscriber.class);

        when(mockSession.createDurableSubscriber(mockTopic, subscriptionName, selector, false)).thenReturn(mockConsumer);

        JMSConsumer consumer = jmsContext.createSharedDurableConsumer(mockTopic, subscriptionName, selector);

        verify(mockSession, times(1)).createDurableSubscriber(mockTopic, subscriptionName, selector, false);

        consumer.receiveNoWait();

        verify(mockConsumer, times(1)).receiveNoWait();
        verifyNoMoreInteractions(mockConsumer);
        verifyZeroInteractions(mockConnection);
    }

    @Test
    public void testCreateSharedConsumer() throws Exception {

        String subscriptionName = "MYSUB";
        Topic mockTopic = mock(Topic.class);
        TopicSubscriber mockConsumer = mock(TopicSubscriber.class);

        when(mockSession.createDurableSubscriber(mockTopic, subscriptionName)).thenReturn(mockConsumer);

        JMSConsumer consumer = jmsContext.createSharedConsumer(mockTopic, subscriptionName);

        verify(mockSession, times(1)).createDurableSubscriber(mockTopic, subscriptionName);

        consumer.receiveNoWait();

        verify(mockConsumer, times(1)).receiveNoWait();
        verifyNoMoreInteractions(mockConsumer);
        verifyZeroInteractions(mockConnection);
    }

    @Test
    public void testCreateSharedConsumer_3args() {
    }

    @Test
    public void testCreateBrowser() throws Exception {

        Queue mockQueue = mock(Queue.class);
        jmsContext.createBrowser(mockQueue);

        verify(mockSession, times(1)).createBrowser(mockQueue);

        verifyNoMoreInteractions(mockSession);
        verifyZeroInteractions(mockConnection, mockQueue);
    }

    @Test
    public void testCreateBrowserWithSelector() throws Exception {

        Queue mockQueue = mock(Queue.class);
        jmsContext.createBrowser(mockQueue, "SELECTOR");

        verify(mockSession, times(1)).createBrowser(mockQueue, "SELECTOR");

        verifyNoMoreInteractions(mockSession);
        verifyZeroInteractions(mockConnection, mockQueue);
    }

    @Test
    public void testCreateTemporaryQueue() throws Exception {
        jmsContext.createTemporaryQueue();
        verify(mockSession, times(1)).createTemporaryQueue();

        verifyNoMoreInteractions(mockSession);
        verifyZeroInteractions(mockConnection);
    }

    @Test
    public void testCreateTemporaryTopic() throws Exception {
        jmsContext.createTemporaryTopic();
        verify(mockSession, times(1)).createTemporaryTopic();

        verifyNoMoreInteractions(mockSession);
        verifyZeroInteractions(mockConnection);

    }

    @Test
    public void testUnsubscribe() throws Exception {
        jmsContext.unsubscribe("SOMETOPIC");
        verify(mockSession, times(1)).unsubscribe("SOMETOPIC");

        verifyNoMoreInteractions(mockSession);
        verifyZeroInteractions(mockConnection);

    }

    @Test
    public void testAcknowledge() throws Exception {

        when(mockSession.getAcknowledgeMode()).thenReturn(Session.CLIENT_ACKNOWLEDGE);

        Message msg = mock(Message.class);
        Message msg2 = mock(Message.class);
        Message msg3 = mock(Message.class);
        List<Message> messages = Arrays.asList(msg, msg2, msg3);

        Jms2MessageUtil.acknowledge(mockSession, messages);

        verify(msg).acknowledge();
        verify(msg2).acknowledge();
        verify(msg3).acknowledge();

        when(mockSession.getAcknowledgeMode()).thenReturn(Session.AUTO_ACKNOWLEDGE);

        Jms2MessageUtil.acknowledge(mockSession, messages);

        verify(mockSession, times(2)).getAcknowledgeMode();
        verifyNoMoreInteractions(msg, msg2, msg3, mockSession);

    }

    @Test
    public void testAutoStart() throws Exception {
        jmsContext.setAutoStart(true);
        assertTrue(jmsContext.getAutoStart());
        Queue q = mock(Queue.class);
        MessageListener listener = mock(MessageListener.class);
        jmsContext.createConsumer(q).setMessageListener(listener);
        verify(mockConnection).start();
        verify(mockSession).createConsumer(q);
        verifyNoMoreInteractions(mockSession, mockConnection);

    }

}
