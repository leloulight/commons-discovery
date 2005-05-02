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

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.commons.discovery.ResourceNameDiscover;
import org.apache.commons.discovery.ResourceNameListener;
import org.apache.commons.discovery.log.DiscoveryLogFactory;
import org.apache.commons.logging.Log;


/**
 * Recover resources from a Dictionary.  This covers Properties as well,
 * since <code>Properties extends Hashtable extends Dictionary</code>.
 * 
 * The recovered value is expected to be either a <code>String</code>
 * or a <code>String[]</code>.
 * 
 * @author Richard A. Sitze
 */
public class DiscoverNamesInDictionary extends ResourceNameDiscoverImpl
    implements ResourceNameDiscover, ResourceNameListener
{
    private static Log log = DiscoveryLogFactory.newLog(DiscoverNamesInDictionary.class);
    public static void setLog(Log _log) {
        log = _log;
    }

    private Dictionary dictionary;
    
    /** Construct a new resource discoverer
     */
    public DiscoverNamesInDictionary() {
        this(new Hashtable());
    }
    
    /** Construct a new resource discoverer
     */
    public DiscoverNamesInDictionary(Dictionary dictionary) {
        setDictionary(dictionary);
    }

    protected Dictionary getDictionary() {
        return dictionary;
    }

    /**
     * Specify set of class loaders to be used in searching.
     */
    public void setDictionary(Dictionary table) {
        this.dictionary = dictionary;
    }
    
    public void addResource(String resourceName, String resource) {
        dictionary.put(resourceName, resource);
    }
    
    public void addResource(String resourceName, String[] resources) {
        dictionary.put(resourceName, resources);
    }

    /**
     * @return Enumeration of ResourceInfo
     */
    public boolean find(String resourceName) {
        if (log.isDebugEnabled())
            log.debug("find: resourceName='" + resourceName + "'");

        Object baseResource = dictionary.get(resourceName);

        if (baseResource instanceof String) {
            return notifyListener((String)baseResource);
        } else if (baseResource instanceof String[]) {
            String[] resources = (String[])baseResource;
            for (int i = 0; i < resources.length; i++) {
                if (!notifyListener(resources[i])) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean found(String resourceName) {
        return find(resourceName);
    }
}
