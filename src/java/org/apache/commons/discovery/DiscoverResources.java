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

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.commons.discovery.jdk.JDKHooks;
import org.apache.commons.discovery.log.DiscoveryLogFactory;
import org.apache.commons.logging.Log;


/**
 * Holder for multiple Discover instances.
 * The result is the union of the results from each
 * (not a chained sequence, where results feed the next in line.
 *
 * @author Richard A. Sitze
 */
public class DiscoverResources implements Discover
{
    private static Log log = DiscoveryLogFactory.newLog(DiscoverResources.class);
    public static void setLog(Log _log) {
        log = _log;
    }

    private Vector discoverers = new Vector();
    
    /** Construct a new resource discoverer
     */
    public DiscoverResources() {
    }

    public int size() {
        return discoverers.size();
    }
    
    public Discover get(int idx) {
        return (Discover)discoverers.get(idx);
    }

    /**
     * Specify a new class loader to be used in searching.
     * The order of loaders determines the order of the result.
     * It is recommended to add the most specific loaders first.
     */
    public void put(Discover discover) {
        if (discover != null) {
            discoverers.addElement(discover);
        }
    }

    /**
     * Sum of all discoverers (execute independently [in parallel]),
     * then put it all together..
     * 
     * @return ResourceIterator
     */
    public ResourceIterator find(final String resourceName) {
        if (log.isDebugEnabled())
            log.debug("find: resourceName='" + resourceName + "'");

        return new ResourceIterator() {
            private int idx = 0;
            private ResourceIterator iterator = null;
            
            public boolean hasNext() {
                if (iterator == null  ||  !iterator.hasNext()) {
                    iterator = getNextIterator();
                    if (iterator == null) {
                        return false;
                    }
                }
                return iterator.hasNext();
            }
            
            public ResourceInfo next() {
                return iterator.next();
            }
            
            private ResourceIterator getNextIterator() {
                while (idx < size()) {
                    ResourceIterator iter = get(idx++).find(resourceName);
                    if (iter.hasNext()) {
                        return iter;
                    }
                }
                return null;
            }
        };
    }
}
