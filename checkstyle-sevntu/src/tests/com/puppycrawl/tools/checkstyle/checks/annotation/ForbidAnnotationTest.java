package com.puppycrawl.tools.checkstyle.checks.annotation;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class ForbidAnnotationTest extends BaseCheckTestSupport{
	
	@Test
    public void testParansAlways() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);
        
        checkConfig.addAttribute("annotation", "pack1,pack2,pack3");
        checkConfig.addAttribute("target", "PACKAGE_DEF");
        
        final String[] expected1 = {
        	"1: Incorrect target: 'package' for annotation: 'pack1'.",
        	"2: Incorrect target: 'package' for annotation: 'pack2'.",
        	"3: Incorrect target: 'package' for annotation: 'pack3'."
        };
        
        verify(checkConfig, getPath("annotation" + File.separator + "ForbiAnnotationInput.java"), expected1);
	}
	
	@Test
	public void testParansAlways2() throws Exception {
		DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);
		
        checkConfig.addAttribute("annotation", "Edible,Author,Author2");
        checkConfig.addAttribute("target", "VARIABLE_DEF");
        
        final String[] expected2 = {
        		"12: Incorrect target: 'VARIABLE_DEF' for annotation: 'Edible'.",
                "19: Incorrect target: 'VARIABLE_DEF' for annotation: 'Author'.",
                "20: Incorrect target: 'VARIABLE_DEF' for annotation: 'Author2'."
        };
        
        verify(checkConfig, getPath("annotation" + File.separator + "ForbiAnnotationInput.java"), expected2);
	}
	
	@Test
	public void testParansAlways3() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);
        
        checkConfig.addAttribute("annotation", "Twizzle,One,Two,Three,B");
        checkConfig.addAttribute("target", "METHOD_DEF");
        
        final String[] expected3 = {
        	"27: Incorrect target: 'METHOD_DEF' for annotation: 'Twizzle'.",
        	"38: Incorrect target: 'METHOD_DEF' for annotation: 'One'.",
            "39: Incorrect target: 'METHOD_DEF' for annotation: 'Two'.",
            "40: Incorrect target: 'METHOD_DEF' for annotation: 'Three'.",
            "46: Incorrect target: 'METHOD_DEF' for annotation: 'B'."
        };
        
        verify(checkConfig, getPath("annotation" + File.separator + "ForbiAnnotationInput.java"), expected3);
	}
	
	@Test
    public void testParansAlways4() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);
        
        checkConfig.addAttribute("annotation", "Test,ctor,ctor2");
        checkConfig.addAttribute("target", "CLASS_DEF,CTOR_DEF");
        
        final String[] expected4 = {
        		"5: Incorrect target: 'CLASS_DEF' for annotation: 'Test'.",
            	"7: Incorrect target: 'CTOR_DEF' for annotation: 'ctor'.",
            	"8: Incorrect target: 'CTOR_DEF' for annotation: 'ctor2'."
        };
        
        verify(checkConfig, getPath("annotation" + File.separator + "ForbiAnnotationInput.java"), expected4);
    }
	
	@Test
    public void testParansAlways5() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);
        
        checkConfig.addAttribute("annotation", "Retention,Target");
        checkConfig.addAttribute("target", "ANNOTATION_DEF");
        
        final String[] expected5 = {
        		"33: Incorrect target: 'ANNOTATION_DEF' for annotation: 'Retention'.",
                "34: Incorrect target: 'ANNOTATION_DEF' for annotation: 'Target'."
        };
        
        verify(checkConfig, getPath("annotation" + File.separator + "ForbiAnnotationInput.java"), expected5);
    }
	
	@Test
    public void testParansAlways6() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);
        
        checkConfig.addAttribute("annotation", "MyAnnotation,A");
        checkConfig.addAttribute("target", "PARAMETER_DEF,INTERFACE_DEF");
        
        final String[] expected6 = {
        		"42: Incorrect target: 'PARAMETER_DEF' for annotation: 'MyAnnotation'.",
                "44: Incorrect target: 'INTERFACE_DEF' for annotation: 'A'.",
        };
        
        verify(checkConfig, getPath("annotation" + File.separator + "ForbiAnnotationInput.java"), expected6);
    }
	
	@Test
    public void testParansAlways7() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);
        
        checkConfig.addAttribute("annotation", "C,int1,int2,int3");
        checkConfig.addAttribute("target", "ENUM_DEF,ENUM_CONSTANT_DEF");
        
        final String[] expected7 = {
        		"49: Incorrect target: 'ENUM_DEF' for annotation: 'C'.",
                "51: Incorrect target: 'ENUM_CONSTANT_DEF' for annotation: 'int1'.",
                "53: Incorrect target: 'ENUM_CONSTANT_DEF' for annotation: 'int2'.",
                "55: Incorrect target: 'ENUM_CONSTANT_DEF' for annotation: 'int3'.",
        };
        
        verify(checkConfig, getPath("annotation" + File.separator + "ForbiAnnotationInput.java"), expected7);
    }
}
