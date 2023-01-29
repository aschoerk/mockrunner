package com.mockrunner.mock.jms.jms2_compat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.Serializable;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import  com.mockrunner.mock.jms.jms2_compat.*;
import com.mockrunner.mock.jms.JMSMockObjectFactory;
import com.mockrunner.mock.jms.MockBytesMessage;
import com.mockrunner.mock.jms.MockMapMessage;
import com.mockrunner.mock.jms.MockMessage;
import com.mockrunner.mock.jms.MockObjectMessage;
import com.mockrunner.mock.jms.MockSession;
import com.mockrunner.mock.jms.MockStreamMessage;
import com.mockrunner.mock.jms.MockTextMessage;
import com.mockrunner.mock.jms.MockTopic;

import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import jakarta.jms.StreamMessage;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;

public class Jms2SessionTest {

    private MockSession mockSession;

    private MockSession session;

    public Jms2SessionTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        mockSession = (MockSession) new JMSMockObjectFactory()
                .getMockConnectionFactory()
                .createConnection()
                .createSession(false, Session.AUTO_ACKNOWLEDGE);
        session = mockSession;
    }

    @After
    public void tearDown() {
        mockSession = null;
    }

    @Test
    public void createBytesMessage() throws Exception {
        BytesMessage result = session.createBytesMessage();
        assertNotNull(result);
        assertEquals(MockBytesMessage.class, result.getClass());
    }

    @Test
    public void createMapMessage() throws Exception {
        MapMessage result = session.createMapMessage();
        assertNotNull(result);
        assertEquals(MockMapMessage.class, result.getClass());
    }

    @Test
    public void createMessage() throws Exception {
        Message result = session.createMessage();
        assertNotNull(result);
        assertEquals(MockMessage.class, result.getClass());
    }

    @Test
    public void createObjectMessage() throws Exception {
        ObjectMessage result = session.createObjectMessage();
        assertNotNull(result);
        assertEquals(MockObjectMessage.class, result.getClass());
    }

    static class Payload implements Serializable {
    };

    @Test
    public void createObjectMessageWithPayload() throws Exception {
        Payload payload = new Payload();
        ObjectMessage result = session.createObjectMessage(payload);
        assertNotNull(result);
        assertEquals(MockObjectMessage.class, result.getClass());
        assertSame(payload, result.getObject());
    }

    @Test
    public void createStreamMessage() throws Exception {
        StreamMessage result = session.createStreamMessage();
        assertNotNull(result);
        assertEquals(MockStreamMessage.class, result.getClass());
    }

    @Test
    public void createTextMessage() throws Exception {
        TextMessage result = session.createTextMessage();
        assertNotNull(result);
        assertEquals(MockTextMessage.class, result.getClass());
    }

    @Test
    public void createTextMessageWithBody() throws Exception {
        TextMessage result = session.createTextMessage("BODY");
        assertNotNull(result);
        assertEquals(MockTextMessage.class, result.getClass());
        assertEquals("BODY", result.getText());
    }

    @Test
    public void setGetMessageListener() throws Exception {
        MessageListener l = new MessageListener() {

            @Override
            public void onMessage(Message message) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        session.setMessageListener(l);

        assertEquals(l.getClass(), session.getMessageListener().getClass());

    }

    @Test
    public void createSharedConsumer() throws JMSException {
        Topic topic = new MockTopic("MOCKT");

        MessageConsumer messageConsumer = session.createSharedConsumer(topic, "FOO");
        assertNotNull(messageConsumer);
        assertEquals(TopicSubscriberAdaptor.class, messageConsumer.getClass());
    }

    @Test
    public void createSharedConsumerWithSelector() throws JMSException {
        Topic topic = new MockTopic("MOCKT");

        MessageConsumer messageConsumer = session.createSharedConsumer(topic, "FOO", "SELECTOR");
        assertNotNull(messageConsumer);
        assertEquals(TopicSubscriberAdaptor.class, messageConsumer.getClass());
        assertEquals("SELECTOR", messageConsumer.getMessageSelector());
    }

    @Test
    public void createDurableConsumer() throws Exception {
        Topic topic = new MockTopic("MOCKT");

        MessageConsumer messageConsumer = session.createDurableConsumer(topic, "FOO");
        assertNotNull(messageConsumer);
        assertEquals(TopicSubscriberAdaptor.class, messageConsumer.getClass());
    }

    @Test
    public void createDurableConsumerWithSelector() throws Exception {
        Topic topic = new MockTopic("MOCKT");
        
        MessageConsumer messageConsumer = session.createDurableConsumer(topic, "FOO", "SELECTOR", true);
        assertNotNull(messageConsumer);
        assertEquals(TopicSubscriberAdaptor.class, messageConsumer.getClass());
        assertEquals("SELECTOR", messageConsumer.getMessageSelector());
        
        
    }
    
    
    @Test
    public void createSharedDurableConsumer() throws JMSException {
        Topic topic = new MockTopic("MOCKT");

        MessageConsumer messageConsumer = session.createSharedDurableConsumer(topic, "FOO");
        assertNotNull(messageConsumer);
        assertEquals(TopicSubscriberAdaptor.class, messageConsumer.getClass());
    }
    
    
    @Test
    public void createSharedDurableConsumerWithSelector() throws JMSException {
        Topic topic = new MockTopic("MOCKT");

        MessageConsumer messageConsumer = session.createSharedDurableConsumer(topic, "FOO", "SELECTOR");
        assertNotNull(messageConsumer);
        assertEquals(TopicSubscriberAdaptor.class, messageConsumer.getClass());
        assertEquals("SELECTOR", messageConsumer.getMessageSelector());
    }


}
