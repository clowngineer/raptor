package com.cobinrox.io.impl.gpio;

import org.apache.log4j.Logger;

import com.cobinrox.io.impl.MotorProps;

public class WiringPiMotorControl extends AbstractMotorControl {
	static final Logger logger = Logger.getLogger(WiringPiMotorControl.class);
	MotorProps mp; // = new MotorProps();
	static Runtime rt = Runtime.getRuntime();

	String fwdCmd;
	String lrCmd;

	public WiringPiMotorControl() {
		super();
	}

	public void initHardware(MotorProps mp) throws Throwable {
		//mp.setProps(false);
		this.mp = mp;
		logger.info("Initializing pins to OUTPUT MODE");
		try {
			if (!mp.SIMULATE_PI) {
				Process p = rt.exec("gpio mode " + mp.FWD_GPIO_PIN_NUM + " out");
				p.waitFor();
				p = rt.exec("gpio mode " + mp.BACK_GPIO_PIN_NUM + " out");
				p.waitFor();
				if (mp.NUM_MOTORS > 1) {
					p = rt.exec("gpio mode " + mp.LEFT_GPIO_PIN_NUM + " out");
					p.waitFor();
					p = rt.exec("gpio mode " + mp.RIGHT_GPIO_PIN_NUM + " out");
					p.waitFor();
				}
				brakeAll();
			} else {
				logger.info("Simulating setting pins to OUTPUT MODE");
			}
		} catch (Throwable t) {
			t.printStackTrace();
			System.err.println("ERROR setting pin modes!  Cannot run!");
			return;
		}
	}

	String m1TString;

	private void setM1T() {
		m1T = new Thread() {
			public void run() {
				setName("M1");
				int i = 0;
				while (true) {
					if( m1TString != null ) {
						System.out.print(m1TString);
						i++;
						if (i > 300000) {
							i = 0;
							System.out.println("");
						}
					}
					try
					{
					sleep(1);}catch(Throwable t){}
				}
			}
		};
	}

	Thread m1T;
	Thread m2T;
	String m2TString;

	private void setM2T() {
		m2T = new Thread() {
			int i;

			public void run() {
				setName("M2");
				while (true)  {
					if( m2TString != null ) {
						System.out.print(m2TString);
						i++;
						if (i > 300000) {
							i = 0;
							System.out.println("");
						}
					}
					try
					{
						sleep(1);}catch(Throwable t){}
				}
			}
		};
	}

	public void lowLevelMove(final String foreAft, final String leftRight) throws Throwable {
		logger.debug("          low level move [" + foreAft + "] [" + leftRight + "]" +
				(mp.SIMULATE_PI ? "SIMULATED" : "") + "...");
		Process p = null;
		fwdCmd = null;
		lrCmd = null;
		String m1Info = null;
		String m2Info = null;


		if (foreAft != null && (foreAft.equals(mp.FORWARD) || foreAft.equals(mp.BACKWARD))) {
			fwdCmd = "gpio write "
					+ (foreAft.equals(mp.FORWARD) ?
					mp.FWD_GPIO_PIN_NUM
					:
					mp.BACK_GPIO_PIN_NUM);
			m1Info = fwdCmd + " (m1 " + (foreAft.equals(mp.FORWARD) ? "+" : "-") + ")";
			m1TString = (foreAft.equals(mp.FORWARD) ? "F" : "B");
			if (m1T == null ){ setM1T(); m1T.start();}
			//if( m1T.isInterrupted() ){m1T=null;setM1T();m1T.start();}

		}
		if (leftRight != null && (leftRight.equals(mp.LEFT) || leftRight.equals(mp.RIGHT))) {
			lrCmd = "gpio write "
					+ (leftRight.equals(mp.LEFT) ?
					mp.LEFT_GPIO_PIN_NUM
					:
					mp.RIGHT_GPIO_PIN_NUM);
			m2TString = (leftRight.equals(mp.LEFT) ? "L" : "R");
			if (m2T == null){ setM2T(); m2T.start();}
			//if (m2T != null && !m2T.isAlive()) m2T.start();
			m2Info = lrCmd + " (m2 " + (leftRight.equals(mp.LEFT) ? "+" : "-") + ")";

		}
		String pulseOnCmd = (fwdCmd != null ? fwdCmd : lrCmd) + " " + mp.GPIO_ON;
		logger.debug("          " + pulseOnCmd + (mp.SIMULATE_PI ? " SIMULATED" : ""));
		logger.debug("          " + (m1Info == null ? m2Info : m1Info) + (mp.SIMULATE_PI ? " SIMULATED" : ""));
		if (!mp.SIMULATE_PI) {
			p = rt.exec(pulseOnCmd);
			p.waitFor();
		}
		//kind of annoying logger.info("          ...end low level move");
	}

