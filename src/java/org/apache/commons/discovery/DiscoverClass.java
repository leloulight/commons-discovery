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

import java.util.Properties;

import org.apache.commons.discovery.load.SPIContext;
import org.apache.commons.discovery.strategy.LoadStrategy;
import org.apache.commons.discovery.strategy.DefaultLoadStrategy;


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
    private final Class  rootFinderClass;
    private final String groupContext;
    
    DiscoverClass() {
        this(DiscoverClass.class, DiscoverSingleton.nullGroupContext);
    }
    
    /**
     * @param rootFinderClass Wrapper class used by end-user, that ultimately
     *        calls this finder method.  The root finder class is used to
     *        determine the 'real' caller, and hence the caller's class loader -
     *        thereby preserving knowledge that is relevant to finding the
     *        correct/expected implementation class.
     */
    DiscoverClass(Class rootFinderClass) {
        this(rootFinderClass, DiscoverSingleton.nullGroupContext);
    }
    
    /**
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
    DiscoverClass(String groupContext) {
        this(DiscoverClass.class, groupContext);
    }
    
    /**
     * @param rootFinderClass Wrapper class used by end-user, that ultimately
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
    DiscoverClass(Class rootFinderClass, String groupContext) {
        this.rootFinderClass = rootFinderClass;
        this.groupContext = groupContext;
    }
    
    /**
     * Find class implementing SPI.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @return Class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded, or if
     *            the resulting class does not implement (or extend) the SPI.
     */
    public Class find(Class spi)
        throws DiscoveryException
    {
        return find(spi, DiscoverSingleton.nullProperties, DiscoverSingleton.nullDefaultImplName);
    }

    /**
     * Find class implementing SPI.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param properties Used to determine name of SPI implementation.
     * 
     * @return Class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded, or if
     *            the resulting class does not implement (or extend) the SPI.
     */
    public Class find(Class spi, Properties properties)
        throws DiscoveryException
    {
        return find(spi, properties, DiscoverSingleton.nullDefaultImplName);
    }

    /**
     * Find class implementing SPI.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param defaultImplName Default implementation name.
     * 
     * @return Class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded, or if
     *            the resulting class does not implement (or extend) the SPI.
     */
    public Class find(Class spi, String defaultImplName)
        throws DiscoveryException
    {
        return find(spi, DiscoverSingleton.nullProperties, defaultImplName);
    }

    /**
     * Find class implementing SPI.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param properties Used to determine name of SPI implementation,.
     * 
     * @param defaultImplName Default implementation name.
     * 
     * @return Class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded, or if
     *            the resulting class does not implement (or extend) the SPI.
     */
    public Class find(Class spi, Properties properties, String defaultImplName)
        throws DiscoveryException
    {
        SPIContext spiContext = new SPIContext(groupContext, spi);
        LoadStrategy strategy = new DefaultLoadStrategy(spiContext, rootFinderClass);
        return strategy.loadClass(properties, defaultImplName);
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
     * @param defaultImplName Default implementation name.
     * 
     * @return Class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded, or if
     *            the resulting class does not implement (or extend) the SPI.
     */    
    public Class find(Class spi, String propertiesFileName, String defaultImplName)
        throws DiscoveryException
    {
        SPIContext spiContext = new SPIContext(groupContext, spi);
        LoadStrategy strategy = new DefaultLoadStrategy(spiContext, rootFinderClass);
        return strategy.loadClass(strategy.loadProperties(propertiesFileName), defaultImplName);
    }

    
    /**
     * Create new instance of class implementing SPI.
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
    public Object newInstance(Class spi)
        throws DiscoveryException
    {
        return newInstance(spi, DiscoverSingleton.nullProperties, DiscoverSingleton.nullDefaultImplName);
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
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */
    public Object newInstance(Class spi, Properties properties)
        throws DiscoveryException
    {
        return newInstance(spi, properties, DiscoverSingleton.nullDefaultImplName);
    }

    /**
     * Create new instance of class implementing SPI.
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
    public Object newInstance(Class spi, String defaultImplName)
        throws DiscoveryException
    {
        return newInstance(spi, DiscoverSingleton.nullProperties, defaultImplName);
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
     * @param defaultImplName Default implementation name.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */
    public Object newInstance(Class spi, Properties properties, String defaultImplName)
        throws DiscoveryException
    {
        return newInstance(spi, find(spi, properties, defaultImplName), properties);
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
     * @param defaultImplName Default implementation name.
     * 
     * @return Instance of a class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */    
    public Object newInstance(Class spi, String propertiesFileName, String defaultImplName)
        throws DiscoveryException
    {
        SPIContext spiContext = new SPIContext(groupContext, spi);
        LoadStrategy strategy = new DefaultLoadStrategy(spiContext, rootFinderClass);
        Properties properties = strategy.loadProperties(propertiesFileName);
        return newInstance(spi,
                           strategy.loadClass(properties, defaultImplName),
                           properties);
    }
    
    /**
     * Instantiate SPI.
     *   If the loaded class implements the <code>Service</code> interface,
     *   then invoke the <code>init(Properties)</code> method, passing in the
     *   <code>Properties properties</code> parameter, if provided.
     * 
     * @param spi Service Provider Interface Class.
     * 
     * @param impl Class implementing SPI, class to instantiate.
     * 
     * @param properties if impl implements Service, call impl.init(properties).
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, if the class cannot be loaded and
     *            instantiated, or if the resulting class does not implement
     *            (or extend) the SPI.
     */
    private Object newInstance(Class spi, Class impl, Properties properties)
        throws DiscoveryException
    {
        Object service = null;
    
        try {
            service = impl.newInstance();
        } catch (Exception e) {
            throw new DiscoveryException("Unable to instantiate " + impl.getName() + " for " + spi.getName(), e);
        }

        if (service instanceof Service) {
            ((Service)service).init(groupContext, properties);
        }

        return service;
    }
}
