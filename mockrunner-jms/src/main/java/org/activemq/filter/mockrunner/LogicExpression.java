/*

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

import jakarta.jms.JMSException;
import jakarta.jms.Message;

/**
 * Alwin Ibba: Changed package
 * 
 * A filter performing a comparison of two objects
 * 
 * @version $Revision: 1.3 $
 */
public abstract class LogicExpression extends BinaryExpression implements BooleanExpression {

    public static BooleanExpression createOR(BooleanExpression lvalue, BooleanExpression rvalue) {
        return new LogicExpression(lvalue, rvalue) {
        	
            public Object evaluate(Message message) throws JMSException {
                
            	Boolean lv = (Boolean) left.evaluate(message);
                // Can we do an OR shortcut??
            	if (lv !=null && lv) {
                    return Boolean.TRUE;
                }
            	
                Boolean rv = (Boolean) right.evaluate(message);
                return rv==null ? null : rv;
            }

            public String getExpressionSymbol() {
                return "OR";
            }
        };
    }

    public static BooleanExpression createAND(BooleanExpression lvalue, BooleanExpression rvalue) {
        return new LogicExpression(lvalue, rvalue) {
            
        	public Object evaluate(Message message) throws JMSException {
                
            	Boolean lv = (Boolean) left.evaluate(message);
            	
                // Can we do an AND shortcut??
            	if( lv==null )
            		return null;
            	if (!lv) {
                    return Boolean.FALSE;
                }
            	
                Boolean rv = (Boolean) right.evaluate(message);
                return rv==null ? null : rv;
            }

            public String getExpressionSymbol() {
                return "AND";
            }
        };
    }

    /**
     * @param left the expression on the left
     * @param right the expression on the right
     */
    public LogicExpression(BooleanExpression left, BooleanExpression right) {
        super(left, right);
    }

    abstract public Object evaluate(Message message) throws JMSException;


}
