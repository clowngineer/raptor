package com.cobinrox.io;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
	static final Logger logger = Logger.getLogger(AppTest.class);
	public void testDummy(){}
    public void atestDoMotorCmd() throws Throwable
    {
    	DoMotorCmd d = new DoMotorCmd();
    	d.doThis("f");
    	d.shutdown();
    			
    }
}
