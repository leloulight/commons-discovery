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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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

package org.apache.commons.discovery.resource.names;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.discovery.Resource;
import org.apache.commons.discovery.ResourceDiscover;
import org.apache.commons.discovery.ResourceListener;
import org.apache.commons.discovery.ResourceNameDiscover;
import org.apache.commons.discovery.ResourceNameListener;
import org.apache.commons.discovery.log.DiscoveryLogFactory;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.resource.DiscoverResources;
import org.apache.commons.logging.Log;



/**
 * Discover ALL files of a given name, and return resource names
 * contained within the set of files:
 * <ul>
 *   <li>one resource name per line,</li>
 *   <li>whitespace ignored,</li>
 *   <li>comments begin with '#'</li>
 * </ul>
 * 
 * Default discoverer is DiscoverClassLoaderResources,
 * but it can be set to any other.
 *
 * @author Richard A. Sitze
 * @author Costin Manolache
 * @author James Strachan
 */
public class DiscoverNamesInFile extends ResourceNameDiscoverImpl
    implements ResourceNameDiscover, ResourceNameListener, ResourceListener
{
    private static Log log = DiscoveryLogFactory.newLog(DiscoverNamesInFile.class);
    public static void setLog(Log _log) {
        log = _log;
    }
    
    private ResourceDiscover discoverResources;
    
    /**
     *  Construct a new resource discoverer
     */
    public DiscoverNamesInFile() {
        this.discoverResources = new DiscoverResources();
        this.discoverResources.setListener(this);
    }
    
    /**
     *  Construct a new resource discoverer
     */
    public DiscoverNamesInFile(ClassLoaders loaders) {
        this.discoverResources = new DiscoverResources(loaders);
        this.discoverResources.setListener(this);
    }

    /**
     * Specify set of class loaders to be used in searching.
     */
    public void setClassLoaders(ClassLoaders loaders) {
        discoverResources.setClassLoaders(loaders);
    }

    /**
     */
    public boolean find(String fileName) {
        if (log.isDebugEnabled())
            log.debug("find: fileName='" + fileName + "'");
            
        return discoverResources.find(fileName);
    }
    
    public boolean found(String resourceName) {
        return find(resourceName);
    }
    
    /**
     * Read everything, no defering here..
     * Ensure that files are closed before we leave.
     */
    public boolean found(Resource info) {
        InputStream is = info.getResourceAsStream();
        
        if( is != null ) {
            try {
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
                        String resourceName;
                        while( (resourceName = rd.readLine()) != null) {
                            int idx = resourceName.indexOf('#');
                            if (idx >= 0) {
                                resourceName = resourceName.substring(0, idx);
                            }
                            resourceName = resourceName.trim();
    
                            if (resourceName.length() != 0) {
                                if (log.isDebugEnabled())
                                    log.debug("readResourceNames: found '" + resourceName + "'");

                                if (!notifyListener(resourceName)) {
                                    return false;
                                }
                            }
                        }
                    } finally {
                        rd.close();
                    }
                } finally {
                    is.close();
                }
            } catch (IOException e) {
                // ignore
            }
        }
        
        return true;
    }
}
