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
package org.apache.commons.discovery.resource.classes;

import java.net.URL;
import java.util.Vector;

import org.apache.commons.discovery.ResourceClass;
import org.apache.commons.discovery.ResourceClassDiscover;
import org.apache.commons.discovery.ResourceNameListener;
import org.apache.commons.discovery.log.DiscoveryLogFactory;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.logging.Log;


/**
 * The findResources() method will check every loader.
 *
 * @author Richard A. Sitze
 * @author Craig R. McClanahan
 * @author Costin Manolache
 * @author James Strachan
 */
public class DiscoverClasses extends ResourceClassDiscoverImpl
    implements ResourceClassDiscover, ResourceNameListener
{
    private static Log log = DiscoveryLogFactory.newLog(DiscoverClasses.class);
    public static void setLog(Log _log) {
        log = _log;
    }

    /** Construct a new resource discoverer
     */
    public DiscoverClasses() {
        super();
    }
    
    /** Construct a new resource discoverer
     */
    public DiscoverClasses(ClassLoaders classLoaders) {
        super(classLoaders);
    }
    
    public boolean find(String className) {
        final String resourceName = className.replace('.','/') + ".class";
        
        if (log.isDebugEnabled())
            log.debug("find: className='" + className + "'");

        Vector history = new Vector();

        for (int idx = 0; idx < getClassLoaders().size(); idx++) {
            ClassLoader loader = getClassLoaders().get(idx);
            URL url = loader.getResource(resourceName);
            if (url != null) {
                if (!history.contains(url)) {
                    history.addElement(url);
                    if (!notifyListener(new ResourceClass(className,
                                                          url,
                                                          loader)))
                        return false;
                } else {
                    if (log.isDebugEnabled())
                        log.debug("find: duplicate URL='" + url + "'");
                }
            } else {
                if (log.isDebugEnabled())
                    log.debug("find: '" + resourceName + "' not found with loader: " + loader);
            }
        }
        
        return true;
    }
    
    public boolean found(String className) {
        return find(className);
    }
}
