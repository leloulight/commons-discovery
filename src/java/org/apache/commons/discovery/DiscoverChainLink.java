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


/**
 * This is a helper-class for chains,
 * where the results of one feed the next.
 * The only place where this makes sense is when
 * a Discover implementation returns names that
 * differ from the input name
 * (i.e. DiscoverFiledResources & ServiceResources).
 * 
 * Default discoverer is DiscoverClassLoaderResources,
 * but it can be set to any other.
 *
 * @author Richard A. Sitze
 */
public abstract class DiscoverChainLink implements Discover
{
    private Discover discoverResources;
    
    /**
     *  Construct a new resource discoverer
     */
    public DiscoverChainLink() {
        discoverResources = new DiscoverClassLoaderResources();
    }
    
    /**
     *  Construct a new resource discoverer
     */
    public DiscoverChainLink(ClassLoaders loaders) {
        discoverResources = new DiscoverClassLoaderResources(loaders);
    }
    
    /**
     *  Construct a new resource discoverer
     */
    public DiscoverChainLink(Discover discoverer) {
        this.discoverResources = discoverer;
    }

    /**
     * Specify set of class loaders to be used in searching.
     */
    public void setDiscoverer(Discover discoverer) {
        this.discoverResources = discoverer;
    }

    /**
     * To be used by downstream elements..
     */
    public Discover getDiscoverer() {
        return discoverResources;
    }
}
