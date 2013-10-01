package com.github.sevntu.checkstyle.checks.javadoc;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class JavadocMethodCheckExtendedTest extends BaseCheckTestSupport
{
    private final DefaultConfiguration checkConfig = createCheckConfig(JavaDocMethodCheckExtended.class);

    @Test
    public void test0()
            throws Exception
    {
        String[] expected = {
                "10:9: comment is require",
                "13:9: comment is require",
                "30:9: comment is require",
                "37:9: comment is require",
                "47:9: comment is require",
                "56:13: comment is require"

        };
        verify(checkConfig, getPath("InputJavadocMethodCheckExtended.java"),
                expected);
    }

    @Test
    public void test1()
            throws Exception
    {
        checkConfig.addAttribute("allowCheckFinal", "true");

        String[] expected = {
                "10:9: comment is require",
                "13:9: comment is require",
                "30:9: comment is require",
                "37:9: comment is require",
                "47:9: comment is require",
                "56:13: comment is require",

        };
        verify(checkConfig, getPath("InputJavadocMethodCheckExtended.java"),
                expected);
    }

    @Test
    public void test2()
            throws Exception
    {
        checkConfig.addAttribute("allowCheckAbstract", "true");

        String[] expected = {
                "10:9: comment is require",
                "13:9: comment is require",
                "14:9: comment is require",
                "47:9: comment is require",
                "47:9: comment is require",
                "47:9: comment is require",

        };
        verify(checkConfig, getPath("InputJavadocMethodCheckExtended.java"),
                expected);

    }

    @Test
    public void test3()
            throws Exception
    {
        checkConfig.addAttribute("allowCheckOverrideMethod", "false");

        String[] expected = {
                "21:9: comment is require",
                "47:9: comment is require",
                "55:9: comment is require",
                "65:9: comment is require",
                "74:13: comment is require"

        };
        verify(checkConfig, getPath("InputJavadocMethodCheckExtended.java"),
                expected);

    }

}
