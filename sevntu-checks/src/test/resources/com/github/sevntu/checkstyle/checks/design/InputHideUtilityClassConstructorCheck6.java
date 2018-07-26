package com.github.sevntu.checkstyle.checks.design;

/**
 * Input for HideUtilityClassConstructorCheck, a non utility class that has 
 * 
 * @author lkuehne
 */
public class InputHideUtilityClassConstructorCheck6
{
    public long constructionTime = System.currentTimeMillis();

    public static InputHideUtilityClassConstructorCheck6 create()
    {
        return new InputHideUtilityClassConstructorCheck6();
    }
}
