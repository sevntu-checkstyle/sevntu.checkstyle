package com.github.sevntu.checkstyle.checks.coding;

public class InputSimpleAccessorNameNotationCheck6
{
    public static void
            updateCheckSum(final String metadataFile, String metadataFileSum, String hashFunction)
                    throws Exception
    {
        new Object() {
            public String getInput() throws Exception
            {
                return metadataFile;
            }
        };
    }
}
