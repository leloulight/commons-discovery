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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.discovery.types.Environment;
import org.apache.commons.discovery.types.ImplClass;
import org.apache.commons.discovery.types.SPInterface;
import org.apache.commons.discovery.load.ClassLoaderUtils;


/**
 * <p>Discover singleton service providers,  with discovery and configuration features
 * similar to that employed by standard Java APIs such as JAXP.
 * </p>
 * 
 * <p>DiscoverSingleton instances are managed (life-cycle) by the Discovery
 * service, which maintains a cache keyed by a combination of
 * <ul>
 *   <li>thread context class loader,</li>
 *   <li>groupContext, and</li>
 *   <li>SPI.</li>
 * </ul>
 * This DOES allow multiple instances of a given <i>singleton</i> class
 * to exist for different class loaders and different group contexts.
 * </p>
 * 
 * <p>In the context of this package, a service interface is defined by a
 * Service Provider Interface (SPI).  The SPI is expressed as a Java interface,
 * abstract class, or (base) class that defines an expected programming
 * interface.
 * </p>
 * 
 * <p>DiscoverSingleton provides the <code>find</code> methods for locating and
 * instantiating a singleton instance of an implementation of a service (SPI).
 * Each form of <code>find</code> varies slightly, but they all perform the
 * same basic function.
 * 
 * The simplest <code>find</code> methods are intended for direct use by
 * components looking for a service.  If you are not sure which finder(s)
 * to use, you can narrow your search to one of these:
 * <ul>
 * <li>static Object find(Class spi);</li>
 * <li>static Object find(Class spi, Properties properties);</li>
 * <li>static Object find(Class spi, String defaultImpl);</li>
 * <li>static Object find(Class spi, Properties properties, String defaultImpl);</li>
 * <li>static Object find(Class spi, String propertiesFileName, String defaultImpl);</li>
 * <li>static Object find(String groupContext, Class spi,
 *                        Properties properties, String defaultImpl);</li>
 * <li>static Object find(String groupContext, Class spi,
 *                        String propertiesFileName, String defaultImpl);</li>
 * </ul>
 * 
 * The <code>DiscoverSingleton.find</code> methods proceed as follows:
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
 *     <li>
 *     The value of the (scoped) system property whose name is the same as
 *     the SPI's fully qualified class name (as given by SPI.class.getName()).
 *     The <code>ScopedProperties</code> class provides a way to bind
 *     properties by classloader, in a secure hierarchy similar in concept
 *     to the way classloader find class and resource files.
 *     See <code>ScopedProperties</code> for more details.
 *     <p>If the ScopedProperties are not set by users, then behaviour
 *     is equivalent to <code>System.getProperty()</code>.
 *     </p>
 *     </li>
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
 *     <li>DiscoverSingleton's Caller's Class Loader</li>
 *     <li>SPI's Class Loader</li>
 *     <li>DiscoverSingleton's (this class or wrapper) Class Loader</li>
 *     <li>System Class Loader</li>
 *   </ul>
 *   An exception is thrown if the class cannot be loaded.
 *   </li></p>
 *   <p><li>
 *   If the name of the implementation class is null, AND the default
 *   implementation class (<code>defaultImpl</code>) is null,
 *   then an exception is thrown.
 *   </li></p>
 *   <p><li>
 *   If the name of the implementation class is null, AND the default
 *   implementation class (<code>defaultImpl</code>) is non-null,
 *   then load the default implementation class.  The class loaded is the
 *   first class loaded by the following sequence of class loaders:
 *   <ul>
 *     <li>SPI's Class Loader</li>
 *     <li>DiscoverSingleton's (this class or wrapper) Class Loader</li>
 *     <li>System Class Loader</li>
 *   </ul>
 *   <p>
 *   This limits the scope in which the default class loader can be found
 *   to the SPI, DiscoverSingleton, and System class loaders.  The assumption here
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
 *   Create an instance of the class.
 *   </li></p>
 *   <p><li>
 *   If the loaded class implements the <code>Service</code> interface,
 *   then invoke the <code>init(Properties)</code> method, passing in the
 *   <code>Properties properties</code> parameter, if provided.
 *   </li></p>
 * </ul>
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
 *     <li>DiscoverSingleton's Caller's Class Loader</li>
 *     <li>SPI's Class Loader</li>
 *     <li>DiscoverSingleton's (this class) Class Loader</li>
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
 * <p><strong>IMPLEMENTATION NOTE</strong> - This implementation is modelled
 * after the SAXParserFactory and DocumentBuilderFactory implementations
 * (corresponding to the JAXP pluggability APIs) found in Apache Xerces.
 * </p>
 * 
 * @author Richard A. Sitze
 * @author Craig R. McClanahan
 * @author Costin Manolache
 * @version $Revision$ $Date$
 */
