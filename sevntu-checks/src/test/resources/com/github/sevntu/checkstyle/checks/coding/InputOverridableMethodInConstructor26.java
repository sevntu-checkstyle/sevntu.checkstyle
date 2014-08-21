package com.github.sevntu.checkstyle.checks.coding;

public class InputOverridableMethodInConstructor26
{

    class ULCWorkbenchPartSite
    {

        public ULCWorkbenchPartSite()
        {
            init();
            initPart("");
        }

        private void initPart(String str)
        {
            
        }

        public void getPart()
        {

        }

        private void init()
        {

            getPart();
        }
    }
}
