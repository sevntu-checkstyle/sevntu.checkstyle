class Ok_1 {
    private Integer i1 = new Integer(){
        int getInt() {
            return 1;
        }
    };
    protected Integer i2 = new Integer(){
        int getInt() {
            return 1;
        }
    };
    Integer i3 = new Integer(){
        int getInt() {
            return 1;
        }
    };
    public Integer i4 = new Integer(){
        int getInt() {
            return 1;
        }
    };
    
    private int max = 3;
    public int min = 1;
    
    public Ok() {
    }
    
    public void a() {
    }
}

class Errors_1 {
    //wrong order
    private int max = 3;
    public int min = 1;
    
    private Integer i1 = new Integer(){
        int getInt() {
            return 1;
        }
    };
}

class Errors_2 {
    private Integer i1 = new Integer(){
        int getInt() {
            return 1;
        }
    };
    //wrong order
    private double d1 = 4.6;
    private Integer i2 = new Integer(){
        int getInt() {
            return 1;
        }
    };
}

class Errors_3 {
    //wrong order
    public Errors_3() {
    }
    
    Integer i = new Integer() {
        int getInt() {
            return 34;
        }
    };
}

class Ok_2 {
    private int k = 7;
    
    public void process(){
        Integer i = new Integer(){
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
        private Integer i = new Integer() {
            int getInt() {
                return 1;
            }
        };
        public void title() {}
    }
}

public class SpacePadder {

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
