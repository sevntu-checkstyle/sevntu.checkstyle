package com.github.sevntu.checkstyle.checks.annotation;

public class InputRequiredParameterForAnnotationCheck
{
   //methods
   @testAnnotation1()
   public void method1() {
   }
   
   @testAnnotation1(firstParameter="thevalue", secondParameter="thevalue")
   public void method2() {
   }
   
   @testAnnotation1(firstParameter="thevalue", secondParameter="thevalue", thirdParameter="thevalue")
   public void method3() {
   }
   
   @testAnnotation1(firstParameter="thevalue")
   public void method4() {
   }

   @com.github.sevntu.checkstyle.checks.annotation.InputRequiredParameterForAnnotationCheck
   .testAnnotation2(par1=1, par2=2)
   public void method5() {
   }
   
   @com.github.sevntu.checkstyle.checks.annotation.InputRequiredParameterForAnnotationCheck
   .testAnnotation2()
   public void method6() {
   }
   
   //classes
   @testAnnotation1()
   class Class1 
   {
   }
   
   @testAnnotation1(firstParameter="thevalue", secondParameter="thevalue")
   class Class2 
   {
   }
   
   @testAnnotation1(firstParameter="thevalue", secondParameter="thevalue", thirdParameter="thevalue")
   class Class4 
   {
   }
   
   @testAnnotation1(firstParameter="thevalue")
   class Class5 
   {
   }

   @com.github.sevntu.checkstyle.checks.annotation.InputRequiredParameterForAnnotationCheck
   .testAnnotation2(par1=1, par2=2)
   class Class6 
   {
   }
   
   @com.github.sevntu.checkstyle.checks.annotation.InputRequiredParameterForAnnotationCheck
   .testAnnotation2()
   class Class7 
   {
   }
   
   //fields
   @testAnnotation1()
   public int var1;
   
   @testAnnotation1(firstParameter="thevalue", secondParameter="thevalue")
   public int var2;
   
   @testAnnotation1(firstParameter="thevalue", secondParameter="thevalue", thirdParameter="thevalue")
   public int var3;
   
   @testAnnotation1(firstParameter="thevalue")
   public int var4;

   @com.github.sevntu.checkstyle.checks.annotation.InputRequiredParameterForAnnotationCheck
   .testAnnotation2(par1=1, par2=2)
   public int var5;
   
   @com.github.sevntu.checkstyle.checks.annotation.InputRequiredParameterForAnnotationCheck
   .testAnnotation2()
   public int var6;
   
   //interfaces
   @testAnnotation1()
   interface interface1 {
   }
   
   @testAnnotation1(firstParameter="thevalue", secondParameter="thevalue")
   interface interface2 {
   }
   
   @testAnnotation1(firstParameter="thevalue", secondParameter="thevalue", thirdParameter="thevalue")
   interface interface3 {
   }
   
   @testAnnotation1(firstParameter="thevalue")
   interface interface4 {
   }

   @com.github.sevntu.checkstyle.checks.annotation.InputRequiredParameterForAnnotationCheck
   .testAnnotation2(par1=1, par2=2)
   interface interface5 {
   }
   
   @com.github.sevntu.checkstyle.checks.annotation.InputRequiredParameterForAnnotationCheck
   .testAnnotation2()
   interface interface6 {
   }
   
   //enums
   @testAnnotation1()
   enum enum1 {
   }
   
   @testAnnotation1(firstParameter="thevalue", secondParameter="thevalue")
   enum enum2 {
   }
   
   @testAnnotation1(firstParameter="thevalue", secondParameter="thevalue", thirdParameter="thevalue")
   enum enum3 {
   }
   
   @testAnnotation1(firstParameter="thevalue")
   enum enum4 {
   }

   @com.github.sevntu.checkstyle.checks.annotation.InputRequiredParameterForAnnotationCheck
   .testAnnotation2(par1=1, par2=2)
   enum enum5 {
   }
   
   @com.github.sevntu.checkstyle.checks.annotation.InputRequiredParameterForAnnotationCheck
   .testAnnotation2()
   enum enum6 {
   }

   
   @interface testAnnotation1
   {
       String firstParameter() default "1";
       String secondParameter() default "2";
       String thirdParameter() default "3";
   }
   
   @interface testAnnotation2
   {
       int par1() default 1;
       int par2() default 2;
       int par3() default 3;
       int par4() default 4;
   }
   
}
