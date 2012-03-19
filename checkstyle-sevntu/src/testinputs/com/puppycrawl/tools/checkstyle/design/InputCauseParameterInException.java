import project.ParentException;

public class InputCauseParameterInException extends ParentException
{

    public InputCauseParameterInException(String str)
    {
        super(str, null);
    }

    class MyException {
        
        public MyException()
        {
        }
        
        public MyException(String str)
        {
        }
        
        public MyException(String str, int integer)
        {
        }
        
        public MyException(String str, Exception ex) // Exception | Throwable only!
        {
        }

    }

    public InputCauseParameterInException(String str, int integer)
    {
        super(str, null);
    }

//    public InputCauseParameterInException(String str, Throwable ex)
//    {
//        super(str, ex);
//    }

    
    
}