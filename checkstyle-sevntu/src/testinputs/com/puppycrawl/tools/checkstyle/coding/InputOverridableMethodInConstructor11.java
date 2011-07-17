package TestClasses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class InputOverridableMethodInConstructor11 {


<<<<<<< HEAD
    public static void main(String args[]) throws IOException, ClassNotFoundException {

        File file = new File("tmp.bla");

        ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(file));
        outStream.writeObject(new Object2());

        ObjectInputStream inStream = new ObjectInputStream(new FileInputStream(file));
        System.out.println(((Object2)inStream.readObject()).value);
        
        file.delete();
    }


    private static class Object1 implements Serializable {

        public void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
             doSmth(); // no warnings here
        }

        public void doSmth() {
            System.out.println("Bar!");
        }
    }


    private static class Object2 extends Object1 implements Serializable {

        String value;

        public void doSmth() {
            value = "Foo!";
            System.out.println(value);
        }

        //@Override
        public void readObject(java.io.ObjectInputStream in)throws IOException, ClassNotFoundException {
            //doSmth();
        }

    }

}
=======
	public static void main(String args[]) throws IOException, ClassNotFoundException {

		File file = new File("tmp.bla");

		ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(file));
		outStream.writeObject(new Object2());

		ObjectInputStream inStream = new ObjectInputStream(new FileInputStream(file));
		System.out.println(((Object2)inStream.readObject()).value);
		
		file.delete();
	}


	private static class Object1 implements Serializable {

		public void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
			 doSmth(); // no warnings here
		}

		public void doSmth() {
			System.out.println("Bar!");
		}
	}


	private static class Object2 extends Object1 implements Serializable {

		String value;

		public void doSmth() {
			value = "Foo!";
			System.out.println(value);
		}

		//@Override
		public void readObject(java.io.ObjectInputStream in)throws IOException, ClassNotFoundException {
			//doSmth();
		}

	}

}
>>>>>>> 0f2bb80789b144152f5dd13fa0eb07137c73de0e
