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

import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;


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
public class ClassFinder {
    /**
     * JDK1.3+ 'Service Provider' specification 
     * ( http://java.sun.com/j2se/1.3/docs/guide/jar/jar.html )
     */
    private static final String SERVICE_HOME = "META-INF/services/";

    /**
     * The SPI (and thread context) for which we are (presumably)
     * looking for an implementation of.
     */
    private final SPIContext spiContext;
    private final Class      rootFinderClass;
    
    private final ClassLoader[] localLoaders;
    private final ClassLoader[] allLoaders;
    
    ClassLoader[] getAllLoaders() { return allLoaders; }

    public ClassFinder(SPIContext spiContext, Class rootFinderClass) {
        this.spiContext = spiContext;
        this.rootFinderClass = rootFinderClass;
        this.localLoaders = getLocalLoaders(spiContext, rootFinderClass);
        this.allLoaders = getAllLoaders(spiContext, rootFinderClass);

        //System.out.println("Finding '" + spiContext.getSPI().getName() + "'");
    }
    
    public ClassFinder(Class spi, Class rootFinderClass) {
        this.spiContext = new SPIContext(spi);
        this.rootFinderClass = rootFinderClass;
        this.localLoaders = getLocalLoaders(spiContext, rootFinderClass);
        this.allLoaders = getAllLoaders(spiContext, rootFinderClass);

        //System.out.println("Finding '" + spi.getName() + "'");
    }
    
    /**
     * Return the specified <code>serviceImplName</code> implementation
     * class.  If <code>localOnly</code> is <code>true</code>, try the
     * class loaders local to the caller: root finder class's (default
     * ServiceFinder) and system.  If <code>localOnly</code> is
     * <code>false</code>, try each of the following class loaders:
     * thread context, caller's, spi's, root finder class's (default
     * ServiceFinder), and system.
     *
     * @param serviceImplName Fully qualified name of the implementation class
     * @param localOnly Use only local class loader
     *                  (do not try thread context class loader).
     *
     * @exception DiscoveryException if a suitable instance cannot be created,
     *                             or if the class created is not an instance
     *                             of <code>spi</code>
     */
    public Class findClass(String serviceImplName, boolean localOnly)
        throws DiscoveryException
    {
        Class clazz = ClassLoaderUtils.loadClass(serviceImplName,
                           localOnly ? localLoaders : allLoaders);
            
        if (clazz != null  &&  !spiContext.getSPI().isAssignableFrom(clazz)) {
            throw new DiscoveryException("Class " + serviceImplName +
                          " does not implement " + spiContext.getSPI().getName());
        }
        
        return clazz;
    }
    
    /**
     * Return the specified <code>resourceName</code> as an
     * <code>InputStream</code>.  If <code>localOnly</code> is
     * <code>true</code>, try the class loaders local to the caller:
     * root finder class's (default ServiceFinder) and system.
     * If <code>localOnly</code> is <code>false</code>, try each of
     * the following class loaders: thread context, caller's, spi's,
     * root finder class's (default ServiceFinder), and system.
     *
     * @param resourceName name of the resource
     * @param localOnly Use only local class loader
     *                  (do not try thread context class loader).
     *
     * @exception DiscoveryException if a suitable resource cannot be created.
     */
    public InputStream findResourceAsStream(String resourceName, boolean localOnly)
        throws DiscoveryException
    {
        return ClassLoaderUtils.getResourceAsStream(spiContext.getSPI().getPackage().getName(),
                                                    resourceName,
                                                    localOnly ? localLoaders : allLoaders);
    }
    
    /**
     * Load the class whose name is given by the value of a System Property.
     * 
     * @param attribute the name of the system property whose value is
     *        the name of the class to load.
     */
    public Class systemFindClass(String attribute) {
        String value;
        try {
            value = System.getProperty(attribute);
        } catch (SecurityException e) {
            value = null;
        }
        return findClass(value, false);
    }

    /**
     * Load the class whose name is given by the value of a System Property,
     * whose name is the fully qualified name of the SPI class.
     */
    public Class systemFindClass() {
        return systemFindClass(spiContext.getSPI().getName());
    }

    /**
     * Load the class whose name is given by the value of a property.
     * 
     * @param properties the properties set.
     * @param attribute the name of the property whose value is
     *        the name of the class to load.
     */
    public Class findClass(Properties properties, String attribute) {
        return findClass(properties.getProperty(attribute), false);
    }

    /**
     * Load the class whose name is given by the value of a property.
     * whose name is the fully qualified name of the SPI class.
     * 
     * @param properties the properties set.
     */
    public Class findClass(Properties properties) {
        return findClass(properties, spiContext.getSPI().getName());
    }

    /**
     * Load the class implementing the SPI using the JDK 1.3
     * location discovery mechanism.
     * This will allow users to plug a service implementation by just
     * placing it in the META-INF/services directory of the webapp
     * (or in CLASSPATH or equivalent).
     */
    public Class jdk13FindClass() {
        return findClass(getJDKImplClassName(), false);
    }

    /**
     * Find the name of a service using the JDK 1.3 jar discovery mechanism.
     * This will allow users to plug a service implementation by just
     * placing it in the META-INF/services directory of the webapp
     * (or in CLASSPATH or equivalent).
     */
    private String getJDKImplClassName() {
        String serviceImplName = null;

        // Name of J2EE application file that identifies the service implementation.
        String servicePropertyFile = SERVICE_HOME + spiContext.getSPI().getName();
        
        ClassLoader contextLoader = spiContext.getThreadContextClassLoader();
    
        InputStream is = (contextLoader == null
                          ? ClassLoader.getSystemResourceAsStream(servicePropertyFile)
                          : contextLoader.getResourceAsStream(servicePropertyFile));
                          
        if( is != null ) {
            try {
                try {
                    // This code is needed by EBCDIC and other strange systems.
                    // It's a fix for bugs reported in xerces
                    BufferedReader rd;
                    
                    try {
                        rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    } catch (java.io.UnsupportedEncodingException e) {
                        rd = new BufferedReader(new InputStreamReader(is));
                    }
                        
                    try {
                        serviceImplName = rd.readLine();
                    } finally {
                        rd.close();
                    }
                } finally {
                    is.close();
                }
            } catch (IOException ioe) {
                ; // ignore
            }
        }
        
        return serviceImplName;
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
     * List of 'local' classes and class loaders.
     * By using ClassLoaderHolder to holder Class and ClassLoader objects,
     * we preserve the difference in behaviour between the two for loading
     * resources..
     */
    private static final ClassLoader[] getLocalLoaders(SPIContext spiContext,
                                                       Class rootFinderClass) {
        return ClassLoaderUtils.compactUniq(
                new ClassLoader[] {BootstrapLoader.wrap(rootFinderClass.getClassLoader()),
                                   spiContext.getSystemClassLoader()
                                  });
    }
    
    private static final ClassLoader[] getAllLoaders(SPIContext spiContext,
                                                     Class rootFinderClass) {
        return ClassLoaderUtils.compactUniq(
                new ClassLoader[] {spiContext.getThreadContextClassLoader(),
                                   getCallerClassLoader(rootFinderClass),
                                   BootstrapLoader.wrap(spiContext.getSPI().getClassLoader()),
                                   BootstrapLoader.wrap(rootFinderClass.getClassLoader()),
                                   spiContext.getSystemClassLoader()
                                  });
    }
}
