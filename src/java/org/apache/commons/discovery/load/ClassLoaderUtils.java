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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.discovery.DiscoveryException;


/**
 * Mechanisms to locate and load a class.
 * The load methods locate a class only.
 * The find methods locate a class and verify that the
 * class implements an given interface or extends a given class.
 * 
 * @author Richard A. Sitze
 * @author Craig R. McClanahan
 * @author Costin Manolache
 */
public class ClassLoaderUtils {
    /**
     * This doesn't change across threads... so cache the value.
     */
    private static final ClassLoader systemClassLoader
        = findSystemClassLoader();
        
    private static final boolean debug = false;
    
    /**
     * Get package name.
     * Not all class loaders 'keep' package information,
     * in which case Class.getPackage() returns null.
     * This means that calling Class.getPackage().getName()
     * is unreliable at best.
     */
    public static String getPackageName(Class clazz) {
        Package clazzPackage = clazz.getPackage();
        String packageName;
        if (clazzPackage != null) {
            packageName = clazzPackage.getName();
        } else {
            String clazzName = clazz.getName();
            packageName = new String(clazzName.toCharArray(), 0, clazzName.lastIndexOf('.'));
        }
        return packageName;
    }
    
    /**
     * Load the class <code>serviceImplName</code>, no safety checking
     * 
     * @param serviceImplName The name of the class to load.
     */
    private static Class rawLoadClass(String serviceImplName, ClassLoader loader)
        throws DiscoveryException
    {
        Class clazz = null;
        
        try {
            // first the thread class loader
            clazz = loader.loadClass(serviceImplName);
        } catch (ClassNotFoundException e) {
            clazz = null;
        }
        
        return clazz;
    }
    
    /**
     * Load the class <code>serviceImplName</code>.
     * Try each classloader in succession,
     * until first succeeds, or all fail.
     * 
     * @param serviceImplName The name of the class to load.
     */
    public static Class loadClass(String serviceImplName,
                                  ClassLoader[] loaders,
                                  int length)
        throws DiscoveryException
    {
        Class clazz = null;
        
        if (serviceImplName != null  &&  serviceImplName.length() > 0) {
            if (debug)
                System.out.println("Loading class '" + serviceImplName + "'");

            for (int i = 0; i < length && clazz == null; i++)
            {
                if (loaders[i] != null)
                    clazz = rawLoadClass(serviceImplName, loaders[i]);
            }
        }
        
        return clazz;
    }

    /**
     * Load the class <code>serviceImplName</code>.
     * Try each classloader in succession,
     * until first succeeds, or all fail.
     * 
     * @param serviceImplName The name of the class to load.
     */
    public static Class loadClass(String serviceImplName, ClassLoader[] loaders)
        throws DiscoveryException
    {
        return loadClass(serviceImplName, loaders, loaders.length);
    }
    
    /**
     * Load the class <code>serviceImplName</code> using the
     * class loaders associated with the SPI's context.
     * 
     * @param serviceImplName The name of the class to load.
     */
    public static Class loadClass(String serviceImplName, SPIContext spiContext)
        throws DiscoveryException
    {
        return loadClass(serviceImplName, spiContext.getClassLoaders());
    }


    /**
     * Load the resource <code>resourceName</code>.
     * Try each classloader in succession,
     * until first succeeds, or all fail.
     * 
     * @param resourceName The name of the resource to load.
     */
    public static InputStream getResourceAsStream(String resourceName,
                                                  ClassLoader[] loaders,
                                                  int length)
        throws DiscoveryException
    {
        InputStream stream = null;
        
        if (resourceName != null  &&  resourceName.length() > 0) {
            if (debug)
                System.out.println("Loading resource '" + resourceName + "'");

            for (int i = 0; i < length && stream == null; i++)
            {
                if (loaders[i] != null)
                    stream = loaders[i].getResourceAsStream(resourceName);
            }
        }
        
        return stream;
    }

    /**
     * Load the resource <code>resourceName</code>.
     * Try each classloader in succession,
     * until first succeeds, or all fail.
     * 
     * @param resourceName The name of the resource to load.
     */
    public static InputStream getResourceAsStream(String resourceName, ClassLoader[] loaders)
        throws DiscoveryException
    {
        return getResourceAsStream(resourceName, loaders, loaders.length);
    }
    
    /**
     * Load the resource resourceName using the
     * class loaders associated with the SPI's context.
     * 
     * @param resourceName The name of the resource to load.
     */
    public static InputStream getResourceAsStream(String resourceName, SPIContext spiContext)
        throws DiscoveryException
    {
        return getResourceAsStream(resourceName, spiContext.getClassLoaders());
    }

