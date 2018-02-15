////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2017 the original author or authors.
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

package com.github.sevntu.checkstyle.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.DetailAstRootHolder;
import com.google.common.collect.Lists;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;
import com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationLocationCheck;
import com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocMethodCheck;
import com.puppycrawl.tools.checkstyle.checks.naming.ConstantNameCheck;
import com.puppycrawl.tools.checkstyle.checks.naming.MemberNameCheck;
import com.puppycrawl.tools.checkstyle.checks.naming.MethodNameCheck;
import com.puppycrawl.tools.checkstyle.checks.naming.ParameterNameCheck;

public class SuppressionAnnotationFilterTest extends BaseCheckTestSupport {

    @Test
    public void testNone()
            throws Exception {
        final DefaultConfiguration filterConfig = null;
        final String[] suppressed = {
        };
        verifySuppressed(filterConfig, suppressed);
    }

    @Test
    public void testDefault()
            throws Exception {
        final DefaultConfiguration filterConfig =
                createFilterConfig(SuppressionAnnotationFilter.class);
        final String[] suppressed = {
        };
        verifySuppressed(filterConfig, suppressed);
    }

    //com.github.sevntu.checkstyle.filters
    @Test
    public void testNamesParsing1() throws Exception {
        final DefaultConfiguration filterConfig =
                createFilterConfig(SuppressionAnnotationFilter.class);
        filterConfig.addAttribute("annotationNames", "@Test1");
        filterConfig.addAttribute("annotationNames", "@com.github.sevntu.checkstyle.filters.Test2");
        filterConfig.addAttribute("annotationNames", "Test3");
        filterConfig.addAttribute("annotationNames", "com.github.sevntu.checkstyle.filters.Test4");
        filterConfig.addAttribute("annotationNames", "com.github.sevntu.checkstyle.checks.test.external.InputSuppressionAnnotation");
        final String[] resulted = {
            "14:11: " + getCheckMessage("javadoc.missing"),
            "21:5: " + getCheckMessage("javadoc.missing"),
            "22:5: " + getCheckMessage("javadoc.missing"),
            "64:5: " + getCheckMessage("javadoc.missing"),
            "64:10: " + invalidPattern("FOO1"),
            "70:32: " + invalidPattern("A7"),
            "125:9: " + invalidPattern("B10"),
            "131:22: " + invalidPattern("FOO2"),
            "146: " + getCheckMessage("javadoc.return.expected"),
            "146:9: " + invalidPattern("FOO2"),
            "162:10: " + getCheckMessage("javadoc.unusedTag", "@param", "it"),
            "171:21: " + invalidPattern("C5"),
        };
        verify(createChecker(filterConfig),
                getPath("InputSuppressionAnnotationFilter.java"), resulted);
    }

    @Test
    public void testNamesParsing2()
            throws Exception {
        final DefaultConfiguration filterConfig =
                createFilterConfig(SuppressionAnnotationFilter.class);
        filterConfig.addAttribute("annotationNames", "@Test1");
        filterConfig.addAttribute("annotationNames", "@com.github.sevntu.checkstyle.filters.Test2");
        filterConfig.addAttribute("annotationNames", "Test3");
        filterConfig.addAttribute("annotationNames", "com.github.sevntu.checkstyle.filters.Test4");
        filterConfig.addAttribute("annotationNames", "com.github.sevntu.checkstyle.filters.InputSuppressionAnnotation");
        final String[] resulted = {
            "14:11: " + getCheckMessage("javadoc.missing"),
            "21:5: " + getCheckMessage("javadoc.missing"),
            "22:5: " + getCheckMessage("javadoc.missing"),
            "64:5: " + getCheckMessage("javadoc.missing"),
            "64:10: " + invalidPattern("FOO1"),
            "70:32: " + invalidPattern("A7"),
            "125:9: " + invalidPattern("B10"),
            "131:22: " + invalidPattern("FOO2"),
            "146: " + getCheckMessage("javadoc.return.expected"),
            "146:9: " + invalidPattern("FOO2"),
            "162:10: " + getCheckMessage("javadoc.unusedTag", "@param", "it"),
            "171:21: " + invalidPattern("C5"),
        };
        verify(createChecker(filterConfig),
                getPath("InputSuppressionAnnotationFilter.java"), resulted);
    }

