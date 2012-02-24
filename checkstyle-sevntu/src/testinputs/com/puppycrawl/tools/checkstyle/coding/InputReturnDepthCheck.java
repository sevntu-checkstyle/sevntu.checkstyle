public class InputReturnDepthCheck
{

    public int a = 0;

    // 6 "returns", max depth is 2
    public InputReturnDepthCheck()
    {
        int a = this.hashCode();
        if (a != 0) {
            switch (a) {
            case 1:
                return a;
                break;
            case 2:
                return a;
                break;
            case 3:
                return a;
                break;
            case 4:
                return a;
                break;
            case 5:
                return a;
                break;
            }
            return a;
        }
    }

    
    public InputReturnDepthCheck(int x) {      
        if(true){
            return; // will be ignored until ignoreEmptyReturns options checked  
        } else {
            return; // will be ignored until ignoreEmptyReturns options checked  
        }        
    }
 
    //exclusive test for "try-catch block processing"
    public int nm()
    {
        if (true) {
            try {
                return 5; // depth is 2
            }
            catch (Exception e) {
                if (true)
                    return 5; // depth is 3
            }
        }
        return mCurReturnCount; // depth is 0
    }
    
}
