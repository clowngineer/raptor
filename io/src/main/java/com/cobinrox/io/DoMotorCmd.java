package com.cobinrox.io;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.cobinrox.common.Utils;
import com.cobinrox.io.impl.IMotorControl;
//import com.cobinrox.io.impl.IMotorControl;
import com.cobinrox.io.impl.MotorProps;
import com.cobinrox.io.impl.RemoteCarMotorControl;
import com.cobinrox.io.impl.SimpleSingleMotorControl;
import com.cobinrox.io.impl.WheelChairMotorControl;
/* test */

public class DoMotorCmd {
	static final Logger logger = Logger.getLogger(DoMotorCmd.class);
	static final String VERSION = "412";
	
	MotorProps mp = new MotorProps();
	IMotorControl motor;
	public static void main(String args[]) {
		System.out.println("* * * * * * * VERSION " + VERSION + " * * * * * * * *");
		DoMotorCmd dmc = new DoMotorCmd( );
		
		String input = "";
		Scanner scanIn = new Scanner(System.in);
		while(!input.equalsIgnoreCase("x"))
		{
		   System.out.println("Enter F/L/R/B/FL/FR/BL/BR/D/X --> ");
	       input = scanIn.nextLine();
	       System.out.println("You entered [" + input + "]");
	       dmc.doThis(input);
		}
        scanIn.close();            

		//dmc.moveOneDutyCycle(MotorProps.FORWARD, null);
	}
	public void doThis(String fblr )
	{
	       if( fblr.equalsIgnoreCase("F"))
	    	    moveOneDutyCycle(MotorProps.FORWARD, null);
	       else if(fblr.equalsIgnoreCase("B"))
	    	    moveOneDutyCycle(MotorProps.BACKWARD, null);
	       else if(fblr.equalsIgnoreCase("L"))
	    	     moveOneDutyCycle(null, MotorProps.LEFT );
	       else if(fblr.equalsIgnoreCase("R"))
	    	     moveOneDutyCycle(null, MotorProps.RIGHT );

	       else if(fblr.equalsIgnoreCase("FL"))
	    	    moveOneDutyCycle(MotorProps.FORWARD, MotorProps.LEFT);
	       else if(fblr.equalsIgnoreCase("FR"))
	    	    moveOneDutyCycle(MotorProps.FORWARD, MotorProps.RIGHT);

	       else if(fblr.equalsIgnoreCase("BL"))
	    	    moveOneDutyCycle(MotorProps.BACKWARD, MotorProps.LEFT);
	       else if(fblr.equalsIgnoreCase("BR"))
	    	    moveOneDutyCycle(MotorProps.BACKWARD, MotorProps.RIGHT);
	       
	       else if(fblr.toUpperCase().startsWith("D"))
	    	    System.out.println(changeData(fblr.toUpperCase()));
	}
	public DoMotorCmd( )
	{
		mp.setProps(true);
                if( mp.SIMULATE_PI )
                {
                   logger.info("***************************");
                   logger.info("     W A R N I N G   ! !  *");
                   logger.info("                          *");
                   logger.info(" Simulating GPIO Pins !!  *");
                   logger.info("                          *");
                   logger.info("***************************");
                }
        if( mp.MOTOR_CONFIG.equals(MotorProps.MOTOR_CONFIG_PROP_VAL_REMOTE_CAR))
        	motor = new RemoteCarMotorControl() ;
        else if( mp.MOTOR_CONFIG.equals(MotorProps.MOTOR_CONFIG_PROP_VAL_SINGLE))
        	motor = new SimpleSingleMotorControl();
        else
        	motor = new WheelChairMotorControl();
        try
        {
        	motor.initHardware(mp);
        }
        catch(Throwable t)
        {
        	logger.error("COULD NOT INIT HWARE",t);
        	return;
        }
	}
	
	public String changeData(String dataStr)
	{
		String ret = "done";
		System.out.println("data change request [" + dataStr + "]");
		
		String[] parsed = dataStr.split("_");
		if( parsed.length < 3 )
		{
			ret = "Invalid data change request [" + dataStr + "] Expecting one of\n";
			ret+= dchelp();
			
			return ret;
		}
		String key = parsed[1];
		String newVal = parsed[2];
		
		//sb.append("D_RT_<float> (set cmd run time ms)");
		//sb.append("D_HI_<float> (set duty hi time ms)");
		//sb.append("D_LO_<float> (set duty lo time ms)");
		try
		{
			if( key.equals("RT"))
			{
				mp.CMD_RUN_TIME_MS = Integer.parseInt(newVal);
				ret = "Updated " + mp.CMD_RUN_TIME_MS_PROP + " [" + newVal + "]";
			}
			else if(key.equals("HI"))
			{
				mp.DUTY_CYCLE_HI_MS = Integer.parseInt(newVal);
				ret = "Updated " + mp.DUTY_CYCLE_HI_MS_PROP + " [" + newVal + "]";

			}
			else if(key.equals("LO"))
			{
				mp.DUTY_CYCLE_LO_MS = Integer.parseInt(newVal);
				ret = "Updated " + mp.DUTY_CYCLE_LO_MS_PROP + " [" + newVal + "]";

			}
		}
		catch(Throwable t)
		{
			ret = "Invalid data change request [" + dataStr + "] Expecting one of\n";
			ret+= dchelp();
			
			return ret;
		}
		return ret;
	}
	public static String dchelp()
	{
		StringBuffer sb = new StringBuffer(
				  "D_SWAP_FB (swap front/back motors)\n");
		sb.append("D_SWAP_LR (swap left/right motors)\n");
		sb.append("D_F_<integer> (set FRONT GPIO )\n");
		sb.append("D_B_<integer> (set BACK GPIO )\n");
		sb.append("D_L_<integer> (set LEFT GPIO )\n");
		sb.append("D_R_<integer> (set RIGHT GPIO )\n");
		sb.append("D_RT_<float> (set cmd run time ms)\n");
		sb.append("D_HI_<float> (set duty hi time ms)\n");
		sb.append("D_LO_<float> (set duty lo time ms)\n");
		sb.append("D_SIM_<integer> (set simulate mode)");
		return sb.toString();
	}
	public String moveOneDutyCycle(String fwdAft, String leftRight)
	{
		String err = null;
		try
		{
			motor.move(fwdAft,leftRight);
		}
		catch(Throwable t)
		{
			err = "ERROR DURING MOVE " + Utils.getStackTraceAsString(t);
			logger.error("ERROR DURING MOVE",t);
		}
		return err;
	}

	public void shutdown() throws Throwable
	{
		motor.shutdown();
	}
	public int getDutyCycleHiMs() {
		return mp.DUTY_CYCLE_HI_MS;
	}
	public void setDutyCycleHiMs(int dutyCycleHiMs) {
		mp.DUTY_CYCLE_HI_MS = dutyCycleHiMs;
	}
}
