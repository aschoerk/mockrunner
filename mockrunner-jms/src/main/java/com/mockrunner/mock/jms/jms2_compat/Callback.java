package com.mockrunner.mock.jms.jms2_compat;

import jakarta.jms.JMSException;

//Functional interface for unchecking JMSExceptions
public interface Callback<T> {

    T execute() throws JMSException;

}
