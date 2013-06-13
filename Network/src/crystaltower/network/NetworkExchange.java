package crystaltower.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import crystaltower.util.trace.Trace;
import crystaltower.util.trace.TraceLogger;

public class NetworkExchange {
	// Methods
	public NetworkExchange (HttpExchange exchange) {
		String						buf;
		Map<String, List<String>>	headers;
		
		m_exchange = exchange;
		
		m_inputReader = new BufferedReader(new InputStreamReader(m_exchange.getRequestBody()));
		m_outputWriter = new BufferedWriter(new OutputStreamWriter(m_exchange.getResponseBody()));
		
		// Get request method
		m_reqMethod = m_exchange.getRequestMethod();
		
		// Get request header
		headers = m_exchange.getRequestHeaders();
		for (String h : headers.keySet()) {
			for (String i : headers.get(h)) {
				m_reqHeader += h + ": " + i + "\n";
			}
		}
		
		// Get request body
		try {
			while ((buf = m_inputReader.readLine()) != null) {
				m_reqBody += buf;
			}
		} catch (IOException e) {
			m_logger.error("IO error %s when reading network request body!", e);
		}
	}
	
	public String getReqMethod() {return m_reqMethod;}
	public String getReqHeader() {return m_reqHeader;}
	public String getReqBody() {return m_reqBody;}
	
	// Properties
	protected HttpExchange			m_exchange = null;
	
	protected BufferedReader		m_inputReader = null;
	protected BufferedWriter		m_outputWriter = null;
	
	protected String				m_reqMethod = "";
	protected String				m_reqHeader = "";
	protected String				m_reqBody = "";
	
	protected TraceLogger			m_logger = Trace.getLogger("Network Exchange");
}
