/*
 * Copyright 1999-2004 The Apache Software Foundation
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


import java.net.URL;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.discovery.Resource;
import org.apache.commons.discovery.ResourceClass;
import org.apache.commons.discovery.ResourceClassIterator;
import org.apache.commons.discovery.ResourceIterator;
import org.apache.commons.discovery.jdk.JDKHooks;
import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.resource.DiscoverResources;
import org.apache.commons.discovery.resource.classes.DiscoverClasses;
import org.apache.commons.discovery.tools.DefaultClassHolder;
import org.apache.commons.discovery.tools.DiscoverClass;
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
//        org.apache.commons.discovery.log.SimpleLog.LOG_LEVEL_DEBUG;

    
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

    public void testLowLevelFind() {
        org.apache.commons.discovery.log.SimpleLog.setLevel(logLevel);

        ClassLoaders loaders = ClassLoaders.getAppLoaders(TestInterface2.class, getClass(), false);
        String name = "org.apache.commons.discovery.test.TestImpl2_1";
        
        DiscoverClasses discovery = new DiscoverClasses(loaders);
        ResourceClassIterator iter = discovery.findResourceClasses(name);
        while (iter.hasNext()) {
            ResourceClass resource = iter.nextResourceClass();
            try {                
                Class implClass = resource.loadClass();
                if ( implClass != null ) {
                    assertEquals("org.apache.commons.discovery.test.TestImpl2_1", implClass.getName());
                    return;
                }
            }
            catch (Exception e) {
                fail("Could not load service: " + resource );
            }
        }
        fail("failed to load class resource: " + name);
    }
    
    public void testFindResources() {
        org.apache.commons.discovery.log.SimpleLog.setLevel(logLevel);

        ClassLoaders loaders = new ClassLoaders();

        /**
         * To many class loaders when searching for multiple
         * resources means that we can find the same (same URL)
         * resource for each loader...
         * let's keep this to a minimum.
         */
        ClassLoader cl = getClass().getClassLoader();
        if (cl != null)
            loaders.put(getClass().getClassLoader(), true);
        else
            loaders.put(JDKHooks.getJDKHooks().getSystemClassLoader(), true);
        

        String name = "testResource";
        
        String partialPaths[] = { "/test/", "/testAlt1/", "/testAlt2/" };
        int expected = partialPaths.length;
        
        DiscoverResources discovery = new DiscoverResources(loaders);
        ResourceIterator iter = discovery.findResources(name);
        int count = 0;
        
        while (iter.hasNext()) {
            Resource resource = iter.nextResource();
            URL url = resource.getResource();
            if ( url != null ) {
                System.out.println("URL = " + url.toString());
                
                if (url.getFile().indexOf(partialPaths[count]) == -1) {
                    fail(url + " does not contain " + partialPaths[count]);
                }
                count++;
            }
        }
        
        if (count != expected) {
            fail("located " + count + " resources, failed to locate all " + expected + " resources: " + name);
        }
    }

    public void testViaDiscoverClass() {
        org.apache.commons.discovery.log.SimpleLog.setLevel(logLevel);

        ClassLoaders loaders = ClassLoaders.getAppLoaders(TestInterface2.class, getClass(), false);
        
        DiscoverClass discover = new DiscoverClass(loaders);
        Class implClass = discover.find(TestInterface2.class);
        
        assertTrue("Failed to find an implementation class", implClass != null);
        assertEquals("org.apache.commons.discovery.test.TestImpl2_1", implClass.getName());
        
    }
    
    /**
     * This allows the tests to run as a standalone application.
     */
    public static void main(String args[]) {
        String[] testCaseName = { TestAll.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }
}
