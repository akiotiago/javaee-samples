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
package audit.domain;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.CalendarBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.SortableField;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;

import javax.enterprise.inject.Vetoed;
import javax.persistence.Access;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static audit.util.Dates.format;
import static java.nio.ByteBuffer.allocate;
import static java.time.ZoneOffset.UTC;
import static java.util.TimeZone.getTimeZone;
import static javax.persistence.AccessType.FIELD;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.TemporalType.TIMESTAMP;
import static org.hibernate.search.annotations.Resolution.SECOND;

@Vetoed
@Entity
@Table(name = "AUDIT")
@Access(FIELD)
@Indexed
@NamedQueries({
        @NamedQuery(name = "Audit.all", query = "select a from Audit a"),
        @NamedQuery(name = "Audit.count", query = "select count(a) from Audit a")
})
public class Audit extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1;

    private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("initiator", Long.TYPE),
            new ObjectStreamField("module", String.class),
            new ObjectStreamField("description", String.class),
            new ObjectStreamField("operationKey", String.class)
    };

    public Audit() {
        this(Calendar.getInstance(getTimeZone(UTC)));
    }

    public Audit(Calendar storedAt) {
        this.storedAt = storedAt;
    }

    /**
     * @serial request UUID derived from initial startup time of the application server (mostSigBits)
     * in milli seconds, and atomic counter (leastSigBits)
     */
    @Column(name = "REQUEST_UUID", columnDefinition = "varchar(36)", nullable = false, updatable = false)
    //@Convert(converter = UuidConverter.class)
    @NotNull
    @Basic
    private UUID request;

    /**
     * @serialField initiator long person-id
     */
    @Column(name = "INITIATOR", precision = 19, nullable = false, updatable = false)
    @Fields({
            @Field(store = Store.YES, termVector = TermVector.YES),
            @Field(name = "sortInitiator", analyze = Analyze.NO, index = Index.NO)
    })
    @SortableField(forField = "sortInitiator")
    private long initiator;

    /**
     * @serialField module java.langString project-module
     */
    @Column(name = "MODULE", length = 31, nullable = false, updatable = false)
    @NotNull
    @Size(min = 1, max = 31)
    @Fields({
            @Field(store = Store.YES, termVector = TermVector.YES),
            @Field(name = "sortModule", analyze = Analyze.NO, index = Index.NO)
    })
    @SortableField(forField = "sortModule")
    private String module;

    @Column(name = "STORED")
    @Temporal(TIMESTAMP)
    @Field(store = Store.YES, termVector = TermVector.YES)
    @SortableField
    @CalendarBridge(resolution = SECOND)
    private Calendar storedAt;

    /**
     * @serialField description java.langString audit-description
     */
    @Column(name = "DESCRIPTION", updatable = false)
    @Size(max = 255)
    @Fields({
            @Field(store = Store.YES, termVector = TermVector.YES),
            @Field(name = "sortDescription", analyze = Analyze.NO, index = Index.NO)
    })
    @SortableField(forField = "sortDescription")
    private String description;

    /**
     * @serialField operationKey java.langString audit-operationKey
     */
    @Column(name = "OPERATION_KEY", nullable = false, updatable = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Field(store = Store.YES, termVector = TermVector.YES)
    private String operationKey;

    /**
     * @serial flows - List<AuditFlow>
     */
    @OneToMany(fetch = EAGER, orphanRemoval = true)
    @JoinColumn(name = "FK_AUDIT", nullable = false, updatable = false)
    @ForeignKey(name = "FK_AUDIT__FLOW")
    @Size(min = 1)
    @NotNull
    private List<AuditFlow> flows;

    public UUID getRequest() {
        return request;
    }

    public void setRequest(UUID request) {
        this.request = request;
    }

    public long getInitiator() {
        return initiator;
    }

    public void setInitiator(long initiator) {
        this.initiator = initiator;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Calendar getStoredAt() {
        return storedAt;
    }

    public void setStoredAt(Calendar storedAt) {
        this.storedAt = storedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOperationKey() {
        return operationKey;
    }

    public void setOperationKey(String operationKey) {
        this.operationKey = operationKey;
    }

    public List<AuditFlow> getFlows() {
        if (flows == null) {
            flows = new ArrayList<>();
        }
        return flows;
    }

    /**
     * @serialData serializing (module and initiator by default), request and flows
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        if (request != null) {
            stream.writeBoolean(true);
            stream.write(allocate(16)
                    .putLong(request.getMostSignificantBits())
                    .putLong(request.getLeastSignificantBits())
                    .array());
        } else {
            stream.writeBoolean(false);
        }
        stream.writeInt(getFlows().size());
        for (AuditFlow flow : flows) {
            stream.writeObject(flow);
        }
        stream.writeObject(storedAt == null ? null : format(storedAt));
    }

    /**
     * @serialData deserializing (module and initiator by default), request, flows
     */
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        request = stream.readBoolean() ? new UUID(stream.readLong(), stream.readLong()) : null;
        int flowsCount = stream.readInt();
        flows = new ArrayList<>(flowsCount);
        while (flowsCount-- > 0) {
            flows.add((AuditFlow)
                    stream.readObject());
        }
        String storedAtAsString = (String) stream.readObject();
        storedAt = storedAtAsString == null ? null : format(storedAtAsString);
    }
}