public class DiscoverSingleton {
    /********************** (RELATIVELY) SIMPLE FINDERS **********************
     * 
     * These finders are suitable for direct use in components looking for a
     * service.  If you are not sure which finder(s) to use, you can narrow
     * your search to one of these.
     */
    
    /**
     * Find implementation of SPI.
     * 
     * @param spiClass Service Provider Interface Class.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */
    public static Object find(Class spiClass)
        throws DiscoveryException
    {
        return find(DiscoverSingleton.class, spiClass);
    }

    /**
     * Find implementation of SPI.
     * 
     * @param spiClass Service Provider Interface Class.
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
    public static Object find(Class spiClass, Properties properties)
        throws DiscoveryException
    {
        return find(DiscoverSingleton.class, spiClass, properties);
    }

    /**
     * Find implementation of SPI.
     * 
     * @param spiClass Service Provider Interface Class.
     * 
     * @param defaultImpl Default implementation.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */
    public static Object find(Class spiClass, String defaultImpl)
        throws DiscoveryException
    {
        return find(DiscoverSingleton.class, spiClass, defaultImpl);
    }

    /**
     * Find implementation of SPI.
     * 
     * @param spiClass Service Provider Interface Class.
     * 
     * @param properties Used to determine name of SPI implementation,
     *                   and passed to implementation.init() method if
     *                   implementation implements Service interface.
     * 
     * @param defaultImpl Default implementation.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */
    public static Object find(Class spiClass,
                              Properties properties,
                              String defaultImpl)
        throws DiscoveryException
    {
        return find(DiscoverSingleton.class, spiClass, properties, defaultImpl);
    }
    
    /**
     * Find implementation of SPI.
     * 
     * @param spiClass Service Provider Interface Class.
     * 
     * @param propertiesFileName The property file name.
     * 
     * @param defaultImpl Default implementation.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */    
    public static Object find(Class spiClass,
                              String propertiesFileName,
                              String defaultImpl)
        throws DiscoveryException
    {
        return find(DiscoverSingleton.class, spiClass, propertiesFileName, defaultImpl);
    }

    /**
     * Find implementation of SPI.
     * 
     * @param groupContext qualifier for the name of the system property
     *        used to find the service implementation.  If a system property
     *        cannot be found by that name, then the unqualified property
     *        name is used.
     * 
     * @param spiClass Service Provider Interface Class.
     * 
     * @param properties Used to determine name of SPI implementation,
     *                   and passed to implementation.init() method if
     *                   implementation implements Service interface.
     * 
     * @param defaultImpl Default implementation.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */
    public static Object find(String groupContext,
                              Class spiClass,
                              Properties properties,
                              String defaultImpl)
        throws DiscoveryException
    {
        return find(DiscoverSingleton.class, groupContext, spiClass, properties, defaultImpl);
    }
    
