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
package javaee.samples.frameworks.junitjparule;

import java.util.ArrayList;
import java.util.Collection;

final class PathFinder {
    private final Collection<String> path = new ArrayList<>();

    static PathFinder path(Class<?> beanType) {
        PathFinder pathFinder = new PathFinder();
        pathFinder.path.add(beanType.getName());
        return pathFinder;
    }

    static PathFinder path(PathFinder origin, Class<?> beanType) {
        PathFinder pathFinder = new PathFinder();
        pathFinder.path.addAll(origin.path);
        pathFinder.path.add(beanType.getName());
        return pathFinder;
    }

    @Override
    public String toString() {
        String separator = "";
        String humanReadablePath = "";
        for (String path : this.path) {
            if (humanReadablePath.isEmpty()) {
                humanReadablePath = ": ";
            } else {
                humanReadablePath += "\n";
                humanReadablePath += separator;
                humanReadablePath += "< ";
            }
            humanReadablePath += path;
            separator += "  ";
        }
        return humanReadablePath;
    }
}
