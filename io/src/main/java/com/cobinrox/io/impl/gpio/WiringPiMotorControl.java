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
			if (!mp.simulate_pi) {
				Process p = rt.exec("gpio mode " + mp.fwd_gpio_pin_num + " out");
				p.waitFor();
				p = rt.exec("gpio mode " + mp.back_gpio_pin_num + " out");
				p.waitFor();
				if (mp.NUM_MOTORS > 1) {
					p = rt.exec("gpio mode " + mp.left_gpio_pin_num + " out");
					p.waitFor();
					p = rt.exec("gpio mode " + mp.right_gpio_pin_num + " out");
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

	String m1InfoString;
	Thread m1InfoThread;
	Thread m2InfoThread;
	String m2InfoString;

	private void setM1T() {
		m1InfoThread = new Thread() {
			public void run() {
				setName("M1");
				int i = 0;
				while (true) {
					if( m1InfoString != null ) {
						System.out.print("[" + m1InfoString + (i+1) + "]");
						i++;
						if (i > 80) {
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



	private void setM2T() {
		m2InfoThread = new Thread() {
			int i;

			public void run() {
				setName("M2");
				while (true)  {
					if( m2InfoString != null ) {
						//System.out.print(m2InfoString);
						System.out.print("[" + m2InfoString + (i+1) + "]");

						i++;
						if (i > 80) {
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
				(mp.simulate_pi ? "SIMULATED" : "") + "...");
		Process p = null;
		fwdCmd = null;
		lrCmd = null;
		String m1Info = null;
		String m2Info = null;


		if (foreAft != null && (foreAft.equals(mp.FORWARD) || foreAft.equals(mp.BACKWARD))) {
			fwdCmd = "gpio write "
					+ (foreAft.equals(mp.FORWARD) ?
					mp.fwd_gpio_pin_num
					:
					mp.back_gpio_pin_num);
			m1Info = fwdCmd + " (m1 " + (foreAft.equals(mp.FORWARD) ? "+" : "-") + ")";
			m1InfoString = (foreAft.equals(mp.FORWARD) ? "F" : "B");
			if (m1InfoThread == null ){ setM1T(); m1InfoThread.start();}
			//if( m1InfoThread.isInterrupted() ){m1InfoThread=null;setM1T();m1InfoThread.start();}

		}
		if (leftRight != null && (leftRight.equals(mp.LEFT) || leftRight.equals(mp.RIGHT))) {
			lrCmd = "gpio write "
					+ (leftRight.equals(mp.LEFT) ?
					mp.left_gpio_pin_num
					:
					mp.right_gpio_pin_num);
			m2InfoString = (leftRight.equals(mp.LEFT) ? "L" : "R");
			if (m2InfoThread == null){ setM2T(); m2InfoThread.start();}
			//if (m2InfoThread != null && !m2InfoThread.isAlive()) m2InfoThread.start();
			m2Info = lrCmd + " (m2 " + (leftRight.equals(mp.LEFT) ? "+" : "-") + ")";

		}
		String pulseOnCmd = (fwdCmd != null ? fwdCmd : lrCmd) + " " + mp.gpio_on;
		logger.debug("          " + pulseOnCmd + (mp.simulate_pi ? " SIMULATED" : ""));
		logger.debug("          " + (m1Info == null ? m2Info : m1Info) + (mp.simulate_pi ? " SIMULATED" : ""));
		if (!mp.simulate_pi) {
			p = rt.exec(pulseOnCmd);
			p.waitFor();
		}
		else
		{
			//logger.info("Simulate sending a low level pulse");

		}
		//kind of annoying logger.info("          ...end low level move");
	}

	public void lowLevelStop(final String m1foreAft, final String m2leftRight) throws Throwable {
		logger.debug("          low level stopping [" + m1foreAft + "] [" + m2leftRight + "]" +
				(mp.simulate_pi ? "SIMULATED" : "") + "...");
		Process p = null;
		if (m1foreAft!=null ) {
			fwdCmd = "gpio write "
					+ (m1foreAft.equals(mp.FORWARD) ?
					mp.fwd_gpio_pin_num
					:
					mp.back_gpio_pin_num);
			m1InfoString = (m1foreAft.equals(mp.FORWARD) ? "f" : "b");
			if (m1InfoThread == null){ setM1T(); m1InfoThread.start();}
			//if (m1InfoThread != null && !m1InfoThread.isAlive())
		}
		if (m2leftRight!= null) {
			lrCmd = "gpio write "
					+ (m2leftRight.equals(mp.LEFT) ?
					mp.left_gpio_pin_num
					:
					mp.right_gpio_pin_num);
			m2InfoString = (m2leftRight.equals(mp.LEFT) ? "l" : "r");
			if (m2InfoThread == null){setM2T(); m2InfoThread.start();}
			//if (m2InfoThread != null && !m2InfoThread.isAlive()) m2InfoThread.start();
		}
		String pulseOffCmd = (fwdCmd != null ? fwdCmd : lrCmd) + " " + mp.gpio_off;
		logger.debug("          " + pulseOffCmd + (mp.simulate_pi ? " SIMULATED" : ""));
		if (!mp.simulate_pi) {
			p = rt.exec(pulseOffCmd);
			p.waitFor();
		}
		else
		{
			//logger.info("Simulate sending a low level stop");
		}
		//if( m1foreAft != null) m1InfoString="*";
		//if( m2leftRight != null) m2InfoString = "&";
		logger.debug("          ...end low level stop");
	}

	public void brakeAll() {
		Process p = null;
		try {
			long start = new java.util.Date().getTime();
			logger.debug("     Shutdown (brake) all pins...");
			String cmd = "gpio write " + mp.fwd_gpio_pin_num + " " + mp.gpio_off;
			logger.debug(cmd + (mp.simulate_pi ? " SIMULATED" : ""));
			if (!mp.simulate_pi) {
				p = rt.exec(cmd);
				p.waitFor();
			}
			long stop = new java.util.Date().getTime();
			long dur = stop - start;
			logger.debug("stop " + stop + " dur " + dur);
			cmd = "gpio write " + mp.back_gpio_pin_num + " " + mp.gpio_off;
			logger.debug(cmd + (mp.simulate_pi ? " SIMULATED" : ""));
			if (!mp.simulate_pi) {
				p = rt.exec(cmd);
				p.waitFor();
			}
			if (mp.NUM_MOTORS > 1) {
				cmd = "gpio write " + mp.left_gpio_pin_num + " " + mp.gpio_off;
				logger.debug(cmd + (mp.simulate_pi ? " SIMULATED" : ""));
				if (!mp.simulate_pi) {
					p = rt.exec(cmd);
					p.waitFor();
				}

				cmd = "gpio write " + mp.right_gpio_pin_num + " " + mp.gpio_off;
				logger.debug(cmd + (mp.simulate_pi ? " SIMULATED" : ""));
				if (!mp.simulate_pi) {
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
			m2InfoString = null;
			m1InfoString = null;

		} catch (Throwable t) {

		}
	}
}
