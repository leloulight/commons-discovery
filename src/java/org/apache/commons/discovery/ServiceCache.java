/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.discovery;

import java.util.Iterator;
import java.util.HashMap;



/**
 * Cache of previously discovered implementations of SPI's.
 * 
 * Unsynchronized, using code must manage threading issues.
 * 
 * @author Richard A. Sitze
 */
class ServiceCache
{
    /**
     * Previously encountered service interfaces (spis), keyed by the
     * <code>ClassLoader</code> with which it was created.
     */
    private HashMap services = new HashMap(13);
    
    /**
     * Get service keyed by classLoader.
     * Special cases null bootstrap classloader (classLoader == null).
     */
    public Object get(ClassLoader classLoader)
    {
        classLoader = BootstrapLoader.wrap(classLoader);

        return (classLoader == null)
                ? null
                : services.get(classLoader);
    }
    
    /**
     * Put service keyed by classLoader.
     * Special cases null bootstrap classloader (classLoader == null).
     */
    public void put(ClassLoader classLoader, Object service)
    {
        classLoader = BootstrapLoader.wrap(classLoader);

        if (classLoader != null  &&  service != null) {
            services.put(classLoader, service);
        }
    }

    /**
     * Release any internal references to previously created service instances,
     * after calling the instance method <code>release()</code> on each of them.
     *
     * This is useful environments like servlet containers,
     * which implement application reloading by throwing away a ClassLoader.
     * Dangling references to objects in that class loader would prevent
     * garbage collection.
     */
    public void releaseAll() {
        Iterator elements = services.values().iterator();
        while (elements.hasNext()) {
            Object service = elements.next();
            
            if (service instanceof Service)
                ((Service)service).release();
        }
        services.clear();
    }
}
