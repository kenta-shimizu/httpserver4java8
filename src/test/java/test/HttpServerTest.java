package test;

public class HttpServerTest {

	public HttpServerTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		echo("Test start");
		
		// TODO Auto-generated method stub

		echo("Test finished.");
	}
	
	private static final Object syncEcho = new Object();
	
	private static void echo(Object o) {
		
		synchronized ( syncEcho ) {
			
			if ( o instanceof Throwable ) {
				
				((Throwable) o).printStackTrace();
				
			} else {
				
				System.out.println(o);
			}
			
			System.out.println();
		}
	}

}
