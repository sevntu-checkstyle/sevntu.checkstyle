package com.github.sevntu.checkstyle.checks.coding;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
public class InputAvoidDefaultSerializableInInnerClasses2
{
    public class Foo implements Serializable {

        public Date date;

//      real readObject is commented
//      private void readObject(ObjectInputStream aInputStream)
//              throws ClassNotFoundException, IOException {
//          // always perform the default de-serialization first
//          aInputStream.defaultReadObject();
//          date = (Date) aInputStream.readObject();
//      }
        //there should be warning, though method is both private and named readObject
        private Foo readObject(String str)  throws  ParseException {
            Foo result = new Foo();
            result.date = new SimpleDateFormat().parse(str);
            return result;
        }
    }
}