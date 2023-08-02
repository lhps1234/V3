package com.example.order.model.member;

public enum GenderType {

	MALE("남성"),
	FEMALE("여성");
	
	private final String description;
	
	private GenderType(String descString) {
		this.description = descString; 
	}
	
	public String getDescription() {
		return description;
	}
}
