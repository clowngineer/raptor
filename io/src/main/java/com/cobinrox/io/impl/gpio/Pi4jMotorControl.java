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
	MotorProps mp;//= new MotorProps();
	
	public void initHardware(MotorProps mp) throws Throwable
	{
		//mp.setProps(false);
		this.mp = mp;
		logger.info("Initializing pins to OUTPUT MODE");

		if( !mp.simulate_pi)
		{
			try
			{
			pi4jController = GpioFactory.getInstance();
			PinState pinoff = mp.gpio_on ==1?PinState.HIGH:PinState.LOW;
			
			Pin pi4jPinNum = null;
			switch(mp.fwd_gpio_pin_num)
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
	        
	        switch(mp.back_gpio_pin_num)
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
		        switch(mp.left_gpio_pin_num)
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
		 
		        switch(mp.right_gpio_pin_num)
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
                     (mp.simulate_pi ?"SIMULATED":""));
		String m1Info = null;
		String m2Info = null;
		if (foreAft != null)
		{
			m1Info = " (m1 " + (foreAft.equals(mp.FORWARD) ? "+" : "-") + ")";
			logger.info(m1Info);
			if( !mp.simulate_pi) {
				if (foreAft.equals(mp.FORWARD)) {
					pi4jForwardPin.high();
				} else {
					pi4jBackPin.high();
				}
			}

		}
		if (leftRight != null)
		{
			m2Info = " (m2 " + (leftRight.equals(mp.LEFT) ? "+" : "-") + ")";
			logger.info(m2Info);
			if( !mp.simulate_pi) {
				if (leftRight.equals(mp.LEFT)) {
					pi4jLeftPin.high();
				} else {
					pi4jRightPin.high();
				}
			}
		}
    }

	public  void lowLevelStop(final String foreAft, final String leftRight) throws Throwable
	{
		logger.info("low level stop [" + foreAft + "] [" + leftRight + "]" +
				(mp.simulate_pi ? "SIMULATED" : ""));
		if( !mp.simulate_pi) {
			pi4jBackPin.low();
			pi4jForwardPin.low();
			pi4jLeftPin.low();
			pi4jRightPin.low();
		}
	}
	public  void brakeAll() {
		if (!mp.simulate_pi) {
			pi4jBackPin.low();
			pi4jForwardPin.low();
			pi4jLeftPin.low();
			pi4jRightPin.low();
		} else {
			logger.info("Simulating shut down all pins");
		}
	}
	public void shutdown() throws Throwable
	{
		brakeAll();
	}
}
