package test;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.shimizukenta.httpserver.AbstractHttpApiServer;
import com.shimizukenta.httpserver.AbstractHttpServerConfig;
import com.shimizukenta.httpserver.HttpApi;
import com.shimizukenta.httpserver.HttpApiServer;
import com.shimizukenta.httpserver.HttpConnectionValue;
import com.shimizukenta.httpserver.HttpRequestMessage;
import com.shimizukenta.httpserver.HttpResponseCode;
import com.shimizukenta.httpserver.HttpResponseMessage;
import com.shimizukenta.httpserver.HttpServerException;

public class HttpServerTest {

	public HttpServerTest() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) {
		
		echo("Test start");
		
		AbstractHttpServerConfig config = new AbstractHttpServerConfig() {
			
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
			
			server.addApi(new HttpApi() {
				
				@Override
				public boolean accept(HttpRequestMessage request) {
					return true;
				}

				@Override
				public HttpResponseMessage receiveRequest(
						HttpRequestMessage request,
						HttpConnectionValue connectionValue)
								throws InterruptedException, HttpServerException {
					
					return HttpResponseMessage.build(request, HttpResponseCode.BadRequest);
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
