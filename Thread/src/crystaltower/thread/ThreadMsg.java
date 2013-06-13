package crystaltower.thread;

public class ThreadMsg {
	// Method
	public ThreadMsg(final int msgId, final Object... msgBody) {
		m_msgId = msgId;
		m_msgBody = msgBody;
	}
	
	public ThreadMsg(int msgId) {
		m_msgId = msgId;
		m_msgBody = null;
	}
	
	public int getMsgId() {
		return m_msgId;
	}
	
	public Object[] getMsgBody() {
		return m_msgBody;
	}
	
	// Properties
	private int					m_msgId;
	private Object[]			m_msgBody;
}
