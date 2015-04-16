package com.sizemore.citrixtest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;


public class ContactActivity extends Activity {
	
	// database helper
	ContactDBHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set layout, kill title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// retrieve database
		dbHelper = new ContactDBHelper(this);
		// query the contact, do exact match on name
		String name = getIntent().getStringExtra("name");
		Cursor cursor = dbHelper.query(name, null, true);

		// nothing in the cursor means nothing was found. return to the calling activity
		if (cursor.getCount() == 0) {
			Toast.makeText(this, "No such contact found.", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		setContentView(R.layout.contact_activity_layout);
		
		// build contact object from returned data
		cursor.moveToFirst();
		Contact c = dbHelper.buildContact(cursor);
		
		// objects used for expandable list view
		List<String> fields = new ArrayList<String>();
		Map<String, List<String>> profile = new HashMap<String, List<String>>();
		
		// set title to contact name
		TextView tv = (TextView) findViewById(R.id.nameView);
		tv.setText(name);
		
		// add groups to expandable list view; only add those the contact has
		if (null != c.getPhones()) {
			fields.add("Phones");
			profile.put("Phones", c.getPhones());
		}
		if (null != c.getAddresses()) {
			fields.add("Addresses");
			profile.put("Addresses", c.getAddresses());
		}
		if (c.isBusiness()) {
			Business b = (Business) c;
			if (null != b.getParent()) {
				fields.add("Parent");
				List<String> parent = new ArrayList<String>();
				parent.add(b.getParent());
				profile.put("Parent", parent);
			}
			if (null != b.getManagers()) {
				fields.add("Managers");
				profile.put("Managers", b.getManagers());
			}
		}
		
		// attach the adapter
		ContactExpandableListAdapter adapter = new ContactExpandableListAdapter(this, fields, profile);
		ExpandableListView elv = (ExpandableListView) findViewById(R.id.contactProfileListView);
		elv.setAdapter(adapter);
	}

	// re-open database
	@Override
	protected void onResume() {
		dbHelper.open();
		super.onResume();
	}

	// close the db here as the OS might terminate our activity if we suspend it
	@Override
	protected void onPause() {
		dbHelper.close();
		super.onPause();
	}


	
}
