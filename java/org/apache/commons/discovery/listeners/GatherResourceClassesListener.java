/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.discovery.listeners;

import java.util.Vector;

import org.apache.commons.discovery.ResourceClass;
import org.apache.commons.discovery.ResourceClassListener;


public class GatherResourceClassesListener implements ResourceClassListener {
    private Vector results = new Vector();  // ResourceClass

    public Vector getResourceClasses() {
        return results;
    }

    public boolean found(ResourceClass resource) {
        results.add(resource);
        return true; // only get first.
    }
}
