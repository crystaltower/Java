package crystaltower.thread;

import java.util.concurrent.LinkedBlockingDeque;

public abstract class MsgThread extends Thread {
	// Message ID
	protected final static int		MSG_CREATE = 1;
	protected final static int		MSG_QUIT = MSG_CREATE + 1;
	protected final static int		MSG_USER = 0xFFFF;		// User message must be started after it
	
	// Method
	public MsgThread() {
		super();
	}
	
	public MsgThread(String threadName) {
		super(threadName);
	}
	
	public static boolean postMsg(Integer threadId, ThreadMsg msg) {
		MsgThread thread = (MsgThread)getThread(threadId);
		
		if (thread != null) {
			return thread.postMsg(msg);
		} else {
			return false;
		}
	}
	
	public boolean postMsg(ThreadMsg msg) {
		if (msg != null) {
			return m_msgQue.add(msg);
		} else {
			return false;
		}
	}
	
	@Override
	public boolean start() {
		boolean		ret;
		
		ret = super.start();
		if (ret) {
			ThreadMsg		msg = new ThreadMsg(MSG_CREATE);
			
			ret = postMsg(msg);
		}
		
		return ret;
	}
	
	@Override
	public void stop() {
		ThreadMsg		msg = new ThreadMsg(MSG_QUIT);
		
		postMsg(msg);
		
		super.stop();
	}
	
	protected boolean onCreate(ThreadMsg msg) {
		printInfo("Create Thread %d (%s).", m_threadId, m_threadName);
		
		return true;
	}
	
	protected boolean onQuit(ThreadMsg msg) {
		return false;
	}
	
	protected boolean msgHandler(ThreadMsg msg) {
		return true;
	}
	
	@Override
	protected void ThreadProc() {
		// TODO Auto-generated method stub
		boolean			bProcess = true;
		ThreadMsg		msg;
		
		super.ThreadProc();
		
		while (bProcess) {
			try {
				msg = m_msgQue.take();
			} catch (InterruptedException e) {
				printWarning("Thread %d (%s) was interrupted by %s!", e);
				
				break;
			}
			
			switch (msg.getMsgId()) {
			case MSG_CREATE:
				bProcess = onCreate(msg);
				
				break;
				
			case MSG_QUIT:
				bProcess = onQuit(msg);
				
				break;
				
			default:
				bProcess = msgHandler(msg);
				
				break;
			}
		}
	}
	
	@Override
	protected void ThreadEnter() {
		super.ThreadEnter();
	}
	
	@Override
	protected void ThreadExit() {
		if (m_msgQue.isEmpty() == false) {
			printWarning("There are still %d messages in the thread %d!", m_msgQue.size());
		}
		
		super.ThreadExit();
	}

	// Properties
	private LinkedBlockingDeque<ThreadMsg> m_msgQue = new LinkedBlockingDeque<ThreadMsg>();
}