    @Test
    public void testRegEx1()
            throws Exception {
        final DefaultConfiguration filterConfig =
                createFilterConfig(SuppressionAnnotationFilter.class);
        filterConfig.addAttribute("annotationNames", "@Test1");
        filterConfig.addAttribute("annotationNames", "@com.github.sevntu.checkstyle.filters.Test2");
        filterConfig.addAttribute("annotationNames", "Test3");
        filterConfig.addAttribute("annotationNames", "com.github.sevntu.checkstyle.filters.Test4");
        filterConfig.addAttribute("annotationNames", "com.github.sevntu.checkstyle.filters.InputSuppressionAnnotation");
        filterConfig.addAttribute("checkNames", ".*");
        final String[] suppressed = {
        };
        verifySuppressed(filterConfig, suppressed);
    }

    @Test
    public void testRegEx2()
            throws Exception {
        final DefaultConfiguration filterConfig =
                createFilterConfig(SuppressionAnnotationFilter.class);
        filterConfig.addAttribute("annotationNames", "@Test1");
        filterConfig.addAttribute("annotationNames", "@com.github.sevntu.checkstyle.filters.Test2");
        filterConfig.addAttribute("annotationNames", "Test3");
        filterConfig.addAttribute("annotationNames", "com.github.sevntu.checkstyle.filters.Test4");
        filterConfig.addAttribute("annotationNames", "InputSuppressionAnnotation");
        filterConfig.addAttribute("checkNames", ".*Name.*");
        filterConfig.addAttribute("checkNames", ".*Javadoc.*");
        final String[] suppressed = {
            "135: " + getCheckMessage("annotation.location.alone", "Deprecated"),
        };
        verifySuppressed(filterConfig, suppressed);
    }

    @Test
    public void testThrowWrongRegexp()
            throws Exception {
        final DefaultConfiguration filterConfig =
                createFilterConfig(SuppressionAnnotationFilter.class);
        filterConfig.addAttribute("checkNames", "*\\.*");
        try {
            createChecker(filterConfig);
            fail();
        }
        catch (CheckstyleException ex) {
            Assert.assertTrue(ex.getMessage().contains("Cannot set property 'checkNames' to '*\\.*'"));
        }
    }

    @Test
    public void testMisc()
            throws Exception {
        final SuppressionAnnotationFilter test = new SuppressionAnnotationFilter();
        final String[] dummyArray = {
        };
        final AuditEvent noMessageEvent = new AuditEvent(test, "abc", null);
        Assert.assertTrue(test.accept(noMessageEvent));
        final LocalizedMessage dummyMessage =
            new LocalizedMessage(0, 0, null, null, dummyArray, null, null, null, null);
        final AuditEvent noTreeEvent = new AuditEvent(test, "abc", dummyMessage);
        // Here abortive branch of accept method with no AST present is tested.
        Assert.assertTrue(test.accept(noTreeEvent));
    }

    public static DefaultConfiguration createFilterConfig(Class<?> aClass) {
        return new DefaultConfiguration(aClass.getName());
    }

