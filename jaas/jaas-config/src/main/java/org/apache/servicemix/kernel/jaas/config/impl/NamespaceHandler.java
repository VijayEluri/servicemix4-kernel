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
package org.apache.servicemix.kernel.jaas.config.impl;

import java.net.URL;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.EntityReference;

import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.ValueMetadata;
import org.osgi.service.blueprint.reflect.RefMetadata;
import org.osgi.service.blueprint.namespace.ParserContext;
import org.osgi.service.blueprint.context.ComponentDefinitionException;
import org.apache.servicemix.kernel.jaas.config.JaasRealm;
import org.apache.servicemix.kernel.jaas.config.KeystoreInstance;
import org.apache.servicemix.kernel.jaas.boot.ProxyLoginModule;
import org.apache.geronimo.blueprint.mutable.MutableBeanMetadata;
import org.apache.geronimo.blueprint.mutable.MutableValueMetadata;
import org.apache.geronimo.blueprint.mutable.MutableRefMetadata;
import org.apache.geronimo.blueprint.mutable.MutableCollectionMetadata;
import org.apache.geronimo.blueprint.mutable.MutableServiceMetadata;
import org.apache.geronimo.blueprint.ExtendedParserContext;

public class NamespaceHandler implements org.osgi.service.blueprint.namespace.NamespaceHandler {

    public URL getSchemaLocation(String namespace) {
        return getClass().getResource("/org/apache/servicemix/kernel/jaas/config/servicemix-jaas.xsd");
    }

    public ComponentMetadata parse(Element element, ParserContext ctx) {
        ExtendedParserContext context = (ExtendedParserContext) ctx;
        if ("config".equals(element.getNodeName())) {
            return parseConfig(element, context);
        } else if ("keystore".equals(element.getNodeName())) {
            return parseKeystore(element, context);
        }
        throw new ComponentDefinitionException("Bad xml syntax: unknown element '" + element.getNodeName() + "'");
    }

    public ComponentMetadata decorate(Node node, ComponentMetadata component, ParserContext context) {
        throw new ComponentDefinitionException("Bad xml syntax: node decoration is not supported");
    }

    public ComponentMetadata parseConfig(Element element, ExtendedParserContext context) {
        MutableBeanMetadata bean = context.createMetadata(MutableBeanMetadata.class);
        bean.setClassName(Config.class.getName());
        String name = element.getAttribute("name");
        if (name == null || name.length() == 0) {
            name = element.getAttribute("id");
        }
        bean.setId(name);
        bean.addProperty("bundleContext", createRef(context, "bundleContext"));
        bean.addProperty("name", createValue(context, name));
        String rank = element.getAttribute("rank");
        if (rank != null && rank.length() > 0) {
            bean.addProperty("rank", createValue(context, rank));
        }
        NodeList childElements = element.getElementsByTagName("module");
        if (childElements != null && childElements.getLength() > 0) {
            MutableCollectionMetadata children = context.createMetadata(MutableCollectionMetadata.class);
            for (int i = 0; i < childElements.getLength(); ++i) {
                Element childElement = (Element) childElements.item(i);
                MutableBeanMetadata md = context.createMetadata(MutableBeanMetadata.class);
                md.setClassName(Module.class.getName());
                md.addProperty("className", createValue(context, childElement.getAttribute("className")));
                if (childElement.getAttribute("flags") != null) {
                    md.addProperty("flags", createValue(context, childElement.getAttribute("flags")));
                }
                String options = getTextValue(childElement);
                if (options != null && options.length() > 0) {
                    md.addProperty("options", createValue(context, options));
                }
                children.addValue(md);
            }
            bean.addProperty("modules", children);
        }
        // Publish to OSGi
        String publish = element.getAttribute("publish");
        if (Boolean.valueOf(publish)) {
            // Publish Config
            MutableServiceMetadata service = context.createMetadata(MutableServiceMetadata.class);
            service.setServiceComponent(createRef(context, name));
            service.addInterfaceName(JaasRealm.class.getName());
            service.addServiceProperty(createValue(context, ProxyLoginModule.PROPERTY_MODULE), createValue(context, name));
            context.getComponentDefinitionRegistry().registerComponentDefinition(service);
        }
        return bean;
    }

    public ComponentMetadata parseKeystore(Element element, ExtendedParserContext context) {
        MutableBeanMetadata bean = context.createMetadata(MutableBeanMetadata.class);
        bean.setClassName(ResourceKeystoreInstance.class.getName());
        // Parse name
        String name = element.getAttribute("name");
        if (name == null || name.length() == 0) {
            name = element.getAttribute("id");
        }
        if (name != null && name.length() > 0) {
            bean.addProperty("name", createValue(context, name));
        }
        // Parse rank
        String rank = element.getAttribute("rank");
        if (rank != null && rank.length() > 0) {
            bean.addProperty("rank", createValue(context, rank));
        }
        // Parse path
        String path = element.getAttribute("path");
        if (path != null && path.length() > 0) {
            bean.addProperty("path", createValue(context, path));
        }
        // Parse keystorePassword
        String keystorePassword = element.getAttribute("keystorePassword");
        if (keystorePassword != null && keystorePassword.length() > 0) {
            bean.addProperty("keystorePassword", createValue(context, keystorePassword));
        }
        // Parse keyPasswords
        String keyPasswords = element.getAttribute("keyPasswords");
        if (keyPasswords != null && keyPasswords.length() > 0) {
            bean.addProperty("keyPasswords", createValue(context, keyPasswords));
        }
        // Parse publish
        String publish = element.getAttribute("publish");
        if (Boolean.valueOf(publish)) {
            // Publish Config
            MutableServiceMetadata service = context.createMetadata(MutableServiceMetadata.class);
            service.setServiceComponent(createRef(context, name));
            service.addInterfaceName(KeystoreInstance.class.getName());
            context.getComponentDefinitionRegistry().registerComponentDefinition(service);
        }
        return bean;
    }

    private ValueMetadata createValue(ExtendedParserContext context, String value) {
        MutableValueMetadata v = context.createMetadata(MutableValueMetadata.class);
        v.setStringValue(value);
        return v;
    }

    private RefMetadata createRef(ExtendedParserContext context, String value) {
        MutableRefMetadata r = context.createMetadata(MutableRefMetadata.class);
        r.setComponentId(value);
        return r;
    }

    private static String getTextValue(Element element) {
        StringBuffer value = new StringBuffer();
        NodeList nl = element.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node item = nl.item(i);
            if ((item instanceof CharacterData && !(item instanceof Comment)) || item instanceof EntityReference) {
                value.append(item.getNodeValue());
            }
        }
        return value.toString();
    }
}
