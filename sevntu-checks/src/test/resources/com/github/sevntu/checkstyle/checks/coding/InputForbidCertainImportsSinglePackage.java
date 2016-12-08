package sevntu;

import com.puppycrawl.tools.checkstyle.api.AutomaticBean; // forbidden

public class InputForbidCertainImportsSinglePackage
{
    public int a()
    {
        
        AutomaticBean smth = new com.puppycrawl.tools.checkstyle.api.AutomaticBean(); // forbidden!
        return 5;
    }
}
