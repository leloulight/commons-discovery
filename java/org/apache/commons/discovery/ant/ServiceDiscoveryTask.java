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
package org.apache.commons.discovery.ant;

import java.util.Vector;

import org.apache.commons.discovery.Resource;
import org.apache.commons.discovery.jdk.JDKHooks;
import org.apache.commons.discovery.listeners.GatherResourcesListener;
import org.apache.commons.discovery.resource.DiscoverResources;


/**
 * Small ant task that will use discovery to locate a particular impl.
 * and display all values.
 *
 * You can execute this and save it with an id, then other classes can use it.
 *
 * @author Costin Manolache
 */
public class ServiceDiscoveryTask
{
    String name;
    int debug=0;
    String[] drivers = null;
        
    public void setServiceName(String name ) {
        this.name=name;
    }

    public void setDebug(int i) {
        this.debug=debug;
    }

    public String[] getServiceInfo() {
        return drivers;
    }

    public void execute() throws Exception {
        System.out.println("XXX ");
        
        GatherResourcesListener listener = new GatherResourcesListener();
        DiscoverResources disc = new DiscoverResources();
        disc.addClassLoader( JDKHooks.getJDKHooks().getThreadContextClassLoader() );
        disc.addClassLoader( this.getClass().getClassLoader() );
        disc.setListener(listener);
        disc.find(name);

        Vector vector = listener.getResources();
        drivers = new String[vector.size()];
        for (int i = 0; i < vector.size(); i++) {
            drivers[i] = ((Resource)vector.get(i)).getName();
            if( debug > 0 ) {
                System.out.println("Found " + drivers[i]);
            }
        }
    }
        
}
