package com.github.sevntu.checkstyle.checks.coding;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class MapIterationInForEachLoopTest extends BaseCheckTestSupport
{

    /**
     * Default check configuration
     */
    private final DefaultConfiguration mCheckConfig =
        createCheckConfig(MapIterationInForEachLoopCheck.class);
    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file
     */
    private final String mMsgKeyEntrySet =
            MapIterationInForEachLoopCheck.MSG_KEY_ENTRYSET;
    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file
     */
    private final String mMsgKeyValues =
            MapIterationInForEachLoopCheck.MSG_KEY_VALUES;
    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file
     */
    private final String mMsgKeyKeySet =
            MapIterationInForEachLoopCheck.MSG_KEY_KEYSET;

    @Test
    public final void basicTest() throws Exception
    {
        mCheckConfig.addAttribute("processingKeySet", "true");
        mCheckConfig.addAttribute("processingEntrySet", "true");

        final String[] expected = {
            "22:25: "
                    + getCheckMessage(mMsgKeyEntrySet),
            "45:25: "
                    + getCheckMessage(mMsgKeyValues),
            "47:33: "
                    + getCheckMessage(mMsgKeyKeySet),
            "71:33: "
                    + getCheckMessage(mMsgKeyEntrySet),
            "76:25: "
                    + getCheckMessage(mMsgKeyKeySet),
            "83:17: "
                    + getCheckMessage(mMsgKeyEntrySet),
            "88:17: "
                    + getCheckMessage(mMsgKeyValues), };

        verify(mCheckConfig,
                getPath("InputMapIterationInForEachLoop.java"), expected);
    }

    @Test
    public final void importsWithoutFullPathTest() throws Exception
    {
        mCheckConfig.addAttribute("processingKeySet", "true");
        mCheckConfig.addAttribute("processingEntrySet", "true");

        final String supportedMapImplementationQualifiedNames =
                "java.util.Map, "
                + "java.util.HashMap, java.util.TreeMap, com.myTest.MyMap, ";

        mCheckConfig.addAttribute("supportedMapImplementationQualifiedNames",
                supportedMapImplementationQualifiedNames);
        final String [] expected = {
            "11:9: " + getCheckMessage(mMsgKeyEntrySet),
            "16:9: " + getCheckMessage(mMsgKeyValues), };
        verify(mCheckConfig,
                getPath("InputMapIterationInForEachLoopImport.java"),
                expected);
    }


    @Test
    public final void skipIfConditionTest() throws Exception
    {
        mCheckConfig.addAttribute("processingKeySet", "true");
        mCheckConfig.addAttribute("processingEntrySet", "true");

        final String supportedMapImplementationQualifiedNames =
                "java.util.Map, "
                + "java.util.HashMap, java.util.TreeMap, "
                + "com.myTest.MyMap";

        mCheckConfig.addAttribute("supportedMapImplementationQualifiedNames",
                supportedMapImplementationQualifiedNames);

        final String [] expected =
        {
                "12:9: " + getCheckMessage(mMsgKeyEntrySet), 
                "28:9: " + getCheckMessage(mMsgKeyValues), };
        verify(mCheckConfig,
                getPath("InputMapIterationInForEachLoopSkipIf.java"),
                expected);
    }
}
