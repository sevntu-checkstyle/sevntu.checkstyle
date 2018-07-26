////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2018 the original author or authors.
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
////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.internal;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Test;
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

        Assert.assertTrue("no modules", modules.size() > 0);

        // sonar

        final File extensionFile = new File(getSonarPath("checkstyle-extensions.xml"));

        Assert.assertTrue("'checkstyle-extensions.xml' must exist in sonar",
                extensionFile.exists());

        final String input = new String(Files.readAllBytes(extensionFile.toPath()), UTF_8);

        final Document document = XmlUtil.getRawXml(extensionFile.getAbsolutePath(), input,
                input);

        validateSonarFile(document, new HashSet<>(modules));

        // eclipsecs

        for (String p : packages) {
            Assert.assertTrue("folder " + p + " must exist in eclipsecs", new File(
                    getEclipseCsPath(p)).exists());

            final Set<Class<?>> pkgModules = CheckUtil.getModulesInPackage(modules, p);

            validateEclipseCsMetaXmlFile(
                    new File(getEclipseCsPath(p + "/checkstyle-metadata.xml")), p, new HashSet<>(
                            pkgModules));

            validateEclipseCsMetaPropFile(new File(getEclipseCsPath(p
                    + "/checkstyle-metadata.properties")), p, new HashSet<>(pkgModules));
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

            Assert.assertNotNull("Unknown class found in sonar: " + key, module);

            final String moduleName = module.getName();
            final Node name = SevntuXmlUtil.findElementByTag(children, "name");

            Assert.assertNotNull(moduleName + " requires a name in sonar", name);
            Assert.assertFalse(moduleName + " requires a name in sonar", name.getTextContent()
                    .isEmpty());

            final Node categoryNode = SevntuXmlUtil.findElementByTag(children, "category");

            String expectedCategory = module.getCanonicalName();
            final int lastIndex = expectedCategory.lastIndexOf('.');
            expectedCategory = expectedCategory.substring(
                    expectedCategory.lastIndexOf('.', lastIndex - 1) + 1, lastIndex);

            Assert.assertNotNull(moduleName + " requires a category in sonar", categoryNode);

            final Node categoryAttribute = categoryNode.getAttributes().getNamedItem("name");

            Assert.assertNotNull(moduleName + " requires a category name in sonar",
                    categoryAttribute);
            Assert.assertEquals(moduleName + " requires a valid category in sonar",
                    expectedCategory, categoryAttribute.getTextContent());

            final Node description = SevntuXmlUtil.findElementByTag(children, "description");

            Assert.assertNotNull(moduleName + " requires a description in sonar", description);

            final Node configKey = SevntuXmlUtil.findElementByTag(children, "configKey");
            final String expectedConfigKey = "Checker/TreeWalker/" + key;

            Assert.assertNotNull(moduleName + " requires a configKey in sonar", configKey);
            Assert.assertEquals(moduleName + " requires a valid configKey in sonar",
                    expectedConfigKey, configKey.getTextContent());

            validateSonarProperties(module, SevntuXmlUtil.findElementsByTag(children, "param"));
        }

        for (Class<?> module : modules) {
            Assert.fail("Module not found in sonar: " + module.getCanonicalName());
        }
    }

    private static void validateSonarProperties(Class<?> module, Set<Node> parameters) {
        final String moduleName = module.getName();
        final Set<String> properties = getFinalProperties(module);

        for (Node parameter : parameters) {
            final NamedNodeMap attributes = parameter.getAttributes();
            final Node paramKeyNode = attributes.getNamedItem("key");

            Assert.assertNotNull(moduleName + " requires a key for unknown parameter in sonar",
                    paramKeyNode);

            final String paramKey = paramKeyNode.getTextContent();

            Assert.assertFalse(moduleName
                    + " requires a valid key for unknown parameter in sonar",
                    paramKey.isEmpty());

            Assert.assertTrue(moduleName + " has an unknown parameter in sonar: " + paramKey,
                    properties.remove(paramKey));
        }

        for (String property : properties) {
            Assert.fail(moduleName + " parameter not found in sonar: " + property);
        }
    }

    private static void validateEclipseCsMetaXmlFile(File file, String pkg,
            Set<Class<?>> pkgModules) throws Exception {
        Assert.assertTrue("'checkstyle-metadata.xml' must exist in eclipsecs in inside " + pkg,
                file.exists());

        final String input = new String(Files.readAllBytes(file.toPath()), UTF_8);
        final Document document = XmlUtil.getRawXml(file.getAbsolutePath(), input, input);

        final NodeList ruleGroups = document.getElementsByTagName("rule-group-metadata");

        Assert.assertTrue(pkg + " checkstyle-metadata.xml must contain only one rule group",
                ruleGroups.getLength() == 1);

        for (int position = 0; position < ruleGroups.getLength(); position++) {
            final Node ruleGroup = ruleGroups.item(position);
            final Set<Node> children = XmlUtil.getChildrenElements(ruleGroup);

            validateEclipseCsMetaXmlFileRules(pkg, pkgModules, children);
        }

        for (Class<?> module : pkgModules) {
            Assert.fail("Module not found in " + pkg + " checkstyle-metadata.xml: "
                    + module.getCanonicalName());
        }
    }

    private static void validateEclipseCsMetaXmlFileRules(String pkg,
            Set<Class<?>> pkgModules, Set<Node> rules) throws Exception {
        for (Node rule : rules) {
            final NamedNodeMap attributes = rule.getAttributes();
            final Node internalNameNode = attributes.getNamedItem("internal-name");

            Assert.assertNotNull(pkg + " checkstyle-metadata.xml must contain an internal name",
                    internalNameNode);

            final String internalName = internalNameNode.getTextContent();
            final String classpath = "com.github.sevntu.checkstyle.checks." + pkg + "."
                    + internalName;

            final Class<?> module = findModule(pkgModules, classpath);
            pkgModules.remove(module);

            Assert.assertNotNull("Unknown class found in " + pkg + " checkstyle-metadata.xml: "
                    + internalName, module);

            final Node nameAttribute = attributes.getNamedItem("name");

            Assert.assertNotNull(pkg + " checkstyle-metadata.xml requires a name for "
                    + internalName, nameAttribute);
            Assert.assertEquals(pkg + " checkstyle-metadata.xml requires a valid name for "
                    + internalName, "%" + internalName + ".name", nameAttribute.getTextContent());

            final Node parentAttribute = attributes.getNamedItem("parent");

            Assert.assertNotNull(pkg + " checkstyle-metadata.xml requires a parent for "
                    + internalName, parentAttribute);
            Assert.assertEquals(pkg + " checkstyle-metadata.xml requires a valid parent for "
                    + internalName, "TreeWalker", parentAttribute.getTextContent());

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

                    Assert.assertNotNull(pkg
                            + " checkstyle-metadata.xml must contain an internal name for "
                            + moduleName, internalNameNode);

                    final String internalName = internalNameNode.getTextContent();

                    Assert.assertEquals(pkg
                            + " checkstyle-metadata.xml requires a valid internal-name for "
                            + moduleName, module.getName(), internalName);
                    break;
                case "description":
                    Assert.assertEquals(
                            pkg + " checkstyle-metadata.xml requires a valid description for "
                                    + moduleName, "%" + moduleName + ".desc",
                            child.getTextContent());
                    break;
                case "property-metadata":
                    final String propertyName = attributes.getNamedItem("name").getTextContent();

                    Assert.assertTrue(pkg
                            + " checkstyle-metadata.xml has an unknown parameter for "
                            + moduleName + ": " + propertyName, properties.remove(propertyName));

                    final Node firstChild = child.getFirstChild().getNextSibling();

                    Assert.assertNotNull(pkg
                            + " checkstyle-metadata.xml requires atleast one child for "
                            + moduleName + ", " + propertyName, firstChild);
                    Assert.assertEquals(
                            pkg
                                    + " checkstyle-metadata.xml should have a description for the "
                                    + "first child of "
                                    + moduleName + ", " + propertyName, "description",
                            firstChild.getNodeName());
                    Assert.assertEquals(pkg
                            + " checkstyle-metadata.xml requires a valid description for "
                            + moduleName + ", " + propertyName, "%" + moduleName + "."
                            + propertyName,
                            firstChild.getTextContent());
                    break;
                case "message-key":
                    final String key = attributes.getNamedItem("key").getTextContent();

                    Assert.assertTrue(pkg
                            + " checkstyle-metadata.xml has an unknown message for "
                            + moduleName + ": " + key, messages.remove(key));
                    break;
                default:
                    Assert.fail(pkg + " checkstyle-metadata.xml unknown node for " + moduleName
                            + ": " + child.getNodeName());
                    break;
            }
        }

        for (String property : properties) {
            Assert.fail(pkg + " checkstyle-metadata.xml missing parameter for " + moduleName
                    + ": " + property);
        }

        for (String message : messages) {
            Assert.fail(pkg + " checkstyle-metadata.xml missing message for " + moduleName
                    + ": " + message);
        }
    }

    private static void validateEclipseCsMetaPropFile(File file, String pkg,
            Set<Class<?>> pkgModules) throws Exception {
        Assert.assertTrue("'checkstyle-metadata.properties' must exist in eclipsecs in inside "
                + pkg, file.exists());

        final Properties prop = new Properties();
        prop.load(new FileInputStream(file));

        final Set<Object> properties = new HashSet<>(Collections.list(prop.keys()));

        for (Class<?> module : pkgModules) {
            final String moduleName = module.getSimpleName();

            Assert.assertTrue(moduleName + " requires a name in eclipsecs properties " + pkg,
                    properties.remove(moduleName + ".name"));
            Assert.assertTrue(moduleName + " requires a desc in eclipsecs properties " + pkg,
                    properties.remove(moduleName + ".desc"));

            final Set<String> moduleProperties = getFinalProperties(module);

            for (String moduleProperty : moduleProperties) {
                Assert.assertTrue(moduleName + " requires the property " + moduleProperty
                        + " in eclipsecs properties " + pkg,
                        properties.remove(moduleName + "." + moduleProperty));
            }
        }

        for (Object property : properties) {
            Assert.fail("Unknown property found in eclipsecs properties " + pkg + ": "
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

        for (PropertyDescriptor p : map) {
            if (p.getWriteMethod() != null) {
                result.add(p.getName());
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
