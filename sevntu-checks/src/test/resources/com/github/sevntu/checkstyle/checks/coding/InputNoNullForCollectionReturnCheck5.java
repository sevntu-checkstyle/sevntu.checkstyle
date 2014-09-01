package com.github.sevntu.checkstyle.checks.coding;
import java.util.LinkedList;
public class InputNoNullForCollectionReturnCheck5
{
    private int[] method1()
    {
        Object hashFunction = null;
        HashCode hash = ByteStreams.hash(new InputSupplier<InputStream>()
        {
            public InputStream getInput() throws Exception
            {
                return null;
            }
            
            public LinkedList<Object> test()
            {
                return null; //!!
            }
        }, hashFunction);
        Object metadataFileSum = null;
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
            return new double[4];
        }
        else
        {
            return null;
        }
    }
    
    private class HashCode {
        
    }
    
    private static class ByteStreams {

        public static
                HashCode
                hash(InputSupplier<InputStream> inputSupplier, Object hashFunction)
        {
            return null;
        }
        
    }
    
    private class InputSupplier<T> {
        
    }
    
    private class InputStream {
        
    }
    
    private static class Files {

        public static void write(byte[] bytes, Object metadataFileSum)
        {
            
        }
        
    }

}
