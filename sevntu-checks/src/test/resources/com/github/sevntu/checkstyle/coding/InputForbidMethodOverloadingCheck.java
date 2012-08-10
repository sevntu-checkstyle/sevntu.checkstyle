public class InputForbidMethodOverloadingCheck
{
	public void run(){
		//no code
	}
	
	public void run(String input){
		//no code
	}
	
	public void run1(){
		//no code
	}
	
	private void run1(String input){
		//no code
	}
	
	public interface InputInterface{
		void run();
		
		void run(String input);
	}
	
	private final Runnable anonClass = new Runnable(){

		public void run() {
			// no code
		}
		
		public void run(String input){
			//no code
		}
		
	};
	
	private final Runnable anonClass2 = new Runnable(){

		public void run() {
			// no code
		}
		
		private void run(String input){
			//no code
		}
		
	};
	
}