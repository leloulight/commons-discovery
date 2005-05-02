/*
 * Copyright 1999-2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
  * @version $Revision$
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

            // factory should be cached
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

            // factory should be cached
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
    

    public void testFindPropFileDefault() {
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

    public void testFindServiceFileDefault() {
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
