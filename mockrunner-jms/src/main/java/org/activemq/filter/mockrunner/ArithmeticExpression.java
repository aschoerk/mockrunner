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

import jakarta.jms.JMSException;
import jakarta.jms.Message;

/**
 * Alwin Ibba: Changed package
 * 
 * An expression which performs an operation on two expression values
 * 
 * @version $Revision: 1.3 $
 */
public abstract class ArithmeticExpression extends BinaryExpression {

    protected static final int INTEGER = 1;
    protected static final int LONG = 2;
    protected static final int DOUBLE = 3;

    /**
     * @param left the left expression
     * @param right the expression on the right
     */
    public ArithmeticExpression(Expression left, Expression right) {
        super(left, right);
    }

    public static Expression createPlus(Expression left, Expression right) {
        return new ArithmeticExpression(left, right) {
            protected Object evaluate(Object lvalue, Object rvalue) {
                if (lvalue instanceof String) {
                    String text = (String) lvalue;
                    String answer = text + rvalue;
                    System.out.println("lvalue: " + lvalue + " rvalue: " + rvalue + " result: " + answer);
                    return answer;
                }
                else if (lvalue instanceof Number) {
                    return plus((Number) lvalue, asNumber(rvalue));
                }
                throw new RuntimeException("Cannot call plus operation on: " + lvalue + " and: " + rvalue);
            }

            public String getExpressionSymbol() {
                return "+";
            }
        };
    }

    public static Expression createMinus(Expression left, Expression right) {
        return new ArithmeticExpression(left, right) {
            protected Object evaluate(Object lvalue, Object rvalue) {
                if (lvalue instanceof Number) {
                    return minus((Number) lvalue, asNumber(rvalue));
                }
                throw new RuntimeException("Cannot call minus operation on: " + lvalue + " and: " + rvalue);
            }

            public String getExpressionSymbol() {
                return "-";
            }
        };
    }

    public static Expression createMultiply(Expression left, Expression right) {
        return new ArithmeticExpression(left, right) {

            protected Object evaluate(Object lvalue, Object rvalue) {
                if (lvalue instanceof Number) {
                    return multiply((Number) lvalue, asNumber(rvalue));
                }
                throw new RuntimeException("Cannot call multiply operation on: " + lvalue + " and: " + rvalue);
            }

            public String getExpressionSymbol() {
                return "*";
            }
        };
    }

    public static Expression createDivide(Expression left, Expression right) {
        return new ArithmeticExpression(left, right) {

            protected Object evaluate(Object lvalue, Object rvalue) {
                if (lvalue instanceof Number) {
                    return divide((Number) lvalue, asNumber(rvalue));
                }
                throw new RuntimeException("Cannot call divide operation on: " + lvalue + " and: " + rvalue);
            }

            public String getExpressionSymbol() {
                return "/";
            }
        };
    }

    public static Expression createMod(Expression left, Expression right) {
        return new ArithmeticExpression(left, right) {

            protected Object evaluate(Object lvalue, Object rvalue) {
                if (lvalue instanceof Number) {
                    return mod((Number) lvalue, asNumber(rvalue));
                }
                throw new RuntimeException("Cannot call mod operation on: " + lvalue + " and: " + rvalue);
            }

            public String getExpressionSymbol() {
                return "%";
            }
        };
    }

    protected Number plus(Number left, Number right) {
        switch (numberType(left, right)) {
            case INTEGER:
                return left.intValue() + right.intValue();
            case LONG:
                return left.longValue() + right.longValue();
            default:
                return left.doubleValue() + right.doubleValue();
        }
    }

    protected Number minus(Number left, Number right) {
        switch (numberType(left, right)) {
            case INTEGER:
                return left.intValue() - right.intValue();
            case LONG:
                return left.longValue() - right.longValue();
            default:
                return left.doubleValue() - right.doubleValue();
        }
    }

    protected Number multiply(Number left, Number right) {
        switch (numberType(left, right)) {
            case INTEGER:
                return left.intValue() * right.intValue();
            case LONG:
                return left.longValue() * right.longValue();
            default:
                return left.doubleValue() * right.doubleValue();
        }
    }

    protected Number divide(Number left, Number right) {
        return left.doubleValue() / right.doubleValue();
    }

    protected Number mod(Number left, Number right) {
        return left.doubleValue() % right.doubleValue();
    }

    private int numberType(Number left, Number right) {
        if (isDouble(left) || isDouble(right)) {
            return DOUBLE;
        }
        else if (left instanceof Long || right instanceof Long) {
            return LONG;
        }
        else {
            return INTEGER;
        }
    }

    private boolean isDouble(Number n) {
        return n instanceof Float || n instanceof Double;
    }

    protected Number asNumber(Object value) {
        if (value instanceof Number) {
            return (Number) value;
        }
        else {
            throw new RuntimeException("Cannot convert value: " + value + " into a number");
        }
    }

    public Object evaluate(Message message) throws JMSException {
        Object lvalue = left.evaluate(message);
        if (lvalue == null) {
            return null;
        }
        Object rvalue = right.evaluate(message);
        if (rvalue == null) {
            return null;
        }
        return evaluate(lvalue, rvalue);
    }


    /**
     * @param lvalue left value
     * @param rvalue right value
     * @return the evaluated object
     */
    abstract protected Object evaluate(Object lvalue, Object rvalue);

}
