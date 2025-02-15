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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import jakarta.jms.JMSException;
import jakarta.jms.Message;

/**
 * Alwin Ibba: Changed package
 * Alwin Ibba: Removed create XPath methods
 * 
 * An expression which performs an operation on two expression values
 * 
 * @version $Revision: 1.3 $
 */
public abstract class UnaryExpression implements Expression {

    private static final BigDecimal BD_LONG_MIN_VALUE = BigDecimal.valueOf(Long.MIN_VALUE);
    protected Expression right;

    public static Expression createNegate(Expression left) {
        return new UnaryExpression(left) {
            public Object evaluate(Message message) throws JMSException {
                Object rvalue = right.evaluate(message);
                if (rvalue == null) {
                    return null;
                }
                if (rvalue instanceof Number) {
                    return negate((Number) rvalue);
                }
                return null;
            }

            public String getExpressionSymbol() {
                return "-";
            }
        };
    }

    public static BooleanExpression createInExpression(PropertyExpression right, List elements, final boolean not) {
    	
    	// Use a HashSet if there are many elements.
    	Collection t;
		if( elements.size()==0 )
    		t=null;
    	else if( elements.size() < 5 )
    		t = elements;
    	else {
    		t = new HashSet(elements);
    	}
    	final Collection inList = t;
    	
        return new BooleanUnaryExpression(right) {
            public Object evaluate(Message message) throws JMSException {
            	
                Object rvalue = this.right.evaluate(message);
                if (rvalue == null) {
                    return null;
                }
                if( rvalue.getClass()!=String.class )
                	return null;
                
                if( (inList!=null && inList.contains(rvalue)) ^ not ) {
                	return Boolean.TRUE;
                } else {
                	return Boolean.FALSE;                	
                }
                
            }

            public String toString() {
            	StringBuilder answer = new StringBuilder();
            	answer.append(this.right);
            	answer.append(" ");
            	answer.append(getExpressionSymbol());
            	answer.append(" ( ");

            	int count=0;
                for (Object o : inList) {
                    if (count != 0) {
                        answer.append(", ");
                    }
                    answer.append(o);
                    count++;
                }
            	
            	answer.append(" )");				
                return answer.toString();
			}
			
            public String getExpressionSymbol() {
            	if( not )
            		return "NOT IN";
            	else 
            		return "IN";
            }
        };
    }

    abstract static class BooleanUnaryExpression extends UnaryExpression implements BooleanExpression {
        public BooleanUnaryExpression(Expression left) {        	
            super(left);
        }
    }


    public static BooleanExpression createNOT(BooleanExpression left) {
        return new BooleanUnaryExpression(left) {
            public Object evaluate(Message message) throws JMSException {
                Boolean lvalue = (Boolean) right.evaluate(message);
                if (lvalue == null) {
                    return null;
                }
                return lvalue ? Boolean.FALSE : Boolean.TRUE;
            }

            public String getExpressionSymbol() {
                return "NOT";
            }
        };
    }

    public static BooleanExpression createBooleanCast(Expression left) {
        return new BooleanUnaryExpression(left) {
            public Object evaluate(Message message) throws JMSException {
                Object lvalue = right.evaluate(message);
                if (lvalue == null) 
                    return null;
                if (!lvalue.getClass().equals(Boolean.class)) 
                    return Boolean.FALSE;
                               
                return lvalue;
            }

            public String getExpressionSymbol() {
                return "NOT";
            }
        };
    }

    private static Number negate(Number left) {
    	Class clazz = left.getClass();
        if (clazz == Integer.class) {
            return -left.intValue();
        }
        else if (clazz == Long.class) {
            return -left.longValue();
        }
        else if (clazz ==  Float.class) {
            return -left.floatValue();
        }
        else if (clazz == Double.class) {
            return -left.doubleValue();
        }
        else if (clazz == BigDecimal.class) {
        	// We ussually get a big deciamal when we have Long.MIN_VALUE constant in the 
        	// Selector.  Long.MIN_VALUE is too big to store in a Long as a positive so we store it 
        	// as a Big decimal.  But it gets Negated right away.. to here we try to covert it back
        	// to a Long.        	
        	BigDecimal bd = (BigDecimal)left;
        	bd = bd.negate();
        	
        	if( BD_LONG_MIN_VALUE.compareTo(bd)==0  ) {
        		return Long.MIN_VALUE;
        	}
            return bd;
        }
        else {
            throw new RuntimeException("Don't know how to negate: "+left);
        }
    }

    public UnaryExpression(Expression left) {
        this.right = left;
    }

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression expression) {
        right = expression;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "(" + getExpressionSymbol() + " " + right.toString() + ")";
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
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {

        if (o == null || !this.getClass().equals(o.getClass())) {
            return false;
        }
        return toString().equals(o.toString());

    }

    /**
     * Returns the symbol that represents this binary expression.  For example, addition is
     * represented by "+"
     *
     * @return a string containing the symbol
     */
    abstract public String getExpressionSymbol();

}
