package com.example.legistify;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.Toast;

public class Filter extends Activity implements OnItemSelectedListener,
		OnClickListener {

	Spinner area, city, interest;
	Button submit;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filter);
		area = (Spinner) findViewById(R.id.sp_area);
		city = (Spinner) findViewById(R.id.sp_city);
		interest = (Spinner) findViewById(R.id.sp_interest);
		submit = (Button) findViewById(R.id.bt_submit);

		db = new DatabaseHandler(this);
		db.openDataBase();

		setAdaptersForSpinner();

		area.setOnItemSelectedListener(this);
		city.setOnItemSelectedListener(this);
		interest.setOnItemSelectedListener(this);
		submit.setOnClickListener(this);

	}

	private void setAdaptersForSpinner() {
		ArrayList<String> cityList = db.getUniqueCity();
		List<String> interestList = db.getUniqueInterest();

		ArrayAdapter a1 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, cityList);
		a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		city.setAdapter(a1);

		ArrayAdapter a2 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, interestList);
		a2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		interest.setAdapter(a2);

		String areaList[] = { "ALL" };
		ArrayAdapter a3 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, areaList);
		a3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		area.setAdapter(a3);
	}

	@Override
	public void onClick(View v) {
		Intent i = new Intent();
		i.putExtra("Interest", interest.getSelectedItem().toString());
		i.putExtra("City", city.getSelectedItem().toString());
		i.putExtra("Area", area.getSelectedItem().toString());
		setResult(2, i);
		finish();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		// TODO Auto-generated method stub
		String temp = city.getSelectedItem().toString();
		if (parent.getId() == R.id.sp_city) {
			if (temp.compareTo("ALL") != 0) {

				List<String> areaList = db.getUniqueArea(temp);

				ArrayAdapter a1 = new ArrayAdapter(this,android.R.layout.simple_spinner_item, areaList);
				a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				area.setAdapter(a1);
			} 
			else {
				String areaList[] = { "ALL" };
				ArrayAdapter a3 = new ArrayAdapter(this,android.R.layout.simple_spinner_item, areaList);
				a3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				area.setAdapter(a3);
			}
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}
