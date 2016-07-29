package com.github.sevntu.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static com.github.sevntu.checkstyle.checks.coding.EnumTrailingCommaCheck.MSG_KEY;
import static com.github.sevntu.checkstyle.checks.coding.EnumTrailingCommaCheck.MSG_KEY_SEMI;

public class EnumTrailingCommaCheckTest
    extends BaseCheckTestSupport {
    @Override
    protected String getPath(String filename) throws IOException {
        URL r = getClass().getResource(filename);
        return new File(r.getPath()).getCanonicalPath();
    }

    @Test
    public void testDefault() throws Exception {
        final DefaultConfiguration checkConfig =
            createCheckConfig(EnumTrailingCommaCheck.class);
        final String[] expected = {
            "14: " + getCheckMessage(MSG_KEY),
            "20: " + getCheckMessage(MSG_KEY),
            "26: " + getCheckMessage(MSG_KEY_SEMI),
        };
        verify(checkConfig, getPath("InputEnumTrailingComma.java"), expected);
    }

    @Test
    public void testTokensNotNull() {
        final EnumTrailingCommaCheck check = new EnumTrailingCommaCheck();
        Assert.assertNotNull(check.getAcceptableTokens());
        Assert.assertNotNull(check.getDefaultTokens());
        Assert.assertNotNull(check.getRequiredTokens());
    }
}
