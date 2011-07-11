@pack1
@pack2
@pack3
package com.puppycrawl.tools.checkstyle.checks.annotation;
@Test
public class ForbiAnnotationInput {
	@ctor
	@ctor2
	ForbiAnnotationInput(){
		
	}
	@Edible(true)
	  Item item = new Carrot();
	 
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

}
