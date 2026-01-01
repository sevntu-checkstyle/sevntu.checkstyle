///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2026 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
///////////////////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.internal;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.AbstractFileSetCheck;
import com.puppycrawl.tools.checkstyle.checks.javadoc.AbstractJavadocCheck;
import com.puppycrawl.tools.checkstyle.internal.utils.XmlUtil;

public final class ChecksTest {

    private static final Set<String> CHECK_PROPERTIES = getProperties(AbstractCheck.class);
    private static final Set<String> JAVADOC_CHECK_PROPERTIES =
            getProperties(AbstractJavadocCheck.class);
    private static final Set<String> FILESET_PROPERTIES = getProperties(AbstractFileSetCheck.class);

    @Test
    public void verifyTestConfigurationFiles() throws Exception {
        final Set<Class<?>> modules = CheckUtil.getCheckstyleModules();
        final Set<String> packages = CheckUtil.getPackages(modules);

        Assertions.assertFalse(modules.isEmpty(), "no modules");

        // sonar

        final File extensionFile = new File(getSonarPath("checkstyle-extensions.xml"));

        Assertions.assertTrue(
                extensionFile.exists(), "'checkstyle-extensions.xml' must exist in sonar");

        final String input = new String(Files.readAllBytes(extensionFile.toPath()), UTF_8);

        final Document document = XmlUtil.getRawXml(extensionFile.getAbsolutePath(), input,
                input);

        validateSonarFile(document, new HashSet<>(modules));

        // eclipsecs

        for (String packageName : packages) {
            Assertions.assertTrue(new File(
                    getEclipseCsPath(packageName)).exists(), "folder " + packageName
                    + " must exist in eclipsecs");

            final Set<Class<?>> pkgModules = CheckUtil.getModulesInPackage(modules, packageName);

            validateEclipseCsMetaXmlFile(
                    new File(getEclipseCsPath(packageName
                            + "/checkstyle-metadata.xml")), packageName, new HashSet<>(
                                pkgModules));

            validateEclipseCsMetaPropFile(new File(getEclipseCsPath(packageName
                    + "/checkstyle-metadata.properties")), packageName, new HashSet<>(pkgModules));
        }
    }

    private static void validateSonarFile(Document document, Set<Class<?>> modules) {
        final NodeList rules = document.getElementsByTagName("rule");

        for (int position = 0; position < rules.getLength(); position++) {
            final Node rule = rules.item(position);
            final Set<Node> children = XmlUtil.getChildrenElements(rule);

            final String key = SevntuXmlUtil.findElementByTag(children, "key").getTextContent();

            final Class<?> module = findModule(modules, key);
            modules.remove(module);

            Assertions.assertNotNull(module, "Unknown class found in sonar: " + key);

            final String moduleName = module.getName();
            final Node name = SevntuXmlUtil.findElementByTag(children, "name");

            Assertions.assertNotNull(name, moduleName + " requires a name in sonar");
            Assertions.assertFalse(name.getTextContent()
                    .isEmpty(), moduleName + " requires a name in sonar");

            final Node tagNode = SevntuXmlUtil.findElementByTag(children, "tag");

            String expectedTag = module.getCanonicalName();
            final int lastIndex = expectedTag.lastIndexOf('.');
            expectedTag = expectedTag.substring(
                    expectedTag.lastIndexOf('.', lastIndex - 1) + 1, lastIndex);

            Assertions.assertNotNull(tagNode, moduleName + " requires a tag in sonar");

            Assertions.assertEquals(expectedTag, tagNode.getTextContent(),
                    moduleName + " requires a valid tag in sonar");

            final Node description = SevntuXmlUtil.findElementByTag(children, "description");

            Assertions.assertNotNull(description, moduleName + " requires a description in sonar");

            final Node configKey = SevntuXmlUtil.findElementByTag(children, "configKey");
            final String expectedConfigKey = "Checker/TreeWalker/" + key;

            Assertions.assertNotNull(configKey, moduleName + " requires a configKey in sonar");
            Assertions.assertEquals(expectedConfigKey, configKey.getTextContent(),
                    moduleName + " requires a valid configKey in sonar");

            validateSonarProperties(module, SevntuXmlUtil.findElementsByTag(children, "param"));
        }

        for (Class<?> module : modules) {
            Assertions.fail("Module not found in sonar: " + module.getCanonicalName());
        }
    }

    private static void validateSonarProperties(Class<?> module, Set<Node> parameters) {
        final String moduleName = module.getName();
        final Set<String> properties = getFinalProperties(module);

        for (Node parameter : parameters) {
            final NamedNodeMap attributes = parameter.getAttributes();
            final Node paramKeyNode = attributes.getNamedItem("key");

            Assertions.assertNotNull(
                    paramKeyNode, moduleName + " requires a key for unknown parameter in sonar");

            final String paramKey = paramKeyNode.getTextContent();

            Assertions.assertFalse(
                    paramKey.isEmpty(), moduleName
                            + " requires a valid key for unknown parameter in sonar");

            Assertions.assertTrue(properties.remove(paramKey),
                    moduleName + " has an unknown parameter in sonar: " + paramKey);
        }

        for (String property : properties) {
            Assertions.fail(moduleName + " parameter not found in sonar: " + property);
        }
    }

