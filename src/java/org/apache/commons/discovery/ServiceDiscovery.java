/*
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;



/**
 * [this was ServiceDiscovery12... the 1.1 versus 1.2 issue
 * has been abstracted to org.apache.commons.discover.jdk.JDKHooks]
 * 
 * <p>Implement the JDK1.3 'Service Provider' specification.
 * ( http://java.sun.com/j2se/1.3/docs/guide/jar/jar.html )
 * </p>
 *
 * This class supports any VM, including JDK1.1, via
 * org.apache.commons.discover.jdk.JDKHooks.
 *
 * The caller will first configure the discoverer by adding ( in the desired
 * order ) all the places to look for the META-INF/services. Currently
 * we support loaders.
 *
 * The findResources() method will check every loader.
 *
 * @author Richard A. Sitze
 * @author Craig R. McClanahan
 * @author Costin Manolache
 * @author James Strachan
 */
public class ServiceDiscovery extends ResourceDiscovery
{
    protected static final String SERVICE_HOME = "META-INF/services/";
    
    /** Construct a new service discoverer
     */
    public ServiceDiscovery() {
        super();
    }
    
    /** Construct a new service discoverer
     */
    public ServiceDiscovery(ClassLoaders classLoaders) {
        super(classLoaders);
    }
    
    /**
     * This gets ugly, but...
     * a) it preserves the desired behaviour
     * b) it defers file I/O and class loader lookup until necessary.
     * 
     * @return Enumeration of ClassInfo
     */
    public Enumeration findResources(final String resourceName) {
        final Enumeration files =
            super.findResources(SERVICE_HOME + resourceName);

        return new Enumeration() {
            private ClassDiscovery classDiscovery =
                new ClassDiscovery(getClassLoaders());
                
            private int idx = 0;
            private Vector classNames = null;
            private Enumeration classResources = null;
            private ClassInfo resource = null;
            
            public boolean hasMoreElements() {
                if (resource == null) {
                    resource = getNextResource();
                }
                return resource != null;
            }
            
            public Object nextElement() {
                Object element = resource;
                resource = null;
                return element;
            }
            
            private ClassInfo getNextResource() {
                if (classResources == null || !classResources.hasMoreElements()) {
                    classResources = getNextClassResources();
                    if (classResources == null) {
                        return null;
                    }
                }

                ClassInfo classInfo = (ClassInfo)classResources.nextElement();
                System.out.println("XXX " + classInfo.toString());
                return classInfo;
            }

            private Enumeration getNextClassResources() {
                while (true) {
                    if (classNames == null || idx >= classNames.size()) {
                        classNames = getNextClassNames();
                        if (classNames == null) {
                            return null;
                        }
                        idx = 0;
                    }
    
                    /**
                     * The loader used to find the service file
                     * is of no (limited?) use here... likewise
                     * the URL does not refer to a class.
                     * Go back to original set of classloaders and
                     * find unique classes & their loaders...
                     */
                    Enumeration classes =
                        classDiscovery.findResources((String)classNames.get(idx++));

                    if (classes != null && classes.hasMoreElements()) {
                        return classes;
                    }
                }
            }

            private Vector getNextClassNames() {
                while (files.hasMoreElements()) {
                    ResourceInfo info = (ResourceInfo)files.nextElement();
                    Vector results = readServices(info.getURL());
                    if (results != null  &&  results.size() > 0) {
                        return results;
                    }
                }
                return null;
            }
        };
    }

    /**
     * Read everything, no defering here..
     * Ensure that files are closed before we leave.
     */
    private Vector readServices(final URL url) {
        Vector results = new Vector();
        
        try {
            /**
             * URL is of the form: baseURL/META-INF/services/resourceName
             */
            URL baseURL = new URL( url, "../../.." );
            System.out.println("XXX BaseURL " + baseURL);
            
            InputStream is = url.openStream();
            
            if( is != null ) {
                try {
                    // This code is needed by EBCDIC and other
                    // strange systems.  It's a fix for bugs
                    // reported in xerces
                    BufferedReader rd;
                    try {
                        rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    } catch (java.io.UnsupportedEncodingException e) {
                        rd = new BufferedReader(new InputStreamReader(is));
                    }
                    
                    try {
                        String serviceImplName;
                        while( (serviceImplName = rd.readLine()) != null) {
                            serviceImplName =
                                new String(serviceImplName.getBytes(),
                                           0,
                                           serviceImplName.indexOf('#')).trim();

                            if (serviceImplName.length() != 0) {
                                results.add(serviceImplName);
                            }
                        }
                    } finally {
                        rd.close();
                    }
                } finally {
                    is.close();
                }
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ; // ignore
        }
        
        return results;
    }
}
