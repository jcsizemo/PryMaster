package com.sizemore.citrixtest;

import java.util.List;

// abstract Contact class
public abstract class Contact {
	
	private List<String> phones;
	private List<String> addresses;
	
	public boolean isBusiness() {
		return isBusiness;
	}
	public void setIsBusiness(boolean isBusiness) {
		this.isBusiness = isBusiness;
	}
	public List<String> getPhones() {
		return phones;
	}
	public List<String> getAddresses() {
		return addresses;
	}
	public void setPhones(List<String> phones) {
		this.phones = phones;
	}
	public void setAddresses(List<String> addresses) {
		this.addresses = addresses;
	}
	
	// let subclasses handle this
	public abstract String getName();
	public abstract void setName(String name);
    

}
