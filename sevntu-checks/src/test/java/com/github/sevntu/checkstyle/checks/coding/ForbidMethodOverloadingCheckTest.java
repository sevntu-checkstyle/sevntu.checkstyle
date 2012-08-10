package com.github.sevntu.checkstyle.checks.coding;

import java.io.File;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class ForbidMethodOverloadingCheckTest extends BaseCheckTestSupport
{
    @Test
    public void testWithDefaults() throws Exception
        {
        final DefaultConfiguration checkConfig = createCheckConfig(ForbidMethodOverloadingCheck.class);

        final String[] expected = {
            "7: Method with such name is already defined in this type.",
            "15: Method with such name is already defined in this type.",
            "22: Method with such name is already defined in this type.",
            "31: Method with such name is already defined in this type.",
            "43: Method with such name is already defined in this type.",
             };
        verify(checkConfig, getPath("coding" + File.separator
                + "InputForbidMethodOverloadingCheck.java"),
                expected);
    }
    
    @Test
    public void testWithAllowPrivate() throws Exception
        {
        final DefaultConfiguration checkConfig = createCheckConfig(ForbidMethodOverloadingCheck.class);
        checkConfig.addAttribute("allowPrivate", "true");

        final String[] expected = {
            "7: Method with such name is already defined in this type.",
            "22: Method with such name is already defined in this type.",
            "31: Method with such name is already defined in this type.",
             };
        verify(checkConfig, getPath("coding" + File.separator
                + "InputForbidMethodOverloadingCheck.java"),
                expected);
    }
    
    @Test
    public void testWithAllowClasses() throws Exception
        {
        final DefaultConfiguration checkConfig = createCheckConfig(ForbidMethodOverloadingCheck.class);
        checkConfig.addAttribute("allowInClasses", "true");
        
        final String[] expected = {
            "22: Method with such name is already defined in this type.",
             };
        verify(checkConfig, getPath("coding" + File.separator
                + "InputForbidMethodOverloadingCheck.java"),
                expected);
    }
}
