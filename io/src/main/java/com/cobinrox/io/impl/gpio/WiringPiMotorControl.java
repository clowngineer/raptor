package com.cobinrox.io.impl.gpio;

import org.apache.log4j.Logger;

import com.cobinrox.io.impl.MotorProps;

public class WiringPiMotorControl extends AbstractMotorControl{
	static final Logger logger = Logger.getLogger(WiringPiMotorControl.class);
	MotorProps mp = new MotorProps();
	static Runtime rt = Runtime.getRuntime();

	String fwdCmd;
	String lrCmd;
	
	public WiringPiMotorControl(){super();}
	public void initHardware() throws Throwable
	{	
		mp.setProps(false);
		logger.info("Initializing pins to OUTPUT MODE");
		try
		{
	        if( !mp.SIMULATE_PI )
	        {
	           Process p = rt.exec("gpio mode " + mp.FWD_GPIO_PIN_NUM + " out");p.waitFor();
	           p         = rt.exec("gpio mode " + mp.BACK_GPIO_PIN_NUM+ " out"); p.waitFor();
	           if( mp.NUM_MOTORS>1)
	           {
		           p         = rt.exec("gpio mode " + mp.LEFT_GPIO_PIN_NUM+ " out"); p.waitFor();
		           p         = rt.exec("gpio mode " + mp.RIGHT_GPIO_PIN_NUM+" out"); p.waitFor();
	           }
	           brakeAll();
	        }
	        else
	        {
	            logger.info("Simulating setting pins to OUTPUT MODE");
	        }
		}
		catch(Throwable t)
		{
	       t.printStackTrace();
	       System.err.println("ERROR setting pin modes!  Cannot run!");
	       return ;
		}
	}

protected void lowLevelMove(final String foreAft, final String leftRight) throws Throwable
{
	logger.info("          low level move [" + foreAft + "] [" + leftRight + "]" +
            (mp.SIMULATE_PI?"SIMULATED":"") + "...");
	Process p = null;
	fwdCmd = null;
	lrCmd = null;
	if (foreAft != null && (foreAft.equals(mp.FORWARD) || foreAft.equals(mp.BACKWARD))) {
		fwdCmd = "gpio write "
				+ (foreAft.equals(mp.FORWARD) ? 
						mp.FWD_GPIO_PIN_NUM
						: 
						mp.BACK_GPIO_PIN_NUM);
	}
	if (leftRight != null && (leftRight.equals(mp.LEFT) || leftRight.equals(mp.RIGHT))) {
		lrCmd = "gpio write "
				+ (leftRight.equals(mp.LEFT) ? 
						mp.LEFT_GPIO_PIN_NUM
						: 
						mp.RIGHT_GPIO_PIN_NUM);
	}
	String pulseOnCmd = (fwdCmd!=null?fwdCmd:lrCmd) + " " + mp.GPIO_ON;
	logger.info("          "+pulseOnCmd + (mp.SIMULATE_PI?" SIMULATED":""));
	if(!mp.SIMULATE_PI){p = rt.exec(pulseOnCmd);	p.waitFor();}
	logger.info("          ...end low level move");
}

protected void lowLevelStop(final String foreAft, final String leftRight) throws Throwable
{
	logger.info("          low level stopping [" + foreAft + "] [" + leftRight + "]" +
            (mp.SIMULATE_PI?"SIMULATED":"") + "...");
	Process p = null;
	if (foreAft.equals(mp.FORWARD) || foreAft.equals(mp.BACKWARD)) {
		fwdCmd = "gpio write "
				+ (foreAft.equals(mp.FORWARD) ? 
						mp.FWD_GPIO_PIN_NUM
						: 
						mp.BACK_GPIO_PIN_NUM);
	}
	if (leftRight.equals(mp.LEFT) || leftRight.equals(mp.RIGHT)) {
		lrCmd = "gpio write "
				+ (leftRight.equals(mp.LEFT) ? 
						mp.LEFT_GPIO_PIN_NUM
						: 
						mp.RIGHT_GPIO_PIN_NUM);
	}
	String pulseOffCmd = (fwdCmd!=null?fwdCmd:lrCmd) + " " + mp.GPIO_OFF;
	logger.info("          "+pulseOffCmd + (mp.SIMULATE_PI?" SIMULATED":""));
	if( !mp.SIMULATE_PI){p = rt.exec(pulseOffCmd);p.waitFor();}
	logger.info("          ...end low level stop");
}

protected void brakeAll() {
		Process p = null;
		try {
			long start = new java.util.Date().getTime();
			logger.info("     Shutdown (brake) all pins...");
			String cmd = "gpio write " + mp.FWD_GPIO_PIN_NUM + " " + mp.GPIO_OFF;
			logger.info(cmd + (mp.SIMULATE_PI?" SIMULATED":""));
			if( !mp.SIMULATE_PI){p = rt.exec(cmd);p.waitFor();}
			long stop = new java.util.Date().getTime();
			long dur = stop - start;
			logger.info("stop " + stop + " dur " + dur);
			cmd = "gpio write " + mp.BACK_GPIO_PIN_NUM + " "+ mp.GPIO_OFF;
			logger.info(cmd + (mp.SIMULATE_PI?" SIMULATED":""));
			if(!mp.SIMULATE_PI){p = rt.exec(cmd);p.waitFor();}
			if( mp.NUM_MOTORS > 1)
			{
				cmd = "gpio write " + mp.LEFT_GPIO_PIN_NUM + " "+ mp.GPIO_OFF;
				logger.info(cmd + (mp.SIMULATE_PI?" SIMULATED":""));
				if( !mp.SIMULATE_PI){p = rt.exec(cmd);p.waitFor();}
				
				cmd = "gpio write " + mp.RIGHT_GPIO_PIN_NUM + " "+ mp.GPIO_OFF;
				logger.info(cmd + (mp.SIMULATE_PI?" SIMULATED":""));
				if( !mp.SIMULATE_PI){p = rt.exec(cmd);p.waitFor();}
			}
			logger.info("     ...end shut down all pins");
		} catch (Throwable t) {
			t.printStackTrace();
			System.err.println("ERROR CLEARING ALL");
		}
	}
}
