/*
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.commons.discovery.log.DiscoveryLogFactory;
import org.apache.commons.logging.Log;


/**
 * 'Resource' located by discovery.
 * Naming of methods becomes a real pain ('getClass()')
 * so I've patterned this after ClassLoader...
 * 
 * I think it works well as it will give users a point-of-reference.
 * 
 * @author Richard A. Sitze
 */
public class ResourceClass extends Resource
{
    private static Log log = DiscoveryLogFactory.newLog(ResourceClass.class);
    public static void setLog(Log _log) {
        log = _log;
    }
    protected Class       resourceClass;

    public ResourceClass(Class resourceClass, URL resource) {
        super(resourceClass.getName(), resource, resourceClass.getClassLoader());
        this.resourceClass = resourceClass;
    }

    public ResourceClass(String resourceName, URL resource, ClassLoader loader) {
        super(resourceName, resource, loader);
    }
    
    /**
     * Get the value of resourceClass.
     * Loading the class does NOT guarentee that the class can be
     * instantiated.  Go figure.
     * The class can be instantiated when the class is linked/resolved,
     * and all dependencies are resolved.
     * Various JDKs do this at different times, so beware:
     * java.lang.NoClassDefFoundError when
     * calling Class.getDeclaredMethod() (JDK14),
     * java.lang.reflect.InvocationTargetException
     * (wrapping java.lang.NoClassDefFoundError) when calling
     * java.lang.newInstance (JDK13),
     * and who knows what else..
     *
     * @return value of resourceClass.
     */
    public Class loadClass() {
        if (resourceClass == null  &&  getClassLoader() != null) {
            if (log.isDebugEnabled())
                log.debug("loadClass: Loading class '" + getName() + "' with " + getClassLoader());

            resourceClass = (Class)AccessController.doPrivileged(
                new PrivilegedAction() {
                    public Object run() {
                        try {
                            return getClassLoader().loadClass(getName());
                        } catch (ClassNotFoundException e) {
                            return null;
                        }
                    }
                });
        }
        return resourceClass;
    }
    
    public String toString() {
        return "ResourceClass[" + getName() +  ", " + getResource() + ", " + getClassLoader() + "]";
    }
}
