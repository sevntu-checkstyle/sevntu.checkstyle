package com.github.sevntu.checkstyle.checks.coding;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
public class InputOverridableMethodInConstructorCheck12 {


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
        	 System.out.println("1"); // no warnings here
             doSmth(); // no warnings here
             this.doSmth(); // no warnings here
             Object1.this.doSmth(); // no warnings here
             Object1.doSmth(); // no warnings here
             Object2.doSmth3(); // no warnings here
        }

        private static void doSmth() {
            System.out.println("1");
            Object1.doSmth2();
        }

        private static void doSmth2() {
            System.out.println("2");
            Object1.doSmth3();
        }
        
        public static void doSmth3() {
            System.out.println("Bar!");
        }
        
        public static void println() {
            System.out.println("Bar!");
        }
    }


    private static class Object2 extends Object1 implements Serializable {

        static String value = "";

        public static void doSmth3() {
            value = "Foo!";
            System.out.println(value);
        }

        //@Override
        public void readObject(java.io.ObjectInputStream in)throws IOException, ClassNotFoundException {
            //doSmth();
        }

    }

}
