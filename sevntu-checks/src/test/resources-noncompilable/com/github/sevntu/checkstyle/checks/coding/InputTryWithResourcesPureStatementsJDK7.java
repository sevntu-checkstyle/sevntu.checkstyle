package com.github.sevntu.checkstyle.checks.coding;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.PipedReader;
import java.io.Reader;
import java.util.jar.JarFile;

public class InputTryWithResourcesPureStatementsJDK7
{

    private static class MyAutoCloseable implements AutoCloseable, Closeable
    {
        @Override
        public void close()
        {
        }
    }

    private static FileInputStream buildCloseable()
            throws FileNotFoundException
    {
        return new FileInputStream("");
    }

    public static void main(String[] args)
            throws FileNotFoundException, IOException
    {
        try (FileInputStream resource = new FileInputStream("")) {
        }
        catch (IOException e) {
        }

        try (final BufferedReader resource = new BufferedReader(null)) {
        }
        catch (IOException e) {
        }

        try (CharArrayWriter resource = new CharArrayWriter();
                CharArrayReader resource2 = new CharArrayReader(null)) {
        }

        try (@SuppressWarnings("all")
        Reader resource = new PipedReader()) {
        }
        catch (IOException e) {
        }

        try (JarFile resource = new JarFile("")
        {
        }) {
        }
        catch (Exception e) {
        }

        try (InputStream resource = buildCloseable()) {
        }

        try (ObjectOutputStream resource = (new ObjectOutputStream(null))) {
        }

        try (AutoCloseable resource = new InputTryWithResourcesPureStatementsJDK7.MyAutoCloseable()) {
        }
        catch (Exception e) {
        }
    }
}
