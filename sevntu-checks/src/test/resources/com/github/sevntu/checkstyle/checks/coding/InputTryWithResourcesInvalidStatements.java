package com.github.sevntu.checkstyle.checks.coding;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class InputTryWithResourcesInvalidStatements
{

    private FileInputStream buildInputStream()
            throws FileNotFoundException
    {
        return new FileInputStream("");
    }

    public void failWithShortIndent()
    {
        FileInputStream stream;
    }

    public void failInner()
    {
        {
            FileInputStream stream;
        }
    }

    public void failWithDualShortIndent()
    {
        FileInputStream input, output;
    }

    public void failWithLongIndent()
    {
        java.io.FileInputStream stream;
    }

    public void failWithDualLongIndent()
    {
        java.io.FileInputStream input, output;
    }

    public void tryWithoutResources()
    {
        try {
            FileOutputStream stream = new FileOutputStream("");
        }
        catch (FileNotFoundException e) {
        }
    }
}