    /**
     * Find implementation of SPI unique to a group context.
     * 
     * @param groupContext qualifier for the property file name and for
     *        the system property name used to find the service implementation.
     *        If not found, the unqualified names are used.
     * 
     * @param spiClass Service Provider Interface Class.
     * 
     * @param propertiesFileName The (qualified and unqualified) property file
     *        name.
     * 
     * @param defaultImpl Default implementation.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */    
    public static Object find(String groupContext,
                              Class spiClass,
                              String propertiesFileName,
                              String defaultImpl)
        throws DiscoveryException
    {
        return find(DiscoverSingleton.class, groupContext, spiClass, propertiesFileName, defaultImpl);
    }

    
    /*************** FINDERS FOR USE IN FACTORY/HELPER METHODS ***************
     * 
     * These finders provide a rootFinderClass.  The root finder is the wrapper
     * class (factories or helper classes) that invoke the DiscoverSingleton find
     * methods, presumably providing (default) values for propertiesFileName
     * and defaultImpl.  Having access to this wrapper class provides a
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
     * @param spiClass Service Provider Interface Class.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded, or if
     *            the resulting class does not implement the SPI.
     */
    public static Object find(Class rootFinderClass, Class spiClass)
        throws DiscoveryException
    {
        return find(rootFinderClass, spiClass, DiscoverClass.nullProperties, DiscoverClass.nullDefaultImpl);
    }

    /**
     * Find implementation of SPI.
     * 
     * @param rootFinderClass Wrapper class used by end-user, that ultimately
     *        calls this finder method.
     * 
     * @param spiClass Service Provider Interface Class.
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
    public static Object find(Class rootFinderClass, Class spiClass, Properties properties)
        throws DiscoveryException
    {
        return find(rootFinderClass, spiClass, properties, DiscoverClass.nullDefaultImpl);
    }

    /**
     * Find implementation of SPI.
     * 
     * @param rootFinderClass Wrapper class used by end-user, that ultimately
     *        calls this finder method.
     * 
     * @param spiClass Service Provider Interface Class.
     * 
     * @param defaultImpl Default implementation name.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */
    public static Object find(Class rootFinderClass, Class spiClass, String defaultImpl)
        throws DiscoveryException
    {
        return find(rootFinderClass, spiClass, DiscoverClass.nullProperties, defaultImpl);
    }

    /**
     * Find implementation of SPI.
     * 
     * @param rootFinderClass Wrapper class used by end-user, that ultimately
     *        calls this finder method.
     * 
     * @param spiClass Service Provider Interface Class.
     * 
     * @param properties Used to determine name of SPI implementation,
     *                   and passed to implementation.init() method if
     *                   implementation implements Service interface.
     * 
     * @param defaultImpl Default implementation.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */
    public static Object find(Class rootFinderClass,
                              Class spiClass,
                              Properties properties,
                              String defaultImpl)
        throws DiscoveryException
    {
        return find(rootFinderClass, DiscoverClass.nullGroupContext, spiClass, properties, defaultImpl);
    }
    
    /**
     * Find implementation of SPI.
     * 
     * @param rootFinderClass Wrapper class used by end-user, that ultimately
     *        calls this finder method.
     * 
     * @param spiClass Service Provider Interface Class.
     * 
     * @param propertiesFileName The property file name.
     * 
     * @param defaultImpl Default implementation.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */    
    public static Object find(Class rootFinderClass,
                              Class spiClass,
                              String propertiesFileName,
                              String defaultImpl)
        throws DiscoveryException
    {
        return find(rootFinderClass, DiscoverClass.nullGroupContext,
                    spiClass, propertiesFileName, defaultImpl);
    }

    /**
     * Find implementation of SPI.
     * 
     * @param rootFinderClass Wrapper class used by end-user, that ultimately
     *        calls this finder method.
     * 
     * @param groupContext qualifier for the property file name and for
     *        the system property name used to find the service implementation.
     *        If not found, the unqualified names are used.
     * 
     * @param spiClass Service Provider Interface Class.
     * 
     * @param properties Used to determine name of SPI implementation,
     *                   and passed to implementation.init() method if
     *                   implementation implements Service interface.
     * 
     * @param defaultImpl Default implementation.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */    
    public static Object find(Class rootFinderClass,
                              String groupContext,
                              Class spiClass,
                              Properties properties,
                              String defaultImpl)
        throws DiscoveryException
    {
        /**
         * Env can change between calls... so we don't cache this.
         */
        return find(new Environment(groupContext, rootFinderClass),
                    new SPInterface(spiClass),
                    properties,
                    new ImplClass(defaultImpl));
    }

