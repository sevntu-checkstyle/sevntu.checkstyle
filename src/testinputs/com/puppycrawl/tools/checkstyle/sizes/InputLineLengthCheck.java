package com.puppycrawl.tools.checkstyle.sizes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InputLineLengthCheck {
    
    public InputLineLengthCheck() {
        
    }
    
	Map<StringBuilder, ArrayList<StringBuilder>> mTempVariableForCheck = new HashMap<StringBuilder, ArrayList<StringBuilder>>();
	
	public static <K, V> HashMap<K, V> newInstance(int mParametr1, int mParametr2, int mParametr3) throws IndexOutOfBoundsException, InstantiationException, IllegalMonitorStateException {
		String str = "This InputLineLengthCheck contains declaration of field and method. Their length is more then 120 characters, but it should be ignored";
		System.out.println(str);
	    return new HashMap<K, V>();
	}
	
	class InnerClassCheck
	{
		Map<StringBuilder, ArrayList<StringBuilder>> mTempVariableForInnerClass = new HashMap<StringBuilder, ArrayList<StringBuilder>>();
		
		public <K, V> HashMap<K, V> newInstanceForInnerClass(int mParametr1, int mParametr2, int mParametr3) throws IndexOutOfBoundsException, InstantiationException, IllegalMonitorStateException {
			String str = "This InputLineLengthCheck contains declaration of field and method in Inner Class. Their length is more then 120 characters, but it should be ignored";
			System.out.println(str);
		    return new HashMap<K, V>();
		}
	}

}
