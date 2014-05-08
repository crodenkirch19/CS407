package com.example.bluetoothfinder;

public class Coupon {
	
	private int cost;
	private String description;
	
	public Coupon(int cost, String description) {
		this.cost = cost;
		this.description = description;
	}
	
	public int getCost() {
		return cost;
	}
	
	public String getDescription() {
		return description;
	}
}