    /**
     * Load the resource <code>resourceName</code>.
     * Try each classloader in succession,
     * until first succeeds, or all fail.
     * If all fail and <code>resouceName</code> is not absolute
     * (doesn't start with '/' character), then retry with
     * <code>packageName/resourceName</code> after changing all
     * '.' to '/'.
     * 
     * @param resourceName The name of the resource to load.
     */
    public static InputStream getResourceAsStream(String packageName,
                                                  String resourceName,
                                                  ClassLoader[] loaders,
                                                  int length)
        throws DiscoveryException
    {
        InputStream stream = getResourceAsStream(resourceName, loaders, length);
        
        /**
         * If we didn't find the resource, and if the resourceName
         * isn't an 'absolute' path name, then qualify with
         * package name of the spi.
         */
        return (stream == null)
               ? getResourceAsStream(qualifyName(packageName, resourceName),
                                     loaders, length)
               : stream;
    }

    /**
     * Load the resource <code>resourceName</code>.
     * Try each classloader in succession,
     * until first succeeds, or all fail.
     * If all fail and <code>resouceName</code> is not absolute
     * (doesn't start with '/' character), then retry with
     * <code>packageName/resourceName</code> after changing all
     * '.' to '/'.
     * 
     * @param resourceName The name of the resource to load.
     */
    public static InputStream getResourceAsStream(String packageName,
                                                  String resourceName,
                                                  ClassLoader[] loaders)
        throws DiscoveryException
    {
        return getResourceAsStream(packageName, resourceName, loaders, loaders.length);
    }
    
    /**
     * Load the resource resourceName using the
     * class loaders associated with the SPI's context.
     * If all fail and <code>resouceName</code> is not absolute
     * (doesn't start with '/' character), then retry with
     * <code>packageName/resourceName</code> after changing all
     * '.' to '/'.
     * 
     * @param resourceName The name of the resource to load.
     */
    public static InputStream getResourceAsStream(String packageName,
                                                  String resourceName,
                                                  SPIContext spiContext)
        throws DiscoveryException
    {
        return getResourceAsStream(packageName, resourceName, spiContext.getClassLoaders());
    }

    
    /**
     * Would <code>thisClassLoader</code> use <code>classLoader</code>?
     * Return <code>true</code> if <code>classLoader</code> is the same
     * as </code>thisClassLoader</code> or if <code>classLoader</code>
     * is an ancestor of </code>thisClassLoader</code>.
     */
    public static final boolean wouldUseClassLoader(ClassLoader thisClassLoader,
                                                    ClassLoader classLoader) {
        /* bootstrap classloader, at root of all trees! */
        if (BootstrapLoader.isBootstrapLoader(classLoader))
            return true;
        
        while (thisClassLoader != null) {
            if (thisClassLoader == classLoader) {
                return true;
            }
            thisClassLoader = thisClassLoader.getParent();
        }
        
        return false;
    }

    /**
     * Return <code>true</code> if
     * <code>wouldUseClassLoader(list[idx], classLoader)<code>
     * is <code>true<code> for all <code>idx</code>
     * such that <code>0 <= idx < length</code>.
     */
    public static final boolean wouldUseClassLoader(ClassLoader[] list,
                                                    int length,
                                                    ClassLoader classLoader) {
        for (int idx = 0; idx < length; idx++) {
            if (wouldUseClassLoader(list[idx], classLoader))
                return true;
        }
        return false;
    }

    /***
     * Remove duplicate Objects (as opposed to equivalent) from
     * array.  Also checks previous class loader parents..
     * 
     * Assumes that array is short, so (n^2)*m isn't a problem...
     * 
     * This is exposed to allow a unique array to be computed once,
     * and passed in for different tasks...
     */
    public static int uniq(ClassLoader[] array, ClassLoader[] uneek) {
        int len = 0;
        for (int lookForward = 0; lookForward < array.length; lookForward++) {
            ClassLoader fore = array[lookForward];
            
            if (fore != null  &&  !wouldUseClassLoader(uneek, len, fore)) {
                uneek[len++] = fore;
            }
        }
        return len;
    }

    public static final ClassLoader[] copy(ClassLoader[] src, int first, int lastPlus) {
        int length = lastPlus - first;
        ClassLoader[] dest = new ClassLoader[length];
        System.arraycopy(src, first, dest, 0, length);
        return dest;
    }

    public static final ClassLoader[] compactUniq(ClassLoader[] array) {        
        ClassLoader[] uniqLoaders = new ClassLoader[array.length];
        int length = uniq(array, uniqLoaders);
        return copy(uniqLoaders, 0, length);
    }
    
    /**
     * If <code>name</code> represents an absolute path name, then return null.
     * Otherwise, prepend packageName to name, convert all '.' to '/', and
     * return results.
     */
    private static final String qualifyName(String packageName, String name) {
        return (name.charAt(0)=='/')
               ? null
               : packageName.replace('.','/') + "/" + name;
    }

