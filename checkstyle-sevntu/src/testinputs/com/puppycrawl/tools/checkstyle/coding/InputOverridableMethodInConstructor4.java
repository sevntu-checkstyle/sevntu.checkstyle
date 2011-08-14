package com.puppycrawl.tools.checkstyle.coding;

public class InputOverridableMethodInConstructor4 implements Cloneable {
   private int a;
   private SomeData someData = null;

   public InputOverridableMethodInConstructor4(final int value, final SomeData prop){
       setA(value);
       setSomeData(prop);
    }
   
   public Object clone(){
      Object result = null;
      try{
         result = super.clone();
      } catch (CloneNotSupportedException e){
         e.printStackTrace();
      }
      return result;
   }
   
   
   public void setA(int a){
      this.a = a;
   }
   
   public int getA(){
      return a;
   }
   
   public void setSomeData(SomeData someData){
      this.someData = someData;
   }
   
   public SomeData getSomeData(){
      return someData;
   }
      
   
   
   public class SomeData implements Cloneable{
       private int someInt;
       
       public SomeData(final int data){
           someInt = data;
        }
       
       public Object clone(){
          Object result = null;
          try{
             result = super.clone();
          } catch (CloneNotSupportedException e){
             e.printStackTrace();
          }
          return result;
       }

       public void setSomeInt(int someInt){
          this.someInt = someInt;
       }

       public int getSomeInt(){
          return someInt;
       }
    }
   
   
}

