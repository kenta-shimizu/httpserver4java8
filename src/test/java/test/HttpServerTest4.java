package test;

import java.net.InetSocketAddress;
import java.nio.file.Paths;

import com.shimizukenta.httpserver.AbstractHttpProxyServer;
import com.shimizukenta.httpserver.AbstractHttpServer;
import com.shimizukenta.httpserver.HttpRequestMessage;
import com.shimizukenta.httpserver.HttpRequestQuery;
import com.shimizukenta.httpserver.HttpServer;
import com.shimizukenta.httpserver.HttpServerException;
import com.shimizukenta.httpserver.HttpServers;
import com.shimizukenta.httpserver.SimpleHttpServerConfig;
import com.shimizukenta.httpserver.jsonapi.AbstractJsonApi;

public class HttpServerTest4 {

	public HttpServerTest4() {
		/* Nothing */
	}

	public static void main(String[] args) {
		
		echo("Test start.");
		
		final SimpleHttpServerConfig config = new SimpleHttpServerConfig();
		config.addSocketAddress(new InetSocketAddress("127.0.0.1", 8080));
		config.rootPath(Paths.get("./html"));
		config.addDirectoryIndex("index.html");
		config.addDirectoryIndex("index.htm");
		
		final AbstractJsonApi jsonApi = new AbstractJsonApi() {
			
			@Override
			public boolean accept(HttpRequestMessage request) {
				
				switch ( request.method() ) {
				case HEAD:
				case GET: {
					
					return request.absPath().equalsIgnoreCase("/api");
					/* break; */
				}
				default: {
					/* Nothing */
				}
				}
				
				return false;
			}
			
			@Override
			public String buildJson(HttpRequestMessage request)
					throws InterruptedException, HttpServerException {
				
				HttpRequestQuery query = request.getQueryFromUri();
				
				return "{\"querySize\":" + query.keySet().size() + "}";
			}
		};
		
		try (
				AbstractHttpServer server = HttpServers.jsonApiServer(config, jsonApi);
				) {
			
			try (
					HttpServer proxy = new AbstractHttpProxyServer(server, config.apiServerConfig()) {};
					) {
				
				proxy.addLogListener(log -> {echo(log);});
				
				proxy.open();
				
				synchronized ( proxy ) {
					proxy.wait();
				}
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
