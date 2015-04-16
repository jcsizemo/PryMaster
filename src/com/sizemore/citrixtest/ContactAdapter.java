package com.sizemore.citrixtest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

// adapter to aid in custom deserialization from gson
public class ContactAdapter implements JsonDeserializer<Contact> {

	@Override
	public Contact deserialize(JsonElement json, Type type,
			JsonDeserializationContext context) throws JsonParseException {
		
		Contact c = null;
		JsonObject j = json.getAsJsonObject();
		
		if (null != j.get("name")) {
			c = new Person();
			Person p = (Person) c;
			p.setName(j.get("name").getAsString());
			p.setIsBusiness(false);
		}
		if (null != j.get("companyName")) {
			c = new Business();
			Business b = (Business) c;
			b.setCompanyName(j.get("companyName").getAsString());
			b.setIsBusiness(true);
			if (null != j.get("parent")) {
				b.setParent(j.get("parent").getAsString());
			}
			if (null != j.get("managers")) {
				JsonArray ja = j.get("managers").getAsJsonArray();
				List<String> managers = new ArrayList<String>();
				for (int i = 0; i < ja.size(); i++) managers.add(ja.get(i).getAsString());
				b.setManagers(managers);
			}
		}
		
		if (null != j.get("phones")) {
			JsonArray ja = j.get("phones").getAsJsonArray();
			List<String> phones = new ArrayList<String>();
			for (int i = 0; i < ja.size(); i++) phones.add(ja.get(i).getAsString());
			c.setPhones(phones);
		}
		if (null != j.get("addresses")) {
			JsonArray ja = j.get("addresses").getAsJsonArray();
			List<String> addresses = new ArrayList<String>();
			for (int i = 0; i < ja.size(); i++) addresses.add(ja.get(i).getAsString());
			c.setAddresses(addresses);
		}
		
		return c;
	}
	
	

}
