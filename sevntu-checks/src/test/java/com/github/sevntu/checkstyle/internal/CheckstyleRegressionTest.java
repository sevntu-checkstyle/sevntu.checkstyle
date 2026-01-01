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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class CheckstyleRegressionTest {

    /** List of checks to suppress if we dynamically add it to the configuration. */
    private static final List<String> ADD_CHECK_SUPPRESSIONS = Arrays
            .asList("ReturnCountExtendedCheck");

    // -@cs[CyclomaticComplexity] Can't split
    @Test
    public void setupFiles() throws Exception {
        final String regressionPath = System.getProperty("regression-path");

        if (regressionPath != null) {
            final File path = new File(regressionPath);

            if (!path.exists()) {
                throw new IllegalStateException("Invalid path: " + path.getAbsolutePath());
            }

            System.out.println("Working Path: " + path.getCanonicalPath());

            final File project = new File(path, "checkstyle");

            if (!project.exists() || !project.isDirectory()) {
                throw new IllegalStateException("Can't find project: " + project.getAbsolutePath());
            }

            final File config = new File(project, "config/checkstyle-sevntu-checks.xml");

            if (!config.exists() || !config.isFile() || !config.canRead() || !config.canWrite()) {
                throw new IllegalStateException("Can't read config: " + config.getAbsolutePath());
            }

            System.out.println("Config Path: " + config.getCanonicalPath());

            final File suppression = new File(project, "config/sevntu-suppressions.xml");

            if (!suppression.exists() || !suppression.isFile() || !suppression.canRead()
                    || !suppression.canWrite()) {
                throw new IllegalStateException("Can't read suppression: "
                        + suppression.getAbsolutePath());
            }

            System.out.println("Suppression Path: " + suppression.getCanonicalPath());

            work(config, suppression);

            System.out.println("Done");
        }
    }

    private static void work(File config, File suppression) throws Exception {
        final Set<String> configChecks = CheckUtil.getCheckStyleChecksReferencedInConfig(config
                .getAbsolutePath());
        final List<Class<?>> sevntuChecks = new ArrayList<>(CheckUtil.getCheckstyleModules());

        trimSevntuChecksNotReferenced(configChecks, sevntuChecks);

        if (sevntuChecks.isEmpty()) {
            System.out.println("All sevntu checks listed in config");
        }
        else {
            System.out.println("Adding " + sevntuChecks.size() + " missing check(s)");

            String configAdditions = "";
            String suppressionAdditions = "";

            for (Class<?> sevntuCheck : sevntuChecks) {
                final String name = sevntuCheck.getName();
                final String simpleName = sevntuCheck.getSimpleName();

                System.out.println("-- Adding Check: " + name);

                configAdditions += "<module name=\r\n\"" + name + "\"\r\n></module>\r\n";

                if (ADD_CHECK_SUPPRESSIONS.contains(simpleName)) {
                    System.out.println("-- Adding Suppression: " + simpleName);

                    suppressionAdditions += "<suppress checks=\"" + simpleName
                            + "\" files=\".*\"/>\r\n";
                }
            }

            String configContents = new String(Files.readAllBytes(config.toPath()), UTF_8);

            int treeWalkerPosition = configContents.lastIndexOf("<module name=\"TreeWalker\">");
            treeWalkerPosition = configContents.indexOf('\n', treeWalkerPosition) + 1;

            configContents = configContents.substring(0, treeWalkerPosition) + configAdditions
                    + configContents.substring(treeWalkerPosition);

            Files.write(config.toPath(), configContents.getBytes(UTF_8), StandardOpenOption.CREATE);

            if (!suppressionAdditions.isEmpty()) {
                String suppressionContents = new String(Files.readAllBytes(suppression.toPath()),
                        UTF_8);
                final int position = suppressionContents.lastIndexOf("</suppressions");

                suppressionContents = suppressionContents.substring(0, position)
                        + suppressionAdditions + suppressionContents.substring(position);

                Files.write(suppression.toPath(), suppressionContents.getBytes(UTF_8),
                        StandardOpenOption.CREATE);
            }
        }
    }

    private static void trimSevntuChecksNotReferenced(Set<String> configChecks,
            List<Class<?>> sevntuChecks) {
        for (String configCheck : configChecks) {
            final int position = findCheck(sevntuChecks, configCheck);

            if (position == -1) {
                System.err.println("Found module not in sevntu: " + configCheck);
            }
            else {
                sevntuChecks.remove(position);
            }
        }
    }

    // -@cs[ReturnCount] Not simple to reduce returns
    private static int findCheck(Collection<Class<?>> checks, String findCheck) {
        int position = 0;

        for (Class<?> check : checks) {
            final String simpleName = check.getSimpleName();

            if (findCheck.endsWith("Check")) {
                if (simpleName.equals(findCheck) || check.getName().equals(findCheck)) {
                    return position;
                }
            }
            else {
                if (simpleName.replaceAll("Check$", "").equals(findCheck)
                        || check.getName().replaceAll("Check$", "").equals(findCheck)) {
                    return position;
                }
            }

            position++;
        }

        return -1;
    }

}
