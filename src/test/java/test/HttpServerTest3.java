package test;

import java.net.InetSocketAddress;
import java.nio.file.Paths;

import com.shimizukenta.httpserver.HttpRequestMessage;
import com.shimizukenta.httpserver.HttpRequestQuery;
import com.shimizukenta.httpserver.HttpServer;
import com.shimizukenta.httpserver.HttpServerException;
import com.shimizukenta.httpserver.HttpServers;
import com.shimizukenta.httpserver.jsonapi.AbstractJsonApi;
import com.shimizukenta.httpserver.jsonapi.JsonApi;

public class HttpServerTest3 {

	private HttpServerTest3() {
		/* NOthing */
	}

	public static void main(String[] args) {
		
		echo("Test start.");
		
		final JsonApi jsonApi = new AbstractJsonApi() {
			
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
				HttpServer server = HttpServers.jsonApiServer(
						new InetSocketAddress("127.0.0.1", 8080),
						Paths.get("./html"),
						jsonApi
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
