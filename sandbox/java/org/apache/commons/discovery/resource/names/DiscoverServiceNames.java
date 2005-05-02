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
import org.apache.commons.discovery.resource.ClassLoaders;


/**
 * Provide JDK 1.3 style service discovery...
 * 
 * The caller will first configure the discoverer by creating a
 * root Discoverer for the files.
 *
 * @author Richard A. Sitze
 * @author Craig R. McClanahan
 * @author Costin Manolache
 * @author James Strachan
 */
public class DiscoverServiceNames extends DiscoverNamesInFile
    implements ResourceNameDiscover
{
    protected static final String SERVICE_HOME = "META-INF/services/";
    
    /** Construct a new service discoverer
     */
    public DiscoverServiceNames() {
        super();
    }
    
    /**
     *  Construct a new resource discoverer
     */
    public DiscoverServiceNames(ClassLoaders loaders) {
        super(loaders);
    }
    
    /**
     * @return Enumeration of ServiceInfo
     */
    public boolean find(String serviceName) {
        return super.find(SERVICE_HOME + serviceName);
    }
}
