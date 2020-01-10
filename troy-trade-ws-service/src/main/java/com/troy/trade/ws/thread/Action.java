package com.troy.trade.ws.thread;

/**
 * 
 * @author yanping
 * @datetime 2017年8月19日下午4:57:24
 */
public abstract class Action implements Runnable {
	
	@Override
	public void run() {
		execute();
	}
	
	
	public abstract void execute();

}
