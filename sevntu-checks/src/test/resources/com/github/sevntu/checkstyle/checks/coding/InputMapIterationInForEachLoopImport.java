import com.myTest.*;
import java.util.*;

public class Test
{
    public static void main(String[] args)
    {
        Map<String, String> map = new HashMap<String, String>();
        MyMap<String, String> myMap = new MyMap<String, String>();

        for (String key : map.keySet())
        {
            System.out.println(key + " --> " + map.get(key));
        }

        for (myMap.Entry<String, String> entry : myMap.entrySet())
        {
            System.out.println(entry.getValue() + " --> ");
        }
    }
}
