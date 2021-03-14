package example5;

import java.net.InetSocketAddress;
import java.nio.file.Paths;

import com.shimizukenta.httpserver.HttpServer;
import com.shimizukenta.httpserver.HttpServers;
import com.shimizukenta.httpserver.cacheproxyserver.SimpleHttpCacheProxyServerConfig;

public class Example5b {

	private Example5b() {
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
			
			final SimpleHttpCacheProxyServerConfig config = new SimpleHttpCacheProxyServerConfig();
			
			config.addServerAddress(new InetSocketAddress("127.0.0.1", 8080));
			
			config.addAcceptHostName("127.0.0.1:8080");
			
			config.serverName("SERVER-NAME");
			
			config.keepAliveTimeout(5L);
			config.keepAliveMax(100);
			
			config.cacheAge(86400L);
			
			
			try (
					HttpServer cacheProxy = HttpServers.wrapSimpleCacheProxyServer(server, config);
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
