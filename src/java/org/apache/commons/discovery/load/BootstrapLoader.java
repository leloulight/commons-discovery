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

package org.apache.commons.discovery.load;

import java.io.InputStream;
import java.net.URL;


/**
 * A wrapper class that gives us a "bootstrap" loader.
 * For the moment, we cheat and return the system class loader.
 * Getting a wrapper for the bootstrap loader that works
 * in JDK 1.1.x may require a bit more work...
 * 
 * Expected use:  call BootstrapLoader.wrap(loader),
 * which will return loader (loader != null) or a wrapper class
 * in place of the bootstrap loader (loader == null).
 */
public class BootstrapLoader {
    private static ClassLoader bootstrapLoader =
        new SystemClassLoader();
    
    private BootstrapLoader() {
    }
    
    public static ClassLoader wrap(ClassLoader incoming) {
        return (incoming == null) ? getBootstrapLoader() : incoming;
    }
    
    public static boolean isBootstrapLoader(ClassLoader incoming) {
        return incoming == null  ||  incoming == getBootstrapLoader();
    }
    
    public static ClassLoader getBootstrapLoader() {
        return bootstrapLoader;
    }
    
    /**
     * JDK 1.1.x compatible?
     * There is no direct way to get the system class loader
     * in 1.1.x, so work around...
     */
    private static class SystemClassLoader extends ClassLoader {
        protected Class loadClass(String className, boolean resolve)
            throws ClassNotFoundException
        {
            return findSystemClass(className);
        }
        
        public URL getResource(String resName) {
            return getSystemResource(resName);
        }
        
        public InputStream getResourceAsStream(String resName) {
            return getSystemResourceAsStream(resName);
        }
    }
}