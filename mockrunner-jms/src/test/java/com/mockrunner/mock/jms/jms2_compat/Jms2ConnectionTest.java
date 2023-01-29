
package com.mockrunner.mock.jms.jms2_compat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mockrunner.mock.jms.jms2_compat.*;
import com.mockrunner.mock.jms.JMSMockObjectFactory;
import com.mockrunner.mock.jms.MockConnection;
import com.mockrunner.mock.jms.MockServerSessionPool;
import com.mockrunner.mock.jms.MockSession;
import com.mockrunner.mock.jms.MockTopic;

import jakarta.jms.ConnectionConsumer;
import jakarta.jms.ServerSessionPool;
import jakarta.jms.Session;
import jakarta.jms.Topic;


public class Jms2ConnectionTest {

    
    MockConnection mockConnection;
    
    JMSMockObjectFactory mockObjectFactory = new JMSMockObjectFactory();
    
    public Jms2ConnectionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws Exception  {

        mockConnection = (MockConnection) mockObjectFactory.getMockConnectionFactory().createConnection();
    }
    
    @After
    public void tearDown() {
        mockConnection = null;

    }


    @Test
    public void createSessionWithSessionMode() throws Exception {
        mockConnection.createSession(Session.DUPS_OK_ACKNOWLEDGE);
         MockSession session = mockConnection.getSession(0);
         assertEquals(Session.DUPS_OK_ACKNOWLEDGE, session.getAcknowledgeMode());
         assertFalse(session.getTransacted());
         
    }

    @Test
    public void createSessionWithTransactedSessionMode() throws Exception {
         MockSession jms2session = (MockSession) mockConnection.createSession(Session.SESSION_TRANSACTED);
         MockSession session = mockConnection.getSession(0);
         assertEquals(Session.SESSION_TRANSACTED, session.getAcknowledgeMode());
         assertTrue(session.getTransacted());
         
    }

    @Test
    public void createSession() throws Exception {
        MockSession jms2session = (MockSession) mockConnection.createSession();
         MockSession session = mockConnection.getSession(0);
         assertEquals(Session.AUTO_ACKNOWLEDGE, session.getAcknowledgeMode());
         assertFalse(session.getTransacted());
         
    }

    @Test
    public void createSharedConnectionConsumer() throws Exception {
       Topic topic = new MockTopic("MOCK_T");
        
       ServerSessionPool sessionPool = new MockServerSessionPool(mockConnection);
       ConnectionConsumer result = mockConnection.createSharedConnectionConsumer(topic, "MYSUB", "SELECTOR", sessionPool, 100);
       assertNotNull(result);
    }

    @Test
    public void createSharedDurableConnectionConsumer() throws Exception {
       MockTopic topic = new MockTopic("MOCK_T");
        
       ServerSessionPool sessionPool = new MockServerSessionPool(mockConnection);
       ConnectionConsumer result = mockConnection.createSharedDurableConnectionConsumer(topic, "MYSUB", "SELECTOR", sessionPool, 100);
       assertNotNull(result);
       
        assertSame(sessionPool, result.getServerSessionPool());

       
    }
    

    
}
