# httpserver4java8

## Introduction

This library is HTTP-Server implementation on Java8 or later.  
Works only with Java8, *Not* require Tomcat.


## Create Simple HTTP-Server instance and open.

```java
HttpServer server = HttpServers.simpleHttpServer(
        new InetSocketAddress("127.0.0.1", 8080),
        Paths.get("/var/www/html")
        );

server.open();
```

See also ["/src/examples/example2/Example2a.java"](/src/examples/example2/Example2a.java)  
See also ["/src/examples/example2/Example2b.java"](/src/examples/example2/Example2b.java)  


## Create JSON-API-HTTP-Server instance and open.

1. Create `JsonApi` instance.

```java
JsonApi jsonApi = new AbstractJsonApi() {
    
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
```

2. Create `HttpServer` instance and open.

```java
HttpServer apiServer = HttpServers.jsonApiServer(
        new InetSocketAddress("127.0.0.1", 8080),
        Paths.get("/var/www/html"),
        Arrays.asList(jsonApi)
        );

apiServer.open();
```

See also ["/src/examples/example3/Example3a.java"](/src/examples/example3/Example3a.java)  
See also ["/src/examples/example3/Example3b.java"](/src/examples/example3/Example3b.java)  


## Wrapping with Proxy-Server.

### Wrapping with Cache-Proxy-Server and open.

```java
HttpServer server = HttpServers.simpleHttpServer(
        null,
        Paths.get("/var/www/html")
        );

HttpServer cacheProxy = HttpServers.wrapSimpleCacheProxyServer(
        server,
        new InetSocketAddress("127.0.0.1", 8080));

cacheProxy.open();
```

See also ["/src/examples/example5/Example5a.java"](/src/examples/example5/Example5a.java)  
See also ["/src/examples/example5/Example5b.java"](/src/examples/example5/Example5b.java)  
