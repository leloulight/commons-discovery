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

package org.apache.commons.discovery.strategy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.discovery.DiscoveryException;
import org.apache.commons.discovery.load.Loaders;
import org.apache.commons.discovery.load.SPIContext;


/**
 * <p>Implement the search strategy.  Someday this might be pluggable..
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
public class DefaultLoadStrategy implements LoadStrategy {
    private final SPIContext spiContext;
    private final Loaders loaders;
    
    public DefaultLoadStrategy(SPIContext spiContext, Class rootFinderClass) {
        this.spiContext = spiContext;
        this.loaders = new Loaders(spiContext, rootFinderClass);
    }
    
    /**
     * Load SPI implementation's Class.
     * 
     * The <code>loadInstance</code> methods proceed as follows:
     * </p>
     * <ul>
     *   <p><li>
     *   Get the name of an implementation class.  The name is the first
     *   non-null value obtained from the following resources:
     *   <ul>
     *     <li>ManagedProperty.getProperty(SPI.class.getName());</li>
     *     <li>properties.getProperty(SPI.class.getName());</li>
     *     <li>
     *     The value obtained using the JDK1.3+ 'Service Provider' specification
     *     (http://java.sun.com/j2se/1.3/docs/guide/jar/jar.html) to locate a
     *     service named <code>SPI.class.getName()</code>.  This is implemented
     *     internally, so there is not a dependency on JDK 1.3+.
     *     </li>
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
     *     <li>DiscoverSingleton's (this class) Class Loader</li>
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
     *     <li>DiscoverSingleton's (this class) Class Loader</li>
     *     <li>System Class Loader</li>
     *   </ul>
     *   <p>
     *   This limits the scope in which the default class loader can be found
     *   to the SPI, DiscoverSingleton, and System class loaders.  The assumption here
     *   is that the default implementation is closely associated with the SPI
     *   or system, and is not defined in the user's application space.
     *   </p>
     *   <p>
     *   An exception is thrown if the class cannot be loaded, or if the
     *   resulting class does not implement(or extend) the SPI.
     *   </li></p>
     * </ul>
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
     * @return Class class implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found, or if the class cannot be loaded.
     */
    public Class loadClass(Properties properties, String defaultImplName)
        throws DiscoveryException
    {
        String spiName = spiContext.getSPI().getName();
        Object service = null;

        // First, try the (managed) system property
        String className = Utils.getManagedProperty(spiName);

        if (className == null) {
            // Second, try the properties parameter
            if (properties != null)
                className = properties.getProperty(spiName);
        
            if (className == null) {
                // Third, try to find a service by using the JDK1.3 jar
                // discovery mechanism.
                className = Utils.getJDK13ClassName(
                    spiContext.getThreadContextClassLoader(), spiName);
            }
        }

        Class clazz = null;

        if (className != null) {
            clazz = loaders.loadClass(className, false);
        } else {        
            // Fourth, try the fallback implementation class,
            // but limit loaders to 'system' loaders, in an
            // attempt to ensure that the default picked up is
            // the one that was intended.
            clazz = loaders.loadClass(defaultImplName, true);
        }

        if (clazz == null) {
            throw new DiscoveryException("No implementation defined for " + spiName);
        }

        return clazz;
    }
    
    /**
     * Load property file (qualified by groupContext param to classFinder).
     * 
     * A property file is loaded using the following sequence of class loaders:
     *   <ul>
     *     <li>Thread Context Class Loader</li>
     *     <li>DiscoverSingleton's Caller's Class Loader</li>
     *     <li>SPI's Class Loader</li>
     *     <li>DiscoverSingleton's (this class) Class Loader</li>
     *     <li>System Class Loader</li>
     *   </ul>
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
    public Properties loadProperties(String propertiesFileName)
        throws DiscoveryException
    {
        Properties properties = null;
        
        if (propertiesFileName != null) {
            try {
                InputStream stream =
                    loaders.loadResourceAsStream(propertiesFileName);
    
                if (stream != null) {
                    properties = new Properties();
                    try {
                        properties.load(stream);
                    } finally {
                        stream.close();
                    }
                }
            } catch (IOException e) {
                ;  // ignore
            } catch (SecurityException e) {
                ;  // ignore
            }
        }
        
        return properties;
    }
}
