package test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

import com.shimizukenta.httpserver.AbstractHttpApiServer;
import com.shimizukenta.httpserver.AbstractHttpApiServerConfig;
import com.shimizukenta.httpserver.HttpApiServer;
import com.shimizukenta.httpserver.HttpRequestMessage;
import com.shimizukenta.httpserver.HttpServerException;
import com.shimizukenta.httpserver.generalfileapi.AbstractGeneralFileApi;
import com.shimizukenta.httpserver.generalfileapi.AbstractGeneralFileApiConfig;
import com.shimizukenta.httpserver.jsonapi.AbstractJsonApi;

public class HttpServerTest1 {

	public HttpServerTest1() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) {
		
		echo("Test start");
		
		AbstractHttpApiServerConfig config = new AbstractHttpApiServerConfig() {
			
			private static final long serialVersionUID = 6047203276687113314L;
		};
		
		config.addServerAddress(new InetSocketAddress("127.0.0.1", 8080));
		
		try (
				HttpApiServer server = new AbstractHttpApiServer(config) {};
				) {
			
			AbstractGeneralFileApiConfig generalFileConfig = new AbstractGeneralFileApiConfig() {
				
				private static final long serialVersionUID = -7907965360626723326L;
			};
			
			generalFileConfig.rootPath(Paths.get("./html"));
			generalFileConfig.addDirectoryIndex("index.html");
			
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
			
			server.addApi(new AbstractGeneralFileApi(generalFileConfig) {});
			
			
			server.addLogListener(log -> {
				echo(log);
			});
			
			server.open();
			
			synchronized ( HttpServerTest1.class ) {
				HttpServerTest1.class.wait();
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