    private static void validateEclipseCsMetaXmlFile(File file, String pkg,
            Set<Class<?>> pkgModules) throws Exception {
        Assertions.assertTrue(file.exists(),
                "'checkstyle-metadata.xml' must exist in eclipsecs in inside " + pkg);

        final String input = new String(Files.readAllBytes(file.toPath()), UTF_8);
        final Document document = XmlUtil.getRawXml(file.getAbsolutePath(), input, input);

        final NodeList ruleGroups = document.getElementsByTagName("rule-group-metadata");

        Assertions.assertTrue(ruleGroups.getLength() == 1,
                pkg + " checkstyle-metadata.xml must contain only one rule group");

        for (int position = 0; position < ruleGroups.getLength(); position++) {
            final Node ruleGroup = ruleGroups.item(position);
            final Set<Node> children = XmlUtil.getChildrenElements(ruleGroup);

            validateEclipseCsMetaXmlFileRules(pkg, pkgModules, children);
        }

        for (Class<?> module : pkgModules) {
            Assertions.fail("Module not found in " + pkg + " checkstyle-metadata.xml: "
                    + module.getCanonicalName());
        }
    }

    private static void validateEclipseCsMetaXmlFileRules(String pkg,
            Set<Class<?>> pkgModules, Set<Node> rules) throws Exception {
        for (Node rule : rules) {
            final NamedNodeMap attributes = rule.getAttributes();
            final Node internalNameNode = attributes.getNamedItem("internal-name");

            Assertions.assertNotNull(internalNameNode,
                    pkg + " checkstyle-metadata.xml must contain an internal name");

            final String internalName = internalNameNode.getTextContent();
            final String classpath = "com.github.sevntu.checkstyle.checks." + pkg + "."
                    + internalName;

            final Class<?> module = findModule(pkgModules, classpath);
            pkgModules.remove(module);

            Assertions.assertNotNull(module, "Unknown class found in " + pkg
                    + " checkstyle-metadata.xml: " + internalName);

            final Node nameAttribute = attributes.getNamedItem("name");

            Assertions.assertNotNull(nameAttribute, pkg
                    + " checkstyle-metadata.xml requires a name for " + internalName);
            Assertions.assertEquals("%" + internalName + ".name", nameAttribute.getTextContent(),
                    pkg + " checkstyle-metadata.xml requires a valid name for " + internalName);

            final Node parentAttribute = attributes.getNamedItem("parent");

            Assertions.assertNotNull(parentAttribute, pkg
                    + " checkstyle-metadata.xml requires a parent for " + internalName);
            Assertions.assertEquals("TreeWalker", parentAttribute.getTextContent(), pkg
                    + " checkstyle-metadata.xml requires a valid parent for " + internalName);

            final Set<Node> children = XmlUtil.getChildrenElements(rule);

            validateEclipseCsMetaXmlFileRule(pkg, module, children);
        }
    }

    private static void validateEclipseCsMetaXmlFileRule(String pkg, Class<?> module,
            Set<Node> children) throws Exception {
        final String moduleName = module.getSimpleName();
        final Set<String> properties = getFinalProperties(module);
        final Set<Field> fieldMessages = CheckUtil.getCheckMessages(module);
        final Set<String> messages = new TreeSet<>();

        for (Field fieldMessage : fieldMessages) {
            // below is required for package/private classes
            if (!fieldMessage.isAccessible()) {
                fieldMessage.setAccessible(true);
            }

            messages.add(fieldMessage.get(null).toString());
        }

        for (Node child : children) {
            final NamedNodeMap attributes = child.getAttributes();

            switch (child.getNodeName()) {
                case "alternative-name":
                    final Node internalNameNode = attributes.getNamedItem("internal-name");

                    Assertions.assertNotNull(internalNameNode, pkg
                            + " checkstyle-metadata.xml must contain an internal name for "
                            + moduleName);

                    final String internalName = internalNameNode.getTextContent();

                    Assertions.assertEquals(module.getName(), internalName, pkg
                            + " checkstyle-metadata.xml requires a valid internal-name for "
                            + moduleName);
                    break;
                case "description":
                    Assertions.assertEquals("%" + moduleName + ".desc", child.getTextContent(),
                            pkg + " checkstyle-metadata.xml requires a valid description for "
                                    + moduleName);
                    break;
                case "property-metadata":
                    final String propertyName = attributes.getNamedItem("name").getTextContent();

                    Assertions.assertTrue(properties.remove(propertyName), pkg
                            + " checkstyle-metadata.xml has an unknown parameter for "
                            + moduleName + ": " + propertyName);

                    final Node firstChild = child.getFirstChild().getNextSibling();

                    Assertions.assertNotNull(firstChild, pkg
                            + " checkstyle-metadata.xml requires atleast one child for "
                            + moduleName + ", " + propertyName);
                    Assertions.assertEquals(
                            "description",
                            firstChild.getNodeName(), pkg
                                    + " checkstyle-metadata.xml should have a description for the "
                                    + "first child of "
                                    + moduleName + ", " + propertyName);
                    Assertions.assertEquals("%" + moduleName + "."
                            + propertyName,
                            firstChild.getTextContent(), pkg
                                    + " checkstyle-metadata.xml requires a valid description for "
                                    + moduleName + ", " + propertyName);
                    break;
                case "message-key":
                    final String key = attributes.getNamedItem("key").getTextContent();

                    Assertions.assertTrue(messages.remove(key), pkg
                            + " checkstyle-metadata.xml has an unknown message for "
                            + moduleName + ": " + key);
                    break;
                default:
                    Assertions.fail(pkg + " checkstyle-metadata.xml unknown node for " + moduleName
                            + ": " + child.getNodeName());
                    break;
            }
        }

        for (String property : properties) {
            Assertions.fail(pkg + " checkstyle-metadata.xml missing parameter for " + moduleName
                    + ": " + property);
        }

        for (String message : messages) {
            Assertions.fail(pkg + " checkstyle-metadata.xml missing message for " + moduleName
                    + ": " + message);
        }
    }

