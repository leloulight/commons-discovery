/*
 * $Header: /home/cvs/jakarta-commons/logging/src/java/org/apache/commons/logging/impl/SimpleLog.java,v 1.4 2002/06/15 20:54:48 craigmcc Exp $
 * $Revision: 1.4 $
 * $Date: 2002/06/15 20:54:48 $
 *
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


package org.apache.commons.discovery.log;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.commons.discovery.DiscoveryException;
import org.apache.commons.discovery.tools.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <p>Simple implementation of Log that sends all enabled log messages,
 * for all defined loggers, to System.err.
 * </p>
 * 
 * <p>Hacked from commons-logging SimpleLog for use in discovery.
 * This is intended to be enough of a Log implementation to bootstrap
 * Discovery.
 * </p>
 * 
 * <p>One property: <code>org.apache.commons.discovery.log.level</code>.
 * valid values: all, trace, debug, info, warn, error, fatal, off.
 * </p>
 * 
 * @author Richard A. Sitze
 * @author <a href="mailto:sanders@apache.org">Scott Sanders</a>
 * @author Rod Waldhoff
 * @author Robert Burrell Donkin
 *
 * @version $Id: SimpleLog.java,v 1.4 2002/06/15 20:54:48 craigmcc Exp $
 */
public class DiscoveryLogFactory {
    private static LogFactory logFactory = null;
    private static final Hashtable  classRegistry = new Hashtable();
    private static final Class[] setLogParamClasses = new Class[] { Log.class };

    /**
     * Above fields must be initialied before this one..
     */
    private static Log log = DiscoveryLogFactory._newLog(DiscoveryLogFactory.class);

    /**
     */    
    public static Log newLog(Class clazz) {
        /**
         * Required to implement 'public static void setLog(Log)'
         */
        try {
            Method setLog = ClassUtils.findPublicStaticMethod(clazz,
                                                              void.class,
                                                              "setLog",
                                                              setLogParamClasses);
            
            if (setLog == null) {
                String msg = "Internal Error: " + clazz.getName() + " required to implement 'public static void setLog(Log)'";
                log.fatal(msg);
                throw new DiscoveryException(msg);
            }
        } catch (SecurityException se) {
            String msg = "Required Security Permissions not present";
            log.fatal(msg, se);
            throw new DiscoveryException(msg, se);
        }

        if (log.isDebugEnabled())
            log.debug("Class meets requirements: " + clazz.getName());

        return _newLog(clazz);
    }

    /**
     * This method MUST not invoke any logging..
     */
    public static Log _newLog(Class clazz) {
        classRegistry.put(clazz, clazz);

        return (logFactory == null)
               ? new SimpleLog(clazz.getName())
               : logFactory.getInstance(clazz.getName());
    }
    
    public static void setLog(Log _log) {
        log = _log;
    }

    /**
     * Set logFactory, works ONLY on first call.
     */
    public static void setFactory(LogFactory factory) {
        if (logFactory == null) {
            // for future generations.. if any
            logFactory = factory;
            
            // now, go back and reset loggers for all current classes..
            Enumeration elements = classRegistry.elements();
            while (elements.hasMoreElements()) {
                Class clazz = (Class)elements.nextElement();

                if (log.isDebugEnabled())
                    log.debug("Reset Log for: " + clazz.getName());
                
                Method setLog = null;
                
                // invoke 'setLog(Log)'.. we already know it's 'public static',
                // have verified parameters, and return type..
                try {
                    setLog = clazz.getMethod("setLog", setLogParamClasses);
                } catch(Exception e) {
                    String msg = "Internal Error: pre-check for " + clazz.getName() + " failed?!";
                    log.fatal(msg, e);
                    throw new DiscoveryException(msg, e);
                }
    
                Object[] setLogParam = new Object[] { factory.getInstance(clazz.getName()) };
                
                try {
                    setLog.invoke(null, setLogParam);
                } catch(Exception e) {
                    String msg = "Internal Error: setLog failed for " + clazz.getName();
                    log.fatal(msg, e);
                    throw new DiscoveryException(msg, e);
                }
            }
        }
    }
}
