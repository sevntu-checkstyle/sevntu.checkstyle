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

import static com.github.sevntu.checkstyle.checks.coding.MapIterationInForEachLoopCheck.MSG_KEY_ENTRYSET;
import static com.github.sevntu.checkstyle.checks.coding.MapIterationInForEachLoopCheck.MSG_KEY_KEYSET;
import static com.github.sevntu.checkstyle.checks.coding.MapIterationInForEachLoopCheck.MSG_KEY_VALUES;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

public class MapIterationInForEachLoopCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public final void basicTest() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(MapIterationInForEachLoopCheck.class);
        checkConfig.addProperty("proposeValuesUsage", "true");
        checkConfig.addProperty("proposeKeySetUsage", "true");
        checkConfig.addProperty("proposeEntrySetUsage", "true");

        final String[] expected = {
            "23:13: " + getCheckMessage(MSG_KEY_ENTRYSET),
            "46:13: " + getCheckMessage(MSG_KEY_VALUES),
            "48:17: " + getCheckMessage(MSG_KEY_KEYSET),
            "72:17: " + getCheckMessage(MSG_KEY_ENTRYSET),
            "77:13: " + getCheckMessage(MSG_KEY_KEYSET),
            "84:9: " + getCheckMessage(MSG_KEY_ENTRYSET),
            "89:9: " + getCheckMessage(MSG_KEY_VALUES),
            "107:9: " + getCheckMessage(MSG_KEY_ENTRYSET),
            "117:9: " + getCheckMessage(MSG_KEY_VALUES),
        };

        verify(checkConfig,
                getPath("InputMapIterationInForEachLoopCheck.java"), expected);
    }

    @Test
    public final void importsWithoutFullPathTest() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(MapIterationInForEachLoopCheck.class);
        checkConfig.addProperty("proposeValuesUsage", "true");
        checkConfig.addProperty("proposeKeySetUsage", "true");
        checkConfig.addProperty("proposeEntrySetUsage", "true");

        final String[] expected = {
            "12:9: " + getCheckMessage(MSG_KEY_ENTRYSET),
        };
        verify(checkConfig,
                getPath("InputMapIterationInForEachLoopCheckImport.java"),
                expected);
    }

    @Test
    public final void skipIfConditionTest() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(MapIterationInForEachLoopCheck.class);
        checkConfig.addProperty("proposeValuesUsage", "false");
        checkConfig.addProperty("proposeKeySetUsage", "true");
        checkConfig.addProperty("proposeEntrySetUsage", "true");
        checkConfig.addProperty("supportedMapImplQualifiedNames",
                "java.util.Map, java.util.HashMap, java.util.TreeMap, com.myTest.InputMyMap");

        final String[] expected = {
            "14:9: " + getCheckMessage(MSG_KEY_ENTRYSET),
        };

        verify(checkConfig,
                getPath("InputMapIterationInForEachLoopCheckSkipIf.java"),
                expected);
    }

    @Test
    public final void testClassExtendingMap() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(MapIterationInForEachLoopCheck.class);
        checkConfig.addProperty("proposeValuesUsage", "true");
        checkConfig.addProperty("proposeKeySetUsage", "true");
        checkConfig.addProperty("proposeEntrySetUsage", "true");

        verify(checkConfig,
                getPath("InputMapIterationInForEachLoopCheckExtendingMap.java"),
                CommonUtil.EMPTY_STRING_ARRAY);
    }

}
