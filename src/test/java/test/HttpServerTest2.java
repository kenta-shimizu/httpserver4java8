package test;

import java.net.InetSocketAddress;
import java.nio.file.Paths;

import com.shimizukenta.httpserver.HttpServer;
import com.shimizukenta.httpserver.HttpServers;

public class HttpServerTest2 {

	private HttpServerTest2() {
		/* Nothing */
	}

	public static void main(String[] args) {
		
		echo("Test start.");
		
		try (
				HttpServer server = HttpServers.simpleHttpServer(
						new InetSocketAddress("127.0.0.1", 8080),
						Paths.get("./html")
						);
				) {
			
			server.addLogListener(log -> {echo(log);});
			
			server.open();
			
			synchronized ( server ) {
				server.wait();
			}
		}
		catch ( InterruptedException ignore ) {
		}
		catch ( Throwable t ) {
			echo(t);
		}
		
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
