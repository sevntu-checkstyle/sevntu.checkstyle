package com.github.sevntu.checkstyle.checks.coding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class InputOverridableMethodInConstructorCheck14 {


	public static void main(String args[]) throws IOException, ClassNotFoundException {
		try {
			File file = new File("tmp.bla");

			ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(file));
			outStream.writeObject(new Object2());

			ObjectInputStream inStream = new ObjectInputStream(new FileInputStream(file));
			System.out.println(((Object2) inStream.readObject()).value);

			file.delete();

		} catch (Exception e) {
		}
	}


	private static class Object1 implements Serializable {

		public void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
             doSmth(); // ! a warning here
		}

		public void doSmth() {
			System.out.println("Bar!");
		}
	}


	private static class Object2 extends Object1 {

		String value;

		public void doSmth() {
			value = "Foo!";
			System.out.println("Foo!");
		}

		final void doSmth2() {
			value = "Foo!";
			System.out.println("Foo!");
            doSmth();
		}

		//@Override
		public void readObject(java.io.ObjectInputStream in)throws IOException, ClassNotFoundException {	
            doSmth(); // ! a warning here
            this.doSmth(); // ! a warning here			
            doSmth2(); // ! a warning here
            this.doSmth2(); // ! a warning here
		}

		private class Object3 extends Object2 {

			String value;

			public void doSmth() {
				value = "Foo!";
				System.out.println("Foo!");
			}

			//@Override
			public void readObject(java.io.ObjectInputStream in)throws IOException, ClassNotFoundException {	
                doSmth(); // ! a warning here
                this.doSmth(); // ! a warning here

                doSmth2(); //  ! a warning here
                this.doSmth2(); //  ! a warning here
			}
		
	}

}
}
