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
package org.apache.commons.discovery.resource.names;

import org.apache.commons.discovery.ResourceNameDiscover;
import org.apache.commons.discovery.ResourceNameListener;


/**
 * Helper class for methods implementing the ResourceNameDiscover interface.
 * 
 * @author Richard A. Sitze
 */
public abstract class ResourceNameDiscoverImpl
    implements ResourceNameDiscover
{
    private ResourceNameListener listener = null;
    
    protected ResourceNameListener getListener() {
        return listener;
    }
    
    protected boolean notifyListener(String resourceName) {
        return (listener == null) ? true : listener.found(resourceName);
    }

    public void setListener(ResourceNameListener listener) {
        this.listener = listener;
    }
}
