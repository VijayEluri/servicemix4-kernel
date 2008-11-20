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
package org.apache.servicemix.kernel.gshell.itests;

import org.apache.servicemix.kernel.testing.support.AbstractIntegrationTest;
import org.apache.geronimo.gshell.shell.Shell;

public class CoreTest extends AbstractIntegrationTest {

    @Override
    protected String getManifestLocation() {
        return "classpath:org/apache/servicemix/kernel/gshell/itests/MANIFEST.MF";
    }

    @Override
    protected String[] getTestBundlesNames() {
        return new String[] {
            getBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.commons-httpclient"),
            getBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.commons-jexl"),
            getBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.commons-vfs"),
            getBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.mina"),
            getBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.oro"),
            getBundle("org.apache.servicemix.kernel.jaas", "org.apache.servicemix.kernel.jaas.config"),
            getBundle("com.google.code.sshd", "sshd"),
            getBundle("org.apache.servicemix.kernel.gshell", "org.apache.servicemix.kernel.gshell.core")
        };
    }

    public void testHelp() throws Exception {
        Shell shell = getOsgiService(Shell.class);
        shell.execute("help");
    }

}