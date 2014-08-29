package com.github.sevntu.checkstyle.checks.coding;
public class InputNoNullForCollectionReturnCheck6
{
    class DecoratableResourceMapping {

        public DecoratableResourceMapping() {
            if("" != null || "".length() == 0)
                return;
        }
    }
}