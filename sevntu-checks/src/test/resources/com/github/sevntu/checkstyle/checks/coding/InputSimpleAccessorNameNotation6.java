package com.puppycrawl.tools.checkstyle.checks.coding;

public class InputSimpleAccesorNameNotation7
{
    public static void
            updateCheckSum(final InputStream metadataFile, File metadataFileSum, HashFunction hashFunction)
                    throws IOException
    {
        HashCode hash = ByteStreams.hash(new InputSupplier<InputStream>()
        {
            @Override
                    public InputStream getInput() throws IOException
            {
                return metadataFile;
            }
        }, hashFunction);
    }
}
