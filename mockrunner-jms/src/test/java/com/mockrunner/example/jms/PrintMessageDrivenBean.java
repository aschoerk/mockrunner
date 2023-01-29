package com.mockrunner.example.jms;

import jakarta.ejb.EJBException;
import jakarta.ejb.MessageDrivenBean;
import jakarta.ejb.MessageDrivenContext;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

/*
 * @ejb:bean name="test/TestMessage"
 *           transaction-type="Container"
 *           acknowledge-mode="Auto-acknowledge"
 *           destination-type="javax.jms.Queue"
 *           subscription-durability="NonDurable"
 * 
 * @ejb:transaction type="Required"
 * @jboss:destination-jndi-name name="queue/testQueue"
 */
/**
 * Receiver for {@link PrintSessionBean}.
 */
public class PrintMessageDrivenBean implements MessageDrivenBean, MessageListener
{
    private MessageDrivenContext context;
    
    public void onMessage(Message message)
    {
        if(message instanceof TextMessage)
        {
            //do print
        }
    }
   
    public void setMessageDrivenContext(MessageDrivenContext context) throws EJBException
    {
        this.context = context;
    }
    
    public void ejbCreate()
    {
    
    }
   
    public void ejbRemove()
    {
    
    }
}
