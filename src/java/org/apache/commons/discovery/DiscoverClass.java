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
import java.util.Properties;

import org.apache.commons.discovery.base.Environment;
import org.apache.commons.discovery.base.ImplClass;
import org.apache.commons.discovery.base.SPInterface;
import org.apache.commons.discovery.strategy.DefaultLoadStrategy;
import org.apache.commons.discovery.strategy.LoadStrategy;


/**
 * <p>Discover class that implements a given service interface,
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
 * <p>DiscoverClass provides the <code>find</code> methods for locating a
 * class that implements a service interface (SPI).  Each form of
 * <code>find</code> varies slightly, but they all perform the same basic
 * function.
 * 
 * The <code>DiscoverClass.find</code> methods proceed as follows:
 * </p>
 * <ul>
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
 *   implementation class name (<code>defaultImpl</code>) is null,
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
public class DiscoverClass {
    /**
     * Readable placeholder for a null value.
     */
    public static final String     nullDefaultImpl = null;

    /**
     * Readable placeholder for a null value.
     */
    public static final Properties nullProperties = null;

    private final String groupContext;
    private final Class  rootDiscoveryClass;
    private final Environment env;

    /**
     * Create a class instance with dynamic environment
     * (thread context class loader is determined on each call).
     */    
    public DiscoverClass() {
        this(DiscoverClass.class, Environment.defaultGroupContext);
    }
    
    /**
     * Create a class instance with dynamic environment
     * (thread context class loader is determined on each call).
     * 
     * @param rootDiscoveryClass Wrapper class used by end-user, that ultimately
     *        calls this finder method.  The root finder class is used to
     *        determine the 'real' caller, and hence the caller's class loader -
     *        thereby preserving knowledge that is relevant to finding the
     *        correct/expected implementation class.
     */
    public DiscoverClass(Class rootDiscoveryClass) {
        this(rootDiscoveryClass, Environment.defaultGroupContext);
    }
    
    /**
     * Create a class instance with dynamic environment
     * (thread context class loader is determined on each call).
     * 
     * @param groupContext qualifier for the property file name and for
     *        the system property name used to find the service implementation.
     *        If not found, the unqualified names are used.
     *     <p>Differentiates services for different logical groups of service
     *        users, that might otherwise be forced to share a common service
     *        implementation, and/or a common configuration of that service.
     *        </p>
     *     <p>The groupContext is used to qualify the name of the property file
     *        name: <code>groupContext + '.' + propertiesFileName</code>.  If
     *        that file is not found, then the unqualified propertyFileName is
     *        used.
     *        </p>
     *     <p>In addition, groupContext is used to qualify the name of the
     *        system property used to find the service implementation by
     *        prepending the value of <code>groupContext</code> to the property
     *        name: <code>groupContext&gt; + '.' + SPI.class.getName()</code>.
     *        Again, if a system property cannot be found by that name, then the
     *        unqualified property name is used.
     *        </p>
     */
    public DiscoverClass(String groupContext) {
        this(DiscoverClass.class, groupContext);
    }
    
    /**
     * Create a class instance with dynamic environment
     * (thread context class loader is determined on each call).
     * 
     * @param rootDiscoveryClass Wrapper class used by end-user, that ultimately
     *        calls this finder method.  The root finder class is used to
     *        determine the 'real' caller, and hence the caller's class loader -
     *        thereby preserving knowledge that is relevant to finding the
     *        correct/expected implementation class.
     * 
     * @param groupContext qualifier for the property file name and for
     *        the system property name used to find the service implementation.
     *        If not found, the unqualified names are used.
     *     <p>Differentiates services for different logical groups of service
     *        users, that might otherwise be forced to share a common service
     *        implementation, and/or a common configuration of that service.
     *        </p>
     *     <p>The groupContext is used to qualify the name of the property file
     *        name: <code>groupContext + '.' + propertiesFileName</code>.  If
     *        that file is not found, then the unqualified propertyFileName is
     *        used.
     *        </p>
     *     <p>In addition, groupContext is used to qualify the name of the
     *        system property used to find the service implementation by
     *        prepending the value of <code>groupContext</code> to the property
     *        name: <code>groupContext&gt; + '.' + SPI.class.getName()</code>.
     *        Again, if a system property cannot be found by that name, then the
     *        unqualified property name is used.
     *        </p>
     */
    public DiscoverClass(Class rootDiscoveryClass, String groupContext) {
        this.groupContext = groupContext;
        this.rootDiscoveryClass = rootDiscoveryClass;
        this.env = null;
    }

    /**
     * Create a class instance with static environment.
     */    
    public DiscoverClass(Environment env) {
        this.groupContext = env.getGroupContext();
        this.rootDiscoveryClass = env.getRootDiscoveryClass();
        this.env = env;
    }

    /**
     * Get current <code>Environment</code>.
     * If a static <code>Environment</code> is not set, then
     * dynamically construct <code>Environment</code> on each call.
     */
    protected Environment getEnvironment() {
        return (env != null)
               ? env
               : new Environment(groupContext, rootDiscoveryClass);
    }

    /**
     * Find class implementing SPI.
     * 
     * @param spiClass Service Provider Interface Class.
     * 
     * @return Class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded, or if
     *            the resulting class does not implement (or extend) the SPI.
     */
    public Class find(Class spiClass)
        throws DiscoveryException
    {
        return find(spiClass, nullProperties, nullDefaultImpl);
    }

    /**
     * Find class implementing SPI.
     * 
     * @param spiClass Service Provider Interface Class.
     * 
     * @param properties Used to determine name of SPI implementation.
     * 
     * @return Class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded, or if
     *            the resulting class does not implement (or extend) the SPI.
     */
    public Class find(Class spiClass, Properties properties)
        throws DiscoveryException
    {
        return find(spiClass, properties, nullDefaultImpl);
    }

    /**
     * Find class implementing SPI.
     * 
     * @param spiClass Service Provider Interface Class.
     * 
     * @param defaultImpl Default implementation name.
     * 
     * @return Class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded, or if
     *            the resulting class does not implement (or extend) the SPI.
     */
    public Class find(Class spiClass, String defaultImpl)
        throws DiscoveryException
    {
        return find(spiClass, nullProperties, defaultImpl);
    }

    /**
     * Find class implementing SPI.
     * 
     * @param spiClass Service Provider Interface Class.
     * 
     * @param properties Used to determine name of SPI implementation,.
     * 
     * @param defaultImpl Default implementation class.
     * 
     * @return Class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded, or if
     *            the resulting class does not implement (or extend) the SPI.
     */
    public Class find(Class spiClass, Properties properties, String defaultImpl)
        throws DiscoveryException
    {
        return find(getEnvironment(),
                    new SPInterface(spiClass),
                    properties,
                    new ImplClass(defaultImpl));
    }
    
    /**
     * Find class implementing SPI.
     * 
     * @param spiClass Service Provider Interface Class.
     * 
     * @param propertiesFileName The property file name.
     *        A property file is loaded using the following sequence of class
     *        loaders:
     *        <ul>
     *          <li>Thread Context Class Loader</li>
     *          <li>DiscoverSingleton's Caller's Class Loader</li>
     *          <li>SPI's Class Loader</li>
     *          <li>DiscoverSingleton's (this class) Class Loader</li>
     *          <li>System Class Loader</li>
     *        </ul>
     * 
     * @param defaultImpl Default implementation.
     * 
     * @return Class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded, or if
     *            the resulting class does not implement (or extend) the SPI.
     */    
    public Class find(Class spiClass, String propertiesFileName, String defaultImpl)
        throws DiscoveryException
    {
        return find(getEnvironment(),
                    new SPInterface(spiClass),
                    propertiesFileName,
                    new ImplClass(defaultImpl));
    }

    /**
     * Find class implementing SPI.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param properties Used to determine name of SPI implementation.
     * 
     * @param propertyName Alternate propertyName for value of name of
     *                     SPI implementation.
     * 
     * @param defaultImpl Default implementation.
     * 
     * @return Class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded, or if
     *            the resulting class does not implement (or extend) the SPI.
     */
    public Class find(SPInterface spi, Properties properties, ImplClass defaultImpl)
        throws DiscoveryException
    {
        return find(getEnvironment(), spi, properties, defaultImpl);
    }
    
    /**
     * Find class implementing SPI.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param propertiesFileName The property file name.
     *        A property file is loaded using the following sequence of class
     *        loaders:
     *        <ul>
     *          <li>Thread Context Class Loader</li>
     *          <li>DiscoverSingleton's Caller's Class Loader</li>
     *          <li>SPI's Class Loader</li>
     *          <li>DiscoverSingleton's (this class) Class Loader</li>
     *          <li>System Class Loader</li>
     *        </ul>
     * 
     * @param propertyName Alternate propertyName for value of name of
     *                     SPI implementation.
     * 
     * @param defaultImpl Default implementation.
     * 
     * @return Class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded, or if
     *            the resulting class does not implement (or extend) the SPI.
     */    
    public Class find(SPInterface spi, String propertiesFileName, ImplClass defaultImpl)
        throws DiscoveryException
    {
        return find(getEnvironment(), spi, propertiesFileName, defaultImpl);
    }


    /**
     * Find class implementing SPI.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param properties Used to determine name of SPI implementation.
     * 
     * @param propertyName Alternate propertyName for value of name of
     *                     SPI implementation.
     * 
     * @param defaultImpl Default implementation.
     * 
     * @return Class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded, or if
     *            the resulting class does not implement (or extend) the SPI.
     */
    public static Class find(Environment env, SPInterface spi, Properties properties, ImplClass defaultImpl)
        throws DiscoveryException
    {
        LoadStrategy strategy = new DefaultLoadStrategy(env, spi);
        return strategy.loadClass(properties, defaultImpl).getImplClass();
    }
    
    /**
     * Find class implementing SPI.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param propertiesFileName The property file name.
     *        A property file is loaded using the following sequence of class
     *        loaders:
     *        <ul>
     *          <li>Thread Context Class Loader</li>
     *          <li>DiscoverSingleton's Caller's Class Loader</li>
     *          <li>SPI's Class Loader</li>
     *          <li>DiscoverSingleton's (this class) Class Loader</li>
     *          <li>System Class Loader</li>
     *        </ul>
     * 
     * @param propertyName Alternate propertyName for value of name of
     *                     SPI implementation.
     * 
     * @param defaultImpl Default implementation.
     * 
     * @return Class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded, or if
     *            the resulting class does not implement (or extend) the SPI.
     */    
    public static Class find(Environment env, SPInterface spi, String propertiesFileName, ImplClass defaultImpl)
        throws DiscoveryException
    {
        LoadStrategy strategy = new DefaultLoadStrategy(env, spi);
        return strategy.loadClass(strategy.loadProperties(propertiesFileName),
                                  defaultImpl).getImplClass();
    }
    
    /**
     * Create new instance of class implementing SPI.
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
    public Object newInstance(Class spiClass)
        throws DiscoveryException,
               InstantiationException,
               IllegalAccessException,
               NoSuchMethodException,
               InvocationTargetException
    {
        return newInstance(spiClass, nullProperties, nullDefaultImpl);
    }

    /**
     * Create new instance of class implementing SPI.
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
    public Object newInstance(Class spiClass, Properties properties)
        throws DiscoveryException,
               InstantiationException,
               IllegalAccessException,
               NoSuchMethodException,
               InvocationTargetException
    {
        return newInstance(spiClass, properties, nullDefaultImpl);
    }

    /**
     * Create new instance of class implementing SPI.
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
    public Object newInstance(Class spiClass, String defaultImpl)
        throws DiscoveryException,
               InstantiationException,
               IllegalAccessException,
               NoSuchMethodException,
               InvocationTargetException
    {
        return newInstance(spiClass, nullProperties, defaultImpl);
    }

    /**
     * Create new instance of class implementing SPI.
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
    public Object newInstance(Class spiClass, Properties properties, String defaultImpl)
        throws DiscoveryException,
               InstantiationException,
               IllegalAccessException,
               NoSuchMethodException,
               InvocationTargetException
    {
        return newInstance(getEnvironment(),
                           new SPInterface(spiClass),
                           properties,
                           new ImplClass(defaultImpl));
    }
    
    /**
     * Create new instance of class implementing SPI.
     * 
     * @param spiClass Service Provider Interface Class.
     * 
     * @param propertiesFileName The property file name.
     *        A property file is loaded using the following sequence of class
     *       loaders:
     *      <ul>
     *        <li>Thread Context Class Loader</li>
     *        <li>DiscoverSingleton's Caller's Class Loader</li>
     *        <li>SPI's Class Loader</li>
     *        <li>DiscoverSingleton's (this class) Class Loader</li>
     *        <li>System Class Loader</li>
     *      </ul>
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
    public Object newInstance(Class spiClass, String propertiesFileName, String defaultImpl)
        throws DiscoveryException,
               InstantiationException,
               IllegalAccessException,
               NoSuchMethodException,
               InvocationTargetException
    {
        return newInstance(getEnvironment(),
                           new SPInterface(spiClass),
                           propertiesFileName,
                           new ImplClass(defaultImpl));
    }

    /**
     * Create new instance of class implementing SPI.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param properties Used to determine name of SPI implementation,
     *                   and passed to implementation.init() method if
     *                   implementation implements Service interface.
     * 
     * @param propertyName Alternate propertyName for value of name of
     *                     SPI implementation.
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
    public Object newInstance(SPInterface spi, Properties properties, ImplClass defaultImpl)
        throws DiscoveryException,
               InstantiationException,
               IllegalAccessException,
               NoSuchMethodException,
               InvocationTargetException
    {
        return newInstance(getEnvironment(), spi, properties, defaultImpl);
    }
    
    /**
     * Create new instance of class implementing SPI.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param propertiesFileName The property file name.
     *        A property file is loaded using the following sequence of class
     *       loaders:
     *      <ul>
     *        <li>Thread Context Class Loader</li>
     *        <li>DiscoverSingleton's Caller's Class Loader</li>
     *        <li>SPI's Class Loader</li>
     *        <li>DiscoverSingleton's (this class) Class Loader</li>
     *        <li>System Class Loader</li>
     *      </ul>
     * 
     * @param propertyName Alternate propertyName for value of name of
     *                     SPI implementation.
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
    public Object newInstance(SPInterface spi, String propertiesFileName, ImplClass defaultImpl)
        throws DiscoveryException,
               InstantiationException,
               IllegalAccessException,
               NoSuchMethodException,
               InvocationTargetException
    {
        return newInstance(getEnvironment(), spi, propertiesFileName, defaultImpl);
    }


    /**
     * Create new instance of class implementing SPI.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param properties Used to determine name of SPI implementation,
     *                   and passed to implementation.init() method if
     *                   implementation implements Service interface.
     * 
     * @param propertyName Alternate propertyName for value of name of
     *                     SPI implementation.
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
    public static Object newInstance(Environment env, SPInterface spi, Properties properties, ImplClass defaultImpl)
        throws DiscoveryException,
               InstantiationException,
               IllegalAccessException,
               NoSuchMethodException,
               InvocationTargetException
    {
        LoadStrategy strategy = new DefaultLoadStrategy(env, spi);
        return strategy.loadClass(properties, defaultImpl).newInstance();
    }
    
    /**
     * Create new instance of class implementing SPI.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param propertiesFileName The property file name.
     *        A property file is loaded using the following sequence of class
     *       loaders:
     *      <ul>
     *        <li>Thread Context Class Loader</li>
     *        <li>DiscoverSingleton's Caller's Class Loader</li>
     *        <li>SPI's Class Loader</li>
     *        <li>DiscoverSingleton's (this class) Class Loader</li>
     *        <li>System Class Loader</li>
     *      </ul>
     * 
     * @param propertyName Alternate propertyName for value of name of
     *                     SPI implementation.
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
    public static Object newInstance(Environment env, SPInterface spi, String propertiesFileName, ImplClass defaultImpl)
        throws DiscoveryException,
               InstantiationException,
               IllegalAccessException,
               NoSuchMethodException,
               InvocationTargetException
    {
        LoadStrategy strategy = new DefaultLoadStrategy(env, spi);
        Properties properties = strategy.loadProperties(propertiesFileName);
        return strategy.loadClass(properties, defaultImpl).newInstance();
    }
}
