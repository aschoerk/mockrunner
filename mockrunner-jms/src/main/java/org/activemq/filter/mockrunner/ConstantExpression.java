/*

  Copyright 2004 Protique Ltd
  Copyright 2004 Hiram Chirino

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.

 */
package org.activemq.filter.mockrunner;

import java.math.BigDecimal;

import jakarta.jms.JMSException;
import jakarta.jms.Message;

/**
 * Alwin Ibba: Changed package
 * 
 * Represents a constant expression
 * 
 * @version $Revision: 1.3 $
 */
public class ConstantExpression implements Expression {

    static class BooleanConstantExpression extends ConstantExpression implements BooleanExpression {
        public BooleanConstantExpression(Object value) {
            super(value);
        }
    }

    public static final BooleanConstantExpression NULL = new BooleanConstantExpression(null);
    public static final BooleanConstantExpression TRUE = new BooleanConstantExpression(Boolean.TRUE);
    public static final BooleanConstantExpression FALSE = new BooleanConstantExpression(Boolean.FALSE);

    private Object value;

    public static ConstantExpression createFromDecimal(String text) {
    	    	
    	// Strip off the 'l' or 'L' if needed.
    	if( text.endsWith("l") || text.endsWith("L") )
    		text = text.substring(0, text.length()-1);

    	Number value;
    	try {
    		value = new Long(text);
    	} catch ( NumberFormatException e) {
    		// The number may be too big to fit in a long.
        	value = new BigDecimal(text);    		
    	}
    	
        long l = value.longValue();
        if (Integer.MIN_VALUE <= l && l <= Integer.MAX_VALUE) {
            value = value.intValue();
        }
        return new ConstantExpression(value);
    }

    public static ConstantExpression createFromHex(String text) {
        Number value = Long.parseLong(text.substring(2), 16);
        long l = value.longValue();
        if (Integer.MIN_VALUE <= l && l <= Integer.MAX_VALUE) {
            value = value.intValue();
        }
        return new ConstantExpression(value);
    }

    public static ConstantExpression createFromOctal(String text) {
        Number value = Long.parseLong(text, 8);
        long l = value.longValue();
        if (Integer.MIN_VALUE <= l && l <= Integer.MAX_VALUE) {
            value = value.intValue();
        }
        return new ConstantExpression(value);
    }

    public static ConstantExpression createFloat(String text) {
        Number value = new Double(text);
        return new ConstantExpression(value);
    }

    public ConstantExpression(Object value) {
        this.value = value;
    }

    public Object evaluate(Message message) throws JMSException {
        return value;
    }

    public Object getValue() {
        return value;
    }    

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? "TRUE" : "FALSE";
        }
        if (value instanceof String) {
            return encodeString((String) value);
        }
        return value.toString();
    }

    /**
     * TODO: more efficient hashCode()
     *
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * TODO: more efficient hashCode()
     * @param o object to compare
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {

        if (o == null || !this.getClass().equals(o.getClass())) {
            return false;
        }
        return toString().equals(o.toString());

    }


    /**
     * Encodes the value of string so that it looks like it would look like
     * when it was provided in a selector.
     *
     * @param s the string to encode
     * @return the encoded string
     */
    public static String encodeString(String s) {
        StringBuilder b = new StringBuilder();
        b.append('\'');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\'') {
                b.append(c);
            }
            b.append(c);
        }
        b.append('\'');
        return b.toString();
    }
}