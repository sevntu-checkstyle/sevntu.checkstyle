package com.github.sevntu.checkstyle.checks.coding;

public class InputEmptyPublicCtorInClassCheck5
{
    public InputEmptyPublicCtorInClassCheck5()
    {

    }

    class Inner1
    {
        int field;

        public Inner1()
        {
            
        }
    }
    
    class Inner2
    {
        int field;
        
        public Inner2(int i) {
            field =i;
        }
    }
}
