package com.puppycrawl.tools.checkstyle.checks.coding;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class OverridableMethodInConstructorTest extends BaseCheckTestSupport {

    final DefaultConfiguration checkConfig = createCheckConfig(OverridableMethodInConstructorCheck.class);
    final String mCtorKey = "constructor";
    final String mCloneKey = "'clone()' method";
    final String mReadObjectKey = "'readObject()' method";

    @Before
    public void setTestinputsDir() {
        System.setProperty(
                "testinputs.dir",
                "/media/data/Work/Git repository clone = Eclipse workspace/sevntu.checkstyle/checkstyle-sevntu/src/testinputs/com/puppycrawl/tools/checkstyle");

        checkConfig.addAttribute("checkCloneMethod", "true");
        checkConfig.addAttribute("checkReadObjectMethod", "true");
    }

    @Test
    public final void testNoWarnings() throws Exception {

        String[] expected = {};

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor1.java"), expected);
    }

    @Test
    public final void testWarning() throws Exception {

        String[] expected = { "10:27: " + createMsg("overrideMe", mCtorKey) };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor2.java"), expected);
    }

    @Test
    public final void test2WarningsIn2Ctors() throws Exception {
        String[] expected = { "10:27: " + createMsg("overrideMe", mCtorKey),
                "15:27: " + createMsg("overrideMe", mCtorKey) };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor3.java"), expected);
    }

    @Test
    public final void testWarningInSecondDepth() throws Exception {

        String[] expected = { "10:32: "
                + createLeadsMsg("overrideMe", mCtorKey) };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor4.java"), expected);
    }

    @Test
    public final void testWarningsInThirdDepth() throws Exception {

        String[] expected = {
                "10:32: " + createLeadsMsg("overrideMe", mCtorKey),
                "11:27: " + createLeadsMsg("overrideMe", mCtorKey), };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor5.java"), expected);
    }

    @Test
    public final void testCloneNoWarningsSimple() throws Exception {

        String[] expected = {};

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor6.java"), expected);
    }

    @Test
    public final void testCloneNoWarnings() throws Exception {

        String[] expected = {};

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor7.java"), expected);
    }

    @Test
    public final void testCloneWarnings() throws Exception {

        String[] expected = { "20:37: " + createMsg("doSmth", mCloneKey),
                "37:37: " + createMsg("doSmth", mCloneKey), };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor8.java"), expected);
    }

    @Test
    public final void testCloneSecondDepth() throws Exception {

        String[] expected = { "25:37: " + createLeadsMsg("doSmth", mCloneKey),
                "26:20: " + createMsg("doSmth2", mCloneKey), };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor9.java"), expected);
    }

    @Test
    public final void testCloneThirdDepthImplementation() throws Exception {

        String[] expected = { "25:37: " + createMsg("doSmth", mCloneKey),
                "26:19: " + createMsg("accept", mCloneKey),
                "27:24: " + createMsg("accept", mCloneKey),
                "62:37: " + createMsg("doSmth", mCloneKey), };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor13.java"), expected);
    }

    @Test
    public final void testSerializableNoWarnings() throws Exception {

        String[] expected = {};

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor10.java"), expected);
    }

    @Test
    public final void testSerializableWarning() throws Exception {

        String[] expected = { "31:20: " + createMsg("doSmth", mReadObjectKey), };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor11.java"), expected);
    }

    @Test
    public final void testSerializable3WarningsInThirdDepth() throws Exception {

        String[] expected = {
                "30:20: " + createLeadsMsg("doSmth", mReadObjectKey),
                "31:25: " + createLeadsMsg("doSmth", mReadObjectKey),
                "32:28: " + createLeadsMsg("doSmth", mReadObjectKey),
                "33:29: " + createMsg("doSmth3", mReadObjectKey), };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor12.java"), expected);
    }

    @Test
    public final void testSerializableThirdDepthImplementation()
            throws Exception {

        String[] expected = { "34:20: " + createMsg("doSmth", mReadObjectKey),
                "60:19: " + createMsg("doSmth", mReadObjectKey),
                "61:24: " + createMsg("doSmth", mReadObjectKey),
                "62:20: " + createLeadsMsg("doSmth2", mReadObjectKey),
                "63:25: " + createLeadsMsg("doSmth2", mReadObjectKey),
                "77:23: " + createMsg("doSmth", mReadObjectKey),
                "78:28: " + createMsg("doSmth", mReadObjectKey),
                "80:24: " + createLeadsMsg("doSmth2", mReadObjectKey),
                "81:29: " + createLeadsMsg("doSmth2", mReadObjectKey), };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor14.java"), expected);
    }

    @Test
    public final void testCtorOverloadedMethods() throws Exception {

        String[] expected = {};

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor15.java"), expected);
    }

    @Test
    public final void test2EqualMethodNamesWithNoWarning() throws Exception {

        String[] expected = {};

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor16.java"), expected);
    }

    @Test
    public final void test2EqualMethodNamesWithoutWarning2() throws Exception {

        String[] expected = {};

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor17.java"), expected);
    }

    @Test
    public final void testCallMethodIsNotInBuildPath2() throws Exception {

        String[] expected = {};

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor18.java"), expected);
    }

    @Test
    public final void testCallMethodIsNotInBuildPath() throws Exception {

        String[] expected = {};

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor19.java"), expected);
    }

    @Test
    public final void testReadObjectInInterface() throws Exception {

        String[] expected = {};

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor20.java"), expected);
    }

    @Test
    public final void testStackOverFlowError() throws Exception {

        String[] expected = {};

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor21.java"), expected);
    }

    @Test
    public final void testStackOverFlowErrorWithWarning() throws Exception {

        String[] expected = { "4:15: " + createLeadsMsg("doSMTH", mCtorKey), };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor22.java"), expected);
    }

    @Test
    public final void testStackOverFlowErrorWithoutWarning() throws Exception {

        String[] expected = {};

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor23.java"), expected);
    }

    @Test
    public final void testAbstractMethodCall() throws Exception {

        String[] expected = { "18:22: " + createMsg("buildGetter", mCtorKey), };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor24.java"), expected);
    }

    @Test
    public final void testFinalClass() throws Exception {

        String[] expected = {};

        verify(checkConfig, getPath("coding" + File.separator
                + "InputOverridableMethodInConstructor25.java"), expected);
    }

    public String createMsg(String methodName, String where) {
        return "Overridable method '" + methodName + "' is called in " + where
                + " body.";
    }

    private String createLeadsMsg(String methodName, String where) {
        return "Calling the method '" + methodName + "' in " + where
                + " body leads to the call(s) of the overridable method(s).";
    }

}
