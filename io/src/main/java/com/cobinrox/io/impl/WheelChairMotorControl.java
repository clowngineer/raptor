package com.cobinrox.io.impl;

import org.apache.log4j.Logger;

import com.cobinrox.io.impl.gpio.AbstractMotorControl;
import com.cobinrox.io.impl.gpio.Pi4jMotorControl;
import com.cobinrox.io.impl.gpio.WiringPiMotorControl;

public class WheelChairMotorControl implements IMotorControl {
	static final Logger logger = Logger.getLogger(WheelChairMotorControl.class);

	MotorProps mp;
	AbstractMotorControl myMotors;
	public void initHardware(MotorProps mp) throws Throwable{
		if(myMotors == null )
		{
			this.mp=mp;//new MotorProps();
			//mp.setProps(false);
			if( mp.GPIO_LIB.equals(MotorProps.GPIO_PI4J_LIB_PROP_VAL))
				myMotors = new Pi4jMotorControl();
	        else
	        	myMotors = new WiringPiMotorControl();			
			myMotors.initHardware(mp);
		}
	}
	
	/*public void lowLevelMove(final String foreAft, final String leftRight)
			throws Throwable{myMotors.lowLevelMove(foreAft,leftRight);}
		public void lowLevelStop(final String foreAft,String leftRight)
			throws Throwable{myMotors.lowLevelStop(foreAft, leftRight);}
		public void brakeAll() throws Throwable{myMotors.brakeAll();}
	*/
	
	String m2lrState;
	String m1fbState;
	String m1;
	String m2;
	//String m1State;
	//String m2State;
	public void move( String foreAft,  String leftRight) {
		// first check what the CALLER sent in . . .
		/*if( foreAft != null && foreAft.length() > 2 )
		{
			logger.info("Not sure how to handle foreAft of [" + foreAft + "]");
		    return;
		}
		if( leftRight != null && leftRight.length() > 2)
		{
			logger.info("Not sure how to handle leftRight of [" + leftRight + "]");
			return;
		}*/
		m1 = null;
		m2 = null;
		if( foreAft != null && leftRight != null )
		{
			logger.info("Not sure how to handle both foreAft & leftRight");
			return;
		}
		
		// now re-code for m1 and m2 wheelchair motor mode
		if( foreAft != null )
		{
			if( foreAft.equals(MotorProps.FORWARD))
			{
				// we want both motors to move +
				leftRight = MotorProps.LEFT;
				m1 = "+";
				m2 = "+";
			}
			else if( foreAft.equals(MotorProps.BACKWARD))
			{
				// we want both motors to move -
				leftRight = MotorProps.RIGHT;
				m1 = "-";
				m2 = "-";
			}
		}
		else
		{
			if( leftRight.equals(MotorProps.LEFT))
			{
				foreAft = MotorProps.BACKWARD;
				m1 = "-";
				m2 = "+";
			}
			else if( leftRight.equals(MotorProps.RIGHT))
			{
				foreAft = MotorProps.FORWARD;
				m1="+";
				m2="-";
			}
		}

		logger.info("About to move [" + foreAft + "/" + leftRight+ "]...");
		logger.info("About to move m1 [" + m1 + "] m2 [" + m2 + "]");
		if( mp == null )
		{
			logger.error("INVALID STATE MP NOT SET");
			return;
		}
		/*if( mp==null) 
		{
			mp=new MotorProps();
			mp.setProps(false);
		}*/
		m2lrState =  "processing";
		m1fbState =  "processing";
		try {
			Thread m1fbThread = null;
			Thread m2lrThread = null;
			if (foreAft != null) {
				final String m1foreAftT = foreAft;
				m1fbThread = new Thread() {
					public void run() {
						pulse(m1foreAftT, null,mp.duty_cycle_hi_ms, mp.duty_cycle_lo_ms, mp.cmdRunTimeMs);
						//pulse(m1, null,mp.duty_cycle_hi_ms, mp.duty_cycle_lo_ms, mp.cmdRunTimeMs);
						m1fbState=null;
					}
				};
				m1fbThread.setName("M1FB_THREAD");
			}
			if (leftRight != null) {
				final String m2leftRightT = leftRight;
				m2lrThread = new Thread() {
					public void run() {
						pulse(null,m2leftRightT, mp.duty_cycle_hi_ms, mp.duty_cycle_lo_ms, mp.cmdRunTimeMs);
						//pulse(null,m2, mp.duty_cycle_hi_ms, mp.duty_cycle_lo_ms, mp.cmdRunTimeMs);
						m2lrState = null;
					}
				};
				m2lrThread.setName("M2LR_THREAD");
			}
			if (m2 != null) //leftRight != null)
				m2lrThread.start();
			if (m1 != null) //(foreAft != null)
				m1fbThread.start();
			// if(lrThread  == null &&  (fwdThread!=null && !forwardBackPulseComplete))
			// if(fwdThread == null &&  (lrThead!=null   && !leftRightPulseComplete  )) 
			// 
			while (m2lrThread != null && m2lrState != null)
			{
				//logger.info("......................m2lrState/" + m2lrState);
				Thread.sleep(200);
			}
			while (m1fbThread != null && m1fbState != null)
			{
				///////logger.info("......................m1fbState/" + m1fbState);
				Thread.sleep(200);
			}
			myMotors.brakeAll();
			logger.info("...end move!");
		} catch (Throwable t) {
			t.printStackTrace();
			System.err.println("ERROR SETTING PIN");
		}
	} 
	
	protected void pulse(final String m1foreAft, final String m2leftRight, int dutyCycleHi, int dutyCycleLo,
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
					logger.debug("     turning on for [" + numMsHi + "]ms...");
					myMotors.lowLevelMove(m1foreAft,m2leftRight);
					Thread.sleep(numMsHi);

					if (numMsLo > 0) {
						time = System.currentTimeMillis();
						logger.debug("     turning off for [" + numMsLo + "]ms...");
						myMotors.lowLevelStop(m1foreAft,m2leftRight);
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