	public void lowLevelStop(final String m1foreAft, final String m2leftRight) throws Throwable {
		logger.debug("          low level stopping [" + m1foreAft + "] [" + m2leftRight + "]" +
				(mp.SIMULATE_PI ? "SIMULATED" : "") + "...");
		Process p = null;
		if (m1foreAft!=null ) {
			fwdCmd = "gpio write "
					+ (m1foreAft.equals(mp.FORWARD) ?
					mp.FWD_GPIO_PIN_NUM
					:
					mp.BACK_GPIO_PIN_NUM);
			m1TString = (m1foreAft.equals(mp.FORWARD) ? "f" : "b");
			if (m1T == null){ setM1T(); m1T.start();}
			//if (m1T != null && !m1T.isAlive())
		}
		if (m2leftRight!= null) {
			lrCmd = "gpio write "
					+ (m2leftRight.equals(mp.LEFT) ?
					mp.LEFT_GPIO_PIN_NUM
					:
					mp.RIGHT_GPIO_PIN_NUM);
			m2TString = (m2leftRight.equals(mp.LEFT) ? "l" : "r");
			if (m2T == null){setM2T(); m2T.start();}
			//if (m2T != null && !m2T.isAlive()) m2T.start();
		}
		String pulseOffCmd = (fwdCmd != null ? fwdCmd : lrCmd) + " " + mp.GPIO_OFF;
		logger.debug("          " + pulseOffCmd + (mp.SIMULATE_PI ? " SIMULATED" : ""));
		if (!mp.SIMULATE_PI) {
			p = rt.exec(pulseOffCmd);
			p.waitFor();
		}
		logger.debug("          ...end low level stop");
	}

	public void brakeAll() {
		Process p = null;
		try {
			long start = new java.util.Date().getTime();
			logger.debug("     Shutdown (brake) all pins...");
			String cmd = "gpio write " + mp.FWD_GPIO_PIN_NUM + " " + mp.GPIO_OFF;
			logger.debug(cmd + (mp.SIMULATE_PI ? " SIMULATED" : ""));
			if (!mp.SIMULATE_PI) {
				p = rt.exec(cmd);
				p.waitFor();
			}
			long stop = new java.util.Date().getTime();
			long dur = stop - start;
			logger.debug("stop " + stop + " dur " + dur);
			cmd = "gpio write " + mp.BACK_GPIO_PIN_NUM + " " + mp.GPIO_OFF;
			logger.debug(cmd + (mp.SIMULATE_PI ? " SIMULATED" : ""));
			if (!mp.SIMULATE_PI) {
				p = rt.exec(cmd);
				p.waitFor();
			}
			if (mp.NUM_MOTORS > 1) {
				cmd = "gpio write " + mp.LEFT_GPIO_PIN_NUM + " " + mp.GPIO_OFF;
				logger.debug(cmd + (mp.SIMULATE_PI ? " SIMULATED" : ""));
				if (!mp.SIMULATE_PI) {
					p = rt.exec(cmd);
					p.waitFor();
				}

				cmd = "gpio write " + mp.RIGHT_GPIO_PIN_NUM + " " + mp.GPIO_OFF;
				logger.debug(cmd + (mp.SIMULATE_PI ? " SIMULATED" : ""));
				if (!mp.SIMULATE_PI) {
					p = rt.exec(cmd);
					p.waitFor();
				}
			}
			logger.debug("     ...end shut down all pins");
		} catch (Throwable t) {
			t.printStackTrace();
			System.err.println("ERROR CLEARING ALL");
		}
		try {
			m2TString = null;
			m1TString = null;

		} catch (Throwable t) {

		}
	}
}
