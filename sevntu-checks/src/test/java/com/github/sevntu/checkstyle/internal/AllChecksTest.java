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
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;

public class AllChecksTest {

    @Test
    public void testDefaultTokensAreSubsetOfAcceptableTokens() throws Exception {
        for (Class<?> check : CheckUtil.getCheckstyleChecks()) {
            if (AbstractCheck.class.isAssignableFrom(check)) {
                final AbstractCheck testedCheck = (AbstractCheck) check.getDeclaredConstructor()
                        .newInstance();
                final int[] defaultTokens = testedCheck.getDefaultTokens();
                final int[] acceptableTokens = testedCheck.getAcceptableTokens();

                if (!isSubset(defaultTokens, acceptableTokens)) {
                    final String errorMessage = String.format(Locale.ROOT,
                            "%s's default tokens must be a subset"
                            + " of acceptable tokens.", check.getName());
                    Assertions.fail(errorMessage);
                }
            }
        }
    }

    @Test
    public void testRequiredTokensAreSubsetOfAcceptableTokens() throws Exception {
        for (Class<?> check : CheckUtil.getCheckstyleChecks()) {
            if (AbstractCheck.class.isAssignableFrom(check)) {
                final AbstractCheck testedCheck = (AbstractCheck) check.getDeclaredConstructor()
                        .newInstance();
                final int[] requiredTokens = testedCheck.getRequiredTokens();
                final int[] acceptableTokens = testedCheck.getAcceptableTokens();

                if (!isSubset(requiredTokens, acceptableTokens)) {
                    final String errorMessage = String.format(Locale.ROOT,
                            "%s's required tokens must be a subset"
                            + " of acceptable tokens.", check.getName());
                    Assertions.fail(errorMessage);
                }
            }
        }
    }

    @Test
    public void testRequiredTokensAreSubsetOfDefaultTokens() throws Exception {
        for (Class<?> check : CheckUtil.getCheckstyleChecks()) {
            if (AbstractCheck.class.isAssignableFrom(check)) {
                final AbstractCheck testedCheck = (AbstractCheck) check.getDeclaredConstructor()
                        .newInstance();
                final int[] defaultTokens = testedCheck.getDefaultTokens();
                final int[] requiredTokens = testedCheck.getRequiredTokens();

                if (!isSubset(requiredTokens, defaultTokens)) {
                    final String errorMessage = String.format(Locale.ROOT,
                            "%s's required tokens must be a subset"
                            + " of default tokens.", check.getName());
                    Assertions.fail(errorMessage);
                }
            }
        }
    }

    @Test
    public void testAllChecksAreReferencedInConfigFile() throws Exception {
        final Set<String> checksReferencedInConfig = CheckUtil.getConfigCheckStyleChecks();
        final Set<String> checksNames = getFullNames(CheckUtil.getCheckstyleChecks());

        checksNames.stream().filter(check -> !checksReferencedInConfig.contains(check))
            .forEach(check -> {
                final String errorMessage = String.format(Locale.ROOT,
                    "%s is not referenced in sevntu-checks.xml", check);
                Assertions.fail(errorMessage);
            });
    }

    @Test
    public void testAllCheckstyleModulesHaveTest() throws Exception {
        for (Class<?> module : CheckUtil.getCheckstyleChecks()) {
            final String path = "src/test/java/"
                    + module.getName().replace('.', File.separatorChar) + "Test.java";
            final File file = new File(path);

            Assertions.assertTrue(file.exists(), "Test must exist for " + module.getName()
                    + " and be located at " + path);
        }
    }

    @Test
    public void testAllCheckstyleModulesHaveMessage() throws Exception {
        for (Class<?> module : CheckUtil.getCheckstyleChecks()) {
            Assertions.assertFalse(CheckUtil
                    .getCheckMessages(module).isEmpty(), module.getSimpleName()
                            + " should have atleast one 'MSG_*' field for error messages");
        }
    }

    @Test
    public void testAllCheckstyleMessages() throws Exception {
        final Map<String, List<String>> usedMessages = new TreeMap<>();

        // test validity of messages from checks
        for (Class<?> module : CheckUtil.getCheckstyleModules()) {
            for (Field message : CheckUtil.getCheckMessages(module)) {
                Assertions.assertEquals(Modifier.PUBLIC | Modifier.STATIC
                        | Modifier.FINAL, message.getModifiers(), module.getSimpleName() + "."
                                + message.getName() + " should be 'public static final'");

                // below is required for package/private classes
                if (!message.isAccessible()) {
                    message.setAccessible(true);
                }

                verifyCheckstyleMessage(usedMessages, module, message);
            }
        }

        // test properties for messages not used by checks
        for (Entry<String, List<String>> entry : usedMessages.entrySet()) {
            final Properties pr = new Properties();
            pr.load(AllChecksTest.class.getResourceAsStream(
                    "/" + entry.getKey().replace('.', '/') + "/messages.properties"));

            for (Object key : pr.keySet()) {
                Assertions.assertTrue(entry.getValue().contains(key.toString()), "property '" + key
                        + "' isn't used by any check in package '" + entry.getKey() + "'");
            }
        }
    }

