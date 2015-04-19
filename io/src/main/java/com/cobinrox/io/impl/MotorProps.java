package com.cobinrox.io.impl;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.cobinrox.common.Utils;

public class MotorProps {
	static final Logger logger = Logger.getLogger(MotorProps.class);
	public final static String FORWARD  = "forward";
	public final static String BACKWARD = "backward";
	public final static String LEFT     = "left";
	public final static String RIGHT    = "right";
	
	public static int    FWD_GPIO_PIN_NUM  ;public static final String FWD_GPIO_PIN_PROP     = "FWD_GPIO_PIN";
	public static int    BACK_GPIO_PIN_NUM ;public static final String BACK_GPIO_PIN_PROP    = "BACK_GPIO_PIN";
	public static int    LEFT_GPIO_PIN_NUM ;public static final String LEFT_GPIO_PIN_PROP    = "LEFT_GPIO_PIN";
	public static int    RIGHT_GPIO_PIN_NUM;public static final String RIGHT_GPIO_PIN_PROP   = "RIGHT_GPIO_PIN";
	public static int    FWD_GPIO_OUT      ;public static final String FWD_GPIO_OUT_STR_PROP = "FWD_GPIO_OUT_STR";  
	public static int    BACK_GPIO_OUT     ;public static final String BACK_GPIO_OUT_STR_PROP= "BACK_GPIO_OUT_STR";
	public static int    LEFT_GPIO_OUT     ;public static final String LEFT_GPIO_OUT_STR_PROP= "LEFT_GPIO_OUT_STR";
	public static int    RIGHT_GPIO_OUT    ;public static final String RIGHT_GPIO_OUT_STR_PROP= "RIGHT_GPIO_OUT_STR";
	public static int    CMD_RUN_TIME_MS   ;public static final String CMD_RUN_TIME_MS_PROP  = "CMD_RUN_TIME_MS";
	public static int    GPIO_ON           ;public static final String GPIO_ON_STR_PROP      = "GPIO_ON_STR";  
	public static int    GPIO_OFF          ;public static final String GPIO_OFF_STR_PROP     = "GPIO_OFF_STR";
	public static String GPIO_LIB          ;public static final String GPIO_LIB_PROP         = "GPIO_LIBRARY";
	public static int    NUM_MOTORS        ;public static final String NUM_MOTORS_PROP = "NUM_MOTORS";
	public static String MOTOR_CONFIG      ;public static final String MOTOR_CONFIG_PROP = "MOTOR_CONFIG";
	 
	
	public static final String GPIO_PI4J_LIB_PROP_VAL      = "pi4j";
	public static final String GPIO_WIRING_PI_LIB_PROP_VAL = "wiring_pi";
	
	public static final String MOTOR_CONFIG_PROP_VAL_SINGLE ="simple_single";
	public static final String MOTOR_CONFIG_PROP_VAL_REMOTE_CAR ="remote_car";
	public static final String MOTOR_CONFIG_PROP_VAL_WHEEL_CHAIR ="wheel_chair";
	
	
	public int DUTY_CYCLE_HI_MS             ;public static final String DUTY_CYCLE_HI_MS_PROP = "DUTY_CYCLE_HI_MS";
	public int DUTY_CYCLE_LO_MS             ;public static final String DUTY_CYCLE_LO_MS_PROP = "DUTY_CYCLE_LO_MS";

	public boolean SIMULATE_PI;public static final String SIMULATE_PI_PROP = "SIMULATE_PI";

	public void setProps(boolean verbose)
	{
		try
		{
		Properties properties = Utils.readProps(this,"io");
		SIMULATE_PI = Boolean.parseBoolean(properties.getProperty(SIMULATE_PI_PROP).trim());
		if(verbose)logger.info("Simulate PI: [" + SIMULATE_PI + "]");
		
		GPIO_LIB = properties.getProperty(GPIO_LIB_PROP).trim();
		if(verbose)logger.info("Use gpio lib: [" + GPIO_LIB + "]");
		
		NUM_MOTORS   = Integer.parseInt(properties.getProperty(NUM_MOTORS_PROP).trim());
		if(verbose)logger.info("Num motors: [" + NUM_MOTORS + "]");

		MOTOR_CONFIG = properties.getProperty(MOTOR_CONFIG_PROP).trim();
		if(verbose)logger.info("Motor config: [" + MOTOR_CONFIG + "]");
		
		FWD_GPIO_PIN_NUM       = Integer.parseInt(properties.getProperty(FWD_GPIO_PIN_PROP).trim());
		BACK_GPIO_PIN_NUM      = Integer.parseInt(properties.getProperty(BACK_GPIO_PIN_PROP).trim());
		LEFT_GPIO_PIN_NUM      = Integer.parseInt(properties.getProperty(LEFT_GPIO_PIN_PROP).trim());
		RIGHT_GPIO_PIN_NUM     = Integer.parseInt(properties.getProperty(RIGHT_GPIO_PIN_PROP).trim());
		if(verbose)logger.info("Pins to use: (f/b/l/r) [" + FWD_GPIO_PIN_NUM  + "/" +
		                                         BACK_GPIO_PIN_NUM + "/" +
				                                 LEFT_GPIO_PIN_NUM + "/" +
		                                         RIGHT_GPIO_PIN_NUM+ "]");
				                                 
		
		FWD_GPIO_OUT    = Integer.parseInt(properties.getProperty(FWD_GPIO_OUT_STR_PROP).trim());
		BACK_GPIO_OUT   = Integer.parseInt(properties.getProperty(BACK_GPIO_OUT_STR_PROP).trim());
		LEFT_GPIO_OUT   = Integer.parseInt(properties.getProperty(LEFT_GPIO_OUT_STR_PROP).trim());
		RIGHT_GPIO_OUT  = Integer.parseInt(properties.getProperty(RIGHT_GPIO_OUT_STR_PROP).trim());
		
		DUTY_CYCLE_HI_MS   = Integer.parseInt(properties.getProperty(DUTY_CYCLE_HI_MS_PROP).trim());
		DUTY_CYCLE_LO_MS   = Integer.parseInt(properties.getProperty(DUTY_CYCLE_LO_MS_PROP).trim());
		CMD_RUN_TIME_MS    = Integer.parseInt(properties.getProperty(CMD_RUN_TIME_MS_PROP).trim());
		if(verbose)logger.info("Duty cycle: (hi/lo) [" + DUTY_CYCLE_HI_MS + "/" + DUTY_CYCLE_LO_MS + "]");
		if(verbose)logger.info("Cmd time: [" + CMD_RUN_TIME_MS + "]");
        
		
		GPIO_ON   = Integer.parseInt(properties.getProperty(GPIO_ON_STR_PROP).trim());
		GPIO_OFF  = Integer.parseInt(properties.getProperty(GPIO_OFF_STR_PROP).trim());
		
		}
		catch(Throwable t)
		{
			logger.error("Reading or setting properties",t);
		}
		
	}
}
