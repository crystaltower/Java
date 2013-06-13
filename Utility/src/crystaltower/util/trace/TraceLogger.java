package crystaltower.util.trace;

import java.util.logging.Logger;

public class TraceLogger {
	// Methods
	protected TraceLogger (String name) {
		m_logger = Logger.getLogger(name);
	}
	
	public void info(String format, Object... args) {
		String	str = String.format(format, args);
		
		m_logger.info(str);
	}
	
	public void warning(String format, Object... args) {
		String	str = String.format(format, args);
		
		m_logger.warning(str);
	}
	
	public void error(String format, Object... args) {
		String	str = String.format(format, args);
		
		m_logger.severe(str);
	}
	
	// Properties
	protected Logger				m_logger = null;
}
