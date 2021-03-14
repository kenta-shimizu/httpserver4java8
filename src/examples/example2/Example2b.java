package example2;

import java.net.InetSocketAddress;
import java.nio.file.Paths;

import com.shimizukenta.httpserver.HttpServer;
import com.shimizukenta.httpserver.HttpServers;
import com.shimizukenta.httpserver.SimpleHttpServerConfig;

public class Example2b {

	private Example2b() {
		/* Nothing */
	}

	public static void main(String[] args) {
		
		System.out.println("start.");
		
		try {
			final SimpleHttpServerConfig config = new SimpleHttpServerConfig();
			
			config.addSocketAddress(new InetSocketAddress("127.0.0.1", 8080));
			
			config.addAcceptHostName("127.0.0.1:8080");
			
			config.serverName("SERVER-NAME");
			
			config.apiServerConfig().keepAliveTimeout(5L);
			config.apiServerConfig().keepAliveMax(100);
			
			config.rootPath(Paths.get("/var/www/html"));
			
			config.addDirectoryIndex("index.html");
			config.addDirectoryIndex("index.htm");
			
			try (
					HttpServer server = HttpServers.simpleHttpServer(config);
					) {
				
				server.addLogListener(System.out::println);
				
				server.open();
				
				synchronized ( server ) {
					server.wait();
				}
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