    private static void validateEclipseCsMetaPropFile(File file, String pkg,
            Set<Class<?>> pkgModules) throws Exception {
        Assertions.assertTrue(file.exists(),
                "'checkstyle-metadata.properties' must exist in eclipsecs in inside " + pkg);

        final Properties prop = new Properties();
        prop.load(Files.newBufferedReader(file.toPath()));

        final Set<Object> properties = new HashSet<>(Collections.list(prop.keys()));

        for (Class<?> module : pkgModules) {
            final String moduleName = module.getSimpleName();

            Assertions.assertTrue(properties.remove(moduleName + ".name"),
                    moduleName + " requires a name in eclipsecs properties " + pkg);
            Assertions.assertTrue(properties.remove(moduleName + ".desc"),
                    moduleName + " requires a desc in eclipsecs properties " + pkg);

            final Set<String> moduleProperties = getFinalProperties(module);

            for (String moduleProperty : moduleProperties) {
                Assertions.assertTrue(properties.remove(moduleName + "." + moduleProperty),
                        moduleName + " requires the property " + moduleProperty
                                + " in eclipsecs properties " + pkg);
            }
        }

        for (Object property : properties) {
            Assertions.fail("Unknown property found in eclipsecs properties " + pkg + ": "
                    + property);
        }
    }

    private static Class<?> findModule(Set<Class<?>> modules, String classPath) {
        Class<?> result = null;

        for (Class<?> module : modules) {
            if (module.getCanonicalName().equals(classPath)) {
                result = module;
                break;
            }
        }

        return result;
    }

    private static Set<String> getFinalProperties(Class<?> clss) {
        final Set<String> properties = getProperties(clss);

        if (AbstractJavadocCheck.class.isAssignableFrom(clss)) {
            properties.removeAll(JAVADOC_CHECK_PROPERTIES);
        }
        else if (AbstractCheck.class.isAssignableFrom(clss)) {
            properties.removeAll(CHECK_PROPERTIES);
        }
        else if (AbstractFileSetCheck.class.isAssignableFrom(clss)) {
            properties.removeAll(FILESET_PROPERTIES);

            // override
            properties.add("fileExtensions");
        }

        return properties;
    }

    private static Set<String> getProperties(Class<?> clss) {
        final Set<String> result = new TreeSet<>();
        final PropertyDescriptor[] map = PropertyUtils.getPropertyDescriptors(clss);

        for (PropertyDescriptor descriptor : map) {
            if (descriptor.getWriteMethod() != null) {
                result.add(descriptor.getName());
            }
        }

        return result;
    }

    /**
     * Returns canonical path for the file with the given file name.
     *
     * @param filename file name.
     * @return canonical path for the file name.
     * @throws IOException if I/O exception occurs while forming the path.
     */
    private static String getEclipseCsPath(String filename) throws IOException {
        return new File("../eclipsecs-sevntu-plugin/src/com/github/sevntu/checkstyle/checks/"
                + filename).getCanonicalPath();
    }

    /**
     * Returns canonical path for the file with the given file name.
     *
     * @param filename file name.
     * @return canonical path for the file name.
     * @throws IOException if I/O exception occurs while forming the path.
     */
    private static String getSonarPath(String filename) throws IOException {
        return new File(
                "../sevntu-checkstyle-sonar-plugin/src/main/resources/com/github/sevntu/"
                        + "checkstyle/sonar/" + filename).getCanonicalPath();
    }

}
