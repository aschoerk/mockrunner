package com.mockrunner.mock.jms.jms2_compat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jakarta.jms.MessageListener;
import jakarta.jms.Topic;
import jakarta.jms.TopicSubscriber;

public class TopicSubscriberAdaptorTest {

    TopicSubscriber mockSubscriber;

    TopicSubscriberAdaptor topicSubscriberAdaptor;

    public TopicSubscriberAdaptorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        mockSubscriber = mock(TopicSubscriber.class);
        topicSubscriberAdaptor = new TopicSubscriberAdaptor(mockSubscriber);
    }

    @After
    public void tearDown() {
        reset(mockSubscriber);
    }

    @Test
    public void testGetMessageSelector() throws Exception {
        when(mockSubscriber.getMessageSelector()).thenReturn("BO_SELECTOR");
        String result = topicSubscriberAdaptor.getMessageSelector();
        assertEquals("BO_SELECTOR", result);
        verify(mockSubscriber).getMessageSelector();
        verifyNoMoreInteractions(mockSubscriber);
    }

    @Test
    public void testGetMessageListener() throws Exception {
        MessageListener l = mock(MessageListener.class);
        when(mockSubscriber.getMessageListener()).thenReturn(l);
        MessageListener result = topicSubscriberAdaptor.getMessageListener();
        assertSame(l, result);
        verify(mockSubscriber).getMessageListener();
        verifyNoMoreInteractions(mockSubscriber);
    }

    @Test
    public void testSetMessageListener() throws Exception {
        MessageListener l = mock(MessageListener.class);
        topicSubscriberAdaptor.setMessageListener(l);
        verify(mockSubscriber).setMessageListener(l);
        verifyNoMoreInteractions(mockSubscriber);
    }

    @Test
    public void testReceive() throws Exception {
        topicSubscriberAdaptor.receive();
        verify(mockSubscriber).receive();
        verifyNoMoreInteractions(mockSubscriber);
    }

    @Test
    public void testReceiveWithTimeout() throws Exception {
        topicSubscriberAdaptor.receive(999L);
        verify(mockSubscriber).receive(999L);
        verifyNoMoreInteractions(mockSubscriber);
    }

    @Test
    public void testReceiveNoWait() throws Exception {
        topicSubscriberAdaptor.receiveNoWait();
        verify(mockSubscriber).receiveNoWait();
        verifyNoMoreInteractions(mockSubscriber);
    }

    @Test
    public void testClose() throws Exception {
        topicSubscriberAdaptor.close();
        verify(mockSubscriber).close();
        verifyNoMoreInteractions(mockSubscriber);
    }

    @Test
    public void testGetTopic() throws Exception {
        Topic topic = mock(Topic.class);
        when(mockSubscriber.getTopic()).thenReturn(topic);
        topicSubscriberAdaptor.getTopic();
        verify(mockSubscriber).getTopic();
        verifyNoMoreInteractions(mockSubscriber);
    }

    @Test
    public void testGetNoLocal() throws Exception {
        topicSubscriberAdaptor.getNoLocal();
        verify(mockSubscriber).getNoLocal();
        verifyNoMoreInteractions(mockSubscriber);
    }

}
