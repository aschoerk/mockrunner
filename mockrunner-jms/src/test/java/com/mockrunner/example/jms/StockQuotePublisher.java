package com.mockrunner.example.jms;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;
import jakarta.jms.TopicConnection;
import jakarta.jms.TopicConnectionFactory;
import jakarta.jms.TopicPublisher;
import jakarta.jms.TopicSession;
import javax.naming.InitialContext;

/**
 * This example class sends multiple messages
 * to a topic within a transaction. The first message
 * is a <code>TextMessage</code> with the current
 * timestamp. The following two messages refer to
 * the first message with this timestamp as
 * correlation id. They contain the market rates for
 * some stocks in the form of maps (the company name
 * maps to the market rate).
 */
public class StockQuotePublisher
{
    private Map nasdaqRates = new HashMap();
    private Map dowRates = new HashMap();
    
    public void setQuotes(Map nasdaqRates, Map dowRates)
    {
        this.nasdaqRates.clear();
        this.dowRates.clear();
        this.nasdaqRates.putAll(nasdaqRates);
        this.dowRates.putAll(dowRates);
    }
    
    public void send()
    {
        TopicConnection topicConnection = null;
        TopicSession topicSession = null;
        TopicPublisher topicPublisher = null;
        try
        {   
            InitialContext initialContext = new InitialContext();
            TopicConnectionFactory topicFactory = (TopicConnectionFactory)initialContext.lookup("java:/ConnectionFactory");
            topicConnection = topicFactory.createTopicConnection();
            topicSession = topicConnection.createTopicSession(true, Session.AUTO_ACKNOWLEDGE);
            Topic topic = (Topic)initialContext.lookup("topic/quoteTopic");
            TextMessage timeMessage = createInitialTimestampMessage(topicSession);
            MapMessage nasdaqMessage = createStockQuoteMessage(topicSession, nasdaqRates);
            MapMessage dowMessage = createStockQuoteMessage(topicSession, dowRates);
            nasdaqMessage.setJMSCorrelationID(timeMessage.getText());
            dowMessage.setJMSCorrelationID(timeMessage.getText());
            topicPublisher = topicSession.createPublisher(topic);
            topicPublisher.publish(timeMessage);
            topicPublisher.publish(nasdaqMessage);
            topicPublisher.publish(dowMessage);
            topicSession.commit();
        }
        catch(Exception exc)
        {
            try
            {
                if(null != topicSession) topicSession.rollback();
            }
            catch(JMSException jmsExc)
            {
                jmsExc.printStackTrace();
            }
        }
        finally
        {
            try
            {
                if(null != topicPublisher) topicPublisher.close();
                if(null != topicSession) topicSession.close();
                if(null != topicConnection) topicConnection.close();
            }
            catch(JMSException exc)
            {
                exc.printStackTrace();
            }
        }
    }
    
    private TextMessage createInitialTimestampMessage(TopicSession topicSession) throws JMSException
    {
        TextMessage message = topicSession.createTextMessage();
        message.setText(String.valueOf(System.currentTimeMillis()));
        return message;
    }

    private MapMessage createStockQuoteMessage(TopicSession topicSession, Map rates) throws JMSException
    {
        MapMessage message = topicSession.createMapMessage();
        for (Object o : rates.keySet()) {
            String nextKey = (String) o;
            message.setString(nextKey, (String) rates.get(nextKey));
        }
        return message;
    }
}
