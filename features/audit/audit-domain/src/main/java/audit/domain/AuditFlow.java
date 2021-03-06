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
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.SortableField;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;

import javax.enterprise.inject.Vetoed;
import javax.persistence.Access;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.AccessType.FIELD;
import static javax.persistence.FetchType.EAGER;

@Vetoed
@Entity
@Table(name = "AUDIT_FLOW")
@Access(FIELD)
@Indexed
public class AuditFlow extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1;

    private static final ObjectStreamField[] serialPersistentFields = {new ObjectStreamField("error", String.class)};

    @Column(name = "ERROR", updatable = false)
    @Size(min = 1, max = 255)
    @Fields({
            @Field(store = Store.YES, termVector = TermVector.YES),
            @Field(name = "sortError", analyze = Analyze.NO, index = Index.NO)
    })
    @SortableField(forField = "sortError")
    private String error;

    @OneToMany(fetch = EAGER, orphanRemoval = true)
    @JoinColumn(name = "FK_AUDIT_FLOW", nullable = false, updatable = false)
    @ForeignKey(name = "FK_AUDIT_FLOW__HEADER")
    private List<AuditHeader> headers;

    @OneToMany(fetch = EAGER, orphanRemoval = true)
    @JoinColumn(name = "FK_AUDIT_FLOW", nullable = false, updatable = false)
    @ForeignKey(name = "FK_AUDIT_FLOW__CHANGE")
    private List<AuditChange> changes;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<AuditHeader> getHeaders() {
        if (headers == null) {
            headers = new ArrayList<>();
        }
        return headers;
    }

    public List<AuditChange> getChanges() {
        if (changes == null) {
            changes = new ArrayList<>();
        }
        return changes;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(getHeaders().size());
        for (AuditHeader header : headers) {
            stream.writeObject(header);
        }
        stream.writeInt(getChanges().size());
        for (AuditChange change : changes) {
            stream.writeObject(change);
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();

        int headersCount = stream.readInt();
        headers = new ArrayList<>(headersCount);
        while (headersCount-- > 0) {
            headers.add((AuditHeader) stream.readObject());
        }

        int changesCount = stream.readInt();
        changes = new ArrayList<>(changesCount);
        while (changesCount-- > 0) {
            changes.add((AuditChange) stream.readObject());
        }
    }
}
