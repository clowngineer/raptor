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
	
	public static int fwd_gpio_pin_num;public static final String FWD_GPIO_PIN_PROP     = "FWD_GPIO_PIN";
	public static int back_gpio_pin_num;public static final String BACK_GPIO_PIN_PROP    = "BACK_GPIO_PIN";
	public static int left_gpio_pin_num;public static final String LEFT_GPIO_PIN_PROP    = "LEFT_GPIO_PIN";
	public static int right_gpio_pin_num;public static final String RIGHT_GPIO_PIN_PROP   = "RIGHT_GPIO_PIN";
	public static int fwd_gpio_out;public static final String FWD_GPIO_OUT_STR_PROP = "FWD_GPIO_OUT_STR";
	public static int back_gpio_out;public static final String BACK_GPIO_OUT_STR_PROP= "BACK_GPIO_OUT_STR";
	public static int left_gpio_out;public static final String LEFT_GPIO_OUT_STR_PROP= "LEFT_GPIO_OUT_STR";
	public static int right_gpio_out;public static final String RIGHT_GPIO_OUT_STR_PROP= "RIGHT_GPIO_OUT_STR";
	public static int cmdRunTimeMs;public static final String CMD_RUN_TIME_MS_PROP  = "CMD_RUN_TIME_MS";
	public static int gpio_on;public static final String GPIO_ON_STR_PROP      = "GPIO_ON_STR";
	public static int gpio_off;public static final String GPIO_OFF_STR_PROP     = "GPIO_OFF_STR";
	public static String GPIO_LIB          ;public static final String GPIO_LIB_PROP         = "GPIO_LIBRARY";
	public static int    NUM_MOTORS        ;public static final String NUM_MOTORS_PROP = "NUM_MOTORS";
	public static String MOTOR_CONFIG      ;public static final String MOTOR_CONFIG_PROP = "MOTOR_CONFIG";
	 
	
	public static final String GPIO_PI4J_LIB_PROP_VAL      = "pi4j";
	public static final String GPIO_WIRING_PI_LIB_PROP_VAL = "wiring_pi";
	
	public static final String MOTOR_CONFIG_PROP_VAL_SINGLE ="simple_single";
	public static final String MOTOR_CONFIG_PROP_VAL_REMOTE_CAR ="remote_car";
	public static final String MOTOR_CONFIG_PROP_VAL_WHEEL_CHAIR ="wheel_chair";
	
	
	public int duty_cycle_hi_ms;public static final String DUTY_CYCLE_HI_MS_PROP = "DUTY_CYCLE_HI_MS";
	public int duty_cycle_lo_ms;public static final String DUTY_CYCLE_LO_MS_PROP = "DUTY_CYCLE_LO_MS";
	//public int PERIOD_MS             ;public static final String PERIOD_MS_PROP = "PERIOD_MS";

	public boolean simulate_pi;public static final String SIMULATE_PI_PROP = "SIMULATE_PI";

	public void setProps(boolean verbose)
	{
		try
		{
		Properties properties = Utils.readProps(this,"io");
		simulate_pi = Boolean.parseBoolean(properties.getProperty(SIMULATE_PI_PROP).trim());
		if(verbose)logger.info("Simulate PI: [" + simulate_pi + "]");
		
		GPIO_LIB = properties.getProperty(GPIO_LIB_PROP).trim();
		if(verbose)logger.info("Use gpio lib: [" + GPIO_LIB + "]");
		
		NUM_MOTORS   = Integer.parseInt(properties.getProperty(NUM_MOTORS_PROP).trim());
		if(verbose)logger.info("Num motors: [" + NUM_MOTORS + "]");

		MOTOR_CONFIG = properties.getProperty(MOTOR_CONFIG_PROP).trim();
		if(verbose)logger.info("Motor config: [" + MOTOR_CONFIG + "]");
		
		fwd_gpio_pin_num = Integer.parseInt(properties.getProperty(FWD_GPIO_PIN_PROP).trim());
		back_gpio_pin_num = Integer.parseInt(properties.getProperty(BACK_GPIO_PIN_PROP).trim());
		left_gpio_pin_num = Integer.parseInt(properties.getProperty(LEFT_GPIO_PIN_PROP).trim());
		right_gpio_pin_num = Integer.parseInt(properties.getProperty(RIGHT_GPIO_PIN_PROP).trim());
		if(verbose)logger.info("Pins to use: (f/b/l/r) [" + fwd_gpio_pin_num + "/" +
				back_gpio_pin_num + "/" +
				left_gpio_pin_num + "/" +
				right_gpio_pin_num + "]");
				                                 
		
		fwd_gpio_out = Integer.parseInt(properties.getProperty(FWD_GPIO_OUT_STR_PROP).trim());
		back_gpio_out = Integer.parseInt(properties.getProperty(BACK_GPIO_OUT_STR_PROP).trim());
		left_gpio_out = Integer.parseInt(properties.getProperty(LEFT_GPIO_OUT_STR_PROP).trim());
		right_gpio_out = Integer.parseInt(properties.getProperty(RIGHT_GPIO_OUT_STR_PROP).trim());
		
		duty_cycle_hi_ms = Integer.parseInt(properties.getProperty(DUTY_CYCLE_HI_MS_PROP).trim());
			duty_cycle_lo_ms = Integer.parseInt(properties.getProperty(DUTY_CYCLE_LO_MS_PROP).trim());
		cmdRunTimeMs = Integer.parseInt(properties.getProperty(CMD_RUN_TIME_MS_PROP).trim());
			if(verbose)logger.info("Hi Duty Cycle: [" + duty_cycle_hi_ms + "]");
			if(verbose)logger.info("Lo Duty Cycle: [" + duty_cycle_lo_ms + "]");
		if(verbose)logger.info("Cmd time: [" + cmdRunTimeMs + "]");
        
		
		gpio_on = Integer.parseInt(properties.getProperty(GPIO_ON_STR_PROP).trim());
		gpio_off = Integer.parseInt(properties.getProperty(GPIO_OFF_STR_PROP).trim());
		
		}
		catch(Throwable t)
		{
			logger.error("Reading or setting properties",t);
		}
		
	}
}
