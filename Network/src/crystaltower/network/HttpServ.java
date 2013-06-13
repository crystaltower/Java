package crystaltower.network;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import crystaltower.thread.Thread;
import crystaltower.util.trace.Trace;
import crystaltower.util.trace.TraceLogger;

public class HttpServ {
	// Method
	public HttpServ(int port) {
		m_port = port;
	}
	
	public HttpServ(String addr, int port) {
		m_addr = addr;
		m_port = port;
	}
	
	public boolean create() throws BindException, IOException{
		// MUST BE INVOKED IN MAIN THREAD, NOT IN EXECUTOR!!!
		boolean		ret = false;
		
		try {
			m_httpServ = HttpServer.create(new InetSocketAddress(InetAddress.getByName(m_addr), m_port), 1);
			m_httpServ.setExecutor(Thread.getExecutor());
			
			ret = true;
		} catch (BindException e) {
			m_logger.error("Faild to bind the HTTP server!");
			
			throw e;
		} catch (IOException e) {
			m_logger.error("Failed to create HTTP server by the reason %s!", e);
			
			throw e;
		}
		
		return ret;
	}
	
	public boolean start() {
		boolean			ret = false;
		
		if (m_httpServ != null) {
			m_httpServ.start();
			
			ret = true;
		}
		
		return ret;
	}
	
	public void stop() {
		// Remove context
		for (HttpContext c: m_contextList.values()) {
			m_httpServ.removeContext(c);
		}
		m_contextList.clear();
		
		// Stop HTTP server
		m_httpServ.stop(0);
	}
	
	public boolean addContentPath(String path, RequestHandler handler) {
		boolean		ret = false;
		HttpContext	context;
		
		try {
			context = m_httpServ.createContext(path, handler);
			m_contextList.put(path, context);
			
			ret = true;
		} catch (IllegalArgumentException e) {
			m_logger.error("Failed to add HTTP content by the reason %s!", e);
		} catch (NullPointerException e) {
			m_logger.error("Illegal HTTP content %s!", path);
		}
		
		return ret;
	}
	
	public boolean rmvContentPath(String path) {
		boolean		ret = true;
		
		m_contextList.remove(path);
		
		return ret;
	}
	
	// protected
	protected String				m_addr = "localhost";
	protected int					m_port = 0;
	
	protected HttpServer			m_httpServ = null;
	protected HashMap<String, HttpContext>	m_contextList = new HashMap<String, HttpContext>();
	
	protected TraceLogger			m_logger = Trace.getLogger("HTTP Server");
}
