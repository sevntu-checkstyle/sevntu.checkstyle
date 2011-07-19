public class InputMethodLimitCheck {
	   public static void main(String[] args) {
	    Runnable anonym = new Runnable() {
	      public void run() {
	      }
	    };
	  }
	}

class Main2 {
	   public static void main2(String[] args) {
	    Runnable anonym = new Runnable() {
	      public void run() {
	      }
	      public void run2() {
	      }
	      public void run3() {
	      }
	    
	    };
	  }
	}


class A1 {
	public void a() {
		
	}
	
	public void b() {
		
	}
	
	public void c() {
		
	}
	class A2 {
		public void a() {
			
		}
		
		public void b() {
			
		}
		
		public void c() {
			
		}

	}
}

class A3{
	public void a() {
		
	}
	public void b() {
		
	}
}

interface B {     
	    public void b(int c1);
	    public void c(int c1);
	    public void d(int c1);
	    public void e(int c1);
	    public void f(int c1);
}

enum Foo{
	A,B,C;

	void bar1(){}
	void bar2(){}
	void bar3(){}
		
}