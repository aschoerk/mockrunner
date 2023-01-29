/*

  Copyright 2004 Protique Ltd

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
 * Represents a filter using an expression
 *
 * @version $Revision: 1.3 $
 */
public class ExpressionFilter implements Filter {

    private Expression expression;

    public ExpressionFilter(Expression expression) {
        this.expression = expression;
    }

    public boolean matches(Message message) throws JMSException {
        Object value = expression.evaluate(message);
        if (value != null && value instanceof Boolean) {
            return (Boolean) value;
        }
        return false;
    }

    public boolean isWildcard() {
        return false;
    }

    /**
     * @return Returns the expression.
     */
    public Expression getExpression() {
        return expression;
    }
    
    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Filter: "+expression;
	}
}
