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

import static com.github.sevntu.checkstyle.checks.coding.NoNullForCollectionReturnCheck.MSG_KEY;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class NoNullForCollectionReturnCheckTest extends AbstractModuleTestSupport {

    private final String warningMessage = getCheckMessage(MSG_KEY);

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testArraysNotDeep() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NoNullForCollectionReturnCheck.class);
        final String[] expected = {
            "11:9: " + warningMessage,
            "46:13: " + warningMessage,
            "54:13: " + warningMessage,
            "59:17: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck1.java"), expected);
    }

    @Test
    public void testArraysDeep() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NoNullForCollectionReturnCheck.class);
        checkConfig.addProperty("searchThroughMethodBody", "true");
        final String[] expected = {
            "11:9: " + warningMessage,
            "18:9: " + warningMessage,
            "46:13: " + warningMessage,
            "54:13: " + warningMessage,
            "59:17: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck1.java"), expected);
    }

    @Test
    public void testCollections() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NoNullForCollectionReturnCheck.class);
        checkConfig.addProperty("collectionList",
                "Collection ArrayList LinkedList Stack Vector HashSet TreeSet");
        final String[] expected = {
            "7:9: " + warningMessage,
            "17:9: " + warningMessage,
            "27:9: " + warningMessage,
            "37:9: " + warningMessage,
            "47:9: " + warningMessage,
            "57:9: " + warningMessage,
            "67:9: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck2.java"), expected);
    }

    @Test
    public void testRevereCode() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NoNullForCollectionReturnCheck.class);
        final String[] expected = {
            "13:25: " + warningMessage,
            "16:25: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck3.java"), expected);
    }

    @Test
    public void testRevereCodeDeep() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NoNullForCollectionReturnCheck.class);
        checkConfig.addProperty("searchThroughMethodBody", "true");
        final String[] expected = {
            "13:25: " + warningMessage,
            "16:25: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck3.java"), expected);
    }

    @Test
    public void testInterface() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NoNullForCollectionReturnCheck.class);
        final String[] expected = {
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck4.java"), expected);
    }

    @Test
    public void testInnerClasses() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NoNullForCollectionReturnCheck.class);
        final String[] expected = {
            "17:17: " + warningMessage,
            "25:13: " + warningMessage,
            "27:9: " + warningMessage,
            "39:13: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck5.java"), expected);
    }

    @Test
    public void testRealCode() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NoNullForCollectionReturnCheck.class);
        checkConfig.addProperty("searchThroughMethodBody", "true");
        final String[] expected = {
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck6.java"), expected);
    }

    @Test
    public void testIss148()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NoNullForCollectionReturnCheck.class);
        final String[] expected = {
            "8:9: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck7.java"), expected);
    }

    @Test
    public void testIss148Deep()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NoNullForCollectionReturnCheck.class);
        checkConfig.addProperty("searchThroughMethodBody", "true");
        final String[] expected = {
            "8:9: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck7.java"), expected);
    }

    @Test
    public void testConstructor()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NoNullForCollectionReturnCheck.class);
        final String[] expected = {
            "5:9: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheckConstructor.java"),
                expected);
    }

    @Test
    public void testObjectArray()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NoNullForCollectionReturnCheck.class);

        final String[] expected = {
            "15:13: " + warningMessage,
            "33:13: " + warningMessage,
            "52:9: " + warningMessage,
            "64:15: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheckObjectArray.java"),
                expected);
    }

}
