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
package audit.jms.unit;

import audit.domain.Audit;
import audit.jms.consumer.AuditListener;
import audit.persistence.service.AuditService;

import javax.enterprise.inject.Typed;
import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;
import java.util.concurrent.CyclicBarrier;

import static java.util.concurrent.TimeUnit.SECONDS;

@Vetoed
@Typed(AuditListener.class)
public class AuditStorageListener implements AuditListener {
    @Inject
    private AuditService service;

    @Inject
    private CyclicBarrier synchronizer;

    @Override
    public void onMessage(Audit audit) {
        try {
            service.saveFlow(audit);
            synchronizer.await(3, SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e.getLocalizedMessage(), e);
        }
    }
}
