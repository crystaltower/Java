package crystaltower.network;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


public abstract class RequestHandler implements HttpHandler {
	// Methods
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		NetworkExchange		req = new NetworkExchange(exchange);
		
		// Invoke request procedure
		reqHandler(req);
	}
	
	public abstract void reqHandler(NetworkExchange e);
}
