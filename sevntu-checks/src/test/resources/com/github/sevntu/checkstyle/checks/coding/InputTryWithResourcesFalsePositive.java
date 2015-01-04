package com.github.sevntu.checkstyle.checks.coding;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputTryWithResourcesFalsePositive
{

    // hard to make it work with try-with-resources
    private InputStream stream = null;

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
