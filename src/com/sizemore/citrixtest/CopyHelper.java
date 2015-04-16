//package com.sizemore.citrixtest;
//
//import java.util.Arrays;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//public class CopyHelper extends SQLiteOpenHelper {
//	
//	// database, column names
//	SQLiteDatabase db;
//	
//	public final String TABLE_CONTACTS = "contacts";
//	public final String COLUMN_NAME = "name";
//	public final String COLUMN_PHONES = "phones";
//	public final String COLUMN_ADDRESSES = "addresses";
//	public final String COLUMN_PARENT = "parent";
//	public final String COLUMN_MANAGERS = "managers";
//	public final String COLUMN_ISBUSINESS = "isBusiness";
//	
//	private static final String DATABASE_NAME = "contacts.db";
//	private static final int DATABASE_VERSION = 1;
//	
//	// sql string to make the database
//	public String DATABASE_CREATE = "create table " + TABLE_CONTACTS +
//			" (" + COLUMN_NAME + " text not null," + COLUMN_PHONES + " text,"
//			+ COLUMN_ADDRESSES + " text," + COLUMN_PARENT + " text," + COLUMN_MANAGERS + 
//			" text," + COLUMN_ISBUSINESS + " CHECK (" + COLUMN_ISBUSINESS + " IN (0,1)));";
// 
//	public CopyHelper(Context context) {
//		super(context, DATABASE_NAME, null, DATABASE_VERSION);
//		open();
//	}
//
//	// creates the database
//	@Override
//	public void onCreate(SQLiteDatabase db) {
//		db.execSQL(DATABASE_CREATE);
//	}
//
//	@Override
//	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
//	    onCreate(db);
//	}
//	
//	// get db
//	public void open() {
//		db = getWritableDatabase();
//	}
//	
//	// convert a Contact object into something usable within the database
//	public void insertContact(Contact c) {
//		
//		// don't want to re-add anything
//		if (query(c.getName(), null, true).getCount() == 0) return;
//		
//		ContentValues values = new ContentValues();
//		values.put(COLUMN_NAME, c.getName());
//		StringBuilder sb = new StringBuilder();
//		
//		// for arrays (phones, addresses, managers) we insert the "^,^" delimiter
//		// so when objects are rebuilt from the database we can call 'split' and easily rebuild
//		if (null != c.getPhones()) {
//			sb.append(c.getPhones().get(0));
//			for (int i = 1; i < c.getPhones().size(); i++)  sb.append("^,^" + c.getPhones().get(i));
//			values.put(COLUMN_PHONES, sb.toString());
//		}
//		if (null != c.getAddresses()) {
//			sb.delete(0, sb.length());
//			sb.append(c.getAddresses().get(0));
//			for (int i = 1; i < c.getAddresses().size(); i++)  sb.append("^,^" + c.getAddresses().get(i));
//			values.put(COLUMN_ADDRESSES, sb.toString());
//		}
//		if (c.isBusiness()) {
//			Business b = (Business) c;
//			if (null != b.getManagers()) {
//				sb.delete(0, sb.length());
//				sb.append(b.getManagers().get(0));
//				for (int i = 1; i < (b.getManagers().size()); i++)  sb.append("^,^" + b.getManagers().get(i));
//				values.put(COLUMN_MANAGERS, sb.toString());
//			}
//			values.put(COLUMN_PARENT, b.getParent());
//			values.put(COLUMN_ISBUSINESS, 1);
//		}
//		else {
//			values.put(COLUMN_ISBUSINESS, 0);
//		}
//		
//		db.insert(TABLE_CONTACTS, null, values);
//		
//	}
//	
//	// rebuild contact from returned database data
//	public Contact buildContact(Cursor cursor) {
//		
//		boolean isBusiness = (cursor.getInt(5) == 1);
//		Contact c;
//		if (isBusiness) {
//			c = new Business();
//			((Business) c).setParent(cursor.getString(3));
//			String managers = cursor.getString(4);
//			// easily recreate arrays by calling 'split' on our delimiter
//			if (null != managers) ((Business) c).setManagers(Arrays.asList(managers.split("\\^,\\^")));
//		}
//		else c = new Person();
//		
//		c.setName(cursor.getString(0));
//		c.setIsBusiness(isBusiness);
//		String phones = cursor.getString(1);
//		String addresses = cursor.getString(2);
//		if (null != phones) c.setPhones(Arrays.asList(phones.split("\\^,\\^")));
//		if (null != addresses) c.setAddresses(Arrays.asList(addresses.split("\\^,\\^")));
//	
//		return c;
//	}
//	
//	// database query. handles prefix, exact, and categorical searches
//	public Cursor query(String name, Boolean businessOnly, boolean getExact) {
//		
//		StringBuilder sb = new StringBuilder("select * from contacts where name ");
//		
//		// if: exact, else: prefix
//		if (getExact) sb.append("= \'" + name + "\'");
//		else sb.append("like \'" + name + "%\'");
//		
//		// categorical: tri-state businessOnly variable used here. null means to return everything
//		if (null != businessOnly) sb.append(" and "
//				+ "isBusiness = " + (businessOnly ? 1 : 0));
//		
//		return db.rawQuery(sb.toString(), null);
//	}
//	
//	
//
//}
