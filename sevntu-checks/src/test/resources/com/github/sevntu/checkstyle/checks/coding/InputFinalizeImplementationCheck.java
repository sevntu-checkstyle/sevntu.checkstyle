package com.github.sevntu.checkstyle.checks.coding;
class NormalFinalizer {
    
    public static void doStuff()
    {
        //This method do some stuff 
    }
    protected void finalize() throws Throwable
    {
        try {
            doStuff(); 
        }
        finally {
            super.finalize();
        }
    }
}

//negates effect of superclass finalizer
class EmptyFinalizer {
    
    protected void finalize() throws Throwable
    {
        //empty finalize ()
    }
}

//fails to call superclass finalize method
class WithoutTryCatchFinalizer {
    
    public static void doStuff() 
    {
        //This method do some stuff 
    }
    protected void finalize() throws Throwable
    {
        doStuff();
    }
}

//public finalizer
class PublicFinalizer {
    
    public static void doStuff()
    {
        //This method do some stuff 
    }
    public void finalize() throws Throwable
    {
        try {
            doStuff(); 
        }
        finally {
            super.finalize();
        }
    }
}

//unless (or worse) finalizer 
class SuperFinalizer {
    
    protected void finalize() throws Throwable
    {
        super.finalize();
    }
}

//public finalizer
class PublicFinalizer1 {
  
  public static void doStuff()
  {
      //This method do some stuff 
  }
  protected void finalize() throws Throwable
  {
      try {
          doStuff(); 
      }
      finally {
    	  
      }
  }
}

class TestNoFinal {
    public static void doStuff()
    {
    }
    protected void finalize() throws Throwable
    {
        try {
            doStuff(); 
        }
        catch(Exception e) {
        }
    }
}

interface TestNotFinalize {
    int finalize() throws Throwable;
    void finalize(int n) throws Throwable;
    void someOtherMethod();
}