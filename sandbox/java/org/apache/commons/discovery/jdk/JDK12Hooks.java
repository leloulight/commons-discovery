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
package org.apache.commons.discovery.jdk;

import java.util.Enumeration;
import java.io.IOException;


/**
 * @author Richard A. Sitze
 */
class JDK12Hooks extends JDKHooks {
    /**
     * The thread context class loader is available for JDK 1.2
     * or later, if certain security conditions are met.
     * 
     * @return The thread context class loader, if available.
     *         Otherwise return null.
     */
    public ClassLoader getThreadContextClassLoader() {
        ClassLoader classLoader;
        
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
        } catch (SecurityException e) {
            /**
             * SecurityException is thrown when
             * a) the context class loader isn't an ancestor of the
             *    calling class's class loader, or
             * b) if security permissions are restricted.
             * 
             * For (a), ignore and keep going.  We cannot help but also
             * ignore (b) with the logic below, but other calls elsewhere
             * (to obtain a class loader) will re-trigger this exception
             * where we can make a distinction.
             */
            classLoader = null;  // ignore
        }
        
        // Return the selected class loader
        return classLoader;
    }
    
    /**
     * The system class loader is available for JDK 1.2
     * or later, if certain security conditions are met.
     * 
     * @return The system class loader, if available.
     *         Otherwise return null.
     */
    public ClassLoader getSystemClassLoader() {
        ClassLoader classLoader;
        
        try {
            classLoader = ClassLoader.getSystemClassLoader();
        } catch (SecurityException e) {
            /**
             * Ignore and keep going.
             */
            classLoader = null;  // ignore
        }
        
        // Return the selected class loader
        return classLoader;
    }

    /**
     * Implement ClassLoader.getResources for JDK 1.2
     */
    public Enumeration getResources(ClassLoader loader,
                                    String resourceName)
        throws IOException
    {
        /**
         * The simple answer is/was:
         *    return loader.getResources(resourceName);
         * 
         * However, some classloaders overload the behavior of getResource
         * (loadClass, etc) such that the order of returned results changes
         * from normally expected behavior.
         * 
         * Example: locate classes/resources from child ClassLoaders first,
         *          parents last (in some J2EE environs).
         * 
         * The resource returned by getResource() should be the same as the
         * first resource returned by getResources().  Unfortunately, this
         * is not, and cannot be: getResources() is 'final' in the current
         * JDK's (1.2, 1.3, 1.4).
         * 
         * To address this, the implementation of this method will
         * return an Enumeration such that the first element is the
         * results of getResource, and all trailing elements are
         * from getResources.  On each iteration, we check so see
         * if the resource (from getResources) matches the first resource,
         * and eliminate the redundent element.
         */
        
        final URL first = (URL)loader.getResource(resourceName);
        final Enumeration rest = loader.getResources(resourceName);
        
        return new Enumeration() {
            private boolean firstDone = (first == null);
            private URL next = getNext();
            
            public Object nextElement() {
                URL o = next;
                next = getNext();
                return o;
            }

            public boolean hasMoreElements() {
                return next != null;
            }
            
            private URL getNext() {
                URL n;
                
                if (!firstDone) {
                    /**
                     * First time through, use results of getReference()
                     * if they were non-null.
                     */
                    firstDone = true;
                    n = first;
                } else {
                    /**
                     * Subsequent times through,
                     * use results of getReferences()
                     * but take out anything that matches 'first'.
                     * 
                     * Iterate through list until we find one that
                     * doesn't match 'first'.
                     */
                    n = null;
                    while (rest.hasMoreElements()  &&  n == null) {
                        n = (URL)rest.nextElement();
                        if (first != null &&
                            n != null &&
                            n.equals(first))
                        {
                            n = null;
                        }
                    }
                }
                
                return n;
            }
        };
    }
}
