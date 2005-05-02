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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.discovery.Resource;
import org.apache.commons.discovery.ResourceDiscover;
import org.apache.commons.discovery.ResourceListener;
import org.apache.commons.discovery.ResourceNameDiscover;
import org.apache.commons.discovery.ResourceNameListener;
import org.apache.commons.discovery.log.DiscoveryLogFactory;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.resource.DiscoverResources;
import org.apache.commons.logging.Log;



/**
 * Discover ALL files of a given name, and return resource names
 * contained within the set of files:
 * <ul>
 *   <li>one resource name per line,</li>
 *   <li>whitespace ignored,</li>
 *   <li>comments begin with '#'</li>
 * </ul>
 * 
 * Default discoverer is DiscoverClassLoaderResources,
 * but it can be set to any other.
 *
 * @author Richard A. Sitze
 * @author Costin Manolache
 * @author James Strachan
 */
public class DiscoverNamesInFile extends ResourceNameDiscoverImpl
    implements ResourceNameDiscover, ResourceNameListener, ResourceListener
{
    private static Log log = DiscoveryLogFactory.newLog(DiscoverNamesInFile.class);
    public static void setLog(Log _log) {
        log = _log;
    }
    
    private ResourceDiscover discoverResources;
    
    /**
     *  Construct a new resource discoverer
     */
    public DiscoverNamesInFile() {
        this.discoverResources = new DiscoverResources();
        this.discoverResources.setListener(this);
    }
    
    /**
     *  Construct a new resource discoverer
     */
    public DiscoverNamesInFile(ClassLoaders loaders) {
        this.discoverResources = new DiscoverResources(loaders);
        this.discoverResources.setListener(this);
    }

    /**
     * Specify set of class loaders to be used in searching.
     */
    public void setClassLoaders(ClassLoaders loaders) {
        discoverResources.setClassLoaders(loaders);
    }

    /**
     */
    public boolean find(String fileName) {
        if (log.isDebugEnabled())
            log.debug("find: fileName='" + fileName + "'");
            
        return discoverResources.find(fileName);
    }
    
    public boolean found(String resourceName) {
        return find(resourceName);
    }
    
    /**
     * Read everything, no defering here..
     * Ensure that files are closed before we leave.
     */
    public boolean found(Resource info) {
        InputStream is = info.getResourceAsStream();
        
        if( is != null ) {
            try {
                try {
                    // This code is needed by EBCDIC and other
                    // strange systems.  It's a fix for bugs
                    // reported in xerces
                    BufferedReader rd;
                    try {
                        rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    } catch (java.io.UnsupportedEncodingException e) {
                        rd = new BufferedReader(new InputStreamReader(is));
                    }
                    
                    try {
                        String resourceName;
                        while( (resourceName = rd.readLine()) != null) {
                            int idx = resourceName.indexOf('#');
                            if (idx >= 0) {
                                resourceName = resourceName.substring(0, idx);
                            }
                            resourceName = resourceName.trim();
    
                            if (resourceName.length() != 0) {
                                if (log.isDebugEnabled())
                                    log.debug("readResourceNames: found '" + resourceName + "'");

                                if (!notifyListener(resourceName)) {
                                    return false;
                                }
                            }
                        }
                    } finally {
                        rd.close();
                    }
                } finally {
                    is.close();
                }
            } catch (IOException e) {
                // ignore
            }
        }
        
        return true;
    }
}
