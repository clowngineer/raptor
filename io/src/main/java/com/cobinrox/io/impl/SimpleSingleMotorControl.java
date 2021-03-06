package com.cobinrox.io.impl;

import org.apache.log4j.Logger;

import com.cobinrox.io.impl.gpio.AbstractMotorControl;
import com.cobinrox.io.impl.gpio.Pi4jMotorControl;
import com.cobinrox.io.impl.gpio.WiringPiMotorControl;

public class SimpleSingleMotorControl implements IMotorControl {
	static final Logger logger = Logger.getLogger(SimpleSingleMotorControl.class);

	MotorProps mp;
	AbstractMotorControl myMotor;
	public void initHardware(MotorProps mp) throws Throwable{
		if(myMotor == null )
		{
			this.mp=mp;//new MotorProps();
			//mp.setProps(false);
			if( mp.GPIO_LIB.equals(MotorProps.GPIO_PI4J_LIB_PROP_VAL))
				myMotor = new Pi4jMotorControl();
	        else
	        	myMotor = new WiringPiMotorControl();
			//myMotor = new WiringPiMotorControl();
			myMotor.initHardware(mp);
		}
	}

	/* ISingleMotor implementation
	public void lowLevelMove(final String foreAft)
		throws Throwable{myMotor.lowLevelMove(foreAft,null);}
	public void lowLevelStop(final String foreAft)
		throws Throwable{myMotor.lowLevelStop(foreAft, null);}
	public void brakeAll() throws Throwable{myMotor.brakeAll();}
	*/
	String fbState;
	public void move(final String foreAft, String notUsed) {
		logger.info("About to move [" + foreAft + "]...");
		if( mp==null) 
		{
			mp=new MotorProps();
			mp.setProps(false);
		}
		fbState = "processing";
		try {
			Thread fbThread = null;
			if (foreAft != null) {
				fbThread = new Thread() {
					public void run() {
						pulse(foreAft,mp.duty_cycle_hi_ms, mp.duty_cycle_lo_ms, mp.cmdRunTimeMs);
						fbState=null;
					}
				};
				fbThread.setName("FWD_THREAD");
			}
			
			if (foreAft != null)
				fbThread.start();
			
			while (fbThread != null && fbState != null)
			{
				logger.info("......................fbState/" + fbState);
				Thread.sleep(200);
			}
			myMotor.brakeAll();
			logger.info("...end move!");
		} catch (Throwable t) {
			t.printStackTrace();
			System.err.println("ERROR SETTING PIN");
		}
	} 
	
	protected void pulse(final String foreAft, int dutyCycleHi, int dutyCycleLo,
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
					myMotor.lowLevelMove(foreAft,null);
					Thread.sleep(numMsHi);

					if (numMsLo > 0) {
						time = System.currentTimeMillis();
						logger.info("     turning off for [" + numMsLo + "]ms...");
						myMotor.lowLevelStop(foreAft,null);
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
		myMotor.brakeAll();
	}
}
