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

import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.resource.classes.*;


/**
 * @author Richard A. Sitze
 */
public interface ResourceClassDiscover
{
    /**
     * Specify set of class loaders to be used in searching.
     */
    public void setClassLoaders(ClassLoaders loaders);

    /**
     * Specify a new class loader to be used in searching.
     * The order of loaders determines the order of the result.
     * It is recommended to add the most specific loaders first.
     */
    public void addClassLoader(ClassLoader loader);

    
    public void setListener(ResourceClassListener listener);


    /**
     * Find named class resources that are loadable by a class loader.
     * Listener is notified of each resource found.
     * 
     * @return FALSE if listener terminates discovery prematurely by
     *         returning false, otherwise TRUE.
     */
    public boolean find(String className);
}
