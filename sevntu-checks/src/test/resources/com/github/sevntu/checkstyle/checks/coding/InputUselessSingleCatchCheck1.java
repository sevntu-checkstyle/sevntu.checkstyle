package com.github.sevntu.checkstyle.checks.coding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class InputUselessSingleCatchCheck1
{
    protected InputUselessSingleCatchCheck1() throws Exception
    {        
        try{
            File f =new File("asd");
            FileReader w =new FileReader(f);
        }
        catch(FileNotFoundException e)
        {
            throw e;
        }
        catch(IOException e)
        {
            throw new Exception(e);
        }
        catch(Exception e)
        {
            System.out.println("log");
            throw e;
        }
    }
}
