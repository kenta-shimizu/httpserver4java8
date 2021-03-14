package example3;

import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.Arrays;

import com.shimizukenta.httpserver.HttpRequestMessage;
import com.shimizukenta.httpserver.HttpRequestQuery;
import com.shimizukenta.httpserver.HttpServer;
import com.shimizukenta.httpserver.HttpServerException;
import com.shimizukenta.httpserver.HttpServers;
import com.shimizukenta.httpserver.jsonapi.AbstractJsonApi;
import com.shimizukenta.httpserver.jsonapi.JsonApi;

public class Example3a {

	private Example3a() {
		/* Nothing */
	}

	public static void main(String[] args) {
		
		System.out.println("start.");
		
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
						Paths.get("/var/www/html"),
						Arrays.asList(jsonApi)
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
