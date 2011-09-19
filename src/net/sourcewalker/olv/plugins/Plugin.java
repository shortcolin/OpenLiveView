package net.sourcewalker.olv.plugins;

public class Plugin {
	public boolean handleButtonLeft(Boolean doublePress, Boolean longPress) { return false;} 
	public boolean handleButtonRight(Boolean doublePress, Boolean longPress) {return false;}
	public boolean handleButtonUp(Boolean doublePress, Boolean longPress) { return false; }
	public boolean handleButtonDown(Boolean doublePress, Boolean longPress) { return false; }
	public boolean handleButtonSelect(Boolean doublePress, Boolean longPress) { return false; }
	
	public void start() {}
	public void stop() {}
	
}
