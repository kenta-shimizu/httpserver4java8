package example2;

import java.net.InetSocketAddress;
import java.nio.file.Paths;

import com.shimizukenta.httpserver.HttpServer;
import com.shimizukenta.httpserver.HttpServers;

public class Example2a {

	private Example2a() {
		/* Nothing */
	}

	public static void main(String[] args) {
		
		System.out.println("start.");
		
		try (
				HttpServer server = HttpServers.simpleHttpServer(
						new InetSocketAddress("127.0.0.1", 8080),
						Paths.get("/var/www/html")
						);
				) {
			
			server.addLogListener(System.out::println);
			
			server.open();
			
			synchronized ( server ) {
				server.wait();
			}
		}
		catch ( InterruptedException ignore ) {
		}
		catch ( Throwable t ) {
			t.printStackTrace();
		}
		
		System.out.println("stop.");

	}

}
