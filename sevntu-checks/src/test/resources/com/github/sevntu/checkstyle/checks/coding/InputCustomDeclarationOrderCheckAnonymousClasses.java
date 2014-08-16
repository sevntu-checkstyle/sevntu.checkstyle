package com.github.sevntu.checkstyle.checks.coding;
class InputCustomDeclarationOrderCheckAnonymousClasses {
    private Object i1 = new Object(){
        int getInt() {
            return 1;
        }
    };
    protected Object i2 = new Object(){
        int getInt() {
            return 1;
        }
    };
    Object i3 = new Object(){
        int getInt() {
            return 1;
        }
    };
    public Object i4 = new Object(){
        int getInt() {
            return 1;
        }
    };
    
    private int max = 3;
    public int min = 1;
    
    public InputCustomDeclarationOrderCheckAnonymousClasses() {
    }
    
    public void a() {
    }
}

class Errors_11 {
    //wrong order
    private int max = 3;
    public int min = 1;
    
    private Object i1 = new Object(){
        int getInt() {
            return 1;
        }
    };
}

class Errors_12 {
    private Object i1 = new Object(){
        int getInt() {
            return 1;
        }
    };
    //wrong order
    private double d1 = 4.6;
    private Object i2 = new Object(){
        int getInt() {
            return 1;
        }
    };
}

class Errors_31 {
    //wrong order
    public Errors_31() {
    }
    
    Object i = new Object() {
        int getInt() {
            return 34;
        }
    };
}

class Ok_210{
    private int k = 7;
    
    public void process(){
        Object i = new Object(){
            int getInt() {
                return 34;
            }
        };
        int y = 0;
    }
    
    public void submit(){
    }
}

class ErrorsInInnerClass_1 {
    public ErrorsInInnerClass_1 () {}
    public void simple() {}
    class Erros {
        // wrong order
        private int x;
        private Object i = new Object() {
            int getInt() {
                return 1;
            }
        };
        public void title() {}
    }
}

class SpacePadder {

    final static String[] SPACES = { " ", "  ", "    ", "        ", // 1,2,4,8
        // spaces
        "                ", // 16 spaces
        "                                " }; // 32 spaces

    final static public void leftPad(StringBuilder buf, String s, int desiredLength) {
      int actualLen = 0;
      if (s != null) {
        actualLen = s.length();
      }
      if (actualLen < desiredLength) {
        spacePad(buf, desiredLength - actualLen);
      }
      if (s != null) {
        buf.append(s);
      }
    }

    final static public void rightPad(StringBuilder buf, String s, int desiredLength) {
      int actualLen = 0;
      if (s != null) {
        actualLen = s.length();
      }
      if (s != null) {
        buf.append(s);
      }
      if (actualLen < desiredLength) {
        spacePad(buf, desiredLength - actualLen);
      }
    }
    
    /**
     * Fast space padding method.
     */
    final static public void spacePad(StringBuilder sbuf, int length) {
      while (length >= 32) {
        sbuf.append(SPACES[5]);
        length -= 32;
      }

      for (int i = 4; i >= 0; i--) {
        if ((length & (1 << i)) != 0) {
          sbuf.append(SPACES[i]);
        }
      }
    }
  }