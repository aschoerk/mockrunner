package com.mockrunner.mock.jms.jms2_compat;

import java.util.Objects;

import jakarta.jms.Message;
import jakarta.jms.MessageListener;

public class Jms2MessageListener implements MessageListener {

    private final MessageListener messageListener;

    public Jms2MessageListener(MessageListener messageListener) {
        this.messageListener = Objects.requireNonNull(messageListener);
    }

    @Override
    public void onMessage(Message message) {
        this.messageListener.onMessage(message);
    }

}
