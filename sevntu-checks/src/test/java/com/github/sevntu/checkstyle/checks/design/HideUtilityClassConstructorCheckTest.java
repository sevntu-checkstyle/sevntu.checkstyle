package com.github.sevntu.checkstyle.checks.design;

import static com.github.sevntu.checkstyle.checks.design.HideUtilityClassConstructorCheck.*;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.design.HideUtilityClassConstructorCheck;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
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
        	"11:1: " + getCheckMessage(MSG_KEY),
        };
        verify(checkConfig, getPath("InputArrayTypeStyle.java"), expected);
    }

    /** non static methods - always OK */
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
        verify(checkConfig, getPath("InputNonUtilityClass.java"), expected);
    }

    @Test
    public void testOnlyNonstaticFieldNonUtilClass() throws Exception
    {
        final DefaultConfiguration checkConfig =
            createCheckConfig(HideUtilityClassConstructorCheck.class);
        final String[] expected = {
        };
        verify(checkConfig, getPath("InputRegression1762702.java"), expected);
    }

    @Test
    public void testEmptyAbstractClass() throws Exception
    {
        final DefaultConfiguration checkConfig =
            createCheckConfig(HideUtilityClassConstructorCheck.class);
        final String[] expected = {
        };
        verify(checkConfig, getPath("HideUtilityClassContructor3041574_1.java"), expected);
    }

    @Test
    public void testEmptyClassWithOnlyPrivateFields() throws Exception
    {
        final DefaultConfiguration checkConfig =
            createCheckConfig(HideUtilityClassConstructorCheck.class);
        final String[] expected = {
        };
        verify(checkConfig, getPath("HideUtilityClassContructor3041574_2.java"), expected);
    }

    @Test
    public void testClassWithStaticInnerClass() throws Exception
    {
        final DefaultConfiguration checkConfig =
            createCheckConfig(HideUtilityClassConstructorCheck.class);
        final String[] expected = {
        };
        verify(checkConfig, getPath("HideUtilityClassContructor3041574_3.java"), expected);
    }

}
