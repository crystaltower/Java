package crystaltower.util.time;

import java.util.Calendar;
import java.util.TimeZone;

public class Time {
	// Methods
	public Time() {
		getCurTime();
	}
		
	protected void getCurTime() {
		m_curCal = Calendar.getInstance(TimeZone.getDefault());
	}
	
	public String getDateFileName() {
		String		fileName;
		int			val;
		
		val = m_curCal.get(Calendar.YEAR);
		fileName = String.format("%4d_", val);
		val = m_curCal.get(Calendar.MONTH) + 1;
		if (val < 10) {
			fileName += String.format("0%d_", val);
		} else {
			fileName += String.format("%2d_", val);
		}
		val = m_curCal.get(Calendar.DAY_OF_MONTH);
		if (val < 10) {
			fileName += String.format("0%d", val);
		} else {
			fileName += String.format("%2d", val);
		}
		
		return fileName;
	}
	
	public boolean isSameDay(Time otherTime) {
		if ((m_curCal.get(Calendar.YEAR) != otherTime.m_curCal.get(Calendar.YEAR)) || 
				(m_curCal.get(Calendar.MONTH) != otherTime.m_curCal.get(Calendar.MONTH)) || 
				(m_curCal.get(Calendar.DAY_OF_MONTH) != otherTime.m_curCal.get(Calendar.DAY_OF_MONTH))) {
			return false;
		} else {
			return true;
		}
	}
	public String getFormatTime() {
		String		curTime;
		int			val;
		
		val = m_curCal.get(Calendar.YEAR);
		curTime = String.format("%4d-", val);
		val = m_curCal.get(Calendar.MONTH) + 1;
		if (val < 10) {
			curTime += String.format("0%d-", val);
		} else {
			curTime += String.format("%2d-", val);
		}
		val = m_curCal.get(Calendar.DAY_OF_MONTH);
		if (val < 10) {
			curTime += String.format("0%d ", val);
		} else {
			curTime += String.format("%2d ", val);
		}
		val = m_curCal.get(Calendar.HOUR_OF_DAY);
		if (val < 10) {
			curTime += String.format("0%d:", val);
		} else {
			curTime += String.format("%2d:", val);
		}
		val = m_curCal.get(Calendar.MINUTE);
		if (val < 10) {
			curTime += String.format("0%d:", val);
		} else {
			curTime += String.format("%2d:", val);
		}
		val = m_curCal.get(Calendar.SECOND);
		if (val < 10) {
			curTime += String.format("0%d", val);
		} else {
			curTime += String.format("%2d", val);
		}
		
		return curTime;
	}
	
	// Properties
	protected Calendar			m_curCal;
}
