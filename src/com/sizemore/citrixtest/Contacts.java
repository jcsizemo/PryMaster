package com.sizemore.citrixtest;

import java.util.List;

public class Contacts {

	// top-level contacts object for holding the converted json data from gson
	private List<Contact> contacts;
	
	public List<Contact> getContacts() {
		return contacts;
	}
	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

}
