package com.cobinrox.io.impl.gpio;

import org.apache.log4j.Logger;

import com.cobinrox.io.impl.MotorProps;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class Pi4jMotorControl extends AbstractMotorControl{
	static final Logger logger = Logger.getLogger(Pi4jMotorControl.class);

	GpioPinDigitalOutput pi4jForwardPin;
	GpioPinDigitalOutput pi4jBackPin;
	GpioPinDigitalOutput pi4jLeftPin;
	GpioPinDigitalOutput pi4jRightPin;
	GpioController pi4jController;
	MotorProps mp = new MotorProps();
	
	public void initHardware() throws Throwable
	{
		mp.setProps(false);
		logger.info("Initializing pins to OUTPUT MODE");

		if( !mp.SIMULATE_PI)
		{
			try
			{
			pi4jController = GpioFactory.getInstance();
			PinState pinoff = mp.GPIO_ON==1?PinState.HIGH:PinState.LOW;
			
			Pin pi4jPinNum = null;
			switch(mp.FWD_GPIO_PIN_NUM)
	        {
	        case 7: pi4jPinNum = RaspiPin.GPIO_07;break;
	        case 0: pi4jPinNum = RaspiPin.GPIO_00;break;
	        case 2: pi4jPinNum = RaspiPin.GPIO_02;break;
	        case 3: pi4jPinNum = RaspiPin.GPIO_03;break;
	        case 1: pi4jPinNum = RaspiPin.GPIO_01;break;
	        case 4: pi4jPinNum = RaspiPin.GPIO_04;break;
	        case 5: pi4jPinNum = RaspiPin.GPIO_05;break;
	        case 6: pi4jPinNum = RaspiPin.GPIO_06;break;
	        }
	        pi4jForwardPin = pi4jController.provisionDigitalOutputPin(pi4jPinNum,"FORWARD", pinoff);
	        
	        switch(mp.BACK_GPIO_PIN_NUM)
	        {
	        case 7: pi4jPinNum = RaspiPin.GPIO_07;break;
	        case 0: pi4jPinNum = RaspiPin.GPIO_00;break;
	        case 2: pi4jPinNum = RaspiPin.GPIO_02;break;
	        case 3: pi4jPinNum = RaspiPin.GPIO_03;break;
	        case 1: pi4jPinNum = RaspiPin.GPIO_01;break;
	        case 4: pi4jPinNum = RaspiPin.GPIO_04;break;
	        case 5: pi4jPinNum = RaspiPin.GPIO_05;break;
	        case 6: pi4jPinNum = RaspiPin.GPIO_06;break;
	        }
	        pi4jBackPin = pi4jController.provisionDigitalOutputPin(pi4jPinNum,"BACK", pinoff);
	        
	        if( mp.NUM_MOTORS>1)
	        {
		        switch(mp.LEFT_GPIO_PIN_NUM)
		        {
		        case 7: pi4jPinNum = RaspiPin.GPIO_07;break;
		        case 0: pi4jPinNum = RaspiPin.GPIO_00;break;
		        case 2: pi4jPinNum = RaspiPin.GPIO_02;break;
		        case 3: pi4jPinNum = RaspiPin.GPIO_03;break;
		        case 1: pi4jPinNum = RaspiPin.GPIO_01;break;
		        case 4: pi4jPinNum = RaspiPin.GPIO_04;break;
		        case 5: pi4jPinNum = RaspiPin.GPIO_05;break;
		        case 6: pi4jPinNum = RaspiPin.GPIO_06;break;
		        }
		        pi4jLeftPin = pi4jController.provisionDigitalOutputPin(pi4jPinNum,"LEFT", pinoff);
		 
		        switch(mp.RIGHT_GPIO_PIN_NUM)
		        {
		        case 7: pi4jPinNum = RaspiPin.GPIO_07;break;
		        case 0: pi4jPinNum = RaspiPin.GPIO_00;break;
		        case 2: pi4jPinNum = RaspiPin.GPIO_02;break;
		        case 3: pi4jPinNum = RaspiPin.GPIO_03;break;
		        case 1: pi4jPinNum = RaspiPin.GPIO_01;break;
		        case 4: pi4jPinNum = RaspiPin.GPIO_04;break;
		        case 5: pi4jPinNum = RaspiPin.GPIO_05;break;
		        case 6: pi4jPinNum = RaspiPin.GPIO_06;break;
		        }
		        pi4jRightPin = pi4jController.provisionDigitalOutputPin(pi4jPinNum,"RIGHT", pinoff);
				}
			}
			catch(Throwable t)
		      {
		         t.printStackTrace();
		         System.err.println("ERROR setting pin modes!  Cannot run!");
		         return ;
		      }
		}
		else
        {
            logger.info("Simulated setting pins to OUTPUT MODE");
        }
	}
	
	public  void lowLevelMove(final String foreAft, final String leftRight) throws Throwable
    {
		logger.info("low level move [" + foreAft + "] [" + leftRight + "]" +
                     (mp.SIMULATE_PI?"SIMULATED":""));
		if (foreAft.equals(mp.FORWARD) || foreAft.equals(mp.BACKWARD)) {
			
		}
		if (leftRight != null && (leftRight.equals(mp.LEFT) || leftRight.equals(mp.RIGHT))) {
			//
		}
    }

	public  void lowLevelStop(final String foreAft, final String leftRight) throws Throwable
	{
		logger.info("low level stop [" + foreAft + "] [" + leftRight + "]" +
                (mp.SIMULATE_PI?"SIMULATED":""));
	}
	public  void brakeAll() {
		if (!mp.SIMULATE_PI) {
			
		} else {
			logger.info("Simulating shut down all pins");
		}
	}
	public void shutdown() throws Throwable
	{
		brakeAll();
	}
}
