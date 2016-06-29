package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.FieldSingleDeclarationCheck.MSG_KEY;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;


/**
 * LoggerDeclarationsCountCheck test.
 *
 * @author Michael Vorburger <mike@vorburger.ch>
 */
public class FieldSingleDeclarationCheckTest extends BaseCheckTestSupport {

    private final DefaultConfiguration config = createCheckConfig(FieldSingleDeclarationCheck.class);

    @Test
    public void testFieldSingleDeclaration_NOK() throws Exception {
        String className = "org.slf4j.Logger";
        config.addAttribute("className", className);
        final String expected[] = { "10: " + getCheckMessage(MSG_KEY, className) };
        verify(config, getPath("FieldSingleDeclarationCheck_NOK.java"), expected);
        verify(config, getPath("FieldSingleDeclarationCheck_OK.java"), new String[] {});
    }

    @Test
    public void testFieldSingleDeclaration_FQN_NOK() throws Exception {
        String className = "org.slf4j.Logger";
        config.addAttribute("className", className);
        final String expected[] = { "7: " + getCheckMessage(MSG_KEY, className) };
        verify(config, getPath("FieldSingleDeclarationCheck_FQN_NOK.java"), expected);
        verify(config, getPath("FieldSingleDeclarationCheck_OK.java"), new String[] {});
    }

    @Test
    public void testFieldSingleDeclaration_OK() throws Exception {
        String className = "org.slf4j.Logger";
        config.addAttribute("className", className);
        verify(config, getPath("FieldSingleDeclarationCheck_OK.java"), new String[] {});
    }

    @Test(expected = CheckstyleException.class)
    public void testNoClassNameConfigured() throws Exception {
        verify(config, getPath("FieldSingleDeclarationCheck_OK.java"), new String[] {});
    }

    @Test
    public void testCustomTokens() throws Exception {
        String className = "org.slf4j.Logger";
        config.addAttribute("className", className);
        // This is required just so that getAcceptableTokens() gets coverage
        config.addAttribute("tokens", "VARIABLE_DEF");
        verify(config, getPath("FieldSingleDeclarationCheck_OK.java"), new String[] {});
    }
}
