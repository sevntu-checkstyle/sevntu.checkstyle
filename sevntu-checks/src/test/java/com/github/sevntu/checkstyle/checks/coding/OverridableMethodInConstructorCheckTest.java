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

package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.OverridableMethodInConstructorCheck.MSG_KEY;
import static com.github.sevntu.checkstyle.checks.coding.OverridableMethodInConstructorCheck.MSG_KEY_LEADS;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class OverridableMethodInConstructorCheckTest extends AbstractModuleTestSupport {

    private static final String CTOR_KEY = "constructor";
    private static final String CLONE_KEY = "'clone()' method";
    private static final String READ_OBJECT_KEY = "'readObject()' method";

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public final void revereCodeTest() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "false");
        checkConfig.addProperty("checkReadObjectMethod", "false");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {
            "11:17: " + getCheckMessage(MSG_KEY_LEADS, "init", CTOR_KEY, "getPart"),
        };

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck26.java"), expected);
    }

    @Test
    public final void newFeature() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {
            "14:27: " + getCheckMessage(MSG_KEY, "overrideMe", CTOR_KEY),
            "15:21: " + getCheckMessage(MSG_KEY, "init", CTOR_KEY),
        };

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck27.java"), expected);
    }

    @Test
    public final void newFeatureWithoutMethodsByArgCount() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "false");

        final String[] expected = {
            "15:21: " + getCheckMessage(MSG_KEY, "init", CTOR_KEY),
        };

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck27.java"), expected);
    }

    @Test
    public final void testNoWarnings() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck1.java"), expected);
    }

    @Test
    public final void testWarning() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "false");

        final String[] expected = {
            "10:27: " + getCheckMessage(MSG_KEY, "overrideMe", CTOR_KEY),
        };

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck2.java"), expected);
    }

    @Test
    public final void test2WarningsIn2Ctors() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {
            "10:27: " + getCheckMessage(MSG_KEY, "overrideMe", CTOR_KEY),
            "11:38: " + getCheckMessage(MSG_KEY, "overrideMe", CTOR_KEY),
            "16:27: " + getCheckMessage(MSG_KEY, "overrideMe", CTOR_KEY),
        };

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck3.java"), expected);
    }

    @Test
    public final void testWarningInSecondDepth() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {
            "10:32: " + getCheckMessage(MSG_KEY_LEADS, "overrideMe", CTOR_KEY, "overrideMe2"),
        };

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck4.java"), expected);
    }

    @Test
    public final void testWarningsInThirdDepth() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {
            "10:32: " + getCheckMessage(MSG_KEY_LEADS, "overrideMe", CTOR_KEY, "overrideMe3"),
            "11:27: " + getCheckMessage(MSG_KEY_LEADS, "overrideMe", CTOR_KEY, "overrideMe3"),
        };

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck5.java"), expected);
    }

    @Test
    public final void testCloneNoWarningsSimple() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck6.java"), expected);
    }

    @Test
    public final void testCloneNoWarnings() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck7.java"), expected);
    }

    @Test
    public final void testCloneWarnings() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {
            "20:37: " + getCheckMessage(MSG_KEY, "doSmth", CLONE_KEY),
            "37:37: " + getCheckMessage(MSG_KEY, "doSmth", CLONE_KEY),
        };

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck8.java"), expected);
    }

    @Test
    public final void testCloneSecondDepth() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {
            "25:37: " + getCheckMessage(MSG_KEY_LEADS, "doSmth", CLONE_KEY, "doSmth2"),
            "26:20: " + getCheckMessage(MSG_KEY, "doSmth2", CLONE_KEY),
        };

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck9.java"), expected);
    }

    @Test
    public final void testCloneThirdDepthImplementation() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {
            "25:37: " + getCheckMessage(MSG_KEY, "doSmth", CLONE_KEY),
            "26:19: " + getCheckMessage(MSG_KEY, "accept", CLONE_KEY),
            "27:24: " + getCheckMessage(MSG_KEY, "accept", CLONE_KEY),
            "28:32: " + getCheckMessage(MSG_KEY, "accept", CLONE_KEY),
            "63:37: " + getCheckMessage(MSG_KEY, "doSmth", CLONE_KEY),
        };

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck13.java"), expected);
    }

    @Test
    public final void testSerializableNoWarnings() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck10.java"), expected);
    }

    @Test
    public final void testSerializableWarning() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {
            "32:20: " + getCheckMessage(MSG_KEY, "doSmth", READ_OBJECT_KEY),
        };

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck11.java"), expected);
    }

    @Test
    public final void testStaticModifiers() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {
        };

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck12.java"), expected);
    }

    @Test
    public final void testSerializableThirdDepthImplementation()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {
            "34:20: " + getCheckMessage(MSG_KEY, "doSmth", READ_OBJECT_KEY),
            "60:19: " + getCheckMessage(MSG_KEY, "doSmth", READ_OBJECT_KEY),
            "61:24: " + getCheckMessage(MSG_KEY, "doSmth", READ_OBJECT_KEY),
            "62:20: " + getCheckMessage(MSG_KEY_LEADS, "doSmth2", READ_OBJECT_KEY, "doSmth"),
            "63:25: " + getCheckMessage(MSG_KEY_LEADS, "doSmth2", READ_OBJECT_KEY, "doSmth"),
            "77:23: " + getCheckMessage(MSG_KEY, "doSmth", READ_OBJECT_KEY),
            "78:28: " + getCheckMessage(MSG_KEY, "doSmth", READ_OBJECT_KEY),
            "80:24: " + getCheckMessage(MSG_KEY_LEADS, "doSmth2", READ_OBJECT_KEY, "doSmth"),
            "81:29: " + getCheckMessage(MSG_KEY_LEADS, "doSmth2", READ_OBJECT_KEY, "doSmth"),
        };

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck14.java"), expected);
    }

    @Test
    public final void testCtorOverloadedMethods() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck15.java"), expected);
    }

    @Test
    public final void test2EqualMethodNamesWithNoWarning() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck16.java"), expected);
    }

    @Test
    public final void test2EqualMethodNamesWithoutWarning2() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {
            "17:32: " + getCheckMessage(MSG_KEY, "doSmth", READ_OBJECT_KEY),
            "18:45: " + getCheckMessage(MSG_KEY, "doSmth", READ_OBJECT_KEY),
        };

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck17.java"), expected);
    }

    @Test
    public final void testCallMethodIsNotInBuildPath2() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck18.java"), expected);
    }

    @Test
    public final void testCallMethodIsNotInBuildPath() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck19.java"), expected);
    }

    @Test
    public final void testReadObjectInInterface() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck20.java"), expected);
    }

    @Test
    public final void testStackOverFlowError() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck21.java"), expected);
    }

    @Test
    public final void testStackOverFlowErrorWithWarning() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {
            "5:15: " + getCheckMessage(MSG_KEY_LEADS, "doSMTH", CTOR_KEY, "doPublic"),
        };

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck22.java"), expected);
    }

    @Test
    public final void testStackOverFlowErrorWithoutWarning() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck23.java"), expected);
    }

    @Test
    public final void testAbstractMethodCall() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {
            "11:22: " + getCheckMessage(MSG_KEY, "buildGetter", CTOR_KEY),
        };

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck24.java"), expected);
    }

    @Test
    public final void testFinalClass() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck25.java"), expected);
    }

    @Test
    public final void testExtendsSimilarNamedClass() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(OverridableMethodInConstructorCheck.class);
        checkConfig.addProperty("checkCloneMethod", "true");
        checkConfig.addProperty("checkReadObjectMethod", "true");
        checkConfig.addProperty("matchMethodsByArgCount", "true");

        final String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructorCheck28.java"), expected);
    }

}
