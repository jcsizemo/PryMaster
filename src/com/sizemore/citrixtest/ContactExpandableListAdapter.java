package com.sizemore.citrixtest;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactExpandableListAdapter extends BaseExpandableListAdapter {
	
	private Context context;
	private List<String> fields;
	private Map<String, List<String>> profile;
	
	// adapter constructor. sets the context, groups, and group children
	public ContactExpandableListAdapter(Context context, List<String> fields, 
			Map<String, List<String>> profile) {
		this.context = context;
		this.fields = fields;
		this.profile = profile;
	}

	@Override
	public int getGroupCount() {
		return fields.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return profile.get(fields.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return fields.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return profile.get(fields.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	// return parent view: just a simple text view
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		String fieldName = (String) getGroup(groupPosition);
		if (null == convertView) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.group_item, null);
		}
		TextView groupView = (TextView) convertView.findViewById(R.id.groupView);
		groupView.setText(fieldName);
		return convertView;
	}

	// child view: a text view and a button that changes depending on what the function is.
	// parents and managers have an "eye icon" that will open that parent/manager's page
	// addresses will open google maps
	// phone numbers will instantiate the dialpad screen
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
        String value = (String) getChild(groupPosition, childPosition);
        String group = (String) getGroup(groupPosition);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         
        if (convertView == null) {
        	convertView = inflater.inflate(R.layout.child_item, null);
        }
         
        TextView fieldView = (TextView) convertView.findViewById(R.id.childView);
        fieldView.setText(value);
        ImageView lookView = (ImageView) convertView.findViewById(R.id.lookIcon);
        final Intent intent;
        
        if ("Parent".equals(group) || "Managers".equals(group)) {
            lookView.setBackgroundResource(R.drawable.eyecon);
            intent = new Intent(context, ContactActivity.class);
			intent.putExtra("name", value);
    	}
        else if ("Addresses".equals(group)) {
        	lookView.setBackgroundResource(R.drawable.map_icon);
        	value.replace(",", "%2C");
        	value.replace(" ", "+");
        	value.replace("&", "%20");
        	String uri = "geo:0,0?q=" + value;
        	intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
        }
        else { // phone
        	lookView.setBackgroundResource(R.drawable.phone_icon);
        	intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + value));
        }
        
        convertView.setOnClickListener(new OnClickListener() {

        	@Override
			public void onClick(View v) {
				context.startActivity(intent);
			}
    		
    	});;
        
        return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
