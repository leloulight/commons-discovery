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
package org.apache.commons.discovery.resource;

import org.apache.commons.discovery.*;
import org.apache.commons.discovery.Resource;
import org.apache.commons.discovery.ResourceDiscover;


/**
 * Helper class for methods implementing the ResourceDiscover interface.
 * 
 * @author Richard A. Sitze
 */
public abstract class ResourceDiscoverImpl implements ResourceDiscover
{
    private ClassLoaders classLoaders;
    private ResourceListener listener = null;

    
    /**
     * Construct a new resource discoverer
     */
    public ResourceDiscoverImpl() {
        this(new ClassLoaders());
    }
    
    /**
     *  Construct a new resource discoverer
     */
    public ResourceDiscoverImpl(ClassLoaders classLoaders) {
        setClassLoaders(classLoaders);
    }

    /**
     * Specify set of class loaders to be used in searching.
     */
    public void setClassLoaders(ClassLoaders loaders) {
        classLoaders = loaders;
    }

    /**
     * Specify a new class loader to be used in searching.
     * The order of loaders determines the order of the result.
     * It is recommended to add the most specific loaders first.
     */
    public void addClassLoader(ClassLoader loader) {
        classLoaders.put(loader);
    }
    
    protected ClassLoaders getClassLoaders() {
        return classLoaders;
    }
    
    public void setListener(ResourceListener listener) {
        this.listener = listener;
    }

    protected boolean notifyListener(Resource resource) {
        return (listener == null) ? true : listener.found(resource);
    }
}
