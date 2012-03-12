package com.puppycrawl.tools.checkstyle.checks.coding;


import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class AvoidModifiersForTypesCheckTest extends BaseCheckTestSupport
{

    private final DefaultConfiguration checkConfig = createCheckConfig(AvoidModifiersForTypesCheck.class);

    @Test
    public void testFinal() throws Exception
    {
        String finalRegexp = "InputAvoidModifiersForTypesCheck";
        String staticRegexp = null;
        String transientRegexp = null;
        String volatileRegexp = null;

        checkConfig.addAttribute("forbiddenClassesRegexpFinal", finalRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", staticRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", transientRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", volatileRegexp);

        String[] expected = {
                "11:5: "+ getMessage("InputAvoidModifiersForTypesCheck", "final"),
                "12:5: "+ getMessage("InputAvoidModifiersForTypesCheck", "final"),
                "19:9: "+ getMessage("InputAvoidModifiersForTypesCheck", "final"),
        };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputAvoidModifiersForTypesCheck.java"), expected);
    }
    
    @Test
    public void testStatic() throws Exception
    {
        String finalRegexp = null;
        String staticRegexp = "File";
        String transientRegexp = null;
        String volatileRegexp = null;

        checkConfig.addAttribute("forbiddenClassesRegexpFinal", finalRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", staticRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", transientRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", volatileRegexp);

        String[] expected = {
                "10:5: "+ getMessage("File", "static"),
        };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputAvoidModifiersForTypesCheck.java"), expected);
    }
    
    @Test
    public void testTransient() throws Exception
    {
        String finalRegexp = null;
        String staticRegexp = null;
        String transientRegexp = "InputAvoidModifiersForTypesCheck";
        String volatileRegexp = null;

        checkConfig.addAttribute("forbiddenClassesRegexpFinal", finalRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", staticRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", transientRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", volatileRegexp);

        String[] expected = {
                "13:5: "+ getMessage("InputAvoidModifiersForTypesCheck", "transient"),
        };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputAvoidModifiersForTypesCheck.java"), expected);
    }
    
    @Test
    public void testVolatile() throws Exception
    {
        String finalRegexp = null;
        String staticRegexp = null;
        String transientRegexp = null;
        String volatileRegexp = "InputAvoidModifiersForTypesCheck";

        checkConfig.addAttribute("forbiddenClassesRegexpFinal", finalRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", staticRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", transientRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", volatileRegexp);

        String[] expected = {
                "14:5: "+ getMessage("InputAvoidModifiersForTypesCheck", "volatile"),
        };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputAvoidModifiersForTypesCheck.java"), expected);
    }
    
    @Test
    public void testFinalAndStatic() throws Exception
    {
        String finalRegexp = "InputAvoidModifiersForTypesCheck";
        String staticRegexp = "InputAvoidModifiersForTypesCheck";
        String transientRegexp = null;
        String volatileRegexp = null;

        checkConfig.addAttribute("forbiddenClassesRegexpFinal", finalRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", staticRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", transientRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", volatileRegexp);

        String[] expected = {
                "11:5: "+ getMessage("InputAvoidModifiersForTypesCheck", "final"),
                "12:5: "+ getMessage("InputAvoidModifiersForTypesCheck", "final"), // both
                "12:5: "+ getMessage("InputAvoidModifiersForTypesCheck", "static"), // both
                "19:9: "+ getMessage("InputAvoidModifiersForTypesCheck", "final"),
        };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputAvoidModifiersForTypesCheck.java"), expected);
    }
    
    private String getMessage(String className, String modifierType)
    {
        return "'" + className + "' instance should not have '" + modifierType
                + "' modifier.";
    }

}
