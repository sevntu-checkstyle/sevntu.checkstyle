package com.github.sevntu.checkstyle.checks.coding;

import java.io.Closeable;

public class InputTryWithResourcesUnresolved
{
    private static class MyResource implements AutoCloseable, Closeable
    {
        @Override
        public void close()
        {
        }
    }

    private static class FileInputStream extends MyResource
    {
    }

    private AutoCloseable customAutoClosable()
    {
        // this test would complain only if MyResource is in classpath when Checkstyle is executed
        MyResource stream = new MyResource();
        return stream;
    }

    private void ownFileInputStream()
    {
        // name the same as java.io.FileInputStream but not listed in imports so it won't be matched
        FileInputStream input;
    }
}
