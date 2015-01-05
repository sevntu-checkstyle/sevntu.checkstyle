package com.github.sevntu.checkstyle.checks.coding;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.util.zip.ZipException;

public class InputTryWithResourcesValidStatementsJDK7
{

    public void closeableIsClosed()
            throws FileNotFoundException, ZipException
    {
    }

    private interface SomeInterface
    {
        Closeable getCloseable();
    }

    public void tryWithoutResourcesBitwiseOr()
    {
        try {
            closeableIsClosed();
        }
        catch (FileNotFoundException | ZipException e) {
        }
    }
}
