package com.github.sevntu.checkstyle.checks.coding;

import java.io.File;
import java.io.PrintWriter;

public class InputUselessSuperCtorCallCheck5
{
    private static class Base
    {
        public Base(int i)
        {
            
        }
    }
    
    private static class Derived extends Base
    {
        public Derived()
        {
            super(2);
        }
    }
}
