package com.puppycrawl.tools.checkstyle.checks.design;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import java.io.File;
import org.junit.Test;

public class HideUtilityClassConstructorCheckTest
    extends BaseCheckTestSupport
{
    /** only static methods and no constructor - default ctor is visible */
    @Test
    public void testUtilClass() throws Exception
    {
        final DefaultConfiguration checkConfig =
            createCheckConfig(HideUtilityClassConstructorCheck.class);
        final String[] expected = {
            "11:1: Utility classes should not have a public or default constructor.",
        };
        verify(checkConfig, getPath("InputArrayTypeStyle.java"), expected);
    }

    /** nonstatic methods - always OK */
    @Test
    public void testNonUtilClass() throws Exception
    {
        final DefaultConfiguration checkConfig =
            createCheckConfig(HideUtilityClassConstructorCheck.class);
        final String[] expected = {
        };
        verify(checkConfig, getPath("InputDesignForExtension.java"), expected);
    }

    @Test
    public void testDerivedNonUtilClass() throws Exception
    {
        final DefaultConfiguration checkConfig =
            createCheckConfig(HideUtilityClassConstructorCheck.class);
        final String[] expected = {
        };
        verify(checkConfig, getPath("design" + File.separator + "InputNonUtilityClass.java"), expected);
    }

    @Test
    public void testOnlyNonstaticFieldNonUtilClass() throws Exception
    {
        final DefaultConfiguration checkConfig =
            createCheckConfig(HideUtilityClassConstructorCheck.class);
        final String[] expected = {
        };
        verify(checkConfig, getPath("design" + File.separator + "InputRegression1762702.java"), expected);
    }

    @Test
    public void testEmptyAbstractClass() throws Exception
    {
        final DefaultConfiguration checkConfig =
            createCheckConfig(HideUtilityClassConstructorCheck.class);
        final String[] expected = {
        };
        System.setProperty("testinputs.dir",
        "/home/romani/java/workspaceJEE_Test/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle");
        verify(checkConfig, getPath("design" + File.separator + "HideUtilityClassContructor3041574_1.java"), expected);
    }

    @Test
    public void testEmptyClassWithOnlyPrivateFields() throws Exception
    {
        final DefaultConfiguration checkConfig =
            createCheckConfig(HideUtilityClassConstructorCheck.class);
        final String[] expected = {
        };
        System.setProperty("testinputs.dir",
        "/home/romani/java/workspaceJEE_Test/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle");
        verify(checkConfig, getPath("design" + File.separator + "HideUtilityClassContructor3041574_2.java"), expected);
    }

    @Test
    public void testClassWithStaticInnerClass() throws Exception
    {
        final DefaultConfiguration checkConfig =
            createCheckConfig(HideUtilityClassConstructorCheck.class);
        final String[] expected = {
        };
        System.setProperty("testinputs.dir",
        "/home/romani/java/workspaceJEE_Test/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle");
        verify(checkConfig, getPath("design" + File.separator + "HideUtilityClassContructor3041574_3.java"), expected);
    }

}
