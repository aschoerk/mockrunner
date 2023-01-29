package com.mockrunner.mock.jms.jms2_compat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mockrunner.mock.jms.jms2_compat.TopicSubscriberAdaptor;

import jakarta.jms.CompletionListener;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

public class Jms2ProducerTest {

    Queue mockDestination;

    MessageProducer mockMessageProducer;

    Session mockSession;

    Jms2Producer producer;

    public Jms2ProducerTest() {
    }

    @Before
    public void setUp() throws Exception {
        mockDestination = mock(Queue.class);
        mockSession = mock(Session.class);
        mockMessageProducer = mock(MessageProducer.class);
        when(mockSession.createQueue(anyString())).thenReturn(mockDestination);
        when(mockSession.createProducer(mockDestination)).thenReturn(mockMessageProducer);
        producer = new com.mockrunner.mock.jms.jms2_compat.Jms2Producer(mockSession);
    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(mockSession, mockMessageProducer, mockDestination);
    }

    @Test
    public void testSendMessage() throws Exception {
        Message mockMessage = mock(Message.class);

        producer.send(mockDestination, mockMessage);

        verify(mockSession, times(1)).createProducer(mockDestination);
        verify(mockMessageProducer, times(1)).send(mockMessage);
        verify(mockMessageProducer, times(1)).setDisableMessageID(false);
        verify(mockMessageProducer, times(1)).setDisableMessageTimestamp(false);
    }

    @Test
    public void testSendMessageWithJmsProperties() throws Exception {
        Message mockMessage = mock(Message.class);

        producer.setJMSCorrelationID("CID");
        producer.setJMSReplyTo(mock(Queue.class));
        producer.setJMSType("text");
        producer.setPriority(9);
        producer.send(mockDestination, mockMessage);

        verify(mockSession, times(1)).createProducer(mockDestination);
        verify(mockMessageProducer, times(1)).setPriority(9);
        verify(mockMessageProducer, times(1)).send(mockMessage);
        verify(mockMessageProducer, times(1)).setDisableMessageID(false);
        verify(mockMessageProducer, times(1)).setDisableMessageTimestamp(false);
    }

    @Test
    public void testSendStringBody() throws Exception {

        TextMessage mockMessage = mock(TextMessage.class);
        when(mockSession.createTextMessage()).thenReturn(mockMessage);

        producer.send(mockDestination, "HELLOW");

        verify(mockSession, times(1)).createProducer(mockDestination);
        verify(mockSession, times(1)).createTextMessage();
        verify(mockMessage, times(1)).setText("HELLOW");
        verify(mockMessageProducer, times(1)).send(any(TextMessage.class));
        verify(mockMessageProducer, times(1)).setDisableMessageID(false);
        verify(mockMessageProducer, times(1)).setDisableMessageTimestamp(false);
        verifyNoMoreInteractions(mockMessage);
    }

    @Test
    public void testSendMapBody() throws Exception {
        MapMessage mockMessage = mock(MapMessage.class);
        when(mockSession.createMapMessage()).thenReturn(mockMessage);

        Map<String, Object> map = new HashMap<>();
        map.put("greeting", "HELLOW");
        producer.send(mockDestination, map);

        verify(mockSession, times(1)).createProducer(mockDestination);
        verify(mockSession, times(1)).createMapMessage();
        verify(mockMessage, times(1)).setObject("greeting", "HELLOW");
        verify(mockMessageProducer, times(1)).send(any(MapMessage.class));
        verify(mockMessageProducer, times(1)).setDisableMessageID(false);
        verify(mockMessageProducer, times(1)).setDisableMessageTimestamp(false);

        verifyNoMoreInteractions(mockMessage);

    }

    @Test
    public void testSend_Destination_byteArr() {
    }

    @Test
    public void testSend_Destination_Serializable() {
    }

    @Test
    public void testGetDisableMessageID() throws Exception {
        assertFalse(producer.getDisableMessageID());
        producer.setDisableMessageID(true);
        assertTrue(producer.getDisableMessageID());

    }

