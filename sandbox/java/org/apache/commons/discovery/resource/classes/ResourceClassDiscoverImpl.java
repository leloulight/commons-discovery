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

import org.apache.commons.discovery.*;
import org.apache.commons.discovery.ResourceClass;
import org.apache.commons.discovery.ResourceClassDiscover;
import org.apache.commons.discovery.resource.ClassLoaders;


/**
 * @author Richard A. Sitze
 */
public abstract class ResourceClassDiscoverImpl implements ResourceClassDiscover
{
    private ClassLoaders classLoaders;
    private ResourceClassListener listener = null;

    
    /**
     * Construct a new resource discoverer
     */
    public ResourceClassDiscoverImpl() {
        this(new ClassLoaders());
    }
    
    /**
     *  Construct a new resource discoverer
     */
    public ResourceClassDiscoverImpl(ClassLoaders classLoaders) {
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
    
    public void setListener(ResourceClassListener listener) {
        this.listener = listener;
    }

    protected boolean notifyListener(ResourceClass resource) {
        return (listener == null) ? true : listener.found(resource);
    }
}
