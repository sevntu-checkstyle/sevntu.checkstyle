package com.github.sevntu.checkstyle.checks.coding;

import org.junit.Test;

import static com.github.sevntu.checkstyle.checks.coding.MapIterationInForEachLoopCheck.*;
import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class MapIterationInForEachLoopTest extends BaseCheckTestSupport
{

    /**
     * Default check configuration
     */
    private final DefaultConfiguration checkConfig =
        createCheckConfig(MapIterationInForEachLoopCheck.class);

    @Test
    public final void basicTest() throws Exception
    {
    	checkConfig.addAttribute("proposeValuesUsage", "true");
        checkConfig.addAttribute("proposeKeySetUsage", "true");
        checkConfig.addAttribute("proposeEntrySetUsage", "true");

        final String[] expected = {
            "23:13: " + getCheckMessage(MSG_KEY_ENTRYSET),
            "46:13: " + getCheckMessage(MSG_KEY_VALUES),
            "48:17: " + getCheckMessage(MSG_KEY_KEYSET),
            "72:17: " + getCheckMessage(MSG_KEY_ENTRYSET),
            "77:13: " + getCheckMessage(MSG_KEY_KEYSET),
            "84:9: " + getCheckMessage(MSG_KEY_ENTRYSET),
            "89:9: " + getCheckMessage(MSG_KEY_VALUES),
            "107:9: " + getCheckMessage(MSG_KEY_ENTRYSET),
            "117:9: " + getCheckMessage(MSG_KEY_VALUES),};

        verify(checkConfig,
                getPath("InputMapIterationInForEachLoop.java"), expected);
    }

    @Test
    public final void importsWithoutFullPathTest() throws Exception
    {
    	checkConfig.addAttribute("proposeValuesUsage", "true");
        checkConfig.addAttribute("proposeKeySetUsage", "true");
        checkConfig.addAttribute("proposeEntrySetUsage", "true");

        final String [] expected = {
            "12:9: " + getCheckMessage(MSG_KEY_ENTRYSET),};
        verify(checkConfig,
                getPath("InputMapIterationInForEachLoopImport.java"),
                expected);
    }


    @Test
    public final void skipIfConditionTest() throws Exception
    {
        checkConfig.addAttribute("proposeValuesUsage", "false");
        checkConfig.addAttribute("proposeKeySetUsage", "true");
        checkConfig.addAttribute("proposeEntrySetUsage", "true");

        final String supportedMapImplQualifiedNames =
                "java.util.Map, "
                + "java.util.HashMap, java.util.TreeMap, "
                + "com.myTest.MyMap";

        checkConfig.addAttribute("supportedMapImplQualifiedNames",
                supportedMapImplQualifiedNames);

        final String [] expected = {
        		"14:9: " + getCheckMessage(MSG_KEY_ENTRYSET), 
        		};
        
        verify(checkConfig,
                getPath("InputMapIterationInForEachLoopSkipIf.java"),
                expected);
    }
    
}
