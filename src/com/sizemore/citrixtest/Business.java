package com.sizemore.citrixtest;

import java.util.List;

//  Business subclass
public class Business extends Contact {
	
	private String companyName;
	private String parent;
	private List<String> managers;
	
	@Override
	public void setName(String name) {
		this.companyName = name;
	}
	@Override
	public String getName() {
		return companyName;
	}
	public String getParent() {
		return parent;
	}
	public List<String> getManagers() {
		return managers;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public void setManagers(List<String> managers) {
		this.managers = managers;
	}

}
