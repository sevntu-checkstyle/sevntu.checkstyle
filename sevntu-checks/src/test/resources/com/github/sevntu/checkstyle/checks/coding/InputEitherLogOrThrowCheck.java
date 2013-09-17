import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputEitherLogOrThrowException
{
    private static Logger slfLogger = LoggerFactory.getLogger(App.class);
    private org.slf4j.Logger anotherLogger = LoggerFactory.getLogger(App.class);;
    
    public void get1()
            throws Exception
    {
        try {
            throw new Exception();
        }
        catch (Exception e) { // warning
            slfLogger.error("Exception: ", e);
            throw e;
        }
    }
    
    public void get2()
            throws Exception
    {
        try {
            throw new Exception();
        }
        catch (Exception e) { // warning
            slfLogger.warn("Exception: ", e);
            throw e;
        }
    }
    
    public void get3()
            throws Exception
    {
        try {
            throw new Exception();
        }
        catch (Exception e) { // warning
            anotherLogger.warn("Exception: ", e);
            throw e;
        }
    }
    
    public void get4()
            throws Exception
    {
        try {
            throw new Exception();
        }
        catch (Exception e) { // NO warning
            anotherLogger.warn("Exception");
            throw e;
        }
    }

    public void get5()
            throws Exception
    {
        try {
            throw new Exception();
        }
        catch (Exception e) { // NO warning
            Integer number = new Integer(123);
            slfLogger.error("Number {}", number);
            throw e;
        }
    }

    public void get6()
            throws Exception
    {
        try {
            throw new Exception();
        }
        catch (Exception e) { // warning
            Integer num = new Integer(4);
            slfLogger.error("Number1: {}", num);
            slfLogger.error("Exception", e);
            slfLogger.error("Text");
            slfLogger.error("Number2: {}", num);
            throw e;
        }
    }
    
    public void get7() throws Exception {
        try {
            get1();
        } catch (Exception e) { //warning
            slfLogger.error("Exception: ", e.getMessage());
            throw e;
        }
    }
    
    public void get8() throws Exception {
        try {
            throw new Exception();
        } catch (Exception e) { // warning
            slfLogger.error("Exception", e);
            throw new SQLException("SQL Error", e);
        }
    }
    
    public void get9() throws Exception {
        try {
            throw new Exception();
        } catch (Exception e) { //warning
            Logger log = LoggerFactory.getLogger(App.class);
            log.error("Exception", e);
            throw e;
        }
    }
    
    public void get10() throws Exception {
        try {
            throw new Exception();
        } catch(Exception e) { // warning
            Logger log1 = LoggerFactory.getLogger(App.class);
            Logger log2 = LoggerFactory.getLogger(App.class);
            Logger log3 = LoggerFactory.getLogger(App.class);
            log2.error("Exception", e);
            throw e;
        }
    }
    
    public void get11() throws Exception {
        try {
            throw new Exception();
        } catch (Exception e) { // NO warning
            Logger log = LoggerFactory.getLogger(App.class);
            log.error("Message");
            throw e;
        }
    }
    
    public void get11() throws Exception {
        try {
            throw new Exception();
        } catch (Exception e) { // NO warning
            StringBuilder builder = new StringBuilder();
            builder.append(e);
            throw e;
        }
    }
    
    public void get12() throws SQLException{
        try {
            throw new Exception();
        }
        catch (Exception e) { // warning
            slfLogger.error("Exception", e);
            SQLException newException = new SQLException(e);
            throw newException;
        }
    }
    
    public void get13() {
        try {
            throw new Exception();
        } catch (Exception e) { // warning
            slfLogger.error("Exception", e);
            RuntimeException ex = new RuntimeException("Oh", e);
            throw ex;
        }
    }
    
    public void get14() throws SQLException{
        try {
            throw new Exception();
        }
        catch (Exception e) { // NO warning
            slfLogger.error("Exception", e);
            SQLException newException = new SQLException("abc");
            throw newException;
        }
    }
    
    public void get15() throws SQLException{
        try {
            throw new Exception();
        }
        catch (Exception e) { // NO warning
            slfLogger.error("Exception", e);
            RuntimeException ex = new RuntimeException(e);
            SQLException newException = new SQLException("abc");
            throw newException;
        }
    }
    
    public void get16() throws SQLException{
        try {
            throw new Exception();
        }
        catch (Exception e) { // NO warning
            slfLogger.error("Exception", e);
            throw new Exception();
        }
    }
    
    public void get17() throws Exception {
        try{
            throw new Exception();
        } catch (Exception e) { // warning
            slfLogger.error(getString(), e);
            throw e;
        }
    }
    
    public void get18() throws Exception {
        try{
            throw new Exception();
        } catch (Exception e) { //NO warning
            slfLogger.error(getString());
            throw e;
        }
    }
    
    public void get19() throws Exception {
        try {
            get1();
        }
        catch (Exception e) { // warning
            e.printStackTrace();
            throw e;
        }
    }
    
    public void get20() throws Exception {
        try {
            get1();
        }
        catch (Exception e) { // warning
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public void get21() throws Exception {
        Logger logger = LoggerFactory.getLogger("MyClass");
        try {
            get1();
        }
        catch (Exception e) { // warning
            logger.error("A", e);
            throw e;
        }
    }
    
    public void get22(Integer i, Logger loggerFromParameter, Double d) throws Exception {
        try {
            get1();
        }
        catch (Exception e) { // warning
            loggerFromParameter.error("A", e);
            throw e;
        }
    }
////////////////////////////////////////////////////////////////////////////////
// Check can't detect these situations:
////////////////////////////////////////////////////////////////////////////////
    
    public void get12() throws Exception {
        try{
            throw new Exception();
        } catch (Exception e) { //warning
            slfLogger.error("Exception", e);
            try {
                slfLogger.info("Checking exception conditions...");
            } catch(Exception anotherException) {
                throw e;
            }
        }
    }
    
    public void get13() throws Exception {
        try{
            boolean flag1 = getRandomBoolean();
            boolean flag2 = getRandomBoolean();
            throw new Exception();
        } catch (Exception e) { //warning
            slfLogger.error("Exception", e);
            if (flag1) {
                throw e;
            } else if(flag2) {
                throw new Runtime(e);
            } else {
                slfLogger.debug("OK");
            }
        }
    }
    
    public void get15() throws Exception {
        try {
            get1();
        } catch (Exception e) {
            slfLogger.error("Exception", e);
            try {
                get2();
            } catch (Exception anotherException) {
                throw anotherException;
            }
        }
    }

}
