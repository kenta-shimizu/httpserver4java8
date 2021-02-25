package test;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.shimizukenta.httpserver.AbstractHttpApiServer;
import com.shimizukenta.httpserver.AbstractHttpApiServerConfig;
import com.shimizukenta.httpserver.HttpApi;
import com.shimizukenta.httpserver.HttpApiServer;
import com.shimizukenta.httpserver.HttpConnectionValue;
import com.shimizukenta.httpserver.HttpRequestMessage;
import com.shimizukenta.httpserver.HttpResponseCode;
import com.shimizukenta.httpserver.HttpResponseMessage;
import com.shimizukenta.httpserver.HttpServerConfig;
import com.shimizukenta.httpserver.HttpServerException;
import com.shimizukenta.httpserver.jsonapi.AbstractJsonApi;

public class HttpServerTest {

	public HttpServerTest() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) {
		
		echo("Test start");
		
		AbstractHttpApiServerConfig config = new AbstractHttpApiServerConfig() {
			
			private static final long serialVersionUID = 6047203276687113314L;
		};
		
		
//		try {
//			List<HttpEncoding> encs = HttpEncoding.fromFieldValue("gzip;q=0.4, deflate");
//			
//			encs.forEach(enc -> {
//				echo(enc);
//			});
//		}
//		catch (HttpServerMessageHeaderParseException e) {
//			echo(e);
//		}
		
		
		config.addServerAddress(new InetSocketAddress("127.0.0.1", 8080));
		
		try (
				HttpApiServer server = new AbstractHttpApiServer(config) {};
				) {
			
			server.addApi(new AbstractJsonApi() {

				@Override
				public boolean accept(HttpRequestMessage request) {
					return request.uri().startsWith("/json");
				}
				
				@Override
				public String buildJson(HttpRequestMessage request) throws InterruptedException, HttpServerException {
					return "{\"success\":true}";
				}
			});
			
			server.addLogListener(log -> {
				echo(log);
			});
			
			server.open();
			
			synchronized ( HttpServerTest.class ) {
				HttpServerTest.class.wait();
			}
		}
		catch ( InterruptedException ignore ) {
		}
		catch ( IOException e ) {
			echo(e);
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
