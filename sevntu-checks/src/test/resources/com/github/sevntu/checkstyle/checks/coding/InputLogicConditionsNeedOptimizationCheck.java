
public class InputLogicConditionsNeedOptimizationCheck
{
	private boolean field1;
	
	private boolean returnTrue()
	{
		return true;
	}
	
	private boolean returnFalse()
	{
		return false;
	}
	
	private void forCyclesCheck()
	{
		boolean bTrue = true;
		boolean bFalse = false;
		int a = 3, b = 5, c = 8, d =0;
		d = a * b + c - b;
		for(;bTrue;){}
		for(;bTrue && bFalse;){}
		for(;bTrue && bFalse && returnTrue() && returnFalse();){}
		Testing test1 = new Testing(); 
		for(;!test1.returnTrue() && bFalse;){}	//!!
		for(;returnTrue() && returnFalse();){}
		for(;!returnTrue() && field1 || field1 && a + b - c == d;){} //!!
		for(;field1 && returnTrue() || field1 && returnTrue();){}
	}
	private void whileCyclesCheck()
	{
		boolean bTrue = true;
		boolean bFalse = false;
		while(bTrue);
		while(bTrue && bFalse);
		while(bTrue && bFalse && returnTrue() && returnFalse());
		while(returnTrue() && bFalse);	//!!
		while(returnTrue() && returnFalse());
		while(returnTrue() && returnFalse() && field1); //!!
		
		do{}while(bTrue);
		do{}while(bTrue && bFalse);
		do{}while(bTrue && bFalse && returnTrue() && returnFalse());
		do{}while(returnTrue() && bFalse);	//!!
		do{}while(returnTrue() && returnFalse());
		do{}while(returnTrue() && returnFalse() && field1); //!!
	}
	
	private void ifCheck()
	{
		boolean bTrue = true;
		boolean bFalse = false;
		if(bTrue){}
		if(bTrue && bFalse){}
		if(bTrue && bFalse && returnTrue() && returnFalse()){}
		if(returnTrue() && bFalse){}	//!!
		if(returnTrue() && returnFalse()){}
		if(returnTrue() && returnFalse() && field1){} //!!
		if(returnFalse() && field1 && 1 + 2 - 3 == 0 || field1 && returnTrue()){} //!!
		if (comp.isFocusable() && (!(comp instanceof Container) || ((Container) comp).getLayout() == null)) {
            return comp;
        }
		if (Boolean.TRUE.equals(getClientProperty("JTable.autoStartsEdit"))
                && (ks.getModifiers() & KeyEvent.CTRL_MASK) != 0
                && (ks.getKeyCode() == KeyEvent.VK_S || ks.getKeyCode() == KeyEvent.VK_W)) {
            return false;
        }
		if (!LdHelper.isSystemName(table.getName())
                && (cliTable == null
                || !PermHelper.nullOrEqual(cliTable.getPermission(), table.getPermission()))) {

            forceReloadMetadata = true;
        }
		 if (refCol != null && refCol.getTable() != null
                 && (tableName == null || tableName.equals(refCol.getTable().getName()))) {}
		 if (!LdHelper.isSystemName(table.getName()) && (cliTable == null || false)) {

	            forceReloadMetadata = true;
	        }
		 if(returnTrue() && field1
		         || (field2 && returnFalse() && field1)){}
		 if(returnTrue() && 
		         (field2 && returnFalse() && field1)){}
		 if(returnTrue() && 
                 (field2 && field1 && returnFalse())){}
		 if(returnTrue() && 
                 (field2 && field1)){}
	}
	
	public class Testing
	{
	    boolean returnTrue()
	    {
	        return true;
	    }
	}
}
