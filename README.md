# httpserver4java8

## Introduction

This library is HTTP-Server implementation on Java8 or later.  
Running only Java8, *Not* require Tomcat.

builing ...


## Create Simple HTTP-Server instance and open.

```java
HttpServer server = HttpServers.simpleHttpServer(
        new InetSocketAddress("127.0.0.1", 8080),
        Paths.get("/var/www/html")
        );

server.open();
```

## Create JSON-API-HTTP-Server instance and open.

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

HttpServer apiServer = HttpServers.jsonApiServer(
        new InetSocketAddress("127.0.0.1", 8080),
        Paths.get("/var/www/html"),
        Arrays.asList(jsonApi)
        );

apiServer.open();
```

## Wrapping with Cache-Proxy-Sever

...
