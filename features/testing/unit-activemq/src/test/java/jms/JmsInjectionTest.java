/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package jms;

import javaee.samples.frameworks.injection.InjectionRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;
import javax.ejb.MessageDriven;
import javax.enterprise.inject.Vetoed;
import javax.jms.*;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@Vetoed
@RunWith(InjectionRunner.class)
@MessageDriven
public class JmsInjectionTest implements MessageListener {

    private final CyclicBarrier synchronizer = new CyclicBarrier(2);

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "java:jms/queue/test")
    private Queue producerQueue;

    @Test
    public void test() throws Exception {
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(producerQueue);
        producer.send(session.createTextMessage("Test Message"));
        synchronizer.await(3, SECONDS);
    }

    @Override
    public void onMessage(Message message) {
        try {
            synchronizer.await(3, SECONDS);

            assertThat(message)
                    .isInstanceOf(TextMessage.class);

            assertThat(((TextMessage) message).getText())
                    .isEqualTo("Test Message");

        } catch (JMSException | TimeoutException | BrokenBarrierException | InterruptedException e) {
            fail("Too overloaded build system. Could not acquire permit within 3 seconds. " + e.getLocalizedMessage(), e);
        }
    }
}
