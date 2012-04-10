package org.glen_h.teacherwishlist.editor;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class WishlistEditActivity extends Activity {
    
	private Button submit;
	private EditText passwd;
	private EditText add_item;
	private Spinner wishlist_choose;
	private Button showlist;
	
	public static String md5(String in) {
	    MessageDigest digest;
	    try {
	        digest = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
	        digest.reset();
	        digest.update(in.getBytes());
	        byte[] a = digest.digest();
	        int len = a.length;
	        StringBuilder sb = new StringBuilder(len << 1);
	        for (int i = 0; i < len; i++) {
	            sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
	            sb.append(Character.forDigit(a[i] & 0x0f, 16));
	        }
	        return sb.toString();
	    } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
	    return null;
	}
	
	/**
	 * Downloads a text file and returns its contents as an array.
	 * @author Glen Husman
	 */
	public static String[] downloadFile(URL website) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(
			              new InputStreamReader(
			              website.openStream()));
		} catch (IOException e) {
			in = null;
			e.printStackTrace();
			Log.w("WishlistEditActivity", "in is null!");
		}

	      String input;
	      ArrayList<String> stringList = new ArrayList<String>();
	      	                
	      try {
			while ((input = in.readLine()) != null) {
			      stringList.add(input);
			  }
		} catch (IOException e) {
			stringList = new ArrayList<String>();
			}
	      
	    String[] itemArray = new String[stringList.size()];
		String[] returnedArray = stringList.toArray(itemArray);
		return returnedArray;
		}
	
	/**
	 * Makes a URL from a string without the need for a try/catch.
	 * @see java.net.URL URL
	 * @author Glen Husman
	 * @return URL
	 */
	public static URL makeURL(String webaddress) {
		
		/*
		 * Makes a URL from a string
		 */
		
		URL website;
		try {
			website = new URL(webaddress);
		} catch (MalformedURLException e) {
			website = null;
			Log.e("URL", "Malformed URL Exception was thrown on string to URL conversion");
		}
	return website;
	}
	
	public static void postData(String url, String[] ids, String[] values) {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(url);

	    try {
	        if(ids.length-1 == values.length-1){
	    	// Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(ids.length-1);
	        for(int ii = 0; ii <= ids.length-1; ii++){
	        nameValuePairs.add(new BasicNameValuePair(ids[ii], values[ii]));
	        }
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        httpclient.execute(httppost);
	        }else{
	        	return;
	        }
	    } catch (ClientProtocolException e) {
	    	return;
	    } catch (IOException e) {
	    	return;
	    }
	} 
	
	String[] wlist;
	protected String instance_url;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        submit = (Button) findViewById(R.id.go);
        showlist = (Button) findViewById(R.id.showlist);
        passwd = (EditText) findViewById(R.id.password);
        wishlist_choose = (Spinner) findViewById(R.id.wishlist);
        final EditText input = new EditText(this);
        input.setText("http://192.168.1.101/teacher-wishlist/wishlist-edit.php");
        input.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_URI);
        new AlertDialog.Builder(this)
        .setTitle("Instance URL")
        .setMessage("Please input the URL to the wishlist-edit.php file")
        .setView(input)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable value = input.getText();
                instance_url = value.toString();
                String[] wishlists = downloadFile(makeURL(instance_url+"?listwishlist=ok"));
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(WishlistEditActivity.this, 
                		android.R.layout.simple_spinner_item, wishlists);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                wishlist_choose.setAdapter(spinnerArrayAdapter);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
            }
        }).show();
        add_item = (EditText) findViewById(R.id.add);
        submit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	// Submit data to form!
            	Log.d("WishlistEditActivity", "Current password: "+passwd.getText().toString()); //$NON-NLS-1$ //$NON-NLS-2$
            	String password = md5(passwd.getText().toString());
            	Log.d("WishlistEditActivity", "Current MD5 of password: "+password); //$NON-NLS-1$ //$NON-NLS-2$
            	String wishlist_raw = (String) wishlist_choose.getSelectedItem();
            	Log.d("WishlistEditActivity", "Current selection for wishlist: "+wishlist_raw); //$NON-NLS-1$ //$NON-NLS-2$
            	String add = add_item.getText().toString();
            	Log.d("WishlistEditActivity", "Current add item for wishlist: "+add); //$NON-NLS-1$ //$NON-NLS-2$
            	String[] ids = {"password", "teacherchoice", "concat"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            	String[] values = {password, wishlist_raw, add};
				postData(instance_url , ids, values);
              }
        });
        showlist.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	wlist = downloadFile(makeURL(instance_url.replace("wishlist-edit.php", "wishlist-extension.php?extension=view&wishlist="+(String) wishlist_choose.getSelectedItem()+"&plain=yes")));
            	/**
            	AlertDialog.Builder builder = new AlertDialog.Builder(WishlistEditActivity.this);
                builder.setTitle("Wishlist for: "+(String) wishlist_choose.getSelectedItem());

                ListView modeList = new ListView(WishlistEditActivity.this);
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(WishlistEditActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, wlist);
                modeList.setAdapter(modeAdapter);

                builder.setView(modeList);
                final Dialog dialog = builder.create();

                dialog.show();
              }
              */
            	AlertDialog.Builder builder = new AlertDialog.Builder(WishlistEditActivity.this);
            	builder.setTitle("Wishlist for "+(String) wishlist_choose.getSelectedItem());
            	builder.setItems(wlist, new DialogInterface.OnClickListener() {
            	    private int itemid_dialog;

					public void onClick(DialogInterface dialog, int item) {
            	    	itemid_dialog = item;
            	    	AlertDialog.Builder builder = new AlertDialog.Builder(WishlistEditActivity.this);
            	    	builder.setMessage("Wishlist item "+Integer.toString(item+1)+" on "+(String) wishlist_choose.getSelectedItem()+"'s wishlist: "+wlist[item]+". Password must be correct to rename or remove.")
            	    	       .setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            	    	           public void onClick(DialogInterface dialog, int id) {
            	    	        	   final EditText input = new EditText(WishlistEditActivity.this);
            	    	               new AlertDialog.Builder(WishlistEditActivity.this)
            	    	               .setTitle("Rename")
            	    	               .setMessage("Please input the new name for the item")
            	    	               .setView(input)
            	    	               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            	    	                   public void onClick(DialogInterface dialog, int whichButton) {
            	    	                       Editable value = input.getText();
            	    	                       String[] post_vars = {"teacherchoice", "password", "find", "replace"};
                    	    	        	   String[] post_data = {(String) wishlist_choose.getSelectedItem(), md5(passwd.getText().toString()), wlist[itemid_dialog], value.toString()};
                    	    	        	   postData(instance_url, post_vars, post_data);
            	    	                   }
            	    	               }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            	    	                   public void onClick(DialogInterface dialog, int whichButton) {
            	    	                   }
            	    	               }).show();
            	    	           }
            	    	       })
            	    	       .setNeutralButton("OK", new DialogInterface.OnClickListener() {
            	    	           public void onClick(DialogInterface dialog, int id) {
            	    	                dialog.cancel();
            	    	           }
            	    	       })
            	    	       .setNegativeButton("Remove", new DialogInterface.OnClickListener() {
            	    	           public void onClick(DialogInterface dialog, int id) {
            	    	        	   String[] post_vars = {"teacherchoice", "password", "item"};
            	    	        	   String[] post_data = {(String) wishlist_choose.getSelectedItem(), md5(passwd.getText().toString()), wlist[itemid_dialog]};
            	    	        	   postData(instance_url, post_vars, post_data);
            	    	           }
            	    	       });
            	    	builder.show();
            	    }
            	});
            	builder.show();
              }
        });    
    }
}