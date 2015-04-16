package com.sizemore.citrixtest;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.DocumentsContract.Document;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;

public class MainMenuActivity extends Activity {

	// main menu screen. nothing flashy.
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_menu_activity_layout);

	}

	// our file extension is .json so we just enable a search for anything
	public void onClick(View v) {
//		Intent intent = new Intent(this, OptionsActivity.class);
////		intent.putExtra("path", data.getData().getPath());
//		startActivity(intent);
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		startActivityForResult(intent, 1);
	}
	
	public static String getPath(final Context context, final Uri uri) {

	    // DocumentProvider
	    if (DocumentsContract.isDocumentUri(context, uri)) {
	        // ExternalStorageProvider
	        if (isExternalStorageDocument(uri)) {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];

	            if ("primary".equalsIgnoreCase(type)) {
	                return Environment.getExternalStorageDirectory() + "/" + split[1];
	            }

	            // TODO handle non-primary volumes
	        }
	        // DownloadsProvider
	        else if (isDownloadsDocument(uri)) {

	            final String id = DocumentsContract.getDocumentId(uri);
	            final Uri contentUri = ContentUris.withAppendedId(
	                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

	            return getDataColumn(context, contentUri, null, null);
	        }
	        else return null;
	    }

	    return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
	        String[] selectionArgs) {

	    Cursor cursor = null;
	    final String column = "_data";
	    final String[] projection = {
	            column
	    };

	    try {
	        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
	                null);
	        if (cursor != null && cursor.moveToFirst()) {
	            final int column_index = cursor.getColumnIndexOrThrow(column);
	            return cursor.getString(column_index);
	        }
	    } finally {
	        if (cursor != null)
	            cursor.close();
	    }
	    return null;
	}


	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
	    return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
	    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
	    return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	// go here after returning from our file picket intent in onClick
	// pulls the path out of the returned intent and passes it to the next activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (null != data) {
			Intent intent = new Intent(this, OptionsActivity.class);
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
				intent.putExtra("path", data.getData().getPath());
			}
			else {
//				String path = getPath(this,data.getData());
				Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
				cursor.moveToFirst();
				String doc_id = cursor.getString(0);
				doc_id = doc_id.substring(doc_id.lastIndexOf(":")+1);
				cursor.close();
				
				cursor = getContentResolver().query(android.provider.DocumentsContract.buildDocumentUri("com.android.providers.downloads.documents", doc_id), null, 
						Document.COLUMN_DOCUMENT_ID + " = ? ", new String[]{doc_id}, null);
				cursor.moveToFirst();
				
				String path = cursor.getString(cursor.getColumnIndex(Document.COLUMN_DOCUMENT_ID));
				cursor.close();
				intent.putExtra("path", path);
				
			}
			startActivity(intent);
		}

	}

}