    @Test
    public void testAllInputsHaveTest() throws Exception {
        final Map<String, List<String>> allTests = new HashMap<>();

        Files.walk(Paths.get("src/test/java/com/github/sevntu/checkstyle"))
            .forEach(filePath -> {
                grabAllTests(allTests, filePath.toFile());
            });
        Files.walk(Paths.get("src/test/resources/com/github/sevntu/checkstyle"))
            .forEach(filePath -> {
                verifyInputFile(allTests, filePath.toFile());
            });
        Files.walk(Paths.get("src/test/resources-noncompilable/com/github/sevntu/checkstyle"))
            .forEach(filePath -> {
                verifyInputFile(allTests, filePath.toFile());
            });
    }

    private static void verifyCheckstyleMessage(Map<String, List<String>> usedMessages,
            Class<?> module, Field message) throws Exception {
        final String messageString = message.get(null).toString();
        final String packageName = module.getPackage().getName();
        List<String> packageMessages = usedMessages.get(packageName);

        if (packageMessages == null) {
            packageMessages = new ArrayList<>();
            usedMessages.put(packageName, packageMessages);
        }

        packageMessages.add(messageString);

        String result = null;

        try {
            result = CheckUtil.getCheckMessage(module, messageString);
        }
        catch (IllegalArgumentException exc) {
            Assertions.fail(module.getSimpleName() + " with the message '" + messageString
                    + "' failed with: "
                    + exc.getClass().getSimpleName() + " - " + exc.getMessage());
        }

        Assertions.assertNotNull(
                result, module.getSimpleName() + " should have text for the message '"
                        + messageString + "'");
        final String trimmedResult = result.trim();
        Assertions.assertFalse(
                trimmedResult.isEmpty(), module.getSimpleName()
                        + " should have non-empty text for the message '" + messageString + "'");
        Assertions.assertFalse(
                trimmedResult.startsWith("TODO"), module.getSimpleName()
                        + " should have non-TODO text for the message '" + messageString + "'");
    }

    private static void grabAllTests(Map<String, List<String>> allTests, File file) {
        if (file.isFile() && file.getName().endsWith("Test.java")) {
            final String path;

            try {
                path = getSimplePath(file.getCanonicalPath()).replace("Test.java", "");
            }
            catch (IOException exc) {
                throw new IllegalStateException(exc);
            }

            final int slash = path.lastIndexOf(File.separatorChar);
            final String pkg = path.substring(0, slash);

            List<String> classes = allTests.get(pkg);

            if (classes == null) {
                classes = new ArrayList<>();

                allTests.put(pkg, classes);
            }

            classes.add(path.substring(slash + 1));
        }
    }

    private static void verifyInputFile(Map<String, List<String>> allTests, File file) {
        if (file.isFile()) {
            final String path;

            try {
                path = getSimplePath(file.getCanonicalPath());
            }
            catch (IOException exc) {
                throw new IllegalStateException(exc);
            }

            String fileName = file.getName();

            Assertions.assertTrue(
                    fileName.startsWith("Input"), "Resource must start with 'Input': " + path);

            final int period = fileName.lastIndexOf('.');

            Assertions.assertTrue(period > 0, "Resource must have an extension: " + path);

            fileName = fileName.substring(5, period);

            final int slash = path.lastIndexOf(File.separatorChar);
            final String pkg = path.substring(0, slash);
            final List<String> classes = allTests.get(pkg);

            if (classes != null || !pkg.endsWith("external")) {
                Assertions.assertNotNull(
                        classes, "Resource must be in a package that has tests: " + path);

                boolean found = false;

                for (String clss : classes) {
                    if (fileName.startsWith(clss)) {
                        found = true;
                        break;
                    }
                }

                Assertions.assertTrue(found, "Resource must be named after a Test like "
                        + "'InputMyCheck.java' and be in the same package as the test: " + path);
            }
        }
    }

    /**
     * Removes 'Check' suffix from each class name in the set.
     *
     * @param checks class instances.
     * @return a set of simple names.
     */
    private static Set<String> getFullNames(Set<Class<?>> checks) {
        return checks.stream().map(Class::getName).collect(Collectors.toSet());
    }

    private static String getSimplePath(String path) {
        return path.substring(path.lastIndexOf("com" + File.separator + "github"));
    }

    /**
     * Checks that an array is a subset of other array.
     *
     * @param array to check whether it is a subset.
     * @param arrayToCheckIn array to check in.
     */
    private static boolean isSubset(int[] array, int... arrayToCheckIn) {
        boolean result = true;
        if (arrayToCheckIn == null) {
            result = array == null || array.length == 0;
        }
        else {
            Arrays.sort(arrayToCheckIn);
            for (final int element : array) {
                if (Arrays.binarySearch(arrayToCheckIn, element) < 0) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

}
