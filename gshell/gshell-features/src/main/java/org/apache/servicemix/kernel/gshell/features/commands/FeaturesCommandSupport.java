/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.servicemix.kernel.gshell.features.commands;

import org.apache.servicemix.kernel.gshell.core.OsgiCommandSupport;
import org.apache.servicemix.kernel.gshell.features.FeaturesService;
import org.osgi.framework.ServiceReference;

public abstract class FeaturesCommandSupport extends OsgiCommandSupport {

    protected Object doExecute() throws Exception {
        // Get repository admin service.
        ServiceReference ref = getBundleContext().getServiceReference(FeaturesService.class.getName());
        if (ref == null) {
            io.out.println("FeaturesService service is unavailable.");
            return null;
        }
        try {
            FeaturesService admin = (FeaturesService) getBundleContext().getService(ref);
            if (admin == null) {
                io.out.println("FeaturesService service is unavailable.");
                return null;
            }

            doExecute(admin);
        }
        finally {
            getBundleContext().ungetService(ref);
        }
        return null;
    }

    protected abstract void doExecute(FeaturesService admin) throws Exception;

}
