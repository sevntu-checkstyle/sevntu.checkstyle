package sevntu;

import com.puppycrawl.tools.checkstyle.api.AutomaticBean; // forbidden

public class InputForbidCertainImportsCheckSinglePackage
{
    public int a()
    {
        
        AutomaticBean smth = new com.puppycrawl.tools.checkstyle.api.AutomaticBean() { // forbidden!
            @Override
            protected void finishLocalSetup() {
            }
        };
        return 5;
    }
}
