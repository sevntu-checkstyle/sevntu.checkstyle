////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2011  Oliver Burn
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

import org.junit.Before;
import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.coding.OverridableMethodInConstructorCheck;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class OverridableMethodInConstructorCheckTest extends BaseCheckTestSupport
{

    private final DefaultConfiguration checkConfig = createCheckConfig(OverridableMethodInConstructorCheck.class);
    private static final String mCtorKey = "constructor";
    private static final String mCloneKey = "'clone()' method";
    private static final String mReadObjectKey = "'readObject()' method";

    @Before
    public void setTestinputsDir()
    {
        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
        checkConfig.addAttribute("matchMethodsByArgCount", "true");
    }

    @Test
    public final void revereCodeTest() throws Exception
    {

        String[] expected = {"11:17: "+ createLeadsMsg("init", mCtorKey,"getPart")};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor26.java"), expected);
    }
    
    @Test
    public final void newFeauture() throws Exception
    {

        String[] expected = {"14:27: "+ createMsg("overrideMe", mCtorKey),
                "15:21: "+ createMsg("init", mCtorKey)};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor27.java"), expected);
    }
    
    @Test
    public final void testNoWarnings() throws Exception
    {

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor1.java"), expected);
    }

    @Test
    public final void testWarning() throws Exception
    {

        String[] expected = {"10:27: " + createMsg("overrideMe", mCtorKey) };

        verify(checkConfig, getPath("InputOverridableMethodInConstructor2.java"), expected);
    }

    @Test
    public final void test2WarningsIn2Ctors() throws Exception
    {
        String[] expected = {"10:27: " + createMsg("overrideMe", mCtorKey),
            "15:27: " + createMsg("overrideMe", mCtorKey), };

        verify(checkConfig, getPath("InputOverridableMethodInConstructor3.java"), expected);
    }

    @Test
    public final void testWarningInSecondDepth() throws Exception
    {

        String[] expected = {"10:32: "
                + createLeadsMsg("overrideMe", mCtorKey, "overrideMe2"), };

        verify(checkConfig, getPath("InputOverridableMethodInConstructor4.java"), expected);
    }

    @Test
    public final void testWarningsInThirdDepth() throws Exception
    {

        String[] expected = {
            "10:32: " + createLeadsMsg("overrideMe", mCtorKey, "overrideMe3"),
            "11:27: " + createLeadsMsg("overrideMe", mCtorKey, "overrideMe3"), };

        verify(checkConfig, getPath("InputOverridableMethodInConstructor5.java"), expected);
    }

    @Test
    public final void testCloneNoWarningsSimple() throws Exception
    {

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor6.java"), expected);
    }

    @Test
    public final void testCloneNoWarnings() throws Exception
    {

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor7.java"), expected);
    }

    @Test
    public final void testCloneWarnings() throws Exception
    {

        String[] expected = {"20:37: " + createMsg("doSmth", mCloneKey),
            "37:37: " + createMsg("doSmth", mCloneKey), };

        verify(checkConfig, getPath("InputOverridableMethodInConstructor8.java"), expected);
    }

    @Test
    public final void testCloneSecondDepth() throws Exception
    {

        String[] expected = {"25:37: " + createLeadsMsg("doSmth", mCloneKey, "doSmth2"),
            "26:20: " + createMsg("doSmth2", mCloneKey), };

        verify(checkConfig, getPath("InputOverridableMethodInConstructor9.java"), expected);
    }

    @Test
    public final void testCloneThirdDepthImplementation() throws Exception
    {

        String[] expected = {"25:37: " + createMsg("doSmth", mCloneKey),
            "26:19: " + createMsg("accept", mCloneKey),
            "27:24: " + createMsg("accept", mCloneKey),
            "62:37: " + createMsg("doSmth", mCloneKey), };

        verify(checkConfig, getPath("InputOverridableMethodInConstructor13.java"), expected);
    }

    @Test
    public final void testSerializableNoWarnings() throws Exception
    {

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor10.java"), expected);
    }

    @Test
    public final void testSerializableWarning() throws Exception
    {

        String[] expected = {"31:20: " + createMsg("doSmth", mReadObjectKey), };

        verify(checkConfig, getPath("InputOverridableMethodInConstructor11.java"), expected);
    }

    @Test
    public final void testStaticModifiers() throws Exception
    {

        String[] expected = {
        };

        verify(checkConfig, getPath("InputOverridableMethodInConstructor12.java"), expected);
    }

    @Test
    public final void testSerializableThirdDepthImplementation()
        throws Exception
    {

        String[] expected = {"34:20: " + createMsg("doSmth", mReadObjectKey),
            "60:19: " + createMsg("doSmth", mReadObjectKey),
            "61:24: " + createMsg("doSmth", mReadObjectKey),
            "62:20: " + createLeadsMsg("doSmth2", mReadObjectKey, "doSmth"),
            "63:25: " + createLeadsMsg("doSmth2", mReadObjectKey, "doSmth"),
            "77:23: " + createMsg("doSmth", mReadObjectKey),
            "78:28: " + createMsg("doSmth", mReadObjectKey),
            "80:24: " + createLeadsMsg("doSmth2", mReadObjectKey, "doSmth"),
            "81:29: " + createLeadsMsg("doSmth2", mReadObjectKey, "doSmth"), };

        verify(checkConfig, getPath("InputOverridableMethodInConstructor14.java"), expected);
    }

    @Test
    public final void testCtorOverloadedMethods() throws Exception
    {

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor15.java"), expected);
    }

    @Test
    public final void test2EqualMethodNamesWithNoWarning() throws Exception
    {

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor16.java"), expected);
    }

    @Test
    public final void test2EqualMethodNamesWithoutWarning2() throws Exception
    {

        String[] expected = {"17:32: " + createMsg("doSmth", mReadObjectKey)};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor17.java"), expected);
    }

    @Test
    public final void testCallMethodIsNotInBuildPath2() throws Exception
    {

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor18.java"), expected);
    }

    @Test
    public final void testCallMethodIsNotInBuildPath() throws Exception
    {

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor19.java"), expected);
    }

    @Test
    public final void testReadObjectInInterface() throws Exception
    {

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor20.java"), expected);
    }

    @Test
    public final void testStackOverFlowError() throws Exception
    {

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor21.java"), expected);
    }

    @Test
    public final void testStackOverFlowErrorWithWarning() throws Exception
    {

        String[] expected = {"4:15: " + createLeadsMsg("doSMTH", mCtorKey, "doPublic"), };

        verify(checkConfig, getPath("InputOverridableMethodInConstructor22.java"), expected);
    }

    @Test
    public final void testStackOverFlowErrorWithoutWarning() throws Exception
    {

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor23.java"), expected);
    }

    @Test
    public final void testAbstractMethodCall() throws Exception
    {

        String[] expected = {"18:22: " + createMsg("buildGetter", mCtorKey), };

        verify(checkConfig, getPath("InputOverridableMethodInConstructor24.java"), expected);
    }

    @Test
    public final void testFinalClass() throws Exception
    {

        String[] expected = {};

        verify(checkConfig, getPath("InputOverridableMethodInConstructor25.java"), expected);
    }

    
    public String createMsg(String methodName, String where)
    {
        return "Overridable method '" + methodName + "' is called in " + where
                + " body.";
    }

    private static String createLeadsMsg(String methodName, String where, String overridableName)
    {
        return "Calling the method '" + methodName + "' in " + where
                + " body leads to the call of the overridable method '"
                + overridableName + "'.";
    }

}