    /**
     * Find implementation of SPI.
     * 
     * @param rootFinderClass Wrapper class used by end-user, that ultimately
     *        calls this finder method.
     * 
     * @param groupContext qualifier for the property file name and for
     *        the system property name used to find the service implementation.
     *        If not found, the unqualified names are used.
     * 
     * @param spiClass Service Provider Interface Class.
     * 
     * @param propertiesFileName The property file name.
     * 
     * @param defaultImpl Default implementation.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */    
    public static Object find(Class  rootFinderClass,
                              String groupContext,
                              Class  spiClass,
                              String propertiesFileName,
                              String defaultImpl)
        throws DiscoveryException
    {
        /**
         * Env can change between calls... so we don't cache this.
         */
        return find(new Environment(groupContext, rootFinderClass),
                    new SPInterface(spiClass),
                    propertiesFileName,
                    new ImplClass(defaultImpl));
    }

    /**
     * Find implementation of SPI.
     * 
     * @param rootFinderClass Wrapper class used by end-user, that ultimately
     *        calls this finder method.
     * 
     * @param groupContext qualifier for the property file name and for
     *        the system property name used to find the service implementation.
     *        If not found, the unqualified names are used.
     * 
     * @param spiClass Service Provider Interface Class.
     * 
     * @param properties Used to determine name of SPI implementation,
     *                   and passed to implementation.init() method if
     *                   implementation implements Service interface.
     * 
     * @param defaultImpl Default implementation.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */    
    public static Object find(Class rootFinderClass,
                              String groupContext,
                              SPInterface spi,
                              Properties properties,
                              ImplClass defaultImpl)
        throws DiscoveryException
    {
        /**
         * Env can change between calls... so we don't cache this.
         */
        return find(new Environment(groupContext, rootFinderClass),
                    spi,
                    properties,
                    defaultImpl);
    }

    /**
     * Find implementation of SPI.
     * 
     * @param rootFinderClass Wrapper class used by end-user, that ultimately
     *        calls this finder method.
     * 
     * @param groupContext qualifier for the property file name and for
     *        the system property name used to find the service implementation.
     *        If not found, the unqualified names are used.
     * 
     * @param spiClass Service Provider Interface Class.
     * 
     * @param propertiesFileName The property file name.
     * 
     * @param defaultImpl Default implementation.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */    
    public static Object find(Class  rootFinderClass,
                              String groupContext,
                              SPInterface spi,
                              String propertiesFileName,
                              ImplClass defaultImpl)
        throws DiscoveryException
    {
        /**
         * Env can change between calls... so we don't cache this.
         */
        return find(new Environment(groupContext, rootFinderClass),
                    spi,
                    propertiesFileName,
                    defaultImpl);
    }

    /**
     * Find implementation of SPI.
     * 
     * @param rootFinderClass Wrapper class used by end-user, that ultimately
     *        calls this finder method.
     * 
     * @param groupContext qualifier for the property file name and for
     *        the system property name used to find the service implementation.
     *        If not found, the unqualified names are used.
     * 
     * @param spiClass Service Provider Interface Class.
     * 
     * @param properties Used to determine name of SPI implementation,
     *                   and passed to implementation.init() method if
     *                   implementation implements Service interface.
     * 
     * @param defaultImpl Default implementation.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */    
    public static Object find(Environment env,
                              SPInterface spi,
                              Properties properties,
                              ImplClass defaultImpl)
        throws DiscoveryException
    {
        /**
         * Return previously registered service object (not class)
         * for this spi, bound only to current thread context class loader.
         */
        Object service = get(env.getThreadContextClassLoader(), env.getGroupContext(), spi.getSPName());

        if (service == null) {
            service = DiscoverClass.newInstance(env, spi, properties, defaultImpl);

            if (service != null) {
                put(env.getThreadContextClassLoader(), env.getGroupContext(), spi.getSPName(), service);
            }
        }

        return service;
    }

