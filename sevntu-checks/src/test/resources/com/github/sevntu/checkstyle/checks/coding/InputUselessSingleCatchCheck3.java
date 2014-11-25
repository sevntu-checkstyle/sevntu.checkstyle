package com.github.sevntu.checkstyle.checks.coding;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class InputUselessSingleCatchCheck3
{
    protected InputUselessSingleCatchCheck3() throws Exception
    {        
        try {
            File f =new File("asd");
            FileReader w =new FileReader(f);
        }
        catch(IOException e) {
            System.out.println("log");
            throw e;
        }
    }
}
