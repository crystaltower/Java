package crystaltower.thread;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import crystaltower.util.trace.Trace;
import crystaltower.util.trace.TraceLogger;

public class Thread implements Runnable {
	// Types
	
	// Methods
	public Thread() {
		m_threadId = hashCode();			// Get object id as thread id
		m_threadName = "";
		m_threadExec = null;
		
		m_logger = Trace.getLogger("Thread " + m_threadId.toString());
		
		register();
	}
	
	public Thread(String threadName) {
		m_threadId = hashCode();
		m_threadName = threadName;
		
		m_threadExec = null;
		
		m_logger = Trace.getLogger("Thread " + m_threadName);
		
		register();
	}
	
	public static Thread getThread(Integer threadId) {
		return m_threadList.get(threadId);
	}
	
	public static Executor getExecutor() {
		return m_threadPool;
	}
	
	public static void startThreadPool() {
		if (m_threadPool == null) {
			m_threadPool = Executors.newCachedThreadPool();
		}
	}
	
	public static void stopThreadPool() {
		try {
			m_threadLock.lock();
			
			if (m_threadPool != null) {
				m_threadPool.shutdown();
			}
			
			for (Thread thread : m_threadList.values()) {
				if (m_logThread != thread) {
					thread.stop();
				}
			}
			
			if (m_logThread != null) {
				m_logThread.stop();
			}
		} finally {
			m_threadPool = null;
			m_threadList.clear();
			
			m_threadLock.unlock();
		}
	}
	
	public boolean start() {
		boolean		ret = false;
		
		try {
			m_threadLock.lock();
			
			if (m_threadPool != null) {
				m_threadExec = m_threadPool.submit(this);
				
				ret = true;
			}
		} catch (RejectedExecutionException e) {
			printError("Thread %d (%s) is rejected by executor!", m_threadId, m_threadName);
		} finally {
			m_threadLock.unlock();
		}
		
		return ret;
	}
	
	public void stop() {
		if (m_threadExec != null) {
			try {
				m_threadExec.get();
			} catch (CancellationException e) {
				printWarning("Thread %d (%s) has been cancelled!", m_threadId, m_threadName);
			} catch (ExecutionException e) {
				printError("Execution exception %s was thrown in the thread %d (%s)!", e, m_threadId, m_threadName);
			} catch (InterruptedException e) {
				printWarning("The thread %d (%s) was interrupted!", m_threadId, m_threadName);
			} finally {
				m_threadExec = null;
			}
			
			printInfo("Try to stop the thread %d (%s)!", m_threadId, m_threadName);
		}
	}
	
	@Override
	public void run() {
		// Thread enter
		ThreadEnter();
		
		// Thread procedure
		ThreadProc();
		
		// Thread Exit
		ThreadExit();
	}
	
	protected void ThreadEnter() {
		printInfo("Enter thread %d (%s).", m_threadId, m_threadName);
	}
	
	protected void ThreadProc() {
		printInfo("Thread %d (%s) procedure.", m_threadId, m_threadName);
	}
	
	protected void ThreadExit() {
		unregister();
		printInfo("Exit thread %d (%s).", m_threadId, m_threadName);
	}
	
	protected void register() {
		try {
			m_threadLock.lock();
			m_threadList.putIfAbsent(m_threadId, this);
		} finally {
			m_threadLock.unlock();
		}
	}
	
	protected void unregister() {
		try {
			m_threadLock.lock();
			m_threadList.remove(m_threadId);
			
			if (m_threadList.size() == 0) {
				// No other thread, shutdown executor
				m_threadPool.shutdown();
			}
		} finally {
			m_threadLock.unlock();
		}
	}
	
	public void printInfo(String format, Object... args) {
		String	str = String.format(format, args);
		
		m_logger.info(str);
	}
	
	public void printWarning(String format, Object... args) {
		String	str = String.format(format, args);
		
		m_logger.warning(str);
	}
	
	public void printError(String format, Object... args) {
		String	str = String.format(format, args);
		
		m_logger.error(str);
	}
	
	// Properties
	protected static ConcurrentHashMap<Integer, Thread>	m_threadList = new ConcurrentHashMap<Integer, Thread>();
	
	protected Integer								m_threadId;
	protected String								m_threadName;
	protected Future<?>								m_threadExec;
	
	protected TraceLogger							m_logger;
	protected static String							LOG_THREAD_NAME = "Log Thread";
	protected static LogThread						m_logThread = null;
	
	private static ExecutorService					m_threadPool;
	private static Lock								m_threadLock = new ReentrantLock();
}
