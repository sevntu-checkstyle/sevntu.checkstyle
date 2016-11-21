package com.github.sevntu.checkstyle.internal;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
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

public final class ChecksTest {
    private static final Set<String> CHECK_PROPERTIES = getProperties(AbstractCheck.class);
    private static final Set<String> JAVADOC_CHECK_PROPERTIES = getProperties(AbstractJavadocCheck.class);
    private static final Set<String> FILESET_PROPERTIES = getProperties(AbstractFileSetCheck.class);

    @Test
    public void verifyTestConfigurationFiles() throws Exception {
        final Set<Class<?>> modules = CheckUtil.getCheckstyleModules();
        final Set<String> packages = CheckUtil.getPackages(modules);

        Assert.assertTrue("no modules", modules.size() > 0);

        // sonar
        {

            final File extensionFile = new File(getSonarPath("checkstyle-extensions.xml"));

            Assert.assertTrue("'checkstyle-extensions.xml' must exist in sonar",
                    extensionFile.exists());

            final String input = new String(Files.readAllBytes(extensionFile.toPath()), UTF_8);
            final String fileName = extensionFile.getName().toString();

            final Document document = XmlUtil.getRawXml(fileName, input, input);

            validateSonarFile(document, new HashSet<>(modules));
        }

        // eclipsecs
        for (String p : packages) {
            Assert.assertTrue("folder " + p + " must exist in eclipsecs", new File(
                    getEclipseCsPath(p)).exists());

            validateEclipseCsMetaXmlFile(
                    new File(getEclipseCsPath(p + "/checkstyle-metadata.xml")), p);

            validateEclipseCsMetaPropFile(new File(getEclipseCsPath(p
                    + "/checkstyle-metadata.properties")), p);
        }
    }

    private static void validateSonarFile(Document document, HashSet<Class<?>> modules) {
        final NodeList rules = document.getElementsByTagName("rule");

        for (int position = 0; position < rules.getLength(); position++) {
            final Node rule = rules.item(position);
            final Set<Node> children = XmlUtil.getChildrenElements(rule);

            final String key = XmlUtil.findElementByTag(children, "key").getTextContent();

            final Class<?> module = findModule(modules, key);
            modules.remove(module);

            Assert.assertNotNull("Unknown class found in sonar: " + key, module);

            final String moduleName = module.getName();
            final Node name = XmlUtil.findElementByTag(children, "name");

            Assert.assertNotNull(moduleName + " requires a name in sonar", name);
            Assert.assertFalse(moduleName + " requires a name in sonar", name.getTextContent()
                    .isEmpty());

            final Node categoryNode = XmlUtil.findElementByTag(children, "category");

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

            final Node description = XmlUtil.findElementByTag(children, "description");

            Assert.assertNotNull(moduleName + " requires a description in sonar", description);

            final Node configKey = XmlUtil.findElementByTag(children, "configKey");
            String expectedConfigKey = "Checker/TreeWalker/" + key;

            Assert.assertNotNull(moduleName + " requires a configKey in sonar", configKey);
            Assert.assertEquals(moduleName + " requires a valid configKey in sonar",
                    expectedConfigKey, configKey.getTextContent());

            final Set<Node> parameters = XmlUtil.findElementsByTag(children, "param");

            final Set<String> properties = getProperties(module);

            if (AbstractJavadocCheck.class.isAssignableFrom(module)) {
                properties.removeAll(JAVADOC_CHECK_PROPERTIES);
            }
            else if (AbstractCheck.class.isAssignableFrom(module)) {
                properties.removeAll(CHECK_PROPERTIES);
            }
            else if (AbstractFileSetCheck.class.isAssignableFrom(module)) {
                properties.removeAll(FILESET_PROPERTIES);

                // override
                properties.add("fileExtensions");
            }

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

        for (Class<?> module : modules) {
            Assert.fail("Module not found in sonar: " + module.getCanonicalName());
        }
    }

    private static void validateEclipseCsMetaXmlFile(File file, String packge) {
        Assert.assertTrue("'checkstyle-metadata.xml' must exist in eclipsecs in inside " + packge,
                file.exists());
    }

    private static void validateEclipseCsMetaPropFile(File file, String packge) {
        Assert.assertTrue("'checkstyle-metadata.properties' must exist in eclipsecs in inside "
                + packge, file.exists());
    }

    private static Class<?> findModule(HashSet<Class<?>> modules, String classPath) {
        Class<?> result = null;

        for (Class<?> module : modules) {
            if (module.getCanonicalName().equals(classPath)) {
                result = module;
                break;
            }
        }

        return result;
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
    private String getEclipseCsPath(String filename) throws IOException {
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
    private String getSonarPath(String filename) throws IOException {
        return new File(
                "../sevntu-checkstyle-sonar-plugin/src/main/resources/com/github/sevntu/checkstyle/sonar/"
                        + filename).getCanonicalPath();
    }
}
