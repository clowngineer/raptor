package com.cobinrox.io.impl;

public interface IMotorControl {
	
	public void initHardware() throws Throwable;
	
	
	/**
	 * Send complex motor control command
	 * @param foreAft "forward" or "backward" or null
	 * @param leftRight "left" or "right" or null
	 * @throws Throwable
	 */
	public void move(final String foreAft, final String leftRight) throws Throwable;
	
	public void shutdown() throws Throwable;
}
