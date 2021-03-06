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
package javaee.samples.frameworks.injection.entities;

import javaee.samples.frameworks.injection.InjectionRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import javaee.samples.frameworks.injection.tablegenerator.EntityWithTableGenerator;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceProperty;
import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(InjectionRunner.class)
@PersistenceContext(unitName = "table-generator-pu", properties = {
        @PersistenceProperty(name = "hibernate.id.new_generator_mappings", value = "true")
})
public class BothPersistenceDefinitionsWithoutRuleTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Inject
    EntityManager em;

    @Test
    @PersistenceContext(unitName = "containerless-test-pu")
    public void shouldPreferMethodPU$NegativeTest() {
        assertThat(em)
                .isNotNull();

        EntityWithTableGenerator e = new EntityWithTableGenerator();
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Unknown entity: javaee.samples.frameworks.injection.tablegenerator.EntityWithTableGenerator");
        em.persist(e);
    }

    @Test
    @PersistenceContext(unitName = "containerless-test-pu")
    @Transactional
    public void shouldPreferMethodPU() {
        assertThat(em)
                .isNotNull();

        MyEntity e = new MyEntity();
        em.persist(e);
    }

    @Test
    public void shouldPreferClassPU$NegativeTest() {
        assertThat(em)
                .isNotNull();

        MyEntity e = new MyEntity();
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Unknown entity: javaee.samples.frameworks.injection.entities.MyEntity");
        em.persist(e);
    }

    @Test
    @Transactional
    public void shouldPreferClassPU() {
        assertThat(em)
                .isNotNull();

        EntityWithTableGenerator e = new EntityWithTableGenerator();
        em.persist(e);
    }
}
