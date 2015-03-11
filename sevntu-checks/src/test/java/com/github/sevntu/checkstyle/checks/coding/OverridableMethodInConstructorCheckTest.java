////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2015 the original author or authors.
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
package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.OverridableMethodInConstructorCheck.MSG_KEY;
import static com.github.sevntu.checkstyle.checks.coding.OverridableMethodInConstructorCheck.MSG_KEY_LEADS;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class OverridableMethodInConstructorCheckTest extends BaseCheckTestSupport
{

    private final DefaultConfiguration checkConfig = createCheckConfig(OverridableMethodInConstructorCheck.class);
    private static final String ctorKey = "constructor";
    private static final String cloneKey = "'clone()' method";
    private static final String readObjectKey = "'readObject()' method";

    @Test
    public final void revereCodeTest() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "false");
        checkConfig.addAttribute("checkReadObjectMethod", "false");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {
        	"11:17: " + getCheckMessage(MSG_KEY_LEADS, "init", ctorKey, "getPart"), 
        	};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor26.java"), expected);
    }
    
    @Test
    public final void newFeauture() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {
        	"14:27: " + getCheckMessage(MSG_KEY, "overrideMe", ctorKey),
       		"15:21: " + getCheckMessage(MSG_KEY, "init", ctorKey),
            };

        verify(checkConfig, getPath("InputOverridableMethodInConstructor27.java"), expected);
    }

    @Test
    public final void newFeautureWithoutMethodsByArgCount() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "false");

        String[] expected = {
                "15:21: " + getCheckMessage(MSG_KEY, "init", ctorKey),
            };

        verify(checkConfig, getPath("InputOverridableMethodInConstructor27.java"), expected);
    }

    @Test
    public final void testNoWarnings() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor1.java"), expected);
    }

    @Test
    public final void testWarning() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "false");

        String[] expected = {
        	"10:27: " + getCheckMessage(MSG_KEY, "overrideMe", ctorKey),
        	};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor2.java"), expected);
    }

    @Test
    public final void test2WarningsIn2Ctors() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {
        	"10:27: " + getCheckMessage(MSG_KEY, "overrideMe", ctorKey),
        	"15:27: " + getCheckMessage(MSG_KEY, "overrideMe", ctorKey),
            };

        verify(checkConfig, getPath("InputOverridableMethodInConstructor3.java"), expected);
    }

    @Test
    public final void testWarningInSecondDepth() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {
        		"10:32: " + getCheckMessage(MSG_KEY_LEADS, "overrideMe", ctorKey, "overrideMe2"),
                };

        verify(checkConfig, getPath("InputOverridableMethodInConstructor4.java"), expected);
    }

    @Test
    public final void testWarningsInThirdDepth() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {
        	"10:32: " + getCheckMessage(MSG_KEY_LEADS, "overrideMe", ctorKey, "overrideMe3"),
        	"11:27: " + getCheckMessage(MSG_KEY_LEADS, "overrideMe", ctorKey, "overrideMe3"),
            };

        verify(checkConfig, getPath("InputOverridableMethodInConstructor5.java"), expected);
    }

    @Test
    public final void testCloneNoWarningsSimple() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor6.java"), expected);
    }

    @Test
    public final void testCloneNoWarnings() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor7.java"), expected);
    }

    @Test
    public final void testCloneWarnings() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {
        	"20:37: " + getCheckMessage(MSG_KEY, "doSmth", cloneKey),
        	"37:37: " + getCheckMessage(MSG_KEY, "doSmth", cloneKey),
            };

        verify(checkConfig, getPath("InputOverridableMethodInConstructor8.java"), expected);
    }

    @Test
    public final void testCloneSecondDepth() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {
        	"25:37: " + getCheckMessage(MSG_KEY_LEADS, "doSmth", cloneKey, "doSmth2"),
        	"26:20: " + getCheckMessage(MSG_KEY, "doSmth2", cloneKey),
            };

        verify(checkConfig, getPath("InputOverridableMethodInConstructor9.java"), expected);
    }

    @Test
    public final void testCloneThirdDepthImplementation() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {
        	"25:37: " + getCheckMessage(MSG_KEY, "doSmth", cloneKey),
        	"26:19: " + getCheckMessage(MSG_KEY, "accept", cloneKey),
        	"27:24: " + getCheckMessage(MSG_KEY, "accept", cloneKey),
        	"62:37: " + getCheckMessage(MSG_KEY, "doSmth", cloneKey),
            };

        verify(checkConfig, getPath("InputOverridableMethodInConstructor13.java"), expected);
    }

    @Test
    public final void testSerializableNoWarnings() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor10.java"), expected);
    }

    @Test
    public final void testSerializableWarning() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {
        	"31:20: " + getCheckMessage(MSG_KEY, "doSmth", readObjectKey),
        	};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor11.java"), expected);
    }

    @Test
    public final void testStaticModifiers() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {
        };

        verify(checkConfig, getPath("InputOverridableMethodInConstructor12.java"), expected);
    }

    @Test
    public final void testSerializableThirdDepthImplementation()
        throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {
        	"34:20: " + getCheckMessage(MSG_KEY, "doSmth", readObjectKey),
        	"60:19: " + getCheckMessage(MSG_KEY, "doSmth", readObjectKey),
        	"61:24: " + getCheckMessage(MSG_KEY, "doSmth", readObjectKey),
        	"62:20: " + getCheckMessage(MSG_KEY_LEADS, "doSmth2", readObjectKey, "doSmth"),
        	"63:25: " + getCheckMessage(MSG_KEY_LEADS, "doSmth2", readObjectKey, "doSmth"),
        	"77:23: " + getCheckMessage(MSG_KEY, "doSmth", readObjectKey),
        	"78:28: " + getCheckMessage(MSG_KEY, "doSmth", readObjectKey),
        	"80:24: " + getCheckMessage(MSG_KEY_LEADS, "doSmth2", readObjectKey, "doSmth"),
        	"81:29: " + getCheckMessage(MSG_KEY_LEADS, "doSmth2", readObjectKey, "doSmth"),
            };

        verify(checkConfig, getPath("InputOverridableMethodInConstructor14.java"), expected);
    }

    @Test
    public final void testCtorOverloadedMethods() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor15.java"), expected);
    }

    @Test
    public final void test2EqualMethodNamesWithNoWarning() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor16.java"), expected);
    }

    @Test
    public final void test2EqualMethodNamesWithoutWarning2() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {
        	"17:32: " + getCheckMessage(MSG_KEY, "doSmth", readObjectKey),
        	};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor17.java"), expected);
    }

    @Test
    public final void testCallMethodIsNotInBuildPath2() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor18.java"), expected);
    }

    @Test
    public final void testCallMethodIsNotInBuildPath() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor19.java"), expected);
    }

    @Test
    public final void testReadObjectInInterface() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor20.java"), expected);
    }

    @Test
    public final void testStackOverFlowError() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor21.java"), expected);
    }

    @Test
    public final void testStackOverFlowErrorWithWarning() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {
        	"5:15: " + getCheckMessage(MSG_KEY_LEADS, "doSMTH", ctorKey, "doPublic"),
        	};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor22.java"), expected);
    }

    @Test
    public final void testStackOverFlowErrorWithoutWarning() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor23.java"), expected);
    }

    @Test
    public final void testAbstractMethodCall() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {
        	"11:22: " + getCheckMessage(MSG_KEY, "buildGetter", ctorKey),
        	};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor24.java"), expected);
    }

    @Test
    public final void testFinalClass() throws Exception
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor25.java"), expected);
    }
}
