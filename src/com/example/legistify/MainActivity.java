package com.example.legistify;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import android.R.string;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener, OnClickListener {

	Button search;
	ListView listView1;
	
	DatabaseHandler db ;
	ArrayList<Contact> contacts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		search = (Button) findViewById(R.id.search);
		listView1 = (ListView) findViewById(R.id.listView1);
		
		search.setOnClickListener(this);
		
		contacts = new ArrayList<Contact>();
		db = new DatabaseHandler(this);
		db.openDataBase();
		
		try {
			db.createDataBase();
		} catch(Exception e) {
			Log.d("database", "unable to create/open database");
		}
		
		db.showDataBase();
		contacts = db.getAllContacts() ;
		
		MyAdapter adp = new MyAdapter(MainActivity.this , contacts);

		listView1.setAdapter(adp);
		listView1.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long id) {
		
		final Dialog dialog = new Dialog(MainActivity.this);
		dialog.setContentView(R.layout.popup);
		dialog.setTitle("Details : ");

		TextView name = (TextView) dialog.findViewById(R.id.name);
		TextView interest = (TextView) dialog.findViewById(R.id.interest);
		TextView address = (TextView) dialog.findViewById(R.id.address);
		TextView phone = (TextView) dialog.findViewById(R.id.phone);

		name.setText(contacts.get(pos).name);
		interest.setText(contacts.get(pos).interest);
		address.setText(contacts.get(pos).address);
		phone.setText(contacts.get(pos).phone);

		Button dialogButton = (Button) dialog.findViewById(R.id.ok);
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent i = new Intent(MainActivity.this, Filter.class);
		startActivityForResult(i, 2);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(data == null) return;
		
		if(requestCode == 2) {
			String interest = data.getStringExtra("Interest");
			String city = data.getStringExtra("City");
			String area = data.getStringExtra("Area");
			
			contacts = db.getCustomizedContacts(interest, city , area);
			MyAdapter adp = new MyAdapter(MainActivity.this , contacts);
			listView1.setAdapter(adp);
		}
	}
	
	
}
