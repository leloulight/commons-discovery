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

import java.util.Vector;

import org.apache.commons.discovery.ResourceNameDiscover;
import org.apache.commons.discovery.ResourceNameListener;
import org.apache.commons.discovery.log.DiscoveryLogFactory;
import org.apache.commons.logging.Log;


/**
 * Holder for multiple ResourceNameDiscover instances.
 * The result is the union of the results from each
 * (not a chained sequence, where results feed the next in line.
 *
 * @author Richard A. Sitze
 */
public class NameDiscoverers extends ResourceNameDiscoverImpl
    implements ResourceNameDiscover, ResourceNameListener
{
    private static Log log = DiscoveryLogFactory.newLog(NameDiscoverers.class);
    public static void setLog(Log _log) {
        log = _log;
    }

    private Vector discoverers = new Vector();  // ResourceNameDiscover
    
    /**
     *  Construct a new resource name discoverer
     */
    public NameDiscoverers() {
    }
    
    /**
     * Specify an additional class loader to be used in searching.
     * The order of loaders determines the order of the result.
     * It is recommended to add the most specific loaders first.
     */
    public void addResourceNameDiscover(ResourceNameDiscover discover) {
        if (discover != null) {
            discover.setListener(getListener());
            discoverers.addElement(discover);
        }
    }
    
    public void setListener(ResourceNameListener listener) {
        super.setListener(listener);
        for (int i = 0; i < discoverers.size(); i++) {
            ((ResourceNameDiscover)discoverers.get(i)).setListener(listener);
        }
    }
    
    protected ResourceNameDiscover getResourceNameDiscover(int idx) {
        return (ResourceNameDiscover)discoverers.get(idx);
    }

    protected int size() {
        return discoverers.size();
    }

    /**
     * Set of results of all discoverers.
     * 
     * @return ResourceIterator
     */
    public boolean find(String resourceName) {
        if (log.isDebugEnabled())
            log.debug("find: resourceName='" + resourceName + "'");

        for (int i = 0; i < discoverers.size(); i++) {
            if (!((ResourceNameDiscover)discoverers.get(i)).find(resourceName)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean found(String resourceName) {
        return find(resourceName);
    }
}
