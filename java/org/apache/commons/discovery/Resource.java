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
package org.apache.commons.discovery;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.discovery.log.DiscoveryLogFactory;
import org.apache.commons.discovery.resource.DiscoverResources;
import org.apache.commons.logging.Log;


/**
 * 'Resource' located by discovery.
 * Naming of methods becomes a real pain ('getClass()')
 * so I've patterned this after ClassLoader...
 * 
 * I think it works well as it will give users a point-of-reference.
 * 
 * @author Craig R. McClanahan
 * @author Costin Manolache
 * @author Richard A. Sitze
 */
public class Resource
{
    private static Log log = DiscoveryLogFactory.newLog(DiscoverResources.class);
    public static void setLog(Log _log) {
        log = _log;
    }
    protected final String      name;
    protected final URL         resource;
    protected final ClassLoader loader;

    public Resource(String resourceName, URL resource, ClassLoader loader) {
        this.name = resourceName;
        this.resource = resource;
        this.loader = loader;

        if (log.isDebugEnabled())
            log.debug("new " + this);
    }

    /**
     * Get the value of resourceName.
     * @return value of resourceName.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get the value of URL.
     * @return value of URL.
     */
    public URL getResource() {
        return resource;
    }
    
    /**
     * Get the value of URL.
     * @return value of URL.
     */
    public InputStream getResourceAsStream() {
        try {
            return resource.openStream();
        } catch (IOException e) {
            return null;  // ignore
        }
    }
    
    /**
     * Get the value of loader.
     * @return value of loader.
     */
    public ClassLoader getClassLoader() {
        return loader ;
    }
}
