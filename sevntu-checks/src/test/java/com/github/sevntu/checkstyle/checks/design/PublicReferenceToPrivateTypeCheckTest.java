////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2018 the original author or authors.
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

package com.github.sevntu.checkstyle.checks.design;

import static com.github.sevntu.checkstyle.checks.design.PublicReferenceToPrivateTypeCheck.MSG_KEY;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * @author <a href="mailto:nesterenko-aleksey@list.ru">Aleksey Nesterenko</a>
 */
public class PublicReferenceToPrivateTypeCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/design";
    }

    /**
     * Test file without method return instance of private class.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public void noReturnProblemsTest()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(PublicReferenceToPrivateTypeCheck.class);
        final String[] expected = {};
        verify(checkConfig,
                getPath("InputPublicReferenceToPrivateTypeCheck1.java"),
                expected);
    }

    /**
     * Test file with method return instance of private class.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public void returnPrivateTest()
            throws Exception {
        final String typeName = "PrivateInner";
        final DefaultConfiguration checkConfig =
                createModuleConfig(PublicReferenceToPrivateTypeCheck.class);
        final String[] expected = {"13: " + getCheckMessage(MSG_KEY, typeName), };
        verify(checkConfig,
                getPath("InputPublicReferenceToPrivateTypeCheck2.java"),
                expected);
    }

    /**
     * Test file with method return instance of private class private class
     * implements interface.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public void returnPrivateThatImplementTest()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(PublicReferenceToPrivateTypeCheck.class);
        final String[] expected = {};
        verify(checkConfig,
                getPath("InputPublicReferenceToPrivateTypeCheck4.java"),
                expected);
    }

    /**
     * Test file with method return instance of private class private class
     * extends another class.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public void returnPrivateThatExtendsTest()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(PublicReferenceToPrivateTypeCheck.class);
        final String[] expected = {};
        verify(checkConfig,
                getPath("InputPublicReferenceToPrivateTypeCheck3.java"),
                expected);
    }

    /**
     * Test file with method return array of instances of private class.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public void returnPrivateArrayTest()
            throws Exception {
        final String typeName = "PrivateInner";
        final DefaultConfiguration checkConfig =
                createModuleConfig(PublicReferenceToPrivateTypeCheck.class);
        final String[] expected = {"13: " + getCheckMessage(MSG_KEY, typeName), };
        verify(checkConfig,
                getPath("InputPublicReferenceToPrivateTypeCheck5.java"),
                expected);
    }

    /**
     * Test file with method return collection of instances of private class.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public void returnPrivateInShell()
            throws Exception {
        final String typeName = "PrivateInner";
        final DefaultConfiguration checkConfig =
                createModuleConfig(PublicReferenceToPrivateTypeCheck.class);
        final String[] expected = {"14: " + getCheckMessage(MSG_KEY, typeName), };
        verify(checkConfig,
                getPath("InputPublicReferenceToPrivateTypeCheck6.java"),
                expected);
    }

    /**
     * Test file with method return collection of collections instances of
     * private class.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public void returnPrivateInCollectionInCollection()
            throws Exception {
        final String typeName = "PrivateInner";
        final DefaultConfiguration checkConfig =
                createModuleConfig(PublicReferenceToPrivateTypeCheck.class);
        final String[] expected = {"14: " + getCheckMessage(MSG_KEY, typeName), };
        verify(checkConfig,
                getPath("InputPublicReferenceToPrivateTypeCheck7.java"),
                expected);
    }

    /**
     * Test file with method return several times included in collection
     * instances of private class.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public void returnPrivateInMapInCollectionInCollection()
            throws Exception {
        final String typeName = "PrivateInner";
        final DefaultConfiguration checkConfig =
                createModuleConfig(PublicReferenceToPrivateTypeCheck.class);
        final String[] expected = {"14: " + getCheckMessage(MSG_KEY, typeName), };
        verify(checkConfig,
                getPath("InputPublicReferenceToPrivateTypeCheck9.java"),
                expected);
    }

    /**
     * Test file with different inner classes(default, public, private,
     * protected).
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public void complexTest()
            throws Exception {
        final String typeName = "PrivateInner";
        final String typeName1 = "OutClass";
        final DefaultConfiguration checkConfig =
                createModuleConfig(PublicReferenceToPrivateTypeCheck.class);
        final String[] expected = {
            "25: " + getCheckMessage(MSG_KEY, typeName),
            "28: " + getCheckMessage(MSG_KEY, typeName1),
        };
        verify(checkConfig,
                getPath("InputPublicReferenceToPrivateTypeCheck8.java"),
                expected);
    }

    /**
     * Test file with different methods modifiers((default, public, private,
     * protected).
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public void methodModifiersTest()
            throws Exception {
        final String typeName = "PrivateInner";
        final DefaultConfiguration checkConfig =
                createModuleConfig(PublicReferenceToPrivateTypeCheck.class);
        final String[] expected = {
            "14: " + getCheckMessage(MSG_KEY, typeName),
            "20: " + getCheckMessage(MSG_KEY, typeName),
            "23: " + getCheckMessage(MSG_KEY, typeName),
        };
        verify(checkConfig,
                getPath("InputPublicReferenceToPrivateTypeCheck10.java"),
                expected);
    }

    /**
     * Test file with wildcards.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public void wildcardsTest()
            throws Exception {
        final String typeName = "PrivateInner";
        final DefaultConfiguration checkConfig =
                createModuleConfig(PublicReferenceToPrivateTypeCheck.class);
        final String[] expected = {
            "14: " + getCheckMessage(MSG_KEY, typeName),
            "20: " + getCheckMessage(MSG_KEY, typeName),
            "23: " + getCheckMessage(MSG_KEY, typeName),
        };
        verify(checkConfig,
                getPath("InputPublicReferenceToPrivateTypeCheck11.java"),
                expected);
    }

    @Test
    public void interfacesTest()
            throws Exception {
        final String typeName = "PrivateInner";
        final DefaultConfiguration checkConfig =
                createModuleConfig(PublicReferenceToPrivateTypeCheck.class);
        final String[] expected = {
            "13: " + getCheckMessage(MSG_KEY, typeName),
            "19: " + getCheckMessage(MSG_KEY, typeName),
        };
        verify(checkConfig,
                getPath("InputPublicReferenceToPrivateTypeCheck12.java"),
                expected);
    }

    @Test
    public void enumsTest()
            throws Exception {
        final String typeName = "First";
        final DefaultConfiguration checkConfig =
                createModuleConfig(PublicReferenceToPrivateTypeCheck.class);
        final String[] expected = {
            "8: " + getCheckMessage(MSG_KEY, typeName),
            "10: " + getCheckMessage(MSG_KEY, typeName),
        };
        verify(checkConfig,
                getPath("InputPublicReferenceToPrivateTypeCheck13.java"),
                expected);
    }

    @Test
    public void testClassEnumInterface()
            throws Exception {
        final String typeName = "PrivateInner";
        final String typeName1 = "PrivateInner1";
        final String typeName2 = "First";
        final DefaultConfiguration checkConfig =
                createModuleConfig(PublicReferenceToPrivateTypeCheck.class);
        final String[] expected = {
            "15: " + getCheckMessage(MSG_KEY, typeName),
            "25: " + getCheckMessage(MSG_KEY, typeName1),
            "31: " + getCheckMessage(MSG_KEY, typeName1),
            "35: " + getCheckMessage(MSG_KEY, typeName2),
            "37: " + getCheckMessage(MSG_KEY, typeName2),
        };
        verify(checkConfig,
                getPath("InputPublicReferenceToPrivateTypeCheck14.java"),
                expected);
    }

    @Test
    public void returnFromInnerTypeTest()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(PublicReferenceToPrivateTypeCheck.class);
        final String[] expected = {
            "4: " + getCheckMessage(MSG_KEY, "Inner"),
            "4: " + getCheckMessage(MSG_KEY, "Inner1"),
            "9: " + getCheckMessage(MSG_KEY, "Inner"),
            "9: " + getCheckMessage(MSG_KEY, "Inner2"),
            "9: " + getCheckMessage(MSG_KEY, "Inner3"),
            "14: " + getCheckMessage(MSG_KEY, "Inner"),
        };
        verify(checkConfig,
                getPath("InputPublicReferenceToPrivateTypeCheck15.java"),
                expected);
    }

    @Test
    public void returnFromInnerAnonymousClassTest()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(PublicReferenceToPrivateTypeCheck.class);
        final String[] expected = {
            "4: " + getCheckMessage(MSG_KEY, "Inner"),
            "4: " + getCheckMessage(MSG_KEY, "Inner2"),
            "4: " + getCheckMessage(MSG_KEY, "Inner3"),
        };
        verify(checkConfig,
                getPath("InputPublicReferenceToPrivateTypeCheck16.java"),
                expected);
    }

    @Test
    public void returnFromPublicFieldTest()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(PublicReferenceToPrivateTypeCheck.class);
        final String[] expected = {
            "4: " + getCheckMessage(MSG_KEY, "Inner"),
            "9: " + getCheckMessage(MSG_KEY, "Inner1"),
        };
        verify(checkConfig,
                getPath("InputPublicReferenceToPrivateTypeCheck17.java"),
                expected);
    }

    @Test
    public void implementingOrExtendingPrivateTypeTest()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(PublicReferenceToPrivateTypeCheck.class);
        final String[] expected = {
            "17: " + getCheckMessage(MSG_KEY, "InnerClass"),
        };
        verify(checkConfig,
                getPath("InputPublicReferenceToPrivateTypeCheck18.java"),
                expected);
    }

    @Test
    public void usingPrivateTypeAsMethodParameterTest()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(PublicReferenceToPrivateTypeCheck.class);
        final String[] expected = {
            "4: " + getCheckMessage(MSG_KEY, "C1"),
            "6: " + getCheckMessage(MSG_KEY, "C1"),
            "8: " + getCheckMessage(MSG_KEY, "C1"),
        };
        verify(checkConfig,
                getPath("InputPublicReferenceToPrivateTypeCheck19.java"),
                expected);
    }

    @Test
    public void testUnsupportedNode() {
        final DetailAST sync = new DetailAST();
        sync.setType(TokenTypes.LITERAL_SYNCHRONIZED);

        try {
            final PublicReferenceToPrivateTypeCheck check = new PublicReferenceToPrivateTypeCheck();
            check.visitToken(sync);

            fail();
        }
        catch (IllegalArgumentException ex) {
            Assert.assertEquals("Found unsupported token: LITERAL_SYNCHRONIZED", ex.getMessage());
        }
    }

}
