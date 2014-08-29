package com.github.sevntu.checkstyle.checks.sizes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InputLineLengthCheck<E, N> {
    
    public InputLineLengthCheck() {
        
    }
    
	Map<StringBuilder, ArrayList<StringBuilder>> mTempVariableForCheck = new HashMap<StringBuilder, ArrayList<StringBuilder>>();
	
	public static <K, V> HashMap<K, V> newInstance(int mParametr1, int mParametr2, int mParametr3) throws IndexOutOfBoundsException, InstantiationException, IllegalMonitorStateException {
		String str = "This InputLineLengthCheck contains declaration of class, constructor, field and method. Their length is more then 120 characters, but it should be ignored";
		System.out.println(str);
	    return new HashMap<K, V>();
	}
	
	class InnerClassCheck
	{
		Map<StringBuilder, ArrayList<StringBuilder>> mTempVariableForInnerClass = new HashMap<StringBuilder, ArrayList<StringBuilder>>();
		
		public <K, V> HashMap<K, V> newInstanceForInnerClass(int mParametr1, int mParametr2, int mParametr3) throws IndexOutOfBoundsException, InstantiationException, IllegalMonitorStateException {
			String str = "This InputLineLengthCheck contains declaration of class, constructor, field and method in Inner Class. Their length is more then 120 characters, but it should be ignored";
			System.out.println(str);
		    return new HashMap<K, V>();
		}
	}
}

abstract class AbstractTreeNodeEntityModel<E extends Comparable<E>, N extends InputLineLengthCheck<E, N>>
extends InputLineLengthCheck<E, N> {
    /* (non-Javadoc)
    * @see com.revere.ria.workbench.spring.config.xml.AbstractDescriptorDefinitionParser#parseDescriptorAttributes(org.w3c.dom.Element, org.springframework.beans.factory.support.BeanDefinitionBuilder, org.springframework.beans.factory.xml.ParserContext)
    */
    protected void parseDescriptorAttributes(String element,
    Integer beanBuilder, Double parserContext) {
    }
    //This is a vary long coment, This is a vary long coment, This is a vary long coment, This is a vary long coment, This is a vary long coment, This is a vary long coment, This is a vary long coment, This is a vary long coment, This is a vary long coment!
    
    /* Some text Some text Some text Some text Some text Some text Some text Some text Some text Some text Some text Some text
     * Some text Some text Some text Some text Some text Some text Some text Some text
     * Some text*/
}
