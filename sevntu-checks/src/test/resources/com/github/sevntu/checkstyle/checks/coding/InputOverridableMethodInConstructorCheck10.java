package com.github.sevntu.checkstyle.checks.coding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class InputOverridableMethodInConstructorCheck10 {


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

		// final
		private void doSmth() {
			System.out.println("Bar!");
		}
	}


	static class Object2 extends com.github.sevntu.checkstyle.checks.coding.InputOverridableMethodInConstructorCheck10.Object1 implements java.io.Serializable {

		String value;

		private void doSmth() {
			value = "Foo!";
			System.out.println("Foo!");
		}

		public void readObject(java.io.ObjectInputStream in)throws IOException, ClassNotFoundException {
			doSmth();
		}

	}

}
