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

import org.apache.commons.discovery.DiscoveryException;


/**
 * Loads classes and resources.
 * 
 * @author Richard A. Sitze
 * @author Craig R. McClanahan
 * @author Costin Manolache
 */
public class Loaders {
    /**
     * The SPI (and thread context) for which we are (presumably)
     * looking for an implementation of.
     */
    private final SPIContext spiContext;
    
    /**
     * System ClassLoaders only
     * 
     * This is NOT the same as spiContext.getClassLoaders(),
     * which includes the thread context class loader.
     */
    private final ClassLoader[] systemLoaders;
    
    private final ClassLoader[] allLoaders;


    /**
     * @param rootFinderClass a wrapper class encapsulating use of DiscoverSingleton.
     *   If DiscoverSingleton is used directly, then this would be DiscoverSingleton itself.
     *   The root finder class is used to determine the 'real' caller, and
     *   hence the caller's class loader - thereby preserving knowledge that
     *   is relevant to finding the correct/expected implementation class.
     */
    public Loaders(SPIContext spiContext, Class rootFinderClass)
    {
        this.spiContext = spiContext;
        this.systemLoaders = getSystemLoaders(spiContext, rootFinderClass);
        this.allLoaders = getAllLoaders(spiContext, rootFinderClass);

        //System.out.println("Finding '" + groupContext + "::" + spiContext.getSPI().getName() + "'");
    }
    
    /**
     * Load <code>className</code>.  If <code>systemOnly</code> is
     * <code>true</code>, use the  'system' class loaders: spi's,
     * root finder class's, and system.  If <code>systemOnly</code> is
     * <code>false</code>, use the following class loaders: thread context,
     * caller's, spi's, root finder class's, and system.
     *
     * @param serviceImplName Fully qualified name of the implementation class
     * 
     * @param systemOnly Use only 'system' class loaders
     *                  (do not try thread context class loader).
     *
     * @exception DiscoveryException if a suitable instance cannot be created,
     *                             or if the class created is not an instance
     *                             of <code>spi</code>
     */
    public Class loadClass(String className, boolean systemOnly)
        throws DiscoveryException
    {
        Class clazz = ClassLoaderUtils.loadClass(className,
                           systemOnly ? systemLoaders : allLoaders);
            
        if (clazz != null  &&  !spiContext.getSPI().isAssignableFrom(clazz)) {
            throw new DiscoveryException("Class " + className +
                          " does not implement " + spiContext.getSPI().getName());
        }
        
        return clazz;
    }
    
    /**
     * Return the specified <code>resourceName</code> as an
     * <code>InputStream</code>.  If <code>systemOnly</code> is
     * <code>true</code>, try the 'system' class loaders: spi's,
     * root finder class's (default is DiscoverSingleton), and system.
     * If <code>systemOnly</code> is <code>false</code>, try each of
     * the following class loaders: thread context, caller's, spi's,
     * root finder class's (default is DiscoverSingleton), and system.
     *
     * @param resourceName name of the resource
     * 
     * @param system Use only 'system' class loaders
     *                  (do not try thread context class loader).
     *
     * @exception DiscoveryException if a suitable resource cannot be created.
     */
    public InputStream loadResourceAsStream(String resourceName)
        throws DiscoveryException
    {
        String packageName = spiContext.getSPI().getPackage().getName();

        InputStream stream =
            (spiContext.getGroupContext() == null)
                ? null
                : ClassLoaderUtils.getResourceAsStream(packageName,
                          spiContext.getGroupContext() + "." + resourceName,
                          allLoaders);

        if (stream == null)
            stream = ClassLoaderUtils.getResourceAsStream(packageName,
                                                          resourceName,
                                                          allLoaders);

        return stream;
    }
    
    /**
     * MAGIC: as in, I don't have a clue how to go about this...
     * SecurityManager.getClassContext() is out of reach at best,
     * and getting to it in a J2EE environment is likely to be beyond
     * hopeless....
     * 
     * If the caller class loader is the bootstrap classloader, then
     * it is 'wrapped' (see BootstrapLoader).  Therefore this method
     * only returns 'null' if a caller class loader could not
     * be identified.
     * 
     */
    private static final ClassLoader getCallerClassLoader(Class rootFinderClass) {
        return BootstrapLoader.wrap(null);
    }

    /**
     * List of 'system' class loaders to the SPI
     */
    private static final ClassLoader[] getSystemLoaders(SPIContext spiContext,
                                                        Class rootFinderClass) {
        return ClassLoaderUtils.compactUniq(
                new ClassLoader[] {BootstrapLoader.wrap(spiContext.getSPI().getClassLoader()),
                                   BootstrapLoader.wrap(rootFinderClass.getClassLoader()),
                                   ClassLoaderUtils.getSystemClassLoader()
                                  });
    }
    
    private static final ClassLoader[] getAllLoaders(SPIContext spiContext,
                                                     Class rootFinderClass) {
        return ClassLoaderUtils.compactUniq(
                new ClassLoader[] {spiContext.getThreadContextClassLoader(),
                                   getCallerClassLoader(rootFinderClass),
                                   BootstrapLoader.wrap(spiContext.getSPI().getClassLoader()),
                                   BootstrapLoader.wrap(rootFinderClass.getClassLoader()),
                                   ClassLoaderUtils.getSystemClassLoader()
                                  });
    }
}