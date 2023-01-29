package com.mockrunner.mock.jms.jms2_compat;

import java.io.Serializable;
import java.util.Map;

import jakarta.jms.BytesMessage;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import jakarta.jms.StreamMessage;
import jakarta.jms.TextMessage;

public class Jms2MessageFactory {

    static MapMessage createMapMessage(final Session session, final Map<String, Object> map) {
        return Jms2Util.execute(new Callback<MapMessage>() {

            @Override
            public MapMessage execute() throws JMSException {
                MapMessage message = session.createMapMessage();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    message.setObject(entry.getKey(), entry.getValue());
                }
                return message;
            }
        });

    }

    private Jms2MessageFactory() {
    }

    static BytesMessage createBytesMessage(final Session session) {
        return createBytesMessage(session, null);
    }

    static BytesMessage createBytesMessage(final Session session, final byte[] data) {
        return Jms2Util.execute(new Callback<BytesMessage>() {

            @Override
            public BytesMessage execute() throws JMSException {
                BytesMessage message = session.createBytesMessage();
                if (ObjectsUtil.nonNull(data) && data.length > 0) {
                    message.writeBytes(data);
                }

                return message;
            }
        });
    }

    static TextMessage createTextMessage(Session session) {
        return createTextMessage(session, null);
    }

    static TextMessage createTextMessage(final Session session, final String data) {
        return Jms2Util.execute(new Callback<TextMessage>() {

            @Override
            public TextMessage execute() throws JMSException {
                TextMessage message = session.createTextMessage();
                if (ObjectsUtil.nonNull(data)) {
                    message.setText(data);
                }

                return message;
            }
        });
    }

    static MapMessage createMapMessage(final Session session) {
        return Jms2Util.execute(new Callback<MapMessage>() {

            @Override
            public MapMessage execute() throws JMSException {
                
                return session.createMapMessage();
            }

        });
    }

    static Message createMessage(final Session session) {
        return Jms2Util.execute(new Callback<Message>() {

            @Override
            public Message execute() throws JMSException {
                return session.createMessage();
            }

        });
    }

    static ObjectMessage createObjectMessage(final Session session) {
        return createObjectMessage(session, null);
    }

    static ObjectMessage createObjectMessage(final Session session, final Serializable object) {

        return Jms2Util.execute(new Callback<ObjectMessage>() {

            @Override
            public ObjectMessage execute() throws JMSException {
                ObjectMessage message = session.createObjectMessage();
                if (ObjectsUtil.nonNull(object)) {
                    message.setObject(object);
                }
               return message;
            }

        });
    }

    static StreamMessage createStreamMessage(final Session session) {
        return Jms2Util.execute(new Callback<StreamMessage>() {

            @Override
            public StreamMessage execute() throws JMSException {
                return session.createStreamMessage();
            }

        });
    }


}
