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

import org.apache.commons.discover.jdk.JDKHooks;


/**
 * This class supports any VM, including JDK1.1, via
 * org.apache.commons.discover.jdk.JDKHooks.
 *
 * The findResources() method will check every loader.
 *
 * @author Richard A. Sitze
 * @author Craig R. McClanahan
 * @author Costin Manolache
 * @author James Strachan
 */
public class ResourceDiscovery
{
    /**
     * this doesn't buy anything except +/- style (subjective).
     */
    protected static JDKHooks jdkHooks = JDKHooks.getJDKHooks();
    
    protected Vector classLoaders = null;
    
    /** Construct a new resource discoverer
     */
    public ResourceDiscovery() {
    }

    /**
     * @deprecated
     */
    public static ResourceDiscovery newInstance() {
        // This is _not_ singleton.
        return new ResourceDiscovery();
        // XXX Check if JDK1.1 is used no longer necessary.
    }

    /**
     * Specify set of class loaders to be used in searching.
     */
    public void setClassLoaders(ClassLoaders loaders) {
        classLoaders = loaders.getClassLoaders();
    }

    public ResourceInfo[] findResources(String resourceName) {
        ResourceInfo resultA[];
        
        if (classLoaders != null) {
            Vector results = new Vector();
        
            // For each loader
            for( int i=0; i<classLoaders.size() ; i++ ) {
                ClassLoader loader=(ClassLoader)classLoaders.elementAt(i);
    
                Enumeration enum=null;
    
                try {
                    enum=jdkHooks.getResources(loader, resourceName);
                } catch( IOException ex ) {
                    ex.printStackTrace();
                }
                if( enum==null ) continue;
    
                while( enum.hasMoreElements() ) {
                    URL url=(URL)enum.nextElement();
    
                    System.out.println("XXX URL " + url );
                    
                    ResourceInfo sinfo = new ResourceInfo(resourceName, loader, url);
                    results.add(sinfo);
                    System.out.println("XXX " + sinfo.toString());
                }
            }
            resultA = new ResourceInfo[ results.size() ];
            results.copyInto( resultA );
        } else {
            resultA = new ResourceInfo[0];
        }

        return resultA;
    }
}
