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

package org.apache.commons.discovery.resource.names;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.commons.discovery.ResourceNameDiscover;
import org.apache.commons.discovery.ResourceNameIterator;
import org.apache.commons.discovery.log.DiscoveryLogFactory;
import org.apache.commons.logging.Log;


/**
 * Recover resource name from Managed Properties,
 * using OLD property names.
 * 
 * This class maintains a mapping between old names and
 * (new) the class names they represent.  The discovery
 * mechanism uses the class names as property names.
 * 
 * @see org.apache.commons.discovery.tools.ManagedProperties
 * 
 * @author Richard A. Sitze
 */
public class DiscoverConstNames
    extends ResourceNameDiscoverImpl
    implements ResourceNameDiscover
{
    private static Log log = DiscoveryLogFactory.newLog(DiscoverConstNames.class);
    public static void setLog(Log _log) {
        log = _log;
    }
    
    private Vector names = new Vector();
    
    /** Construct a new resource discoverer
     */
    public DiscoverConstNames() {
    }

    public DiscoverConstNames(String name) {
        addName(name);
    }

    public DiscoverConstNames(String [] names) {
        addNames(names);
    }
    
    public void addName(String name) {
        names.add(name);
    }
    
    public void addNames(String [] names) {
        for (int i = 0; i < names.length; i++) {
            this.names.add(names[i]);
        }
    }

    /**
     * @return Enumeration of ResourceInfo
     */
    public ResourceNameIterator findResourceNames(final String resourceName) {
        if (log.isDebugEnabled()) {
            log.debug("find: resourceName='" + resourceName + "', mapping to constants");
        }

        return new ResourceNameIterator() {
            private Enumeration enum = names.elements();
            
            public boolean hasNext() {
                return enum.hasMoreElements();
            }
            
            public String nextResourceName() {
                return (String)enum.nextElement();
            }
        };
    }
}
