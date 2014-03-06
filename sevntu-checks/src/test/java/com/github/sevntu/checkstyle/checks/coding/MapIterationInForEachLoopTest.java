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
            "22:13: " + getCheckMessage(MSG_KEY_ENTRYSET),
            "45:13: " + getCheckMessage(MSG_KEY_VALUES),
            "47:17: " + getCheckMessage(MSG_KEY_KEYSET),
            "71:17: " + getCheckMessage(MSG_KEY_ENTRYSET),
            "76:13: " + getCheckMessage(MSG_KEY_KEYSET),
            "83:9: " + getCheckMessage(MSG_KEY_ENTRYSET),
            "88:9: " + getCheckMessage(MSG_KEY_VALUES),
            "106:9: " + getCheckMessage(MSG_KEY_ENTRYSET),
            "116:9: " + getCheckMessage(MSG_KEY_VALUES),};

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
            "11:9: " + getCheckMessage(MSG_KEY_ENTRYSET),};
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

        final String [] expected =
        {"12:9: " + getCheckMessage(MSG_KEY_ENTRYSET), };
        
        verify(mCheckConfig,
                getPath("InputMapIterationInForEachLoopSkipIf.java"),
                expected);
    }
    
}
