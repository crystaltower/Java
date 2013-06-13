package crystaltower.util.trace;

import java.util.logging.LogManager;

public class Trace {
	// Definition
	private static final String		TRACE_LOGGER_NAME = "CrystalTowerTraceLogger";
	
	// Methods
	public Trace(String logConfigFile) {
		setLogConfigFile(logConfigFile);
		
		m_logMgr = LogManager.getLogManager();
		try {
			// Try to read the configuration file to verify it existence
			m_logMgr.readConfiguration();
		} catch (Exception e) {
			System.err.printf("Failed to read logger configuration file for %s!\n", e);
		}
		
		// Logger must be created after object of LogManager has been created.
		m_traceLogger = new TraceLogger(TRACE_LOGGER_NAME);
		
		m_traceLogger.info("Trace logger is initialized!");
	}
	
	protected void setLogConfigFile(String logConfigFile) {
		if ((logConfigFile != null) && (m_logMgr == null)) {
			// Unable to change the "java.util.logging.config" by Preference API
			// Refer to http://tech.groups.yahoo.com/group/ajug/message/5815 for more info
			
			// Set the system properties
			System.setProperty("java.util.logging.config.file", logConfigFile);
		} else {
			System.err.println("Failed to set logger configuration file!");
		}
	}
	
	public static TraceLogger getLogger(String loggerName) {
		if ((loggerName != null) && (loggerName != TRACE_LOGGER_NAME)) {
			return new TraceLogger(loggerName);
		} else {
			return null;
		}
	}
	
	// Properties
	protected LogManager				m_logMgr = null;
	protected TraceLogger				m_traceLogger = null;
}
