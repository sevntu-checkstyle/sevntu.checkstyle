package TestClasses;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import javax.xml.crypto.Data;

public class TestCloneableImplementsMagic {

	public static void main(String args[]) throws IOException,
			ClassNotFoundException, CloneNotSupportedException {
		(new Object3()).clone();
	}

	private static class Object1 implements Cloneable, Data, FileFilter {

		public void doSmth() {
			System.out.println("Bar!");
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
			((Object1) clone).doSmth(); // !
			//doSmth2(); // !
			return clone;
		}

		@Override
		public boolean accept(File pathname) {
			return false;
		}

	}

	private static class Object2 extends Object1 implements Data {
	}


	private static class Object3 {
		private String value;

		public void doSmth() {
			System.out.println(value);
		}


	}

}