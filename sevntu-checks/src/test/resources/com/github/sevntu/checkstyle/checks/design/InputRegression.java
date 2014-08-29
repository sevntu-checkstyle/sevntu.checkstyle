package com.github.sevntu.checkstyle.checks.design;

/**
 * Input for HideUtilityClassConstructorCheck, a non utility class that has 
 * 
 * @author lkuehne
 */
public class InputRegression
{
    public long constructionTime = System.currentTimeMillis();

    public static InputRegression create()
    {
        return new InputRegression();
    }
}