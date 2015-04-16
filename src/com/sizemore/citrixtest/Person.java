package com.sizemore.citrixtest;

public class Person extends Contact {
	
	// basic person object. Also has phone/address data inherited from Contact
	private String name;
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

}
