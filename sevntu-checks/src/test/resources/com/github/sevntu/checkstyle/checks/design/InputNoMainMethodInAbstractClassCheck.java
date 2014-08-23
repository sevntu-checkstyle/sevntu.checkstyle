package com.github.sevntu.checkstyle.checks.design;

/**
 * Test Classes to NoMainMethodInAbstractClass check
 * @author baratali <a href="mailto:barataliba@gmail.com">email</a>
 *
 */

/**
 * Ordinary classes with main methods
 */
class NoAbs1{
    public static void main(String args[]){}
}
class NoAbs2{
    public static void main(String ... args){}
}

/**
 * Abstract classes with main methods
 */
abstract class Abs1{
    public static void main(String args[]){}
}
abstract class Abs2{
    public static void main(String ... args){}
}

/**
 * Inner abstract and ordinary classes(with main methods) in ordinary class  
 */
class OuterNoAbs1{
    static abstract class InnerAbs1{
        public static void main(String args[]){}
    }
    static abstract class InnerAbs2{
        public static void main(String ... args){}
    }
    static class InnerNoAbs1{
        public static void main(String args[]){}
    }
    static class InnerNoAbs2{
        public static void main(String ... args){}
    }
}

/**
 * Inner abstract classes(with main methods) in abstract class with its main method  
 */
abstract class OuterAbs1{
    public static void main(String[] args){}
    static abstract class InnerAbs1{
        public static void main(String[] args){}
    }
}
abstract class OuterAbs2{
    public static void main(String ... args){}
    static abstract class InnerAbs1{
        public static void main(String ... args){}
    }
}

/**
 * There are no main methods
 */
abstract class Abs3{
    public static int main(String[] args){
        return 1;
    }
}
abstract class Abs4{
    public void main(String[] args){}
}
abstract class Abs5{
    private static void main(String[] args){}
}
abstract class Abs6{
    protected static void main(String[] args){}
}
abstract class Abs7{
    static void main(String[] args){}
}
abstract class Abs8{
    public static void Main(String args[]){}
}
abstract class Abs9{
    public static void main(String arg){}
}
abstract class Abs10{
    public static void main(String args[], int i){}
}
abstract class Abs11{
    public static void main(int i, String ... args){}
}

/**
 * There are main methods
 */
abstract class Abs12{
    public static native void main(String[] args);
}
abstract class Abs13{
    public static synchronized void main(String[] args){}
}
abstract class Abs14{
    public static strictfp void main(String args[]){}
}
abstract class Abs15{
    public static final void main(String args[]){}
}

/**
 * There are main methods
 */
abstract class Abs16{
    public static native void main(String... args);
}
abstract class Abs17{
    public static synchronized void main(String... args){}
}
abstract class Abs18{
    public static strictfp void main(String... args){}
}
abstract class Abs19{
    public static final void main(String... args){}
}

abstract class Abs20{
    public static void qwerty(String args[]){}
    public static void main(String args[]){}
    public static void asdfg(String args[]){}
}
abstract class Abs21{
    abstract void method();
    final void m(){}
    public static void main(String ... args){}
}

/**
 * There are no main methods
 */
abstract class Abs22{
    public static void main(){};
}

/**
 * There are main methods
 */
abstract class Abs23{
   static abstract class Abs{
       public static void main(String args[]){}
       void met(){}
       void u(){}
   }
   public static void main(String[] args){}
}

class NoAbsClass{
    public void met1() {
    }
    public void met2() {
    }
    
    static abstract class InnerAbs1{
        public void innermet1(){};
        public static void main(String[] args){}
        public void innermet2(){};
    }
    
    public void met3(){};
    public void met4(){};
    
    static abstract class InnerAbs2{
        public void innermet3(){};
        public static void main(String[] args){}
        public void innermet4(){};
    }
    
    public void met5(){};
    public void met6(){};
}

interface Int1{
    static abstract class Abs{
        public static void main(String... args){}
    }
}