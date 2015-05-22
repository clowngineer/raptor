package com.cobinrox.io.impl.gpio;

import org.apache.log4j.Logger;

import com.cobinrox.io.impl.IMotorControl;
import com.cobinrox.io.impl.MotorProps;

public abstract class AbstractMotorControl implements IMotorControl {
	static final Logger logger = Logger.getLogger(AbstractMotorControl.class);

	MotorProps mp;
	
	
	public abstract void lowLevelMove(final String foreAft, final String leftRight)
		throws Throwable;
	public abstract void lowLevelStop(final String foreAft, final String leftRight)
		throws Throwable;
	public abstract void brakeAll() throws Throwable;
	public abstract void initHardware(MotorProps mp) throws Throwable;
	String lrState;
	String fbState;
	public void move(final String foreAft, final String leftRight) {
		logger.info("About to move [" + foreAft + "/" + leftRight
				+ "]...");
		if( mp==null) 
		{
			mp=new MotorProps();
			mp.setProps(false);
		}
		lrState = "processing";
		fbState = "processing";
		try {
			Thread fbThread = null;
			Thread lrThread = null;
			if (foreAft != null) {
				fbThread = new Thread() {
					public void run() {
						pulse(foreAft, null,mp.duty_cycle_hi_ms, mp.duty_cycle_lo_ms, mp.cmdRunTimeMs);
						fbState=null;
					}
				};
				fbThread.setName("FWD_THREAD");
			}
			if (leftRight != null) {
				lrThread = new Thread() {
					public void run() {
						pulse(null,leftRight, mp.duty_cycle_hi_ms, mp.duty_cycle_lo_ms, mp.cmdRunTimeMs);
						lrState = null;
					}
				};
				lrThread.setName("LR_THREAD");
			}
			if (leftRight != null)
				lrThread.start();
			if (foreAft != null)
				fbThread.start();
			// if(lrThread  == null &&  (fwdThread!=null && !forwardBackPulseComplete))
			// if(fwdThread == null &&  (lrThead!=null   && !leftRightPulseComplete  )) 
			// 
			while (lrThread != null && lrState != null)
			{
				logger.info("......................lrState/" + lrState);
				Thread.sleep(200);
			}
			while (fbThread != null && fbState != null)
			{
				logger.info("......................fbState/" + fbState);
				Thread.sleep(200);
			}
			brakeAll();
			logger.info("...end move!");
		} catch (Throwable t) {
			t.printStackTrace();
			System.err.println("ERROR SETTING PIN");
		}
	} 
	
	protected void pulse(final String foreAft, final String leftRight, int dutyCycleHi, int dutyCycleLo,
			int numMsToRunCmdFor) {
		int numMsHi = dutyCycleHi;//dutyCycleHi;//(int)((float)numMsToRunCmdFor * (float)((float)dutyCycleHi/(float)100));
		int numMsLo = dutyCycleLo;
		long outerStart = System.currentTimeMillis();
		{
			try {
				int numPeriods = (int)((float)numMsToRunCmdFor / ((float)numMsHi + (float)numMsLo));
				logger.info("     Pulsing, need [" + numPeriods + "] pulses across ["
						+ numMsToRunCmdFor + "] ms ...");
				for (int i = 0; i < numPeriods; i++) {
					long time = System.currentTimeMillis();
					logger.info("     turning on for [" + numMsHi + "]ms...");
					lowLevelMove(foreAft,leftRight);
					Thread.sleep(numMsHi);

					if (numMsLo > 0) {
						time = System.currentTimeMillis();
						logger.info("     turning off for [" + numMsLo + "]ms...");
						lowLevelStop(foreAft,leftRight);
						Thread.sleep(numMsLo);
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
				System.err.println("Error pulsing gpio pin");
			} finally {
				long outerStop = System.currentTimeMillis();
				logger.info("     ...end pulsing; total time sending cmd: "
						+ (outerStop - outerStart) + " ms");
			}
		}
	}

	public void shutdown() throws Throwable
	{
		brakeAll();
	}
}