    /**
     * Return the thread context class loader if available.
     * Otherwise return null.  If the thread context class
     * loader is the bootstrap classloader, then it is 'wrapped'
     * (see BootstrapLoader).  Therefore this method only
     * returns 'null' if a thread context class loader could not
     * be identified.
     * 
     * The thread context class loader is available for JDK 1.2
     * or later, if certain security conditions are met.
     * 
     * @exception DiscoveryException if a suitable class loader
     * cannot be identified.
     */
    public static ClassLoader getThreadContextClassLoader()
        throws DiscoveryException
    {
        ClassLoader classLoader = null;
        
        try {
            // Are we running on a JDK 1.2 or later system?
            Method method = Thread.class.getMethod("getContextClassLoader", null);
    
            // Get the thread context class loader (if there is one)
            try {
                classLoader =
                    BootstrapLoader.wrap((ClassLoader)method.invoke(Thread.currentThread(), null));
            } catch (IllegalAccessException e) {
                throw new DiscoveryException("Unexpected IllegalAccessException", e);
            } catch (InvocationTargetException e) {
                /**
                 * InvocationTargetException is thrown by 'invoke' when
                 * the method being invoked (Thread.getContextClassLoader)
                 * throws an exception.
                 * 
                 * Thread.getContextClassLoader() throws SecurityException
                 * when the context class loader isn't an ancestor of the
                 * calling class's class loader, or if security permissions
                 * are restricted.
                 * 
                 * In the first case (the context class loader isn't an
                 * ancestor of the calling class's class loader), we want
                 * to ignore and keep going.  We cannot help but also ignore
                 * the second case (restricted security permissions) with
                 * the logic below, but other calls elsewhere (to obtain
                 * a class loader) will re-trigger this exception where
                 * we can make a distinction.
                 */
                if (e.getTargetException() instanceof SecurityException) {
                    classLoader = null;  // ignore
                } else {
                    // Capture 'e.getTargetException()' exception for details
                    // alternate: log 'e.getTargetException()', and pass back 'e'.
                    throw new DiscoveryException
                        ("Unexpected InvocationTargetException",
                         e.getTargetException());
                }
            }
        } catch (NoSuchMethodException e) {
            // Assume we are running on JDK 1.1
            classLoader = null;
        }
    
        // Return the selected class loader
        return classLoader;
    }
    
    /**
     * Return the system class loader if available.
     * Otherwise return null.  If the system class loader
     * is the bootstrap classloader, then it is 'wrapped'
     * (see BootstrapLoader).  Therefore this method only
     * returns 'null' if a system class loader could not
     * be identified.
     * 
     * The system class loader is available for JDK 1.2
     * or later, if certain security conditions are met.
     * 
     * @exception DiscoveryException if a suitable class loader
     *            cannot be identified.
     */
    public static ClassLoader getSystemClassLoader() {
        return systemClassLoader;
    }

    /**
     * Return the system class loader if available.
     * Otherwise return null.  If the system class loader
     * is the bootstrap classloader, then it is 'wrapped'
     * (see BootstrapLoader).  Therefore this method only
     * returns 'null' if a system class loader could not
     * be identified.
     * 
     * The system class loader is available for JDK 1.2
     * or later, if certain security conditions are met.
     * 
     * @exception DiscoveryException if a suitable class loader
     *            cannot be identified.
     */
    private static ClassLoader findSystemClassLoader()
        throws DiscoveryException
    {
        ClassLoader classLoader = null;
        
        try {
            // Are we running on a JDK 1.2 or later system?
            Method method = ClassLoader.class.getMethod("getSystemClassLoader", null);
    
            // Get the system class loader (if there is one)
            try {
                classLoader =
                    BootstrapLoader.wrap((ClassLoader)method.invoke(null, null));
            } catch (IllegalAccessException e) {
                throw new DiscoveryException("Unexpected IllegalAccessException", e);
            } catch (InvocationTargetException e) {
                /**
                 * InvocationTargetException is thrown by 'invoke' when
                 * the method being invoked (ClassLoader.getSystemClassLoader)
                 * throws an exception.
                 * 
                 * ClassLoader.getSystemClassLoader() throws SecurityException
                 * if security permissions are restricted.
                 */
                if (e.getTargetException() instanceof SecurityException) {
                    classLoader = null;  // ignore
                } else {
                    // Capture 'e.getTargetException()' exception for details
                    // alternate: log 'e.getTargetException()', and pass back 'e'.
                    throw new DiscoveryException
                        ("Unexpected InvocationTargetException",
                         e.getTargetException());
                }
            }
        } catch (NoSuchMethodException e) {
            // Assume we are running on JDK 1.1
            classLoader = null;
        }
    
        // Return the selected class loader
        return classLoader;
    }
}
