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
 * <p>Discover service providers,
 * with discovery and configuration features similar to that employed
 * by standard Java APIs such as JAXP.
 * </p>
 * 
 * <p>In the context of this package, a service interface is defined by a
 * Service Provider Interface (SPI).  The SPI is expressed as a Java interface,
 * abstract class, or (base) class that defines an expected programming
 * interface.
 * </p>
 * 
 * <p>Discovery provides the <code>find</code> methods for locating and
 * instantiating an implementation of a service (SPI).  Each form of
 * <code>find</code> varies slightly, but they all perform the same basic
 * function.  The Discovery <code>find</code> methods proceed as follows:
 * </p>
 * <ul>
 *   <p><li>
 *   Examine an internal cache to determine if the desired service was
 *   previously identified and instantiated.  If found in cache, return it.
 *   </li></p>
 *   <p><li>
 *   Get the name of an implementation class.  The name is the first
 *   non-null value obtained from the following resources:
 *   <ul>
 *     <p><li>
 *     The value of the system property whose name is the same as the SPI's
 *     fully qualified class name (as given by SPI.class.getName()).
 *     </li></p>
 *     <p><li>
 *     The value of a <code>Properties properties</code> property, if provided
 *     as a parameter, whose name is the same as the SPI's fully qualifed class
 *     name (as given by SPI.class.getName()).
 *     </li></p>
 *     <p><li>
 *     The value obtained using the JDK1.3+ 'Service Provider' specification
 *     (http://java.sun.com/j2se/1.3/docs/guide/jar/jar.html) to locate a
 *     service named <code>SPI.class.getName()</code>.  This is implemented
 *     internally, so there is not a dependency on JDK 1.3+.
 *     </li></p>
 *   </ul>
 *   </li></p>
 *   <p><li>
 *   If the name of the implementation class is non-null, load that class.
 *   The class loaded is the first class loaded by the following sequence
 *   of class loaders:
 *   <ul>
 *     <li>Thread Context Class Loader</li>
 *     <li>Discovery's Caller's Class Loader</li>
 *     <li>SPI's Class Loader</li>
 *     <li>Discovery's (this class) Class Loader</li>
 *     <li>System Class Loader</li>
 *   </ul>
 *   An exception is thrown if the class cannot be loaded.
 *   </li></p>
 *   <p><li>
 *   If the name of the implementation class is null, AND the default
 *   implementation class name (<code>defaultImplName</code>) is null,
 *   then an exception is thrown.
 *   </li></p>
 *   <p><li>
 *   If the name of the implementation class is null, AND the default
 *   implementation class name (<code>defaultImplName</code>) is non-null,
 *   then load the default implementation class.  The class loaded is the
 *   first class loaded by the following sequence of class loaders:
 *   <ul>
 *     <li>SPI's Class Loader</li>
 *     <li>Discovery's (this class) Class Loader</li>
 *     <li>System Class Loader</li>
 *   </ul>
 *   <p>
 *   This limits the scope in which the default class loader can be found
 *   to the SPI, Discovery, and System class loaders.  The assumption here
 *   is that the default implementation is closely associated with the SPI
 *   or system, and is not defined in the user's application space.
 *   </p>
 *   <p>
 *   An exception is thrown if the class cannot be loaded.
 *   </p>
 *   </li></p>
 *   <p><li>
 *   Verify that the loaded class implements the SPI: an exception is thrown
 *   if the loaded class does not implement the SPI.
 *   </li></p>
 *   <p><li>
 *   If the loaded class implements the <code>Service</code> interface,
 *   then invoke the <code>init(Properties)</code> method, passing in the
 *   <code>Properties properties</code> parameter, if provided.
 *   </li></p>
 * <ul>
 * 
 * <p>
 * Variances for various forms of the <code>find</code>
 * methods are discussed with each such method.
 * Variances include the following concepts:
 * <ul>
 *   <li><b>rootFinderClass</b> - a wrapper encapsulating a finder method
 *   (factory or other helper class).  The root finder class is used to
 *   determine the 'real' caller, and hence the caller's class loader -
 *   thereby preserving knowledge that is relevant to finding the
 *   correct/expected implementation class.
 *   </li>
 *   <li><b>propertiesFileName</b> - <code>Properties</code> may be specified
 *   directly, or by property file name.  A property file is loaded using the
 *   same sequence of class loaders used to load the SPI implementation:
 *   <ul>
 *     <li>Thread Context Class Loader</li>
 *     <li>Discovery's Caller's Class Loader</li>
 *     <li>SPI's Class Loader</li>
 *     <li>Discovery's (this class) Class Loader</li>
 *     <li>System Class Loader</li>
 *   </ul>
 *   </li>
 *   <li><b>groupContext</b> - differentiates service providers for different
 *   logical groups of service users, that might otherwise be forced to share
 *   a common service and, more importantly, a common configuration of that
 *   service.
 *   <p>The groupContext is used to qualify the name of the property file
 *   name: <code>groupContext + '.' + propertiesFileName</code>.  If that
 *   file is not found, then the unqualified propertyFileName is used.
 *   </p>
 *   <p>In addition, groupContext is used to qualify the name of the system
 *   property used to find the service implementation by prepending the value
 *   of <code>groupContext</code> to the property name:
 *   <code>groupContext&gt; + '.' + SPI.class.getName()</code>.
 *   Again, if a system property cannot be found by that name, then the
 *   unqualified property name is used.
 *   </p>
 *   </li>
 * </ul>
 * </p>
 * 
 * <p>
 * Other concepts
 * </p>
 * 
 * <p><strong>IMPLEMENTATION NOTE</strong> - This implementation is heavily
 * based on the SAXParserFactory and DocumentBuilderFactory implementations
 * (corresponding to the JAXP pluggability APIs) found in Apache Xerces.
 * </p>
 * 
 * @author Richard A. Sitze
 * @author Craig R. McClanahan
 * @author Costin Manolache
 * @version $Revision$ $Date$
 */
public class Discovery {
    /**
     * Readable placeholder for a null value.
     */
    private static final Properties nullProperties = null;
    
    /**
     * Readable placeholder for a null value.
     */
    private static final String     nullDefaultImplName = null;
    
    /**
     * Readable placeholder for a null value.
     */
    private static final String     nullGroupContext = null;
    
    
    /********************** (RELATIVELY) SIMPLE FINDERS **********************
     * 
     * These finders are suitable for direct use in components looking for a
     * service.  If you are not sure which finder(s) to use, you can narrow
     * your search to one of these.
     */
    
    /**
     * Find implementation of SPI.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */
    public static Object find(Class spi)
        throws DiscoveryException
    {
        return find(Discovery.class, spi);
    }

    /**
     * Find implementation of SPI.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param properties Used to determine name of SPI implementation,
     *                   and passed to implementation.init() method if
     *                   implementation implements Service interface.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */
    public static Object find(Class spi, Properties properties)
        throws DiscoveryException
    {
        return find(Discovery.class, spi, properties);
    }

    /**
     * Find implementation of SPI.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param defaultImplName Default implementation name.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */
    public static Object find(Class spi, String defaultImplName)
        throws DiscoveryException
    {
        return find(Discovery.class, spi, defaultImplName);
    }

    /**
     * Find implementation of SPI.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param properties Used to determine name of SPI implementation,
     *                   and passed to implementation.init() method if
     *                   implementation implements Service interface.
     * 
     * @param defaultImplName Default implementation name.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */
    public static Object find(Class spi,
                              Properties properties,
                              String defaultImplName)
        throws DiscoveryException
    {
        return find(Discovery.class, spi, properties, defaultImplName);
    }
    
    /**
     * Find implementation of SPI.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param propertiesFileName The property file name.
     * 
     * @param defaultImplName Default implementation name.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */    
    public static Object find(Class spi,
                              String propertiesFileName,
                              String defaultImplName)
        throws DiscoveryException
    {
        return find(Discovery.class, spi, propertiesFileName, defaultImplName);
    }

    /**
     * Find implementation of SPI.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param groupContext qualifier for the name of the system property
     *        used to find the service implementation.  If a system property
     *        cannot be found by that name, then the unqualified property
     *        name is used.
     * 
     * @param properties Used to determine name of SPI implementation,
     *                   and passed to implementation.init() method if
     *                   implementation implements Service interface.
     * 
     * @param defaultImplName Default implementation name.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */
    public static Object find(Class spi,
                              String groupContext,
                              Properties properties,
                              String defaultImplName)
        throws DiscoveryException
    {
        return find(Discovery.class, spi, groupContext, properties, defaultImplName);
    }
    
    /**
     * Find implementation of SPI unique to a group context.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param groupContext qualifier for the property file name and for
     *        the system property name used to find the service implementation.
     *        If not found, the unqualified names are used.
     * 
     * @param propertiesFileName The (qualified and unqualified) property file
     *        name.
     * 
     * @param defaultImplName Default implementation name.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */    
    public static Object find(Class spi,
                              String groupContext,
                              String propertiesFileName,
                              String defaultImplName)
        throws DiscoveryException
    {
        return find(Discovery.class, spi, groupContext, propertiesFileName, defaultImplName);
    }

    
    /*************** FINDERS FOR USE IN FACTORY/HELPER METHODS ***************
     * 
     * These finders provide a rootFinderClass.  The root finder is the wrapper
     * class (factories or helper classes) that invoke the Discovery find
     * methods, presumably providing (default) values for propertiesFileName
     * and defaultImplName.  Having access to this wrapper class provides a
     * way to determine the 'real' caller, and hence the caller's class loader.
     * Thus preserving knowledge that is relevant to finding the
     * correct/expected implementation class.
     */
    

    /**
     * Find implementation of SPI.
     * 
     * @param rootFinderClass Wrapper class used by end-user, that ultimately
     *        calls this finder method.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded, or if
     *            the resulting class does not implement the SPI.
     */
    public static Object find(Class rootFinderClass, Class spi)
        throws DiscoveryException
    {
        return find(rootFinderClass, spi, nullProperties, nullDefaultImplName);
    }

    /**
     * Find implementation of SPI.
     * 
     * @param rootFinderClass Wrapper class used by end-user, that ultimately
     *        calls this finder method.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param properties Used to determine name of SPI implementation,
     *                   and passed to implementation.init() method if
     *                   implementation implements Service interface.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */
    public static Object find(Class rootFinderClass, Class spi, Properties properties)
        throws DiscoveryException
    {
        return find(rootFinderClass, spi, properties, nullDefaultImplName);
    }

    /**
     * Find implementation of SPI.
     * 
     * @param rootFinderClass Wrapper class used by end-user, that ultimately
     *        calls this finder method.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param defaultImplName Default implementation name.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */
    public static Object find(Class rootFinderClass, Class spi, String defaultImplName)
        throws DiscoveryException
    {
        return find(rootFinderClass, spi, nullProperties, defaultImplName);
    }

    /**
     * Find implementation of SPI.
     * 
     * @param rootFinderClass Wrapper class used by end-user, that ultimately
     *        calls this finder method.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param properties Used to determine name of SPI implementation,
     *                   and passed to implementation.init() method if
     *                   implementation implements Service interface.
     * 
     * @param defaultImplName Default implementation name.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */
    public static Object find(Class rootFinderClass,
                              Class spi,
                              Properties properties,
                              String defaultImplName)
        throws DiscoveryException
    {
        return loadClass(new ClassFinder(spi, nullGroupContext, rootFinderClass),
                         properties, defaultImplName);
    }
    
    /**
     * Find implementation of SPI.
     * 
     * @param rootFinderClass Wrapper class used by end-user, that ultimately
     *        calls this finder method.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param propertiesFileName The property file name.
     * 
     * @param defaultImplName Default implementation name.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */    
    public static Object find(Class rootFinderClass,
                              Class spi,
                              String propertiesFileName,
                              String defaultImplName)
        throws DiscoveryException
    {
        ClassFinder classFinder = new ClassFinder(spi, nullGroupContext, rootFinderClass);
        return loadClass(classFinder,
                         loadProperties(classFinder, propertiesFileName),
                         defaultImplName);
    }

    /**
     * Find implementation of SPI.
     * 
     * @param rootFinderClass Wrapper class used by end-user, that ultimately
     *        calls this finder method.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param groupContext qualifier for the property file name and for
     *        the system property name used to find the service implementation.
     *        If not found, the unqualified names are used.
     * 
     * @param properties Used to determine name of SPI implementation,
     *                   and passed to implementation.init() method if
     *                   implementation implements Service interface.
     * 
     * @param defaultImplName Default implementation name.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */    
    public static Object find(Class rootFinderClass,
                              Class spi,
                              String groupContext,
                              Properties properties,
                              String defaultImplName)
        throws DiscoveryException
    {
        return loadClass(new ClassFinder(spi, groupContext, rootFinderClass),
                         properties, defaultImplName);
    }

    /**
     * Find implementation of SPI.
     * 
     * @param rootFinderClass Wrapper class used by end-user, that ultimately
     *        calls this finder method.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param groupContext qualifier for the property file name and for
     *        the system property name used to find the service implementation.
     *        If not found, the unqualified names are used.
     * 
     * @param propertiesFileName The property file name.
     * 
     * @param defaultImplName Default implementation name.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */    
    public static Object find(Class rootFinderClass,
                              Class spi,
                              String groupContext,
                              String propertiesFileName,
                              String defaultImplName)
        throws DiscoveryException
    {
        ClassFinder classFinder = new ClassFinder(spi, groupContext, rootFinderClass);
        return loadClass(classFinder,
                         loadProperties(classFinder, propertiesFileName),
                         defaultImplName);
    }

    
    /************************* CORE LOADERS *************************
     */
    
    /**
     * Load implementation of SPI.
     * 
     * @param ClassFinder  Represents the spiContext, class loaders
     *        (including root finder class), and the groupContext.
     * 
     * @param properties Used to determine name of SPI implementation,
     *                   and passed to implementation.init() method if
     *                   implementation implements Service interface.
     * 
     * @param defaultImplName Default implementation name.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */
    private static Object loadClass(ClassFinder classFinder,
                                    Properties properties,
                                    String defaultImplName)
        throws DiscoveryException
    {
        /**
         * Return previously registered service object (not class)
         * for this spi.  Try each class loader in succession.
         */
        Object service = null;
        ClassLoader[] allLoaders = classFinder.getAllLoaders();

        for (int idx = 0; service == null  &&  idx < allLoaders.length; idx++) {
            service = get(classFinder.getSPIContext().getSPI().getName(), allLoaders[idx]);
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
                        // Fourth, try the fallback implementation class,
                        // but limit loaders to 'system' loaders, in an
                        // attempt to ensure that the default picked up is
                        // the one that one intended.
                        clazz = classFinder.findClass(defaultImplName, true);
                        
                        if (clazz == null) {
                            throw new DiscoveryException
                                ("No implementation defined for " +
                                 classFinder.getSPIContext().getSPI().getName());
                        }
                    }
                }
            }
            
            if (clazz != null) {
                try {
                    service = clazz.newInstance();
                    put(classFinder.getSPIContext().getSPI().getName(), clazz.getClassLoader(), service);
                } catch (Exception e) {
                    throw new DiscoveryException("Unable to instantiate " + classFinder.getSPIContext().getSPI().getName(), e);
                }
                
                if (service instanceof Service) {
                    ((Service)service).init(properties);
                }
            }
        }

        return service;
    }
    
    /**
     * Load property file (qualified by groupContext param to classFinder).
     * 
     * @param ClassFinder  Represents the spiContext, class loaders
     *        (including root finder class), and the groupContext.
     * 
     * @param propertiesFileName The property file name.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */    
    private static Properties loadProperties(ClassFinder classFinder,
                                             String propertiesFileName)
        throws DiscoveryException
    {
        Properties properties = null;
        
        if (propertiesFileName != null) {
            try {
                InputStream stream =
                    classFinder.findResourceAsStream(propertiesFileName);
    
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
        
        return properties;
    }
    
    
    /************************* SPI LIFE-CYCLE SUPPORT *************************/
    
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

    
    /************************* SPI CACHE SUPPORT *************************/

    /**
     * Sets of previously encountered service interfaces (spis), keyed by the
     * interface (<code>Class</code>).  Each element is a ServiceCache.
     */
    private static final Hashtable service_caches = new Hashtable(13);
    
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
