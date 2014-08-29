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
    private final DefaultConfiguration mCheckConfig =
        createCheckConfig(MapIterationInForEachLoopCheck.class);

    @Test
    public final void basicTest() throws Exception
    {
    	mCheckConfig.addAttribute("proposeValuesUsage", "true");
        mCheckConfig.addAttribute("proposeKeySetUsage", "true");
        mCheckConfig.addAttribute("proposeEntrySetUsage", "true");

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

        verify(mCheckConfig,
                getPath("InputMapIterationInForEachLoop.java"), expected);
    }

    @Test
    public final void importsWithoutFullPathTest() throws Exception
    {
    	mCheckConfig.addAttribute("proposeValuesUsage", "true");
        mCheckConfig.addAttribute("proposeKeySetUsage", "true");
        mCheckConfig.addAttribute("proposeEntrySetUsage", "true");

        final String [] expected = {
            "12:9: " + getCheckMessage(MSG_KEY_ENTRYSET),};
        verify(mCheckConfig,
                getPath("InputMapIterationInForEachLoopImport.java"),
                expected);
    }


    @Test
    public final void skipIfConditionTest() throws Exception
    {
        mCheckConfig.addAttribute("proposeValuesUsage", "false");
        mCheckConfig.addAttribute("proposeKeySetUsage", "true");
        mCheckConfig.addAttribute("proposeEntrySetUsage", "true");

        final String supportedMapImplQualifiedNames =
                "java.util.Map, "
                + "java.util.HashMap, java.util.TreeMap, "
                + "com.myTest.MyMap";

        mCheckConfig.addAttribute("supportedMapImplQualifiedNames",
                supportedMapImplQualifiedNames);

        final String [] expected = {
        		"14:9: " + getCheckMessage(MSG_KEY_ENTRYSET), 
        		};
        
        verify(mCheckConfig,
                getPath("InputMapIterationInForEachLoopSkipIf.java"),
                expected);
    }
    
}