    /**
     * Find implementation of SPI.
     * 
     * @param rootFinderClass Wrapper class used by end-user, that ultimately
     *        calls this finder method.
     * 
     * @param groupContext qualifier for the property file name and for
     *        the system property name used to find the service implementation.
     *        If not found, the unqualified names are used.
     * 
     * @param spiClass Service Provider Interface Class.
     * 
     * @param propertiesFileName The property file name.
     * 
     * @param defaultImpl Default implementation.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */    
    public static Object find(Environment env,
                              SPInterface spi,
                              String propertiesFileName,
                              ImplClass defaultImpl)
        throws DiscoveryException
    {
        /**
         * Return previously registered service object (not class)
         * for this spi, bound only to current thread context class loader.
         */
        Object service = get(env.getThreadContextClassLoader(), env.getGroupContext(), spi.getSPName());

        if (service == null) {
            service = DiscoverClass.newInstance(env, spi, propertiesFileName, defaultImpl);

            if (service != null) {
                put(env.getThreadContextClassLoader(), env.getGroupContext(), spi.getSPName(), service);
            }
        }

        return service;
    }

    
    /************************* SPI LIFE-CYCLE SUPPORT *************************/
    
    /**
     * Release all internal references to previously created service
     * instances associated with the current thread context class loader.
     * The <code>release()</code> method is called for service instances that
     * implement the <code>Service</code> interface.
     *
     * This is useful in environments like servlet containers,
     * which implement application reloading by throwing away a ClassLoader.
     * Dangling references to objects in that class loader would prevent
     * garbage collection.
     */
    public static void release() {
        ClassLoader threadContextClassLoader =
            ClassLoaderUtils.getThreadContextClassLoader();

        /**
         * 'null' (bootstrap/system class loader) thread context class loader
         * is ok...  Until we learn otherwise.
         */
        synchronized (root_cache) {
            HashMap groups = (HashMap)root_cache.get(threadContextClassLoader);

            if (groups != null) {
                Iterator groupIter = groups.values().iterator();

                while (groupIter.hasNext()) {
                    HashMap spis = (HashMap)groupIter.next();
                    
                    if (spis != null) {
                        Iterator spiIter = spis.values().iterator();
        
                        while (spiIter.hasNext()) {
                            Object service = (Object)spiIter.next();
                            
                            if (service instanceof SingletonService)
                                ((SingletonService)service).release();
                        }
                        spis.clear();
                    }
                }
                groups.clear();
            }
            root_cache.remove(threadContextClassLoader);
        }
    }
    
    
    /**
     * Release any internal references to a previously created service
     * instance associated with the current thread context class loader.
     * If the SPI instance implements <code>Service</code>, then call
     * <code>release()</code>.
     */
    public static void release(String groupContext) {
        ClassLoader threadContextClassLoader =
            ClassLoaderUtils.getThreadContextClassLoader();

        /**
         * 'null' (bootstrap/system class loader) thread context class loader
         * is ok...  Until we learn otherwise.
         */
        synchronized (root_cache) {
            HashMap groups = (HashMap)root_cache.get(threadContextClassLoader);

            if (groups != null) {
                HashMap spis = (HashMap)groups.get(groupContext);
                
                if (spis != null) {
                    Iterator spiIter = spis.values().iterator();
    
                    while (spiIter.hasNext()) {
                        Object service = (Object)spiIter.next();
                        
                        if (service instanceof SingletonService)
                            ((SingletonService)service).release();
                    }
                    spis.clear();
                }
                groups.remove(groupContext);
            }
            root_cache.remove(threadContextClassLoader);
        }
    }

