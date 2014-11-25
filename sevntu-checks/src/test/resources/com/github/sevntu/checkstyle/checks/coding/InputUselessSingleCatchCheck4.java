package com.github.sevntu.checkstyle.checks.coding;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class InputUselessSingleCatchCheck4
{
    protected InputUselessSingleCatchCheck4() throws Exception
    {       
        Exception ex =new Exception();
        
        try {
            File f =new File("asd");
            FileReader w =new FileReader(f);
        }
        catch(IOException e) {    
            throw ex;
        }
    }
}
