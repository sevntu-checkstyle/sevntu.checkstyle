package com.github.sevntu.checkstyle.checks.coding;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
public class InputAvoidDefaultSerializableInInnerClassesCheck3 {
    public class Foo implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        public Date date;

        // start iterating methods with allowPartialImplementation == false
        // real readObject, hasRead will become true
        private void readObject(ObjectInputStream aInputStream)
                throws ClassNotFoundException, IOException {
            // always perform the default de-serialization first
            aInputStream.defaultReadObject();
            date = (Date) aInputStream.readObject();
        }

        // other public readObject - hasRead will become false
        public Foo readObject(String str) throws ParseException {
            Foo result = new Foo();
            result.date = new SimpleDateFormat().parse(str);
            return result;
        }

        // real writeObject, hasWrite will become true, but result will be
        // false, since hasRead is already false

        private void writeObject(ObjectOutputStream aOutputStream)
                throws IOException {
            aOutputStream.defaultWriteObject();
            aOutputStream.writeObject(date);
        }
    }
}
