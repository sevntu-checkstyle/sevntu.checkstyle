public class InputAvoidConstantsInInterfacesCheck 
{
	public static void main(String[] args) 
	{

	}
}

interface A 
{
	final int DD = 4;
	public static final int CC = 4;

}

interface B 
{
	interface X 
	{
		static int JJ = 4;
	}

	double BB = 4;
	static int FF = 2;
}

interface C 
{
	interface F 
	{
		public static int HH = 6;
	}
}

interface D 
{
	public static class F 
	{
		interface G 
		{
			public static int r = 9;
		}

		public static int HH = 6;
	}
}

