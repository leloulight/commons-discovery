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

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.discovery.ResourceInfo;
import org.apache.commons.discovery.ServiceDiscovery;
import org.apache.commons.discovery.base.Environment;
import org.apache.commons.discovery.base.SPInterface;
import org.apache.commons.discovery.tools.ManagedProperties;


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
public class DefaultDiscoverStrategy
    implements DiscoverStrategy
{
    /**
     * <p>Discover names of SPI implementation Classes.
     * The names are the non-null values, in order, obtained from the following
     * resources:
     *   <ul>
     *     <li>ManagedProperty.getProperty(SPI.class.getName());</li>
     *     <li>properties.getProperty(SPI.class.getName());</li>
     *     <li>The value obtained using the JDK1.3+ 'Service Provider'
     *     specification (http://java.sun.com/j2se/1.3/docs/guide/jar/jar.html)
     *     to locate a service named <code>SPI.class.getName()</code>.  This is
     *     implemented internally, so there is not a dependency on JDK 1.3+.
     *     </li>
     *   </ul>
     * 
     * @param properties Properties that may define the implementation
     *                   class name(s).
     * 
     * @return String[] Name of classes implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found.
     */
    public String discoverClassName(Environment env,
                                    SPInterface spi,
                                    Properties properties)
    {
        Vector names = new Vector();
        
        String spiName = spi.getSPName();
        String propertyName = spi.getPropertyName();

        boolean includeAltProperty = !spiName.equals(propertyName);
        
        // Try the (managed) system property spiName
        String className = getManagedProperty(spiName);
        if (className != null) return className;
        
        if (includeAltProperty) {
            // Try the (managed) system property propertyName
            className = getManagedProperty(propertyName);
            if (className != null) return className;
        }

        if (properties != null) {
            // Try the properties parameter spiName
            className = properties.getProperty(spiName);
            if (className != null) return className;

            if (includeAltProperty) {
                // Try the properties parameter propertyName
                className = properties.getProperty(propertyName);
                if (className != null) return className;
            }
        }

        // Last, try to find a service by using the JDK1.3 jar
        // discovery mechanism.
        Enumeration classNames = getJDK13ClassNames(env.getThreadContextClassLoader(), spiName);
        if (classNames.hasMoreElements()) {
            className = ((ResourceInfo)classNames.nextElement()).getResourceName();
        }
        return className;
    }

    /**
     * <p>Discover names of SPI implementation Classes.
     * The names are the non-null values, in order, obtained from the following
     * resources:
     *   <ul>
     *     <li>ManagedProperty.getProperty(SPI.class.getName());</li>
     *     <li>properties.getProperty(SPI.class.getName());</li>
     *     <li>The value obtained using the JDK1.3+ 'Service Provider'
     *     specification (http://java.sun.com/j2se/1.3/docs/guide/jar/jar.html)
     *     to locate a service named <code>SPI.class.getName()</code>.  This is
     *     implemented internally, so there is not a dependency on JDK 1.3+.
     *     </li>
     *   </ul>
     * 
     * @param properties Properties that may define the implementation
     *                   class name(s).
     * 
     * @return String[] Name of classes implementing the SPI.
     * 
     * @exception DiscoveryException Thrown if the name of a class implementing
     *            the SPI cannot be found.
     */
    public String[] discoverClassNames(Environment env,
                                       SPInterface spi,
                                       Properties properties)
    {
        Vector names = new Vector();
        
        String spiName = spi.getSPName();
        String propertyName = spi.getPropertyName();

        boolean includeAltProperty = !spiName.equals(propertyName);
        
        // Try the (managed) system property spiName
        String className = getManagedProperty(spiName);
        if (className != null) names.addElement(className);
        
        if (includeAltProperty) {
            // Try the (managed) system property propertyName
            className = getManagedProperty(propertyName);
            if (className != null) names.addElement(className);
        }

        if (properties != null) {
            // Try the properties parameter spiName
            className = properties.getProperty(spiName);
            if (className != null) names.addElement(className);

            if (includeAltProperty) {
                // Try the properties parameter propertyName
                className = properties.getProperty(propertyName);
                if (className != null) names.addElement(className);
            }
        }

        // Last, try to find services by using the JDK1.3 jar
        // discovery mechanism.
        Enumeration classNames = getJDK13ClassNames(env.getThreadContextClassLoader(), spiName);
        while (classNames.hasMoreElements()) {
            className = ((ResourceInfo)classNames.nextElement()).getResourceName();
            if (className != null) names.addElement(className);
        }

        String[] results = new String[names.size()];
        names.copyInto(results);        

        return results;
    }


    /**
     * Load the class whose name is given by the value of a (Managed)
     * System Property.
     * 
     * @see ManagedProperties
     * 
     * @param attribute the name of the system property whose value is
     *        the name of the class to load.
     */
    public static String getManagedProperty(String propertyName) {
        String value;
        try {
            value = ManagedProperties.getProperty(propertyName);
        } catch (SecurityException e) {
            value = null;
        }
        return value;
    }


    /**
     * Find the name of a service using the JDK 1.3 jar discovery mechanism.
     * This will allow users to plug a service implementation by just
     * placing it in the META-INF/services directory of the webapp
     * (or in CLASSPATH or equivalent).
     */
    public static Enumeration getJDK13ClassNames(ClassLoader classLoader,
                                                 String spiName) {
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery();
        serviceDiscovery.addClassLoader(classLoader);
        return serviceDiscovery.findResources(spiName);
    }
}
