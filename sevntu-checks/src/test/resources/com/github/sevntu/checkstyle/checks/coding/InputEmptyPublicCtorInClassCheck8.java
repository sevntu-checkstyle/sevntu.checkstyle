package com.github.sevntu.checkstyle.checks.coding;

import org.junit.runner.RunWith;
import org.junit.*;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


public class InputEmptyPublicCtorInClassCheck8
{
    //RunWith is imported with single type import.
    @RunWith(Parameterized.class)
    class Inner1 {
        public Inner1() {
            
        }
    }
    
    //Ignore is imported with on demand import org.junit.*
    @Ignore
    class Inner2 {
        public Inner2() {
            
        }
    }
    
    //AnnotationName is declared in InputEmptyPublicCtorInClass (this package)
    @AnnotationName
    class Inner3 {
        public Inner3() {
            
        }
    }
    
    //This is case of fully qualified annotation name 
    @com.github.sevntu.checkstyle.checks.coding.AnnotationName
    class Inner4 {
        public Inner4() {
            
        }
    }
    
    @InputEmptyPublicCtorInClassCheck9.InnerAnnotation
    class Inner5 {
        public Inner5() {
            
        }
               
    }
}
