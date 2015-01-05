package com.github.sevntu.checkstyle.checks.coding;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
// one import with .* just for testing and code coverage
import java.io.*;

public class InputTryWithResourcesFalsePositive
{
    public void failEvenStreamAsClosed()
            throws IOException
    {
        FileInputStream stream = new FileInputStream("");
        stream.close();
    }

    public void closeableIsClosed()
            throws IOException
    {
        Closeable stream = new FileOutputStream("");
        stream.close();
    }

    public void autoCloseableIsClosed()
            throws Exception
    {
        AutoCloseable stream = getClass().getResourceAsStream("");
        stream.close();
    }
}
