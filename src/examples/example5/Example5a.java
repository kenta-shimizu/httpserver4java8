package example5;

import java.net.InetSocketAddress;
import java.nio.file.Paths;

import com.shimizukenta.httpserver.HttpServer;
import com.shimizukenta.httpserver.HttpServers;

public class Example5a {

	private Example5a() {
		/* Nothing */
	}

	public static void main(String[] args) {
		
		System.out.println("start.");
		
		try (
				HttpServer server = HttpServers.simpleHttpServer(
						null,
						Paths.get("/var/www/html")
						);
				) {
			
			try (
					HttpServer cacheProxy = HttpServers.wrapSimpleCacheProxyServer(
							server,
							new InetSocketAddress("127.0.0.1", 8080));
					) {
				
				cacheProxy.addLogListener(System.out::println);
				
				cacheProxy.open();
				
				synchronized ( cacheProxy ) {
					cacheProxy.wait();
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
