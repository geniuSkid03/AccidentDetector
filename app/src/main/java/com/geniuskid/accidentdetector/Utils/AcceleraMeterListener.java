package com.geniuskid.accidentdetector.Utils;

public interface AcceleraMeterListener {
	 
	public void onAccelerationChanged(float x, float y, float z);
 
	public void onShake(float force);
 
}