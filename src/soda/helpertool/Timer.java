package soda.helpertool;

public class Timer {
	private static Timer timer;
	
	private Timer(){};
	
	public static Timer getTimer(){
		if(timer == null) timer = new Timer();
		return timer;
	}
}
