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

package org.apache.commons.discovery.base;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.discovery.DiscoveryException;
import org.apache.commons.discovery.tools.ClassLoaderUtils;


/**
 * @author Richard A. Sitze
 */
public class ImplClass {
    private final String implName;

    private Class spiClass;
    
    private Class implClass;
    
    private Class  paramClasses[] = null;
    private Object params[] = null;


    /**
     * Construct object representing implementation
     * Class whose name is <code>implName</code>.
     * 
     * @param implName The SPI class name
     * 
     * @param constructorParamClasses classes representing the
     *        constructor argument types.
     * 
     * @param constructorParams objects representing the
     *        constructor arguments.
     */
    ImplClass(Class spiClass,
              String implName,
              Class constructorParamClasses[],
              Object constructorParams[])
    {
        this.implName = implName;
        this.implClass = null;
        this.paramClasses = constructorParamClasses;
        this.params = constructorParams;
    }
    
    /**
     * Construct object representing implementation
     * Class <code>implClass</code>.
     * 
     * @param implClass The SPI class
     * 
     * @param constructorParamClasses classes representing the
     *        constructor argument types.
     * 
     * @param constructorParams objects representing the
     *        constructor arguments.
     */
    ImplClass(Class spiClass,
              Class implClass,
              Class constructorParamClasses[],
              Object constructorParams[])
    {
        this.implName = (implClass != null) ? implClass.getName() : null;
        this.implClass = implClass;
        this.paramClasses = constructorParamClasses;
        this.params = constructorParams;
    }


    public String getImplName() {
        return implName;
    }

    /**
     * The implementation's Class.
     * If the original constructor specified the class using a
     * <code>String implName</code>, then this will return <code>null</code>.
     * Resolution of the class is defered until it is
     * <ul>
     *   <li>required, and</li>
     *   <li>loaded by <code>loadImplClass</code></li>.
     * </ul>
     * 
     * @return implementation class or null.
     */    
    public Class getImplClass() {
        return implClass;
    }

    /**
     * Load and return the class using the list of class loaders
     * specified by <code>loaders</code>.
     * 
     * @param loaders
     * 
     * @param libOnly Use library loaders, a subset of the application's
     *                class loaders.  Library loaders do not include
     *                the thread context class loader or the calling class'
     *                class loader.
     */    
    public Class loadImplClass(ClassLoaders loaders) {
        if (implClass == null) {
            implClass = ClassLoaderUtils.loadClass(getImplName(), loaders);
            
            if (implClass != null  &&  !spiClass.isAssignableFrom(implClass)) {
                throw new DiscoveryException("Class " + getImplName() +
                              " does not implement " + spiClass.getName());
            }
        }

        return implClass;
    }

    /**
     * Instantiate a new 
     */    
    public Object newInstance()
        throws DiscoveryException,
               InstantiationException,
               IllegalAccessException,
               NoSuchMethodException,
               InvocationTargetException
    {
        if (getImplClass() == null)
            throw new DiscoveryException("Class " + getImplName() + " not loaded!");
            
        if (paramClasses == null || params == null) {
            return getImplClass().newInstance();
        } else {
            Constructor constructor = getImplClass().getConstructor(paramClasses);
            return constructor.newInstance(params);
        }
    }
}
