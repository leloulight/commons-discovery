/*
 * $Header$
 * $Revision: 1.2 $
 * $Date: 2002/01/17 22:55:43 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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


import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.apache.commons.logging.impl.SimpleLog;


/**
  * @author Richard A. Sitze
  * @version $Revision: 1.2 $
 */
public class TestAll extends TestCase {
    
    public static class MyFactory extends LogFactoryImpl {
    }

    public TestAll(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(TestAll.class);
    }

    public void testFindLogFactoryImplDefault() {
        LogFactory factory = null;
        
        try {
            factory = (LogFactory)DiscoverSingleton.find(LogFactory.class,
                                                 LogFactoryImpl.class.getName());

            assertTrue(factory.getClass().getName() + "!=" + LogFactoryImpl.class.getName(),
                       factory.getClass().getName().equals(LogFactoryImpl.class.getName()));
        } finally {
            if (factory != null)
                factory.releaseAll();

            DiscoverSingleton.release();
        }
    }
    
    public void testMyFactoryDefault() {
        LogFactory factory = null;

        try {
            factory = (LogFactory)DiscoverSingleton.find(LogFactory.class,
                                                 MyFactory.class.getName());

            assertTrue(factory.getClass().getName() + "!=" + MyFactory.class.getName(),
                       factory.getClass().getName().equals(MyFactory.class.getName()));
        } finally {
            if (factory != null)
                factory.releaseAll();

            DiscoverSingleton.release();
        }
    }
    
    public void testCache() {
        LogFactory factory = null;
        
        try {
            factory = (LogFactory)DiscoverSingleton.find(LogFactory.class,
                                                 LogFactoryImpl.class.getName());

            assertTrue("1. " + factory.getClass().getName() + "!=" + LogFactoryImpl.class.getName(),
                       factory.getClass().getName().equals(LogFactoryImpl.class.getName()));
            
            // no release, should get cached value..
            
            factory = (LogFactory)DiscoverSingleton.find(LogFactory.class,
                                                 MyFactory.class.getName());

            // factory should be cached LogFactoryImpl
            assertTrue("2. " + factory.getClass().getName() + "!=" + LogFactoryImpl.class.getName(),
                       factory.getClass().getName().equals(LogFactoryImpl.class.getName()));
        } finally {
            if (factory != null)
                factory.releaseAll();

            DiscoverSingleton.release();
        }
    }
    
    public void testRelease() {
        LogFactory factory = null;
        
        try {
            factory = (LogFactory)DiscoverSingleton.find(LogFactory.class,
                                                 LogFactoryImpl.class.getName());

            assertTrue("1. " + factory.getClass().getName() + "!=" + LogFactoryImpl.class.getName(),
                       factory.getClass().getName().equals(LogFactoryImpl.class.getName()));
            
            DiscoverSingleton.release();
            
            factory = (LogFactory)DiscoverSingleton.find(LogFactory.class,
                                                 MyFactory.class.getName());

            // Cache flushed, get new factory:
            assertTrue("2. " + factory.getClass().getName() + "!=" + MyFactory.class.getName(),
                       factory.getClass().getName().equals(MyFactory.class.getName()));
        } finally {
            if (factory != null)
                factory.releaseAll();

            DiscoverSingleton.release();
        }
    }
    
    public void testMyFactoryProperty() {
        LogFactory factory = null;

        try {
            Properties props = new Properties();
            
            props.setProperty(LogFactory.class.getName(),
                              MyFactory.class.getName());
                              
            props.setProperty(Log.class.getName(),
                              SimpleLog.class.getName());
            
            factory = (LogFactory)DiscoverSingleton.find(LogFactory.class, props);

            assertTrue(factory.getClass().getName() + "!=" + MyFactory.class.getName(),
                       factory.getClass().getName().equals(MyFactory.class.getName()));
        } finally {
            if (factory != null)
                factory.releaseAll();

            DiscoverSingleton.release();
        }
    }
    
    public void testMyFactoryManagedProperty() {
        LogFactory factory = null;

        try {
            ManagedProperties.setProperty(LogFactory.class.getName(),
                                          MyFactory.class.getName());
                              
            ManagedProperties.setProperty(Log.class.getName(),
                                          SimpleLog.class.getName());
            
            factory = (LogFactory)DiscoverSingleton.find(LogFactory.class);

            assertTrue(factory.getClass().getName() + "!=" + MyFactory.class.getName(),
                       factory.getClass().getName().equals(MyFactory.class.getName()));
        } finally {
            if (factory != null)
                factory.releaseAll();

            DiscoverSingleton.release();
            
            /**
             * Cleanup, don't want to affect next test..
             */
            ManagedProperties.setProperty(LogFactory.class.getName(), null);
            ManagedProperties.setProperty(Log.class.getName(), null);
        }
    }

    /**
     * This allows the tests to run as a standalone application.
     */
    public static void main(String args[]) {
        String[] testCaseName = { TestAll.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }
}