    protected void verifySuppressed(Configuration aFilterConfig,
            String[] aSuppressed)
                    throws Exception {
        final String[] sAllMessages = {
            "14:11: " + getCheckMessage("javadoc.missing"),
            "21:5: " + getCheckMessage("javadoc.missing"),
            "22:5: " + getCheckMessage("javadoc.missing"),
            "35:3: " + getCheckMessage("javadoc.missing"),
            "40: " + getCheckMessage("javadoc.return.expected"),
            "41:9: " + invalidPattern("FOO"),
            "52:9: " + invalidPattern("A1"),
            "55:11: " + invalidPattern("A2"),
            "56:7: " + getCheckMessage("javadoc.missing"),
            "56:12: " + invalidPattern("TESTCLASS"),
            "64:5: " + getCheckMessage("javadoc.missing"),
            "64:10: " + invalidPattern("FOO1"),
            "64:48: " + invalidPattern("A3"),
            "67:9: " + invalidPattern("A4"),
            "70:9: " + invalidPattern("A5"),
            "70:24: " + invalidPattern("A6"),
            "70:32: " + invalidPattern("A7"),
            "74:43: " + invalidPattern("A8"),
            "83:9: " + invalidPattern("A9"),
            "90:9: " + invalidPattern("B1"),
            "93:9: " + invalidPattern("B2"),
            "96:9: " + invalidPattern("B3"),
            "99:9: " + invalidPattern("B4"),
            "104:9: " + invalidPattern("B5"),
            "108:9: " + invalidPattern("B6"),
            "112:9: " + invalidPattern("B7"),
            "117:9: " + invalidPattern("B8"),
            "121:9: " + invalidPattern("B9"),
            "125:9: " + invalidPattern("B10"),
            "131:22: " + invalidPattern("FOO2"),
            "135: " + getCheckMessage("annotation.location.alone", "Deprecated"),
            "135:5: " + getCheckMessage("javadoc.missing"),
            "139:10: " + invalidPattern("FOO3"),
            "146: " + getCheckMessage("javadoc.return.expected"),
            "146:9: " + invalidPattern("FOO2"),
            "146:25: " + getCheckMessage("javadoc.expectedTag", "@param", "C1"),
            "146:25: " + invalidPattern("C1"),
            "153:9: " + invalidPattern("C2"),
            "153:13: " + getCheckMessage("javadoc.missing"),
            "153:24: " + invalidPattern("FOO3"),
            "153:34: " + invalidPattern("C3"),
            "162:10: " + getCheckMessage("javadoc.unusedTag", "@param", "it"),
            "169:7: " + getCheckMessage("javadoc.missing"),
            "171:21: " + invalidPattern("C5"),
        };

        verify(createChecker(aFilterConfig),
                getPath("InputSuppressionAnnotationFilter.java"),
                removeSuppressed(sAllMessages, aSuppressed));
    }

    private String invalidPattern(String fieldName) {
        return getCheckMessage("name.invalidPattern", fieldName, "^[a-z][a-zA-Z0-9]*$");
    }

    @Override
    protected DefaultConfiguration createCheckerConfig(Configuration aFilterConfig) {
        final DefaultConfiguration checkerConfig = new DefaultConfiguration("configuration");
        final DefaultConfiguration checksConfig = createCheckConfig(TreeWalker.class);
        checksConfig.addChild(createCheckConfig(DetailAstRootHolder.class));
        checksConfig.addChild(createCheckConfig(MemberNameCheck.class));
        checksConfig.addChild(createCheckConfig(MethodNameCheck.class));
        checksConfig.addChild(createCheckConfig(ParameterNameCheck.class));
        checksConfig.addChild(createCheckConfig(ConstantNameCheck.class));
        checksConfig.addChild(createCheckConfig(JavadocMethodCheck.class));
        checksConfig.addChild(createCheckConfig(AnnotationLocationCheck.class));
        checkerConfig.addChild(checksConfig);
        if (aFilterConfig != null) {
            checkerConfig.addChild(aFilterConfig);
        }
        return checkerConfig;
    }

    private String[] removeSuppressed(String[] from, String[] remove) {
        final Collection<String> coll =
                Lists.newArrayList(Arrays.asList(from));
        coll.removeAll(Arrays.asList(remove));
        return coll.toArray(new String[coll.size()]);
    }

    /*
     * This test scans for error messages defined in many other modules
     * Reason: the filter does not introduce own messages but impacts other check
     */
    @Override
    public String getCheckMessage(String messageKey) {
        final Properties pr = new Properties();
        try {
            pr.load(SuppressionAnnotationFilterTest.class
                .getResourceAsStream("/com/puppycrawl/tools/checkstyle/checks/annotation/messages.properties"));
            pr.load(SuppressionAnnotationFilterTest.class
                .getResourceAsStream("/com/puppycrawl/tools/checkstyle/checks/javadoc/messages.properties"));
            pr.load(SuppressionAnnotationFilterTest.class
                .getResourceAsStream("/com/puppycrawl/tools/checkstyle/checks/naming/messages.properties"));
        }
        catch (IOException ex) {
            throw new RuntimeException("cannot find message file", ex);
        }
        return pr.getProperty(messageKey);
    }

}
