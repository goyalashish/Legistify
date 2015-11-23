package com.example.legistify;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static String DB_PATH = "/data/data/com.example.legistify/databases/";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "Lawyers";
	private static final String TABLE_DETAILS = "lawyer_details";
	private static final String TABLE_INTEREST = "interest";

	SQLiteDatabase db;
	Context context;

	public DatabaseHandler(Context context) {
		/**
		 * Constructor Takes and keeps a reference of the passed context in
		 * order to access to the application assets and resources.
		 */

		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	public void createDataBase() throws IOException {
		/**
		 * Creates a empty database on the system and rewrites it with your own
		 * database.
		 **/

		boolean dbExist = checkDataBase();
		dbExist = false ;
		if (dbExist) {
			// do nothing - database already exist
		} else {

			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();

			try {
				copyDataBase();
			} catch (IOException e) {
				Log.d("copying database", "Error in copying database");
			}
		}
	}

	private boolean checkDataBase() {
		/**
		 * Check if the database already exist to avoid re-copying the file each
		 * time you open the application.
		 * 
		 * @return true if it exists, false if it doesn't
		 */

		SQLiteDatabase checkDB = null;

		try {
			String myPath = DB_PATH + DATABASE_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {
			Log.d("database", "database doesnt exists yet");
		}

		if (checkDB != null)
			checkDB.close();

		return checkDB != null ? true : false;
	}

	private void copyDataBase() throws IOException {
		/**
		 * Copies your database from your local assets-folder to the just
		 * created empty database in the system folder, from where it can be
		 * accessed and handled. This is done by transferring byte stream.
		 * */

		// Open your local db as the input stream
		InputStream myInput = context.getAssets().open(DATABASE_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DATABASE_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the input file to the output file
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public void openDataBase() throws SQLException {

		// Open the database
		String myPath = DB_PATH + DATABASE_NAME;
		db = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READONLY);
	}
	
	@Override
	public synchronized void close() {

		if (db != null)
			db.close();

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public void showDataBase() {
		String sql = "SELECT * FROM " + TABLE_DETAILS;
		Cursor cursor = db.rawQuery(sql, null);
		String ans = "";

		if (cursor != null)
			while (cursor.moveToNext()) {
				String name = cursor.getString(1);
				String phone = cursor.getString(5);
				ans += name + "  " + phone + "\n";
			}
		 //Toast.makeText(context, ans, Toast.LENGTH_LONG).show();
	}

	public ArrayList<Contact> getAllContacts() {

		ArrayList<Contact> contactList = new ArrayList<Contact>();

		String selectQuery = "SELECT  * FROM " + TABLE_DETAILS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) { // if (cursor != null)
			do {
				Contact contact = new Contact();
				contact.setName(cursor.getString(1));
				contact.setAddress(cursor.getString(4));
				contact.setPhone(cursor.getString(5));

				String interest = getInterest(cursor.getInt(0));
				contact.setInterest(interest);

				contactList.add(contact);

			} while (cursor.moveToNext());
		}
		return contactList;
	}

	private String getInterest(int id) {

		String selectQuery = "SELECT Interest FROM " + TABLE_INTEREST
				+ " where Sr = " + id;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		String interest = "";

		if (cursor.moveToFirst()) {
			do {
				interest += cursor.getString(0) + ", ";
			} while (cursor.moveToNext());
		}

		return interest;
	}

	public ArrayList<String> getUniqueCity() {

		ArrayList<String> list = new ArrayList<String>();
		db = this.getReadableDatabase();
		list.add("ALL");
		Cursor cursor = db.rawQuery("SELECT Distinct City FROM "
				+ TABLE_DETAILS, null);

		if (cursor.moveToFirst()) {
			do {
				String city = cursor.getString(0);
				list.add(city);
			} while (cursor.moveToNext());
		}
		return list;
	}

	public ArrayList<String> getUniqueInterest() {

		ArrayList<String> list = new ArrayList<String>();
		list.add("ALL");
		db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT Distinct Interest FROM "
				+ TABLE_INTEREST, null);

		if (cursor.moveToFirst()) {
			do {
				String city = cursor.getString(0);
				list.add(city);

			} while (cursor.moveToNext());
		}
		return list;
	}

	public ArrayList<String> getUniqueArea(String city) {

		ArrayList<String> list = new ArrayList<String>();
		db = this.getReadableDatabase();
		list.add("ALL");
		String sql = "SELECT Distinct Area FROM " + TABLE_DETAILS
				+ " where City = '" + city + "'";
		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToFirst()) {
			do {
				String area = cursor.getString(0);
				list.add(area);
			} while (cursor.moveToNext());
		}
		return list;
	}

	public ArrayList<Contact> getCustomizedContacts(String interest,
			String city, String area) {

		ArrayList<Contact> contactList = new ArrayList<Contact>();

		boolean flag = true ;
		
		String selectQuery = "select * from " + TABLE_DETAILS + " as a"
				+ " natural join " + TABLE_INTEREST + " as b " ;

		if (interest.compareTo("ALL") != 0 ) {
			if(flag) { selectQuery += " where " ; flag = false ;}
			else selectQuery += " and ";
			
			selectQuery += " a.Sr = b.sr and b.Interest = '" + interest + "'";
		}

		if (city.compareTo("ALL") != 0) {
			if(flag) { selectQuery += " where " ; flag = false ;}
			else selectQuery += " and ";
	
			selectQuery += " a.City = '" + city + "'" ;
		}
		
		if (area.compareTo("ALL") != 0) {
			if(flag) { selectQuery += " where " ; flag = false ;}
			else selectQuery += " and ";
	
			selectQuery += " a.Area = '" + area + "'" ;
		}
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) { // if (cursor != null)
			do {
				Contact contact = new Contact();
				contact.setName(cursor.getString(1));
				contact.setAddress(cursor.getString(4));
				contact.setPhone(cursor.getString(5));
				contact.setInterest(cursor.getString(6));

				contactList.add(contact);

			} while (cursor.moveToNext());
		}
		return contactList;
	}
}
