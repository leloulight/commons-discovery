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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * <p>Service factory for discovery and creation of service instances,
 * with discovery and configuration features similar to that employed
 * by standard Java APIs such as JAXP.</p>
 *
 * <p><strong>IMPLEMENTATION NOTE</strong> - This implementation is heavily
 * based on the SAXParserFactory and DocumentBuilderFactory implementations
 * (corresponding to the JAXP pluggability APIs) found in Apache Xerces.</p>
 * 
 * @author Richard A. Sitze
 * @author Craig R. McClanahan
 * @author Costin Manolache
 * @version $Revision$ $Date$
 */
public class ServiceFinder {
    /**
     * Sets of previously encountered service interfaces (spis), keyed by the
     * interface (<code>Class</code>).  Each element is a ServiceCache.
     */
    private static final Hashtable service_caches = new Hashtable(13);
    
    /**
     * <p>Locate and instantiate a service.  The service implementation
     * class is located using the following ordered lookup:</p>
     * <ul>
     * <li>Try to load a class with the name obtained from the system
     *     property, having the same name as the spi class:
     *     <code>spiContext.getSPI().getName()</code>.</li>
     * 
     * <li>Try to load a class with the name obtained from the
     *     <code>properties</code> parameter, having the same
     *     name as the spi class: <code>spiContext.getSPI().getName()</code>.</li>
     * 
     * <li>Use the JDK1.3+ 'Service Provider' specification
     *     (http://java.sun.com/j2se/1.3/docs/guide/jar/jar.html)
     *     to locate a service named <code>spiContext.getSPI().getName()</code>.
     *     Implemented internally, so there is not a hard
     *     dependency on JDK 1.3+.</li>
     * 
     * <li>Fall back to a default implementation class, as specified by
     *     non-null <code>defaultImplName</code>.</li>
     * </ul>
     * 
     * <p>In most cases, after class NAME is found, then a
     * number of attempts are made to load the class using different
     * class loaders, in the following sequence:
     * <ul>
     * <li>Thread Context Class Loader</li>
     * <li>Caller's Class Loader</li>
     * <li>SPI's Class Loader</li>
     * <li>ServiceFinder's (this class) Class Loader</li>
     * <li>System Class Loader</li>
     * </ul>
     * 
     * <p>The default implementation is loaded using:
     * <ul>
     * <li>ServiceFinder's (this class) Class Loader</li>
     * <li>System Class Loader</li>
     * </ul>
     * 
     * @param rootFinderClass  The root finder class, which may not
     *        be 'ServiceFinder' if a wrapper class is used.
     * 
     * @param spiContext The SPI Context identifies the SPI and the
     *        thread context class loader.
     *        <code>spiContext.getSPI().getName()</code> id's the (property)
     *        name of the service implementation.  Presumed to be an interface,
     *        but there is nothing in the code that prevents it from
     *        being an abstract base class, or even a class.
     * 
     * @param properties used as one alternative to find name of service
     *        implementation class, with property name specified by
     *        <code>spi.getName()</code>.  If the implementation class found
     *        for <code>spi</code> implements the <code>Service</code>
     *        interface, then <code>spiInstance.init(properties)</code> is
     *        called.
     * 
     * @param defaultImplName Name of the default implementation class.
     *
     * @exception ServiceException if the implementation class
     *            is not available,
     *            cannot be instantiated,
     *            or is not an instance of <code>spi</code>.
     */
    private static Object find(ClassFinder classFinder,
                               SPIContext spiContext,
                               Properties properties,
                               String defaultImplName)
        throws ServiceException
    {
        /**
         * Return previously registered service object (not class)
         * for this spi.  Try each class loader in succession.
         */
        Object service = null;
        ClassLoader[] allLoaders = classFinder.getAllLoaders();

        for (int idx = 0; service == null  &&  idx < allLoaders.length; idx++) {
            service = get(spiContext.getSPI().getName(), allLoaders[idx]);
        }

        if (service != null) {        
            // First, try the system property
            Class clazz = classFinder.systemFindClass();
    
            if (clazz == null) {
                // Second, try the properties parameter
                if (properties != null)
                    clazz = classFinder.findClass(properties);
            
                if (clazz == null) {
                    // Third, try to find a service by using the JDK1.3 jar
                    // discovery mechanism.
                    clazz = classFinder.jdk13FindClass();
                
                    if (clazz == null) {
                        // Fourth, try the fallback implementation class
                        clazz = classFinder.findClass(defaultImplName, true);
                        
                        if (clazz == null) {
                            throw new ServiceException
                                ("No implementation defined for " +
                                 spiContext.getSPI().getName());
                        }
                    }
                }
            }
            
            if (clazz != null) {
                try {
                    service = clazz.newInstance();
                    put(spiContext.getSPI().getName(), clazz.getClassLoader(), service);
                } catch (Exception e) {
                    throw new ServiceException("Unable to instantiate " + spiContext.getSPI().getName(), e);
                }
                
                if (service instanceof Service) {
                    ((Service)service).init(properties);
                }
            }
        }

        return service;
    }
    
