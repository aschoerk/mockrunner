package com.mockrunner.mock.jms.jms2_compat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mockrunner.mock.jms.JMSMockObjectFactory;
import com.mockrunner.mock.jms.MockConnection;
import com.mockrunner.mock.jms.MockConnectionFactory;
import com.mockrunner.mock.jms.jms2_compat.*;

import jakarta.jms.Connection;
import jakarta.jms.JMSContext;

public class Jms2ConnectionFactoryTest {

    MockConnectionFactory mockConnectionFactory;

    JMSMockObjectFactory mockObjectFactory = new JMSMockObjectFactory();

    public Jms2ConnectionFactoryTest() {
    }

    @Before
    public void setUp() {
        mockConnectionFactory = mockObjectFactory.createMockConnectionFactory();
    }

    @After
    public void tearDown() {
        mockConnectionFactory = null;

    }

    @Test
    public void testCreateConnection() throws Exception {
        Connection conn = mockConnectionFactory.createConnection();
        assertNotNull(conn);
        assertEquals(MockConnection.class, conn.getClass());
    }

    @Test
    public void testCreateConnectionwithCredentials() throws Exception {
        final String username = "top";
        final String password = "secret";


        Connection conn = mockConnectionFactory.createConnection(username, password);
        assertNotNull(conn);
        assertEquals(MockConnection.class, conn.getClass());


    }

    @Test
    public void testCreateContext() throws Exception {

        JMSContext context = mockConnectionFactory.createContext();
        assertNotNull(context);


    }

    @Test
    public void testCreateContextWithCredentials() throws Exception {

        final String username = "top";
        final String password = "secret";

        JMSContext context = mockConnectionFactory.createContext(username,password);
        assertNotNull(context);
    }

    @Test
    public void testCreateContextCredentialsAndTransactedSession() throws Exception {
        final String username = "top";
        final String password = "secret";

        JMSContext context = mockConnectionFactory.createContext(username, password, JMSContext.SESSION_TRANSACTED);
        assertNotNull(context);

    }

    @Test
    public void testCreateContextClientAckSession() throws Exception {


        JMSContext context = mockConnectionFactory.createContext(JMSContext.CLIENT_ACKNOWLEDGE);
        assertNotNull(context);
        assertEquals(Jms2Context.class, context.getClass());

    }

    @Test
    public void testCreateTransactedSession() throws Exception {

        JMSContext context = mockConnectionFactory.createContext(JMSContext.SESSION_TRANSACTED);
        assertNotNull(context);
        assertEquals(Jms2Context.class, context.getClass());

    }

    @Test
    public void testCreateContextCredentialsAndClientAckSession() throws Exception {
        final String username = "top";
        final String password = "secret";

        JMSContext context = mockConnectionFactory.createContext(username, password, JMSContext.CLIENT_ACKNOWLEDGE);
        assertNotNull(context);
        assertEquals(Jms2Context.class, context.getClass());

    }

    @Test
    public void createContext() throws Exception {


        JMSContext context = mockConnectionFactory.createContext();
        assertNotNull(context);
        assertEquals(Jms2Context.class, context.getClass());


    }

}
