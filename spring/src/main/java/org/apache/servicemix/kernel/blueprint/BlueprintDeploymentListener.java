/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.servicemix.kernel.blueprint;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.servicemix.kernel.filemonitor.DeploymentListener;

/**
 * A deployment listener that listens for spring xml applications
 * and creates bundles for these.
 */
public class BlueprintDeploymentListener implements DeploymentListener {


    private static final Log LOGGER = LogFactory.getLog(BlueprintDeploymentListener.class);

    private DocumentBuilderFactory dbf;

    public boolean canHandle(File artifact) {
        try {
            if (artifact.isFile() && artifact.getName().endsWith(".xml")) {
                Document doc = parse(artifact);
                String name = doc.getDocumentElement().getLocalName();
                String uri  = doc.getDocumentElement().getNamespaceURI();
                if ("beans".equals(name) && "http://www.osgi.org/xmlns/blueprint/v1.0.0".equals(uri)) {
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Unable to parse deployed file " + artifact.getAbsolutePath(), e);
        }
        return false;
    }

    public File handle(File artifact, File tmpDir) {
        try {
            File destFile = new File(tmpDir, artifact.getName() + ".jar");
            FileOutputStream os = new FileOutputStream(destFile);
            BlueprintTransformer.transform(artifact.toURL(), os);
            os.close();
            return destFile;
        } catch (Exception e) {
            LOGGER.error("Unable to build blueprint application bundle", e);
            return null;
        }
    }

    protected Document parse(File artifact) throws Exception {
        if (dbf == null) {
            dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
        }
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(artifact);
    }

}
