package com.github.sevntu.checkstyle.checks.coding;

import java.io.InputStream;

public class InputTryWithResourcesValidStatements
{

    // hard to make it work with try-with-resources
    private InputStream stream = null;

    public void asParameter(InputStream stream)
    {

    }

    public void asParameter2(java.io.InputStream stream)
    {

    }
}
