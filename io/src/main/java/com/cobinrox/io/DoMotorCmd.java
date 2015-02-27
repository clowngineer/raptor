package com.cobinrox.io;
import java.util.Properties;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.cobinrox.common.Utils;
import com.cobinrox.io.impl.IMotorControl;
import com.cobinrox.io.impl.MotorProps;
import com.cobinrox.io.impl.gpio.Pi4jMotorControl;
import com.cobinrox.io.impl.gpio.WiringPiMotorControl;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class DoMotorCmd {
	static final Logger logger = Logger.getLogger(DoMotorCmd.class);
	MotorProps mp = new MotorProps();
	IMotorControl motor;
	public static void main(String args[]) {
		DoMotorCmd dmc = new DoMotorCmd( );
		
		String input = "";
		Scanner scanIn = new Scanner(System.in);
		while(!input.equals("x"))
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
        if( mp.GPIO_LIB.equals(MotorProps.GPIO_PI4J_LIB_PROP_VAL))
        	motor = new Pi4jMotorControl();
        else
        	motor = new WiringPiMotorControl();
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
