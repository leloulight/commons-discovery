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

package org.apache.commons.discovery.resource;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.apache.commons.discovery.Resource;
import org.apache.commons.discovery.ResourceDiscover;
import org.apache.commons.discovery.ResourceNameListener;
import org.apache.commons.discovery.jdk.JDKHooks;
import org.apache.commons.discovery.log.DiscoveryLogFactory;
import org.apache.commons.logging.Log;


/**
 * @author Richard A. Sitze
 * @author Craig R. McClanahan
 * @author Costin Manolache
 * @author James Strachan
 */
public class DiscoverResources extends ResourceDiscoverImpl
    implements ResourceDiscover, ResourceNameListener
{
    private static Log log = DiscoveryLogFactory.newLog(DiscoverResources.class);
    public static void setLog(Log _log) {
        log = _log;
    }
    
    /**
     * Construct a new resource discoverer
     */
    public DiscoverResources() {
        super();
    }
    
    /**
     *  Construct a new resource discoverer
     */
    public DiscoverResources(ClassLoaders classLoaders) {
        super(classLoaders);
    }

    /**
     * @return ResourceIterator
     */
    public boolean find(String resourceName) {
        if (log.isDebugEnabled())
            log.debug("find: resourceName='" + resourceName + "'");

        for (int idx = 0; idx < getClassLoaders().size(); idx++) {
            ClassLoader loader = getClassLoaders().get(idx);
            if (log.isDebugEnabled())
                log.debug("find: search using ClassLoader '" + loader + "'");

            try {
                Enumeration resources =
                    JDKHooks.getJDKHooks().getResources(loader, resourceName);

                if (resources != null) {
                    while(resources.hasMoreElements()) {
                        if (!notifyListener(new Resource(resourceName,
                                                         (URL)resources.nextElement(),
                                                         loader))) {
                            return false;
                        }
                    }
                }
            } catch( IOException ex ) {
                log.warn("find: Ignoring Exception", ex);
            }
        }
        return true;
    }
    
    public boolean found(String resourceName) {
        return find(resourceName);
    }
}
