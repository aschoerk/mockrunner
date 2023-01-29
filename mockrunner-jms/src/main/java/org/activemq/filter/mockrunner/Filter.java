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
 * Represents a subscription filter
 *
 * @version $Revision: 1.3 $
 */
public interface Filter {

    /**
     * @param message the message to match
     * @return true if this filter matches the given JMS message
     * @throws JMSException is the message cannot be macthed
     */
    boolean matches(Message message) throws JMSException;

    /**
     * @return return true if this filter is a wildcard filter
     *         and so can match multiple destinations
     */
    boolean isWildcard();

}
