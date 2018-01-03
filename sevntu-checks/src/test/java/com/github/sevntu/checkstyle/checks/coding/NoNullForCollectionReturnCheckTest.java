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

package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.NoNullForCollectionReturnCheck.MSG_KEY;

import org.junit.Test;

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
            "11: " + warningMessage,
            "46: " + warningMessage,
            "54: " + warningMessage,
            "59: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck1.java"), expected);
    }

    @Test
    public void testArraysDeep() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NoNullForCollectionReturnCheck.class);
        checkConfig.addAttribute("searchThroughMethodBody", "true");
        final String[] expected = {
            "11: " + warningMessage,
            "18: " + warningMessage,
            "46: " + warningMessage,
            "54: " + warningMessage,
            "59: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck1.java"), expected);
    }

    @Test
    public void testCollections() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NoNullForCollectionReturnCheck.class);
        checkConfig.addAttribute("collectionList",
                "Collection ArrayList LinkedList Stack Vector HashSet TreeSet");
        final String[] expected = {
            "7: " + warningMessage,
            "17: " + warningMessage,
            "27: " + warningMessage,
            "37: " + warningMessage,
            "47: " + warningMessage,
            "57: " + warningMessage,
            "67: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck2.java"), expected);
    }

    @Test
    public void testRevereCode() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NoNullForCollectionReturnCheck.class);
        final String[] expected = {
            "13: " + warningMessage,
            "16: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck3.java"), expected);
    }

    @Test
    public void testRevereCodeDeep() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NoNullForCollectionReturnCheck.class);
        checkConfig.addAttribute("searchThroughMethodBody", "true");
        final String[] expected = {
            "13: " + warningMessage,
            "16: " + warningMessage,
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
            "17: " + warningMessage,
            "25: " + warningMessage,
            "27: " + warningMessage,
            "39: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck5.java"), expected);
    }

    @Test
    public void testRealCode() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NoNullForCollectionReturnCheck.class);
        checkConfig.addAttribute("searchThroughMethodBody", "true");
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
            "8: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck7.java"), expected);
    }

    @Test
    public void testIss148Deep()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NoNullForCollectionReturnCheck.class);
        checkConfig.addAttribute("searchThroughMethodBody", "true");
        final String[] expected = {
            "8: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck7.java"), expected);
    }

    @Test
    public void testConstructor()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NoNullForCollectionReturnCheck.class);
        final String[] expected = {
            "5: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheckConstructor.java"),
                expected);
    }

}
