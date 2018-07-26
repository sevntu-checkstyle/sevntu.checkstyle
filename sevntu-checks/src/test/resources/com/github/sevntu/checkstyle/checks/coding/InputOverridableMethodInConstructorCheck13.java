package com.github.sevntu.checkstyle.checks.coding;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import javax.xml.crypto.Data;

public class InputOverridableMethodInConstructorCheck13 {

	public static void main(String args[]) throws IOException,
			ClassNotFoundException, CloneNotSupportedException {
		(new Object3()).clone();
	}

	static class Object1 implements Cloneable, Data, FileFilter {

		public void doSmth() {
			System.out.println("Bar!");
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
            ((Object1) clone).doSmth(); // ! a warning here
            accept(new File("1.txt")); // ! a warning here
            this.accept(new File("1.txt")); // ! a warning here
            Object1.this.accept(new File("1.txt")); // ! a warning here
            accept2(new File("1.txt")); // no warnings here
            return clone;
		}

		
		public boolean accept(File pathname) {
			return false;
		}		

		final boolean accept2(File pathname) {
			return false;
		}

	}

	static class Object2 extends Object1 implements Data {
		@Override
		public void doSmth() {
			System.out.println("Bar!");
		}
	}
	

	static class Object3 extends Object2 { // implements Serializable 
		private String value;

		@Override
		public void doSmth() {
			System.out.println(value);
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
            ((Object2) clone).doSmth(); // ! a warning here
			return clone;
		}
	}

	static class Object4 {

		public void doSmth() {
		}

		protected Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
			((Object2) clone).doSmth(); // ! no warning here
			return clone;
		}
	}
}