    public static Object find(Class rootFinderClass,
                              SPIContext spiContext,
                              Properties properties,
                              String defaultImplName)
        throws ServiceException
    {
        // thread context can change on each call,
        // so establish context for this one call.
        ClassFinder classFinder = new ClassFinder(spiContext, rootFinderClass);
        return find(classFinder, spiContext, properties, defaultImplName);
    }
    
    /**
     * Equivalent to
     * <code>find(ServiceFinder.class, spiContext, properties, defaultImplName)</code>.
     */
    public static Object find(SPIContext spiContext,
                              Properties properties,
                              String defaultImplName)
        throws ServiceException
    {
        return find(ServiceFinder.class, spiContext, properties, defaultImplName);
    }
    
    /**
     * Equivalent to
     * <code>find(new SPIContext(spi), properties, defaultImplName)</code>.
     */
    public static Object find(Class rootFinderClass,
                              Class spi,
                              Properties properties,
                              String defaultImplName)
        throws ServiceException
    {
        return find(rootFinderClass, new SPIContext(spi), properties, defaultImplName);
    }

    /**
     * Equivalent to
     * <code>find(ServiceFinder.class, spi, properties, defaultImplName)</code>.
     */
    public static Object find(Class spi,
                              Properties properties,
                              String defaultImplName)
        throws ServiceException
    {
        return find(ServiceFinder.class, spi, properties, defaultImplName);
    }

    /**
     * Load properties file, and call
     * <code>find(rootFinderClass, spiContext, properties, defaultImplName)</code>.
     */    
    public static Object find(Class rootFinderClass,
                              SPIContext spiContext,
                              String overloadPrefix,
                              String propertiesFileName,
                              String defaultImplName)
        throws ServiceException
    {
        // thread context can change on each call,
        // so establish context for this one call.
        ClassFinder classFinder = new ClassFinder(spiContext, rootFinderClass);

        Properties properties = null;
        
        if (propertiesFileName != null) {
            try {
                InputStream stream =
                    (overloadPrefix == null)
                    ? null
                    : classFinder.findResourceAsStream(overloadPrefix + "." + propertiesFileName, false);

                if (stream == null)    
                    stream = classFinder.findResourceAsStream(propertiesFileName, false);
    
                if (stream != null) {
                    properties = new Properties();
                    try {
                        properties.load(stream);
                    } finally {
                        stream.close();
                    }
                }
            } catch (IOException e) {
            } catch (SecurityException e) {
            }
        }
        
        return find(classFinder, spiContext, properties, defaultImplName);
    }

    /**
     * Load properties file, and call
     * <code>find(ServiceFinder.class, spiContext, propertiesFileName, defaultImplName)</code>.
     */    
    public static Object find(SPIContext spiContext,
                              String overloadPrefix,
                              String propertiesFileName,
                              String defaultImplName)
        throws ServiceException
    {
        return find(ServiceFinder.class, spiContext, overloadPrefix, propertiesFileName, defaultImplName);
    }
    
    /**
     * Equivalent to
     * <code>find(rootFinderClass, new SPIContext(spi), propertiesFileName, defaultImplName)</code>.
     */    
    public static Object find(Class rootFinderClass,
                              Class spi,
                              String overloadPrefix,
                              String propertiesFileName,
                              String defaultImplName)
        throws ServiceException
    {
        return find(rootFinderClass, new SPIContext(spi), overloadPrefix, propertiesFileName, defaultImplName);
    }
    
    /**
     * Equivalent to
     * <code>find(new SPIContext(spi), propertiesFileName, defaultImplName)</code>.
     */    
    public static Object find(Class spi,
                              String overloadPrefix,
                              String propertiesFileName,
                              String defaultImplName)
        throws ServiceException
    {
        return find(new SPIContext(spi), overloadPrefix, propertiesFileName, defaultImplName);
    }

    /**
     * Load properties file, and call
     * <code>find(rootFinderClass, spiContext, properties, defaultImplName)</code>.
     */    
    public static Object find(Class rootFinderClass,
                              SPIContext spiContext,
                              String propertiesFileName,
                              String defaultImplName)
        throws ServiceException
    {
        return find(rootFinderClass, spiContext, (String)null, propertiesFileName, defaultImplName);
    }
    
