public class InputNoNullForCollectionReturnCheck5
{
    private int[] method1()
    {

        HashCode hash = ByteStreams.hash(new InputSupplier<InputStream>()
        {
            @Override
            public InputStream getInput() throws IOException
            {
                return null;
            }
            
            public LinkedList<Object> test()
            {
                return null; //!!
            }
        }, hashFunction);

        Files.write(hash.toString().getBytes(), metadataFileSum);

        if(true)
        {
            return null; //!!
        }
        return null; //!!
    }
    
    private double[] method2()
    {
        boolean isCorrect = false;
        if(isCorrect)
        {
            return 0.456;
        }
        else
        {
            return null;
        }
    }

}
