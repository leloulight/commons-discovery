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
package org.apache.commons.discovery.resource.names;

import org.apache.commons.discovery.ResourceNameDiscover;
import org.apache.commons.discovery.ResourceNameListener;
import org.apache.commons.discovery.log.DiscoveryLogFactory;
import org.apache.commons.discovery.tools.ManagedProperties;
import org.apache.commons.logging.Log;


/**
 * Recover resource name from Managed Properties.
 * @see org.apache.commons.discovery.tools.ManagedProperties
 * 
 * @author Richard A. Sitze
 */
public class DiscoverNamesInManagedProperties extends ResourceNameDiscoverImpl
    implements ResourceNameDiscover, ResourceNameListener
{
    private static Log log = DiscoveryLogFactory.newLog(DiscoverNamesInManagedProperties.class);
    public static void setLog(Log _log) {
        log = _log;
    }
    
    /** Construct a new resource discoverer
     */
    public DiscoverNamesInManagedProperties() {
        super();
    }

    /**
     * @return Enumeration of ResourceInfo
     */
    public boolean find(String resourceName) {
        if (log.isDebugEnabled())
            log.debug("find: resourceName='" + resourceName + "'");

        return notifyListener(ManagedProperties.getProperty(resourceName));
    }
    
    public boolean found(String resourceName) {
        return find(resourceName);
    }
}
