import java.io.Serializable;


public class InputAvoidDefaultSerializableInInnerClasses
{
	private class A
	{
	}

	private class E1 implements Serializable
	{
	}

	private class A1 implements Runnable
	{
		@Override
		public void run()
		{
		}
	}

	private class A2 implements Serializable
	{
		void readObject()
		{
		}
	}

	private class A3 implements Serializable
	{
		void writeObject()
		{
		}
	}

	private class A4 implements Serializable
	{

		void readObject()
		{
		}

		void writeObject()
		{
		}
	}

	private class B
	{
		class BI implements Serializable
		{
		}
	}

	private class B1
	{
		private class BI implements Serializable
		{

			void readObject()
			{
			}
		}
	}

	private class C implements Serializable, Runnable
	{

		@Override
		public void run()
		{
		}

		void readObject()
		{
		}
	}

	private class D implements Serializable, Runnable
	{

		@Override
		public void run()
		{
		}
	}
}