package com.cobinrox.io.impl;

import org.apache.log4j.Logger;

import com.cobinrox.io.impl.IMotorControl;
import com.cobinrox.io.impl.MotorProps;
import com.cobinrox.io.impl.gpio.AbstractMotorControl;
import com.cobinrox.io.impl.gpio.Pi4jMotorControl;
import com.cobinrox.io.impl.gpio.WiringPiMotorControl;

public class WheelChairMotorControl implements IMotorControl {
	static final Logger logger = Logger.getLogger(WheelChairMotorControl.class);

	MotorProps mp;
	AbstractMotorControl myMotors;
	public void initHardware() throws Throwable{
		if(myMotors == null )
		{
			mp=new MotorProps();
			mp.setProps(false);
			if( mp.GPIO_LIB.equals(MotorProps.GPIO_PI4J_LIB_PROP_VAL))
				myMotors = new Pi4jMotorControl();
	        else
	        	myMotors = new WiringPiMotorControl();			
			myMotors.initHardware();
		}
	}
	
	/*public void lowLevelMove(final String foreAft, final String leftRight)
			throws Throwable{myMotors.lowLevelMove(foreAft,leftRight);}
		public void lowLevelStop(final String foreAft,String leftRight)
			throws Throwable{myMotors.lowLevelStop(foreAft, leftRight);}
		public void brakeAll() throws Throwable{myMotors.brakeAll();}
	*/
	
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
						pulse(foreAft, null,mp.DUTY_CYCLE_HI_MS, mp.DUTY_CYCLE_LO_MS, mp.CMD_RUN_TIME_MS);
						fbState=null;
					}
				};
				fbThread.setName("FWD_THREAD");
			}
			if (leftRight != null) {
				lrThread = new Thread() {
					public void run() {
						pulse(null,leftRight, mp.DUTY_CYCLE_HI_MS, mp.DUTY_CYCLE_LO_MS, mp.CMD_RUN_TIME_MS);
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
			myMotors.brakeAll();
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
					myMotors.lowLevelMove(foreAft,leftRight);
					Thread.sleep(numMsHi);

					if (numMsLo > 0) {
						time = System.currentTimeMillis();
						logger.info("     turning off for [" + numMsLo + "]ms...");
						myMotors.lowLevelStop(foreAft,leftRight);
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
		myMotors.brakeAll();
	}
}
