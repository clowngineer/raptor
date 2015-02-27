package com.cobinrox.blinds;

import com.cobinrox.io.DoMotorCmd;

public class BlindsMain
{
    public static void main(String args[] )
    {
    }
    protected int getCurrentTime()
    {
        // if it is > 5:30AM - 5:35AM return -1
        // if it is > 5:30PM - 5:35PM return +1
        // else return 0
        return 0;
    } 
    protected String getBlindState()
    {
        // if blinds closed, return "CLOSED"
        // if blinds open, return "OPENED"
        String currentState = "OPEN";

        System.out.println("blinds are currently [" + currentState + "]");
        return currentState;
    }
    protected void sendMotorCmd(String cmd)
    {
       String motorcmd = "FORWARD";
       if(cmd.equals("CLOSE")) motorcmd = "BACKWARD";
       //motor.doCmd(motorcmd,null);
    } 
          

    DoMotorCmd motor;
    public void run()
    {
        motor = new DoMotorCmd();
        while( true )
        {
           System.out.println("checking time");
           int time = getCurrentTime();
           if( time != 0 ) 
           {
              String cmd = "";
              if( time > 0 )  
                  cmd = "OPEN";
              else
                  cmd = "CLOSE";
              System.out.println("time to [" + cmd + "]...");
              String blindState = getBlindState();
              while( !blindState.startsWith(cmd))
              {
                  sendMotorCmd(cmd);
                  blindState = getBlindState();
              }
           }
           System.out.println("sleeping");
           sleep(60000);
        }
    }
    
    protected void sleep(int numMs)
    {
    	try
    	{
    		Thread.sleep(numMs);
    	}
    	catch(Throwable t)
    	{
    		System.out.println("Unexpected error during sleep " + t.getMessage());
    	}
    }
    	
    
}
