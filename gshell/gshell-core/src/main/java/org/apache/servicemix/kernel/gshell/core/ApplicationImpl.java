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
package org.apache.servicemix.kernel.gshell.core;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.Properties;

import org.apache.geronimo.gshell.application.Application;
import org.apache.geronimo.gshell.application.ClassPath;
import org.apache.geronimo.gshell.application.model.ApplicationModel;
import org.apache.geronimo.gshell.artifact.Artifact;
import org.apache.geronimo.gshell.command.Variables;
import org.apache.geronimo.gshell.io.IO;

public class ApplicationImpl implements Application  {

	private static final String EMBEDDED_PROPS = "org/apache/servicemix/kernel/version/embedded.properties";
    private static final String SERVICEMIX_VERSION ="org/apache/servicemix/kernel/gshell/core/servicemix-version.properties";
    private static final String VERSION_PROPERTY = "version";

    private String id;
    private IO io;
    private ApplicationModel model;
    private Variables variables;
    private InetAddress localHost;
    private File homeDir;         
    private URL embeddedResource = null; 

    public ApplicationImpl() throws Exception {
        this.localHost = InetAddress.getLocalHost();
        this.homeDir = detectHomeDir();    
    }           
     
    public void init() throws Exception {      	         	    	
        Properties props = new Properties();
        props.load(getClass().getClassLoader().getResourceAsStream(SERVICEMIX_VERSION));
        String kernelVersion = props.getProperty(VERSION_PROPERTY);
        this.model.setVersion(kernelVersion);
    	ServiceMixBranding smxBranding = (ServiceMixBranding) this.model.getBranding();
        smxBranding.setVersion(kernelVersion);
        
     	if (this.getClass().getClassLoader().getResource(EMBEDDED_PROPS) != null) {                    
            embeddedResource = this.getClass().getClassLoader().getResource(EMBEDDED_PROPS);
            smxBranding.setEmbeddedResource(embeddedResource);
        }
    }
    
    public URL getEmbeddedResource() {
    	return embeddedResource;
    }        
        
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public IO getIo() {
        return io;
    }

    public void setIo(IO io) {
        this.io = io;
    }

    public Variables getVariables() {
        return variables;
    }

    public void setVariables(Variables variables) {
        this.variables = variables;
    }

    public ApplicationModel getModel() {
        return model;
    }

    public void setModel(ApplicationModel model) {
        this.model = model;
    }

    public ClassPath getClassPath() {
        throw new UnsupportedOperationException();
    }

    public File getHomeDir() {
        if (homeDir == null) {
            throw new IllegalStateException();
        }
        return homeDir;
    }

    public InetAddress getLocalHost() {
        return localHost;
    }

    public String getUserName() {
        return System.getProperty("user.name");
    }

    public Artifact getArtifact() {
        return null;
    }

    private File detectHomeDir() {
        String homePath = System.getProperty("user.home");
        // And now lets resolve this sucker
        File dir;
        try {
            dir = new File(homePath).getCanonicalFile();
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to resolve home directory: " + homePath, e);
        }
        // And some basic sanity too
        if (!dir.exists() || !dir.isDirectory()) {
            throw new RuntimeException("Home directory configured but is not a valid directory: " + dir);
        }

        return dir;
    }

}
