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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.puppycrawl.tools.checkstyle.PackageNamesLoader;
import com.puppycrawl.tools.checkstyle.utils.ModuleReflectionUtil;

public final class CheckUtil {

    private CheckUtil() {
    }

    public static Set<String> getConfigCheckStyleChecks() {
        return getCheckStyleChecksReferencedInConfig("config/sevntu-checks.xml");
    }

    /**
     * Gets a set of names of checkstyle's checks which are referenced in checkstyle-checks.xml.
     *
     * @param configFilePath
     *            file path of checkstyle-checks.xml.
     * @return names of checkstyle's checks which are referenced in checkstyle-checks.xml.
     */
    public static Set<String> getCheckStyleChecksReferencedInConfig(String configFilePath) {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // Validations of XML file make parsing too slow, that is why we
            // disable all validations.
            factory.setNamespaceAware(false);
            factory.setValidating(false);
            factory.setFeature("http://xml.org/sax/features/namespaces", false);
            factory.setFeature("http://xml.org/sax/features/validation", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
                    false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",
                    false);

            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document document = builder.parse(new File(configFilePath));

            // optional, but recommended
            // FYI:
            // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-
            // how-does-it-work
            document.getDocumentElement().normalize();

            final NodeList nodeList = document.getElementsByTagName("module");

            final Set<String> checksReferencedInCheckstyleChecksXml = new HashSet<>();
            for (int index = 0; index < nodeList.getLength(); index++) {
                final Node currentNode = nodeList.item(index);
                if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                    final Element module = (Element) currentNode;
                    final String checkName = module.getAttribute("name");
                    if (!"Checker".equals(checkName) && !"TreeWalker".equals(checkName)) {
                        checksReferencedInCheckstyleChecksXml.add(checkName);
                    }
                }
            }
            return checksReferencedInCheckstyleChecksXml;
        }
        catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    /**
     * Gets the checkstyle's non abstract checks.
     *
     * @return the set of checkstyle's non abstract check classes.
     * @throws Exception if the attempt to read class path resources failed.
     */
    public static Set<Class<?>> getCheckstyleChecks() throws Exception {
        final Set<Class<?>> checkstyleChecks = new HashSet<>();

        final ClassLoader loader = Thread.currentThread()
                .getContextClassLoader();
        final Set<Class<?>> checkstyleModules = ModuleReflectionUtil.getCheckstyleModules(
                PackageNamesLoader.getPackageNames(loader), loader);

        for (Class<?> clazz : checkstyleModules) {
            if (ModuleReflectionUtil.isCheckstyleTreeWalkerCheck(clazz)) {
                checkstyleChecks.add(clazz);
            }
        }
        return checkstyleChecks;
    }

    public static Set<String> getPackages(Set<Class<?>> modules) {
        final Set<String> result = new HashSet<>();

        for (Class<?> module : modules) {
            result.add(module.getPackage().getName()
                    .replace("com.github.sevntu.checkstyle.checks.", ""));
        }

        return result;
    }

    public static Set<Class<?>> getModulesInPackage(Set<Class<?>> modules, String pkg) {
        final Set<Class<?>> result = new HashSet<>();

        for (Class<?> module : modules) {
            if (module.getPackage().getName().endsWith(pkg)) {
                result.add(module);
            }
        }

        return result;
    }

    /**
     * Gets the checkstyle's modules. Checkstyle's modules are nonabstract
     * classes from com.puppycrawl.tools.checkstyle package which names end with
     * 'Check', do not contain the word 'Input' (are not input files for UTs),
     * checkstyle's filters and SuppressWarningsHolder class.
     *
     * @return a set of checkstyle's modules names.
     * @throws Exception if the attempt to read class path resources failed.
     */
    public static Set<Class<?>> getCheckstyleModules() throws Exception {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return ModuleReflectionUtil.getCheckstyleModules(
                PackageNamesLoader.getPackageNames(loader), loader);
    }

    /**
     * Get's the check's messages.
     *
     * @param module class to examine.
     * @return a set of checkstyle's module message fields.
     * @throws ClassNotFoundException if the attempt to read a protected class fails.
     */
    public static Set<Field> getCheckMessages(Class<?> module) throws ClassNotFoundException {
        final Set<Field> checkstyleMessages = new HashSet<>();

        // get all fields from current class
        final Field[] fields = module.getDeclaredFields();

        for (Field field : fields) {
            if (field.getName().startsWith("MSG_")) {
                checkstyleMessages.add(field);
            }
        }

        // deep scan class through hierarchy
        final Class<?> superModule = module.getSuperclass();

        if (superModule != null) {
            checkstyleMessages.addAll(getCheckMessages(superModule));
        }

        return checkstyleMessages;
    }

    /**
     * Gets the check message 'as is' from appropriate 'messages.properties'
     * file.
     *
     * @param module the module to load resources from.
     * @param messageKey the key of message in 'messages*.properties' file.
     * @param arguments the arguments of message in 'messages*.properties' file.
     * @return the check's formatted message.
     */
    public static String getCheckMessage(Class<?> module, String messageKey,
            Object... arguments) {
        String result;
        final Properties pr = new Properties();
        try {
            pr.load(module.getResourceAsStream("messages.properties"));
            final MessageFormat formatter = new MessageFormat(pr.getProperty(messageKey));
            result = formatter.format(arguments);
        }
        catch (IOException exc) {
            result = null;
        }
        return result;
    }

}
