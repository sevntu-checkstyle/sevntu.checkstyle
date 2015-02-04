package com.github.sevntu.checkstyle.checks.coding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStream;

public class InputTryWithResourcesAdnotations
{

    public @interface MyAnnotation
    {
        BufferedReader reader = null;
    }

    @MyAnnotation()
    BufferedReader reader;

    public void doSomething(@MyAnnotation() OutputStream parameter)
    {
        @MyAnnotation()
        BufferedWriter writer;
    }
}
