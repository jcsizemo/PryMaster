package com.sizemore.citrixtest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

public class OptionsActivity extends Activity implements
		OnItemSelectedListener, TextWatcher, OnItemClickListener {

	// UI objects
	Spinner spinner;
	EditText searchBar;
	ListView contactView;
	// database
	ContactDBHelper dbHelper;
	// tri-state logic variable. 
	// null: everything
	// true: businesses only
	// false: people only
	Boolean searchLogic = null;
	// contacts object
	Contacts contacts = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.options_activity_layout);

		String path = getIntent().getStringExtra("path");
		// start async task to parse the .json file and build the database
		new LoadContactsTask(this).execute(path);

		// set UI objects
		spinner = (Spinner) findViewById(R.id.optionSpinner);
		searchBar = (EditText) findViewById(R.id.searchBar);
		contactView = (ListView) findViewById(R.id.contactListView);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.spinner_options,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	// attach listeners to UI objects, populate screen. This is the final setup method
	private void setListeners() {
		spinner.setOnItemSelectedListener(this);
		searchBar.addTextChangedListener(this);
		contactView.setOnItemClickListener(this);
		query("");
	}

	// This method queries the db given certain search parameters. It then returns a cursor
	// which pulls out names and sets the list view on the screen to contain them
	public void query(String s) {
		Cursor cursor = dbHelper.query(s, searchLogic, false);
		cursor.moveToFirst();
		List<String> names = new ArrayList<String>();
		while (!cursor.isAfterLast()) {
			names.add(dbHelper.buildContact(cursor).getName());
			cursor.moveToNext();
		}

		contactView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, names));
	}

	// attached to the search bar. essentially, if there's anything there then we just re-populate
	// the list view based on its contents and search criteria
	public void onClick(View v) {
		query(((EditText) v).getText().toString());
	}
	
	// attached to the list view. called when a contact is selected and an activity
	// is started to list that contact's data
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String name = (String) contactView.getAdapter().getItem(position);
		Intent intent = new Intent(this, ContactActivity.class);
		intent.putExtra("name", name);
		startActivity(intent);
	}

	// attached to the spinner which drives the tri-state search logic
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0:
			searchLogic = null;
			break;
		case 1:
			searchLogic = true;
			break;
		default:
			searchLogic = false;
		}
		query("");
	}

	// various interface methods that i didn't find a use for but had to include anyway
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		query(s.toString());
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	// re-open the db on resume
	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
	}

	// close the db if we pause: we don't want to leave te db hanging if Android kills the app
	// when it is suspended
	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}

	// async task for reading the .json file and building the database
	class LoadContactsTask extends AsyncTask<String, Integer, Void> {

		// progress bar to let the user know something is happening
		ProgressBar pb;
		Context context;
		// flag for error handling
		boolean failed = false;

		public LoadContactsTask(Context context) {
			this.context = context;
			dbHelper = new ContactDBHelper(context);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			pb.setVisibility(View.INVISIBLE);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// before we do anything, start the progress bar and make it visible
			pb = (ProgressBar) findViewById(R.id.loadProgressBar);
			pb.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			// if we've failed, then there was a problem reading the file.
			// Let the user know so he/she can try again
			if (failed) {
				Toast.makeText(
						context,
						"File could not be parsed: Are you sure it is the right type?",
						Toast.LENGTH_LONG).show();
				((Activity) context).finish();
				return;
			}
			// when we've finished everything and nothing broke,
			// we can make everything else visible and stop the progress bar
			pb.setVisibility(View.INVISIBLE);
			findViewById(R.id.optionSpinner).setVisibility(View.VISIBLE);
			findViewById(R.id.searchBar).setVisibility(View.VISIBLE);
			setListeners();
		}

		@Override
		protected Void doInBackground(String... params) {

			try {
				// generate stream for the file
				String path = params[0];
//				String filepath = "/mnt/shell/emulated/0/Download/contacts.json";
				File f = new File(path);
				FileInputStream fis = new FileInputStream(f);
				
//				InputStream fis = context.getAssets().open("contacts.json");
				
				byte[] buffer = new byte[fis.available()];
				byte[] bytes = {};

				// read the file into a byte array
				for (int i = fis.read(buffer); i != -1; i = fis.read(buffer)) {
					byte[] readBytes = new byte[bytes.length + i];
					System.arraycopy(bytes, 0, readBytes, 0, bytes.length);
					System.arraycopy(buffer, 0, readBytes, bytes.length, i);
					bytes = readBytes;
				}

				// convert the bytes into a string. Granted, I could have used
				// a bufferedreader here but I prefer input streams as you have
				// greater control over the bytes themselves
				String fileContents = new String(bytes);

				// create our GSON object and set our contact adapter
				// for custom deserialization
				GsonBuilder gsonB = new GsonBuilder();
				gsonB.registerTypeAdapter(Contact.class, new ContactAdapter());
				Gson gson = gsonB.create();

				// try and parse the json. If there's a problem, we let
				// the program know we've failed and return.
				try {
					contacts = gson.fromJson(fileContents, Contacts.class);
				}
				catch (JsonSyntaxException ise) {
					failed = true;
					fis.close();
					return null;
				}

				// Lexographic sort
				Collections.sort(contacts.getContacts(),
						new Comparator<Contact>() {

							@Override
							public int compare(Contact lhs, Contact rhs) {
								String name1 = lhs.getName();
								String name2 = rhs.getName();

								return name1.compareToIgnoreCase(name2);
							}

						});

				// insert contact into database
				for (Contact contact : contacts.getContacts()) {
					dbHelper.insertContact(contact);
				}
				// close stream. fis.close() throws IOException so we don't put this in a "finally"
				fis.close();

			} catch (IOException ioe) {
				failed = true;
			}
			return null;
		}

	}

}
