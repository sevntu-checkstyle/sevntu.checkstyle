package com.github.sevntu.checkstyle.checks.annotation;

public class RequiredAnnotation{

   @SomeNote(thename="thevalue")
   public void logged() {
   }

   // the required property thename will be the second property here now
   @SomePackage.SomeNote(firstSomething="something", thename="theValue")
   public void notLogged() {
   }

   @SomePackage.SomeNote()
   public void missingName() {
   }

   @SomePackage.MultipleRequired(prop1="aa", prop2="bb")
   public void bothNames() {
   }

}
