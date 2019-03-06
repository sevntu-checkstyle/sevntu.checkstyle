////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2019 the original author or authors.
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

package com.github.sevntu.checkstyle.checks.jsr305;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.DefaultLogger;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

public final class Jsr305AnnotionsTestUtil {

    private static final String MAVEN_TEST_CLASSES_PATH = "/target/test-classes";

    /**
     * Private utility class constructor.
     */
    private Jsr305AnnotionsTestUtil() {
        // empty
    }

    public static void check(final ExpectedWarning... warnings) throws CheckstyleException {
        final Checker checker = createChecker();

        checker.addListener(new DefaultLogger(System.out, false));

        final Map<String, List<ExpectedWarning>> fileNameWarningMap = new HashMap<>();
        final List<File> files = new ArrayList<>();

        final Map<Class<?>, List<ExpectedWarning>> mappedWarnings = new HashMap<>();
        for (final ExpectedWarning warning : warnings) {
            final List<ExpectedWarning> existing = mappedWarnings.get(warning.clz);
            final List<ExpectedWarning> warningsInMap;
            if (existing != null) {
                warningsInMap = existing;
            }
            else {
                final File file = getFileFromClass(warning.clz);
                files.add(file);
                warningsInMap = new ArrayList<>();
                mappedWarnings.put(warning.clz, warningsInMap);
                fileNameWarningMap.put(file.getPath(), warningsInMap);
            }
            if (!warning.isNoWarning()) {
                warningsInMap.add(warning);
            }
        }

        checker.addListener(new AuditListenerImplementation(fileNameWarningMap));
        checker.process(files);
    }

    private static File getFileFromClass(final Class<?> clz) {
        try {
            final String clzFilename = clz.getSimpleName() + ".class";
            final URL url = clz.getResource(clzFilename);
            final String filename = url.toString();

            final String path = filename.replace(resolveName(clz, ".class"), "");
            final int lastSlash = path.lastIndexOf("/", path.length() - 1);
            final String lsPath = path.substring(0, lastSlash);
            final String buildPath;
            if (lsPath.endsWith(MAVEN_TEST_CLASSES_PATH)) {
                buildPath = lsPath.substring(0, lsPath.length() - MAVEN_TEST_CLASSES_PATH.length());
            }
            else {
                buildPath = lsPath;
            }

            final String newPath = buildPath + "/src/test/java/" + resolveName(clz, ".java");
            return new File(new URI(newPath));
        }
        catch (final URISyntaxException exc) {
            throw new RuntimeException(exc);
        }
    }

    private static String resolveName(final Class<?> clz, final String extension) {
        Class<?> clazz = clz;
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }
        final String baseName = clazz.getName();
        final int index = baseName.lastIndexOf('.');

        final String name = clazz.getSimpleName() + extension;
        final String result;
        if (index != -1) {
            result = baseName.substring(0, index).replace('.', '/') + "/" + name;
        }
        else {
            result = name;
        }
        return result;
    }

    private static Checker createChecker() throws CheckstyleException {
        final Checker checker = new Checker();
        checker.setModuleClassLoader(Checker.class.getClassLoader());
        checker.configure(createCheckerConfiguration());
        return checker;
    }

    private static DefaultConfiguration createCheckerConfiguration() {
        final DefaultConfiguration checkerConfiguration = new DefaultConfiguration("Checker");
        checkerConfiguration.addChild(createTreeWalkerConfiguration());
        return checkerConfiguration;
    }

    private static DefaultConfiguration createTreeWalkerConfiguration() {
        final DefaultConfiguration treeWalkerConfiguration = new DefaultConfiguration("TreeWalker");
        treeWalkerConfiguration.addChild(createJsr305AnnotationsConfiguration());
        return treeWalkerConfiguration;
    }

    private static DefaultConfiguration createJsr305AnnotationsConfiguration() {
        final DefaultConfiguration jsr305AnnotaionsConfiguration =
                new DefaultConfiguration("Jsr305AnnotationsCheck");
        jsr305AnnotaionsConfiguration.addAttribute("packages",
                "com.github.sevntu.checkstyle.checks.jsr305.test");
        return jsr305AnnotaionsConfiguration;
    }

    public static final class ExpectedWarning {

        private final int column;
        private final int line;
        private final Class<?> clz;

        ExpectedWarning(final Class<?> clz, final int line, final int column) {
            this.clz = clz;
            this.line = line;
            this.column = column;
        }

        ExpectedWarning(final Class<?> clz) {
            this(clz, -1, -1);
        }

        public boolean isNoWarning() {
            return column < 0 || line < 0;
        }

        @Override
        public String toString() {
            return clz.getSimpleName() + " (" + line + ":" + column + ")";
        }
    }

    private static final class AuditListenerImplementation implements AuditListener {

        private final Map<String, List<ExpectedWarning>> fileNameWarningMap;
        private Iterator<ExpectedWarning> warningIterator;

        AuditListenerImplementation(final Map<String, List<ExpectedWarning>> fileNameWarningMap) {
            this.fileNameWarningMap = fileNameWarningMap;
        }

        @Override
        public void fileStarted(final AuditEvent event) {
            final List<ExpectedWarning> list = fileNameWarningMap.remove(event.getFileName());
            warningIterator = list.iterator();
        }

        @Override
        public void fileFinished(final AuditEvent event) {
            // can't use Assert.assertFalse because next can not be evaluated
            if (warningIterator.hasNext()) {
                Assert.fail("There is a warnings left: " + warningIterator.next());
            }
        }

        @Override
        public void auditStarted(final AuditEvent event) {
        }

        @Override
        public void auditFinished(final AuditEvent event) {
            Assert.assertTrue("There are Files left: " + fileNameWarningMap.keySet(),
                    fileNameWarningMap.isEmpty());
        }

        @Override
        public void addException(final AuditEvent event, final Throwable throwable) {
            Assert.fail("Exception during check of file " + event.getFileName() + ": "
                    + throwable.getMessage());
        }

        @Override
        public void addError(final AuditEvent event) {
            Assert.assertTrue("There should be an expeceted warning for file \""
                    + event.getFileName() + "\", but none found", warningIterator.hasNext());
            final ExpectedWarning warning = warningIterator.next();
            final String error = "Position (" + event.getLine() + ":" + event.getColumn()
                    + ") didn't match: " + warning;
            Assert.assertEquals(error, warning.line, event.getLine());
            Assert.assertEquals(error, warning.column, event.getColumn());
        }

    }

}
