package com.puppycrawl.tools.checkstyle.naming;

abstract public class IIIInputAbstractClassName {
}

abstract class NonAAAAbstractClassName {
}

abstract class FactoryWithBADNAme {
}

abstract class AbstractCLASSName {
    abstract class NonAbstractInnerClass {
    }
}

abstract class ClassFactory {
    abstract class WellNamedFactory {
    }
}

class NonAbstractClass {
}

class AbstractClass {
}

class Class1Factory {
}

abstract class AbstractClassName2 {
    class AbstractINNERClass {
    }
}

abstract class Class2Factory {
    class WellNamedFACTORY {
    	public void marazmaticMETHODName() {
    		int marazmaticVARIABLEName = 2;
    		int MARAZMATICVariableName = 1;
    	}
    }
}

public interface Directions {
  int RIGHT=1;
  int LEFT=2;
  int UP=3;
  int DOWN=4;
}

interface BadNameForInterfeis
{
   void interfaceMethod();
}