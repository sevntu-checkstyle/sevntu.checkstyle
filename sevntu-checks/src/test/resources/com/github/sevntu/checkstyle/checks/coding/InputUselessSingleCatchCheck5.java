package com.github.sevntu.checkstyle.checks.coding;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class InputUselessSingleCatchCheck5
{
    protected InputUselessSingleCatchCheck5() throws Exception
    {   
        try {
            File f =new File("asd");
            FileReader w =new FileReader(f);
        }
        catch(IOException e) {
            throw e;
        }
    }

    public void expressionHasMoreNodes() throws IOException {
        try {
            File f = new File("asd");
            FileReader w = new FileReader(f);
        } catch (IOException e) {
            throw (e);
        }
    }
}
