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
 
 
package org.apache.commons.discovery.test;


import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.discovery.tools.DefaultClassHolder;
import org.apache.commons.discovery.tools.DiscoverSingleton;
import org.apache.commons.discovery.tools.ManagedProperties;
import org.apache.commons.discovery.tools.PropertiesHolder;
import org.apache.commons.discovery.tools.SPInterface;


/**
  * @author Richard A. Sitze
  * @version $Revision: 1.2 $
 */
public class TestAll extends TestCase {
    private static final int logLevel =
        org.apache.commons.discovery.log.SimpleLog.LOG_LEVEL_INFO;

    
    public TestAll(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(TestAll.class);
    }

    public void testFindDefaultImpl_1() {
        org.apache.commons.discovery.log.SimpleLog.setLevel(logLevel);

        TestInterface1 ti = null;
        
        try {
            ti = (TestInterface1)DiscoverSingleton.find(TestInterface1.class,
                                                        TestImpl1_1.class.getName());

            assertTrue(ti.getClass().getName() + "!=" + TestImpl1_1.class.getName(),
                       ti.getClass().getName().equals(TestImpl1_1.class.getName()));
        } finally {
            DiscoverSingleton.release();
        }
    }
    
    public void testFindDefaultImpl_2() {
        org.apache.commons.discovery.log.SimpleLog.setLevel(logLevel);

        TestInterface1 ti = null;

        try {
            ti = (TestInterface1)DiscoverSingleton.find(TestInterface1.class,
                                                        TestImpl1_2.class.getName());

            assertTrue(ti.getClass().getName() + "!=" + TestImpl1_2.class.getName(),
                       ti.getClass().getName().equals(TestImpl1_2.class.getName()));
        } finally {
            DiscoverSingleton.release();
        }
    }
    
    public void testCache() {
        org.apache.commons.discovery.log.SimpleLog.setLevel(logLevel);

        TestInterface1 ti = null;
        
        try {
            ti = (TestInterface1)DiscoverSingleton.find(TestInterface1.class,
                                                        TestImpl1_1.class.getName());

            assertTrue("1. " + ti.getClass().getName() + "!=" + TestImpl1_1.class.getName(),
                       ti.getClass().getName().equals(TestImpl1_1.class.getName()));
            
            // no release, should get cached value..
            
            ti = (TestInterface1)DiscoverSingleton.find(TestInterface1.class,
                                                        TestImpl1_2.class.getName());

            // factory should be cached LogFactoryImpl
            assertTrue("2. " + ti.getClass().getName() + "!=" + TestImpl1_1.class.getName(),
                       ti.getClass().getName().equals(TestImpl1_1.class.getName()));
        } finally {
            DiscoverSingleton.release();
        }
    }
    
    public void testRelease() {
        org.apache.commons.discovery.log.SimpleLog.setLevel(logLevel);

        TestInterface1 ti = null;
        
        try {
            ti = (TestInterface1)DiscoverSingleton.find(TestInterface1.class,
                                                        TestImpl1_1.class.getName());

            assertTrue("1. " + ti.getClass().getName() + "!=" + TestImpl1_1.class.getName(),
                       ti.getClass().getName().equals(TestImpl1_1.class.getName()));
            
            DiscoverSingleton.release();
            
            ti = (TestInterface1)DiscoverSingleton.find(TestInterface1.class,
                                                        TestImpl1_2.class.getName());

            // factory should be cached LogFactoryImpl
            assertTrue("2. " + ti.getClass().getName() + "!=" + TestImpl1_2.class.getName(),
                       ti.getClass().getName().equals(TestImpl1_2.class.getName()));
        } finally {
            DiscoverSingleton.release();
        }
    }
    
    public void testFindPropertyImpl_1() {
        org.apache.commons.discovery.log.SimpleLog.setLevel(logLevel);

        TestInterface1 ti = null;

        try {
            Properties props = new Properties();
            
            props.setProperty(TestInterface1.class.getName(),
                              TestImpl1_2.class.getName());
            
            ti = (TestInterface1)DiscoverSingleton.find(TestInterface1.class, props);

            assertTrue(ti.getClass().getName() + "!=" + TestImpl1_2.class.getName(),
                       ti.getClass().getName().equals(TestImpl1_2.class.getName()));
        } finally {
            DiscoverSingleton.release();
        }
    }
    
    public void testMyFactoryManagedProperty() {
        org.apache.commons.discovery.log.SimpleLog.setLevel(logLevel);

        TestInterface1 ti = null;

        try {
            ManagedProperties.setProperty(TestInterface1.class.getName(),
                                          TestImpl1_2.class.getName());
                              
            ti = (TestInterface1)DiscoverSingleton.find(TestInterface1.class);

            assertTrue(ti.getClass().getName() + "!=" + TestImpl1_2.class.getName(),
                       ti.getClass().getName().equals(TestImpl1_2.class.getName()));
        } finally {
            DiscoverSingleton.release();
            
            /**
             * Cleanup, don't want to affect next test..
             */
            ManagedProperties.setProperty(TestInterface1.class.getName(), null);
        }
    }
    

    public void testFindGroupLogFactoryImplPropFileDefault() {
        org.apache.commons.discovery.log.SimpleLog.setLevel(logLevel);

        TestInterface1 ti = null;
        
        try {
            ti = (TestInterface1)DiscoverSingleton.find(null,
                                   new SPInterface(TestInterface1.class),
                                   new PropertiesHolder("TestInterface.properties"),
                                   new DefaultClassHolder(TestImpl1_2.class.getName()));

            assertTrue(ti.getClass().getName() + "!=" + TestImpl1_1.class.getName(),
                       ti.getClass().getName().equals(TestImpl1_1.class.getName()));
        } finally {
            DiscoverSingleton.release();
        }
    }

    public void testFindGroupLogFactoryImplServiceFileDefault() {
//        org.apache.commons.discovery.log.SimpleLog.setLevel(org.apache.commons.discovery.log.SimpleLog.LOG_LEVEL_DEBUG);
        org.apache.commons.discovery.log.SimpleLog.setLevel(logLevel);

        TestInterface2 ti = null;
        
        try {
            ti = (TestInterface2)DiscoverSingleton.find(null,
                                   new SPInterface(TestInterface2.class),
                                   null,
                                   new DefaultClassHolder(TestImpl2_2.class.getName()));

            assertTrue(ti.getClass().getName() + "!=" + TestImpl2_1.class.getName(),
                       ti.getClass().getName().equals(TestImpl2_1.class.getName()));
        } finally {
            DiscoverSingleton.release();
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
