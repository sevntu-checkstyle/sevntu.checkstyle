public class InputForbidMethodOverloadingCheck
{
	public void run(){
		//no code
	}
	//Warning here if allowInClasses=false (default)
	public void run(String input){
		//no code
	}
	
	public void run1(){
		//no code
	}
	//Warning here if both allowPrivate==false (default) and allowInClasses=false
	private void run1(String input){
		//no code
	}
	
	public interface InputInterface{
		void run();
		//Always warning here
		void run(String input);
	}
	
	private final Runnable anonClass = new Runnable(){

		public void run() {
			// no code
		}
		//Should be the same as in usual classes
		public void run(String input){
			//no code
		}
		
	};
	
	private final Runnable anonClass2 = new Runnable(){

		public void run() {
			// no code
		}
		//Should be the same as in usual classes
		private void run(String input){
			//no code
		}
		
	};
	
}