package crystaltower.thread;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import crystaltower.util.time.Time;

public class LogThread extends MsgThread {
	// Definition
	public final static int			MSG_PRINT_LOG = MSG_USER + 1;
	
	// Methods
	public LogThread(String logFile) {
		super(LOG_THREAD_NAME);
		
		m_logFile = logFile;
		m_stdOutput = false;
		
		m_curLog = null;
		m_curLogTime = null;
	}
	
	public void printLog(String str, Time logTime) {
		if (str != null) {
			// Print to log file
			ThreadMsg	msg = new ThreadMsg(LogThread.MSG_PRINT_LOG, str, logTime);
			
			postMsg(msg);
		}
	}
	
	public void setStdOutput(boolean stdOutput) {
		m_stdOutput = stdOutput;
	}
	
	@Override
	protected boolean onCreate(ThreadMsg msg) {
		boolean ret;
		
		ret = super.onCreate(msg);
		if (ret) {
			if (m_logFile != null) {
				try {
					printInfo("Start log thread creation procedure...");
					m_logThreadLock.lock();
					
					if (m_logThread == null) {
						m_logPath = Paths.get(m_logFile);
						if (Files.exists(m_logPath)) {
							if (Files.isDirectory(m_logPath)) {
								m_dailyLog = true;
							} else if (Files.isWritable(m_logPath)) {
								m_dailyLog = false;
								
								m_curLog = Files.newBufferedWriter(m_logPath, m_logCharSet, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
							} else {
								ret = false;
							}
						} else {
							ret = false;
						}
						
						if (ret) {
							// Set log thread at last
							m_logThread = this;
							printInfo("Log thread finished creation!");
						}
					} else {
						printWarning("Thread %d (%s) tried to register log thread again!", m_threadId, m_threadName);
						
						ret = false;
					}
				} catch (InvalidPathException e) {
					printError("Invalid log file path %s!", m_logFile);
					
					ret = false;
				} catch (IOException e) {
					printError("Met IO exception when opening log file %s!", m_logFile);
				} finally {
					m_logThreadLock.unlock();
				}
			} else {
				ret = false;
			}
		}
		
		return ret;
	}
	
	@Override
	protected boolean onQuit(ThreadMsg msg) {
		if (m_curLog != null) {
			try {
				m_curLog.close();
			} catch (IOException e) {
				printError("Failed to close current log file!");
			}
			m_curLog = null;
		}
		
		m_logThreadLock.lock();
		
		m_logThread = null;
		
		m_logThreadLock.unlock();
		
		return super.onQuit(msg);
	}
	
	@Override
	protected boolean msgHandler(ThreadMsg msg) {
		int			msgId;
		Object[]	msgBody;
		
		msgId = msg.getMsgId();
		msgBody = msg.getMsgBody();
		switch (msgId) {
		case MSG_PRINT_LOG:
			String		logStr;
			Time		logTime;
			
			logStr = (String)msgBody[0];
			logTime = (Time)msgBody[1];
			
			try {
				if (m_dailyLog) {
					writeDailyLog(logStr, logTime);
				} else {
					if (m_curLog != null) {
						m_curLog.write(logStr);
					}
				}
			} catch (IOException e) {
				printError("Failed to print log string to the log file by exception %s!", e);
			}
			
			if (m_stdOutput) {
				printInfo("%s", logStr);
			}
			
			break;
			
		default:
			printWarning("Log thread couldn't process the message %d!", msgId);
			
			break;
		}
		
		return true;
	}
	
	protected boolean writeDailyLog(String logStr, Time logTime) {
		boolean		ret = false;
		
		Path				logFilePath;
		
		if ((logStr != null) && (logTime != null)) {
			try {
				if ((m_curLog == null) || (m_curLogTime == null) || (!m_curLogTime.isSameDay(logTime))) {
					if (m_curLog != null) {
						m_curLog.close();
						m_curLog = null;
					}
					
					logFilePath = Paths.get(m_logFile, logTime.getDateFileName() + ".log");
					if (Files.exists(logFilePath)) {
						m_curLog = Files.newBufferedWriter(logFilePath, m_logCharSet, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
					} else {
						m_curLog = Files.newBufferedWriter(logFilePath, m_logCharSet, StandardOpenOption.CREATE_NEW);
					}
					m_curLogTime = logTime;
				}
				
				m_curLog.write(logStr);
			} catch (IOException e) {
				System.out.printf("Failed to operate log file by the reason %s", e);
			}
		}
		
		return ret;
	}
	
	// Properties
	protected boolean			m_dailyLog;
	protected boolean			m_stdOutput;
	protected Path				m_logPath;
	
	protected Charset			m_logCharSet = StandardCharsets.UTF_16;
	protected BufferedWriter	m_curLog;
	protected Time				m_curLogTime;
	
	private String				m_logFile;
	private static Lock			m_logThreadLock = new ReentrantLock();
}
