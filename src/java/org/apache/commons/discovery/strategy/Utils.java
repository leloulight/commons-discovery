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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.discovery.ManagedProperties;


/**
 * Helper methods for locating resource names.
 * 
 * @author Richard A. Sitze
 * @author Craig R. McClanahan
 * @author Costin Manolache
 */
public class Utils {
    /**
     * JDK1.3+ 'Service Provider' specification 
     * ( http://java.sun.com/j2se/1.3/docs/guide/jar/jar.html )
     */
    private static final String SERVICE_HOME = "META-INF/services/";

    
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
    public static String getJDK13ClassName(ClassLoader classLoader, String spiName) {
        String serviceImplName = null;

        // Name of J2EE application file that identifies the service implementation.
        String servicePropertyFile = SERVICE_HOME + spiName;

        InputStream is = (classLoader == null
                          ? ClassLoader.getSystemResourceAsStream(servicePropertyFile)
                          : classLoader.getResourceAsStream(servicePropertyFile));

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
}