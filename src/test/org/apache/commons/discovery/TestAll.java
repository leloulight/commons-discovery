/*
 * $Header: /home/cvs/jakarta-commons/logging/src/test/org/apache/commons/logging/TestAll.java,v 1.2 2002/01/17 22:55:43 rdonkin Exp $
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.apache.commons.logging.impl.SimpleLog;

import junit.framework.*;


/**
  * <p> The build script calls just one <code>TestSuite</code> - this one!
  * All tests should be written into separate <code>TestSuite</code>'s
  * and added to this. Don't clutter this class with implementations. </p>
  *
  * <p> This class is based on <code>org.apache.commons.betwixt.TestAll</code> 
  * coded by James Strachan. </p>
  *
  * @author Robert Burrell Donkin
  * @version $Revision: 1.2 $
 */
public class TestAll extends TestCase {

    public TestAll(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(TestAll.class);
    }
    
    public void testServiceFinder1() {
        try {
            LogFactory factory =
                (LogFactory)ServiceFinder.find(LogFactory.class,
                                               LogFactoryImpl.class.getName());
            Log log = factory.getLog(TestAll.class);
            log.info("got log!");
        } finally {
            LogFactory.releaseAll();
            ServiceFinder.releaseAll();
        }
    }
    
    public void testServiceFinder2() {
        Properties props = new Properties();
        
        props.setProperty(LogFactory.class.getName(),
                          LogFactoryImpl.class.getName());
                          
        props.setProperty(Log.class.getName(), SimpleLog.class.getName());
        
        try {
            LogFactory factory =
                (LogFactory)ServiceFinder.find(LogFactory.class, props);
            Log log = factory.getLog(TestAll.class);
            log.info("got log factory via service");
        } finally {
            LogFactory.releaseAll();
            ServiceFinder.releaseAll();
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
