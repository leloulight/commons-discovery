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

import java.io.*;
import java.util.*;
import java.net.*;

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
    protected ServiceDiscovery() {
    }
 
    public ResourceInfo[] findResources(String resourceName) {
        ResourceInfo[] services =
            super.findResources(SERVICE_HOME + resourceName);
            
        // use each loader to find if META-INF/services.
        // find all resources, etc.
    
        Vector results = new Vector();
        
        // For each service resource
        for( int i=0; i<services.length ; i++ ) {
            ResourceInfo info = services[i];

            try {
                URL url = info.getURL();

                /**
                 * URL will be of the form:
                 *  baseURL/META-INF/services/resourceName
                 */
                URL baseURL=new URL( url, "../../.." );
                System.out.println("XXX BaseURL " + baseURL);
                
                InputStream is = url.openStream();
                
                if( is != null ) {
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
                            String serviceImplName;
                            while( (serviceImplName = rd.readLine()) != null) {
                                serviceImplName.trim();
                                if( "".equals(serviceImplName) )
                                    continue;
                                if( serviceImplName.startsWith( "#" ))
                                    continue;
                                ResourceInfo sinfo =
                                    new ResourceInfo(serviceImplName,
                                                     info.getLoader(),
                                                     baseURL);
                                results.add(sinfo);
                                System.out.println("XXX " + sinfo.toString());
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
        }

        ResourceInfo resultA[]=new ResourceInfo[ results.size() ];
        results.copyInto( resultA );
        return resultA;
    }
}
