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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Represents a Service Programming Interface (spi) context,
 * to include an spi and the Thread Context Class Loader for
 * the thread that created an instance of this object.
 * 
 * These matters are, for the most part, trivial.  Bundling
 * these two together is a convenience, but more importantly
 * it gives a home to a portable mechanism for determining
 * the thread context class loader.  JDK 1.1 does not support
 * the thread context class loader, yet the code below must
 * be able to compile & execute in such an environment.
 * 
 * @author Richard A. Sitze
 */
public class SPIContext {
    /**
     * Thread context class loader or null if not available (JDK 1.1).
     * Wrapped bootstrap classloader if classLoader == null.
     */
    private final ClassLoader threadContextClassLoader =
        ClassLoaderUtils.findThreadContextClassLoader();

    /**
     * System class loader or null if not available (JDK 1.1).
     * Wrapped bootstrap classloader if classLoader == null.
     */
    private final ClassLoader systemClassLoader =
        ClassLoaderUtils.findSystemClassLoader();

    /**
     * List of class loaders
     */
    private final ClassLoader[] loaders;

    /**
     * The service programming interface: intended to be
     * an interface or abstract class, but not limited
     * to those two.
     */        
    private Class spi;

    public SPIContext(Class spi) {
        this.spi = spi;
        this.loaders = ClassLoaderUtils.compactUniq(
            new ClassLoader[] { threadContextClassLoader,
                                BootstrapLoader.wrap(spi.getClassLoader()),
                                systemClassLoader });
    }
    
    public ClassLoader getThreadContextClassLoader() {
        return threadContextClassLoader;
    }
    
    public ClassLoader getSystemClassLoader() {
        return systemClassLoader;
    }
    
    public ClassLoader[] getClassLoaders() {
        return loaders;
    }
    
    public Class getSPI() {
        return spi;
    }
}
