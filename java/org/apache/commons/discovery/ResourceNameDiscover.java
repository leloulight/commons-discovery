/*
 * Copyright 1999-2005 The Apache Software Foundation
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
package org.apache.commons.discovery;


/**
 * Interface representing a mapping
 * from a set of source resource names
 * to a resultant set of resource names.
 * 
 * @author Richard A. Sitze
 * @author Costin Manolache
 */
public interface ResourceNameDiscover
{
    public void setListener(ResourceNameListener listener);

    /**
     * Find resource names.
     * Listener is notified of each resource name found.
     * 
     * @return FALSE if listener terminates discovery prematurely by
     *         returning false, otherwise TRUE.
     */
    public boolean find(String resourceName);
}
