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
package audit.query.search.api;

import javax.validation.constraints.Min;

public interface Paging {
    default int getStartRowNum() {
        return 0;
    }

    Paging setStartRowNum(@Min(0) int startRowNum);

    default @Min(1) int getPageSize() {
        return 0;
    }

    Paging setPageSize(@Min(1) int pageSize);
}