    @Test
    public void testSetGetDisableMessageTimestamp() throws Exception {
        assertFalse(producer.getDisableMessageTimestamp());
        producer.setDisableMessageTimestamp(true);
        assertTrue(producer.getDisableMessageTimestamp());
    }

    @Test
    public void testSetGetDeliveryMode() throws Exception {
        assertEquals(0, producer.getDeliveryMode());
        producer.setDeliveryMode(99);
        assertEquals(99, producer.getDeliveryMode());
    }

    @Test
    public void testSetGetPriority() {
        assertEquals(0, producer.getPriority());
        producer.setPriority(99);
        assertEquals(99, producer.getPriority());
    }

    @Test
    public void testSetGetTimeToLive() {
        assertEquals(0, producer.getTimeToLive());
        producer.setTimeToLive(99);
        assertEquals(99, producer.getTimeToLive());
    }

    @Test
    public void testSetGetDeliveryDelay() {
        assertEquals(0, producer.getDeliveryDelay());
        producer.setDeliveryDelay(99);
        assertEquals(99, producer.getDeliveryDelay());
    }

    @Test
    public void testSetGetAsync() {
        CompletionListener async = mock(CompletionListener.class);
        assertNull(producer.getAsync());

        producer.setAsync(async);

        assertNotNull(producer.getAsync());

    }

    @Test
    public void testSetPropertyBoolean() {
        producer.setProperty("FOO", true);
        assertTrue(producer.getBooleanProperty("FOO"));
    }

    @Test
    public void testSetPropertyByte() {
        byte b = 99;
        producer.setProperty("FOO", b);
        assertEquals(b, producer.getByteProperty("FOO"));
    }

    @Test
    public void testSetPropertyShort() {
        short s = 89;
        producer.setProperty("FOO", s);
        assertEquals(s, producer.getShortProperty("FOO"));

    }

    @Test
    public void testSetPropertyInt() {
        int i = 89;
        producer.setProperty("FOO", i);
        assertEquals(i, producer.getIntProperty("FOO"));
    }

    @Test
    public void testSetPropertyLong() {
        long l = Long.MAX_VALUE;
        producer.setProperty("FOO", l);
        assertEquals(l, producer.getLongProperty("FOO"));
    }

    @Test
    public void testSetPropertyFloat() {
        float f = Float.MAX_VALUE;
        producer.setProperty("FOO", f);
        assertEquals(f, producer.getFloatProperty("FOO"), 0.0f);
    }

    @Test
    public void testSetPropertyDouble() {
        double d = Double.MAX_VALUE;
        producer.setProperty("FOO", d);
        assertEquals(d, producer.getDoubleProperty("FOO"), 0.0d);
    }

    @Test
    public void testSetProperty_String_String() {
    }

    @Test
    public void testSetProperty_String_Object() {
    }

    @Test
    public void testClearProperties() {
    }

    @Test
    public void testPropertyExists() {
    }

    @Test
    public void testGetBooleanProperty() {
    }

    @Test
    public void testGetByteProperty() {
    }

    @Test
    public void testGetShortProperty() {
    }

    @Test
    public void testGetIntProperty() {
    }

    @Test
    public void testGetLongProperty() {
    }

    @Test
    public void testGetFloatProperty() {
    }

    @Test
    public void testGetDoubleProperty() {
    }

    @Test
    public void testGetStringProperty() {
    }

    @Test
    public void testGetObjectProperty() {
    }

    @Test
    public void testGetPropertyNames() {
    }

    @Test
    public void testSetJMSCorrelationIDAsBytes() {
    }

    @Test
    public void testGetJMSCorrelationIDAsBytes() {
    }

    @Test
    public void testSetJMSCorrelationID() {
    }

    @Test
    public void testGetJMSCorrelationID() {
    }

    @Test
    public void testSetJMSType() {
    }

    @Test
    public void testGetJMSType() {
    }

    @Test
    public void testSetJMSReplyTo() {
    }

    @Test
    public void testGetJMSReplyTo() {
    }

}
