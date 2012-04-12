import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class InputAvoidDefaultSerializableInInnerClasses implements Serializable
{
	private int field1;
	private String field2 = new String();

	InputAvoidDefaultSerializableInInnerClasses(int a, int b, String s)
	{
		this.field1 = a;
		this.field2 = s;
		final class test1 implements Serializable
		{
			private void readObject(ObjectInputStream s)
			{

			}

			private void writeObject(ObjectOutputStream s)
			{

			}
		}
	}

	private class justEmpty
	{

	}

	private class Error1 extends justEmpty implements Serializable // error1
	{
	}

	private class NotError1 implements Runnable
	{
		@Override
		public void run()
		{
		}
	}

	private class Error2x implements Serializable	//error2x
	{
		private void readObject(ObjectInputStream s)
		{
		}
	}

	private class Error3x implements Serializable	//error3x
	{
		private void writeObject(ObjectOutputStream s)
		{
		}
	}

	private class Error2 implements Serializable // error2
	{
		void readObject(ObjectInputStream s, int a)
		{
		}
	}

	private class Error3 
	implements Serializable // error3
	{
		void writeObject()
		{
		}
	}

	private class Error4 implements Serializable // error4
	{

		void readObject()
		{
		}

		class OLOL
		{

		}

		void writeObject()
		{
		}
	}

	private class ErrorContainer1
	{
		private void method()
		{
		}

		class Error5 implements Serializable // error5
		{
		}
	}

	private class ErrorContainer2
	{
		private class BI implements Serializable // error6
		{

			void readObject()
			{
			}
		}

		private void method()
		{
		}
	}

	public interface EmptyInterface
	{
	}

	private class Error7 implements EmptyInterface, Serializable, Runnable // error7
	{

		@Override
		public void run()
		{
		}

		void readObject()
		{
		}
	}

	private class Error8 implements Runnable, Serializable // error8
	{

		@Override
		public void run()
		{
		}
	}

	private void method()
	{
		final class Error9 implements Serializable // error9
		{
		}
		;
	}

	static class Error10 implements Serializable // error10
	{
		public void writeObject(ObjectOutputStream s)
		{

		}

		public void readObject(ObjectInputStream s)
		{

		}
	}
}