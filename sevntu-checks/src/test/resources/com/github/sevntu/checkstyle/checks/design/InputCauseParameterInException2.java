import project.ParentException;

public class InputCauseParameterInException2 extends ParentException
{

    public InputCauseParameterInException2(String str)
    {
        super(str, null);
    }

    public InputCauseParameterInException2(String str, int integer)
    {
        super(str, null);
    }

//    public InputCauseParameterInException2(String str, Throwable ex)
//    {
//        super(str, ex);
//    }

    
    class MyException2 {
        
        public MyException2()
        {
        }
        
        public MyException2(String str)
        {
        }
        
        public MyException2(String str, int integer)
        {
        }
        
//        public MyException2(String str, Exception ex) // Exception | Throwable only!
//        {
//        }
        
    }   
    
}