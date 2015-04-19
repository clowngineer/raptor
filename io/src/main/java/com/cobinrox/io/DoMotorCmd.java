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
	static final String VERSION = "410";
	
	MotorProps mp = new MotorProps();
	IMotorControl motor;
	public static void main(String args[]) {
		System.out.println("* * * * * * * VERSION " + VERSION + " * * * * * * * *");
		DoMotorCmd dmc = new DoMotorCmd( );
		
		String input = "";
		Scanner scanIn = new Scanner(System.in);
		while(!input.equalsIgnoreCase("x"))
		{
		   System.out.println("Enter F/L/R/B/FL/FR/BL/BR/X --> ");
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
        	motor.initHardware();
        }
        catch(Throwable t)
        {
        	logger.error("COULD NOT INIT HWARE",t);
        	return;
        }
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