    /**
     * Load properties file, and call
     * <code>find(ServiceFinder.class, spiContext, propertiesFileName, defaultImplName)</code>.
     */    
    public static Object find(SPIContext spiContext,
                              String propertiesFileName,
                              String defaultImplName)
        throws ServiceException
    {
        return find(ServiceFinder.class, spiContext, (String)null, propertiesFileName, defaultImplName);
    }
    
    /**
     * Equivalent to
     * <code>find(rootFinderClass, new SPIContext(spi), propertiesFileName, defaultImplName)</code>.
     */    
    public static Object find(Class rootFinderClass,
                              Class spi,
                              String propertiesFileName,
                              String defaultImplName)
        throws ServiceException
    {
        return find(rootFinderClass, new SPIContext(spi), propertiesFileName, defaultImplName);
    }
    
    /**
     * Equivalent to
     * <code>find(new SPIContext(spi), propertiesFileName, defaultImplName)</code>.
     */    
    public static Object find(Class spi,
                              String propertiesFileName,
                              String defaultImplName)
        throws ServiceException
    {
        return find(new SPIContext(spi), propertiesFileName, defaultImplName);
    }

    /**
     * Find implementation of SPI.
     * Equivalent to find(rootFinderClass, spi, (Properties)null, defaultImplName);
     */
    public static Object find(Class rootFinderClass, Class spi, String defaultImplName)
        throws ServiceException
    {
        return find(rootFinderClass, spi, (Properties)null, defaultImplName);
    }

    /**
     * Find implementation of SPI.
     * Equivalent to find(spi, (Properties)null, defaultImplName);
     */
    public static Object find(Class spi, String defaultImplName)
        throws ServiceException
    {
        return find(spi, (Properties)null, defaultImplName);
    }

    /**
     * Find implementation of SPI.
     * Equivalent to find(rootFinderClass, spi, properties, null);
     */
    public static Object find(Class rootFinderClass, Class spi, Properties properties)
        throws ServiceException
    {
        return find(rootFinderClass, spi, properties, null);
    }

    /**
     * Find implementation of SPI.
     * Equivalent to find(spi, properties, null);
     */
    public static Object find(Class spi, Properties properties)
        throws ServiceException
    {
        return find(spi, properties, null);
    }

    /**
     * Find implementation of SPI.
     * Equivalent to find(rootFinderClass, spi, (Properties)null, null);
     */
    public static Object find(Class rootFinderClass, Class spi)
        throws ServiceException
    {
        return find(rootFinderClass, spi, (Properties)null, null);
    }

    /**
     * Find implementation of SPI.
     * Equivalent to find(spi, (Properties)null, null);
     */
    public static Object find(Class spi)
        throws ServiceException
    {
        return find(spi, (Properties)null, null);
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
    public static void releaseAll() {
        synchronized (service_caches) {
            Enumeration cache = service_caches.elements();
            while (cache.hasMoreElements()) {
                ((ServiceCache)cache.nextElement()).releaseAll();
            }
            service_caches.clear();
        }
    }
    
    /**
     * Release any internal references to previously created instances of SPI,
     * after calling the instance method <code>release()</code> on each of them.
     *
     * This is useful environments like servlet containers,
     * which implement application reloading by throwing away a ClassLoader.
     * Dangling references to objects in that class loader would prevent
     * garbage collection.
     */
    public static void releaseAll(Class spi) {
        if (spi != null) {
            synchronized (service_caches) {
                ServiceCache cache = (ServiceCache)service_caches.get(spi.getName());
                if (cache != null) {
                    cache.releaseAll();
                }
            }
        }
    }
    
    /**
     * Get service keyed by spi & classLoader.
     * Special cases null bootstrap classloader (classLoader == null).
     */
    private static Object get(String spi, ClassLoader classLoader)
    {
        ServiceCache cache = (spi == null)
                             ? null
                             : (ServiceCache)service_caches.get(spi);
        
        return (cache == null)
                ? null
                : cache.get(classLoader);
    }
    
    /**
     * Put service keyed by spi & classLoader.
     * Special cases null bootstrap classloader (classLoader == null).
     */
    private static void put(String spi, ClassLoader classLoader, Object service)
    {
        if (spi != null  &&  service != null) {
            ServiceCache cache = (ServiceCache)service_caches.get(spi);
            
            if (cache == null) {
                cache = new ServiceCache();
                service_caches.put(spi, cache);
            }
            
            cache.put(classLoader, service);
        }
    }
}
