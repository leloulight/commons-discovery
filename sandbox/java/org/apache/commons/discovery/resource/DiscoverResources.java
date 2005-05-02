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
package org.apache.commons.discovery.resource;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.apache.commons.discovery.Resource;
import org.apache.commons.discovery.ResourceDiscover;
import org.apache.commons.discovery.ResourceNameListener;
import org.apache.commons.discovery.jdk.JDKHooks;
import org.apache.commons.discovery.log.DiscoveryLogFactory;
import org.apache.commons.logging.Log;


/**
 * @author Richard A. Sitze
 * @author Craig R. McClanahan
 * @author Costin Manolache
 * @author James Strachan
 */
public class DiscoverResources extends ResourceDiscoverImpl
    implements ResourceDiscover, ResourceNameListener
{
    private static Log log = DiscoveryLogFactory.newLog(DiscoverResources.class);
    public static void setLog(Log _log) {
        log = _log;
    }
    
    /**
     * Construct a new resource discoverer
     */
    public DiscoverResources() {
        super();
    }
    
    /**
     *  Construct a new resource discoverer
     */
    public DiscoverResources(ClassLoaders classLoaders) {
        super(classLoaders);
    }

    /**
     * @return ResourceIterator
     */
    public boolean find(String resourceName) {
        if (log.isDebugEnabled())
            log.debug("find: resourceName='" + resourceName + "'");

        for (int idx = 0; idx < getClassLoaders().size(); idx++) {
            ClassLoader loader = getClassLoaders().get(idx);
            if (log.isDebugEnabled())
                log.debug("find: search using ClassLoader '" + loader + "'");

            try {
                Enumeration resources =
                    JDKHooks.getJDKHooks().getResources(loader, resourceName);

                if (resources != null) {
                    while(resources.hasMoreElements()) {
                        if (!notifyListener(new Resource(resourceName,
                                                         (URL)resources.nextElement(),
                                                         loader))) {
                            return false;
                        }
                    }
                }
            } catch( IOException ex ) {
                log.warn("find: Ignoring Exception", ex);
            }
        }
        return true;
    }
    
    public boolean found(String resourceName) {
        return find(resourceName);
    }
}