    /**
     * Release any internal references to a previously created service
     * instance associated with the current thread context class loader.
     * If the SPI instance implements <code>Service</code>, then call
     * <code>release()</code>.
     */
    public static void release(String groupContext, Class spiClass) {
        ClassLoader threadContextClassLoader =
            ClassLoaderUtils.getThreadContextClassLoader();

        /**
         * 'null' (bootstrap/system class loader) thread context class loader
         * is ok...  Until we learn otherwise.
         */
        if (spiClass != null) {
            synchronized (root_cache) {
                HashMap groups = (HashMap)root_cache.get(threadContextClassLoader);

                if (groups != null) {
                    HashMap spis = (HashMap)groups.get(groupContext);

                    if (spis != null) {
                        Object service = (Object)spis.get(spiClass.getName());
                        
                        if (service instanceof SingletonService)
                            ((SingletonService)service).release();
                        
                        spis.remove(spiClass.getName());
                    }
                }
            }
        }
    }

    /**
     * Release any internal references to a previously created service
     * instance associated with the current thread context class loader.
     * If the SPI instance implements <code>Service</code>, then call
     * <code>release()</code>.
     */
    public static void release(Class spiClass) {
        release(DiscoverClass.nullGroupContext, spiClass);
    }
    
    
    /************************* SPI CACHE SUPPORT *************************
     * 
     * Cache services by a 'key' unique to the requesting class/environment:
     * 
     * When we 'release', it is expected that the caller of the 'release'
     * have the same thread context class loader... as that will be used
     * to identify all cached entries to be released.
     * 
     * We will manage synchronization directly, so all caches are implemented
     * as HashMap (unsynchronized).
     * 
     * - ClassLoader::groupContext::SPI::Instance Cache
     *         Cache : HashMap
     *         Key   : Thread Context Class Loader (<code>ClassLoader</code>).
     *         Value : groupContext::SPI Cache (<code>HashMap</code>).
     * 
     * - groupContext::SPI::Instance Cache
     *         Cache : HashMap
     *         Key   : groupContext (<code>String</code>).
     *         Value : SPI Cache (<code>HashMap</code>).
     * 
     * - SPI::Instance Cache
     *         Cache : HashMap
     *         Key   : SPI Class Name (<code>String</code>).
     *         Value : SPI Instance/Implementation (<code>Object</code>.
     */

    /**
     * Allows null key, important as default groupContext is null.
     */
    private static final HashMap root_cache = new HashMap();

    /**
     * Initial hash size for SPI's, default just seem TO big today..
     */
    private static final int smallHashSize = 13;
    
    /**
     * Get service keyed by spi & classLoader.
     */
    static Object get(ClassLoader loader, String groupContext, String spiName)
    {
        Object service = null;

        /**
         * 'null' (bootstrap/system class loader) thread context class loader
         * is ok...  Until we learn otherwise.
         */
        synchronized (root_cache) {
            HashMap groups =
                (HashMap)root_cache.get(loader);

            if (groups != null) {
                HashMap spis =
                    (HashMap)groups.get(groupContext);

                if (spis != null) {
                    
                    service = (Object)spis.get(spiName);
                }
            }
        }

        return service;
    }
    
    /**
     * Put service keyed by spi & classLoader.
     */
    static void put(ClassLoader loader, String groupContext, String spiName,
                    Object service)
    {
        /**
         * 'null' (bootstrap/system class loader) thread context class loader
         * is ok...  Until we learn otherwise.
         */
        if (service != null)
        {
            synchronized (root_cache) {
                HashMap groups =
                    (HashMap)root_cache.get(loader);
                    
                if (groups == null) {
                    groups = new HashMap(smallHashSize);
                    root_cache.put(loader, groups);
                }

                HashMap spis =
                    (HashMap)groups.get(groupContext);

                if (spis == null) {
                    spis = new HashMap(smallHashSize);
                    groups.put(groupContext, spis);
                }

                spis.put(spiName, service);
            }
        }
    }
}
