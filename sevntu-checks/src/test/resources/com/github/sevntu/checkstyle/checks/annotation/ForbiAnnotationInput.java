package com.github.sevntu.checkstyle.checks.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Test
public class ForbiAnnotationInput {
    @ctor
    @ctor2
    ForbiAnnotationInput(){
        
    }
    @Edible(true)
      Item item = (Item) new Carrot();
     
      public @interface Edible {
        boolean value() default false;
      }
     
      @Author(first = "Oompah", last = "Loompah")
      @Author2
      Book book = new Book();
     
      public @interface Author {
        String first();
        String last();
      }
      @Twizzle
      public void toggle() {
      }
     
      public @interface Twizzle {
      }
      @Retention(RetentionPolicy.RUNTIME) 
      @Target({ElementType.METHOD})       
      public @interface Tweezable {
      }
      
      @One
      @Two
      @Three
      public static void doSomethingElse(
                @MyAnnotation(name="aName", value="aValue") String parameter){
          }
      @A
      public interface BaseInterface {
        @B
        public void method11();
      }
      @C
       public enum Numbers {  
           @int1(numericValue = 1)  
           ONE,  
           @int2(numericValue = 2)  
           TWO,  
           @int3(numericValue = 3)  
           THREE;  
           }  
      @SuppressWarnings("unused") int i = 0;
      
      interface Item {
          
      }
      class Carrot {
          
      }
      class Book {
          
      }
}