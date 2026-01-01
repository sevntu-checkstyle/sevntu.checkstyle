///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2026 the original author or authors.
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
///////////////////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.EmptyPublicCtorInClassCheck.MSG_KEY;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.DetailAstImpl;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class EmptyPublicCtorInClassCheckTest extends AbstractModuleTestSupport {

    private final String message = getCheckMessage(MSG_KEY);

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testEmptyPublicCtor()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(EmptyPublicCtorInClassCheck.class);
        final String[] expected = {
            "5:5: " + message,
        };

        verify(checkConfig, getPath("InputEmptyPublicCtorInClassCheck1.java"), expected);
    }

    @Test
    public void testEmptyPrivateCtor()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(EmptyPublicCtorInClassCheck.class);
        final String[] expected = {};

        verify(checkConfig, getPath("InputEmptyPublicCtorInClassCheck2.java"), expected);
    }

    @Test
    public void testEmptyProtectedCtor()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(EmptyPublicCtorInClassCheck.class);
        final String[] expected = {};

        verify(checkConfig, getPath("InputEmptyPublicCtorInClassCheck6.java"), expected);
    }

    @Test
    public void testClassWithMultiplePublicCtors()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(EmptyPublicCtorInClassCheck.class);
        final String[] expected = {};

        verify(checkConfig, getPath("InputEmptyPublicCtorInClassCheck3.java"), expected);
    }

    @Test
    public void testPublicNotEmptyCtor()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(EmptyPublicCtorInClassCheck.class);
        final String[] expected = {};

        verify(checkConfig, getPath("InputEmptyPublicCtorInClassCheck4.java"), expected);
    }

    @Test
    public void testClassWithInnerClasses()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(EmptyPublicCtorInClassCheck.class);
        final String[] expected = {
            "5:5: " + message,
            "14:9: " + message,
        };

        verify(checkConfig, getPath("InputEmptyPublicCtorInClassCheck5.java"), expected);
    }

    @Test
    public void testCtorAnnotatedWithAnnotation() throws Exception {
        final DefaultConfiguration config = createModuleConfig(EmptyPublicCtorInClassCheck.class);

        config.addProperty("ctorAnnotationNames", "com\\.github\\.sevntu\\.checkstyle\\.checks\\."
                + "coding\\.AnnotationName");

        final String[] expected = {};

        verify(config, getPath("InputEmptyPublicCtorInClassCheck7.java"), expected);
    }

    @Test
    public void testClassAnnotatedWithAnnotation1() throws Exception {
        final DefaultConfiguration config = createModuleConfig(EmptyPublicCtorInClassCheck.class);

        config.addProperty("classAnnotationNames",
                "com\\.github\\.sevntu\\.checkstyle\\.checks\\.coding\\.AnnotationName|"
                + "org\\.junit\\.runner\\.RunWith|"
                + "org\\.junit\\.Ignore|"
                + "com\\.github\\.sevntu\\.checkstyle\\.checks\\.coding\\."
                + "InputEmptyPublicCtorInClassCheck9\\.InnerAnnotation");

        final String[] expected = {};

        verify(config, getPath("InputEmptyPublicCtorInClassCheck8.java"), expected);
    }

    @Test
    public void testClassAnnotatedWithAnnotation2() throws Exception {
        final DefaultConfiguration config = createModuleConfig(EmptyPublicCtorInClassCheck.class);

        config.addProperty("classAnnotationNames",
                "org\\.junit\\.runner\\.RunWith|org\\.junit\\.Ignore|");

        final String[] expected = {};

        verify(config, getPath("InputEmptyPublicCtorInClassCheck10.java"), expected);
    }

    @Test
    public void testNullProperties1() throws Exception {
        final DefaultConfiguration config = createModuleConfig(EmptyPublicCtorInClassCheck.class);

        config.addProperty("classAnnotationNames", null);
        config.addProperty("ctorAnnotationNames", null);

        final String[] expected = {
            "5:5: " + message,
            "14:9: " + message,
        };

        verify(config, getPath("InputEmptyPublicCtorInClassCheck5.java"), expected);
    }

    @Test
    public void testNullProperties2() throws Exception {
        final DefaultConfiguration config = createModuleConfig(EmptyPublicCtorInClassCheck.class);

        config.addProperty("classAnnotationNames", "");
        config.addProperty("ctorAnnotationNames", "");

        final String[] expected = {
            "5:5: " + message,
            "14:9: " + message,
        };

        verify(config, getPath("InputEmptyPublicCtorInClassCheck5.java"), expected);
    }

    @Test
    public void testUnsupportedNode() {
        final DetailAstImpl sync = new DetailAstImpl();
        sync.setType(TokenTypes.LITERAL_SYNCHRONIZED);

        try {
            final EmptyPublicCtorInClassCheck check = new EmptyPublicCtorInClassCheck();
            check.visitToken(sync);

            fail("exception expected");
        }
        catch (IllegalArgumentException exc) {
            Assertions.assertEquals("Found unsupported token: LITERAL_SYNCHRONIZED",
                exc.getMessage());
        }
    }

}
