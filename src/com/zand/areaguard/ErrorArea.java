package com.zand.areaguard;

public class ErrorArea extends Area {

	protected ErrorArea() {
		super(-1, -1, "Error!", 100, new int[] {
				Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, 
				Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE});}
	
	@Override
	public boolean playerCan(String player, String[] lists) { return false; }
	@Override
	public boolean setPriority(int priority) { return false; }
}
