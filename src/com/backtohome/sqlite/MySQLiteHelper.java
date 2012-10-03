package com.backtohome.sqlite;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_PLACES = "places";
	public static final String PLACE_ID = "place_id";
	public static final String PLACE_NAME = "place_name";
	public static final String PLACE_TARGET = "palce_target";
	public static final String PLACE_LATITUDE = "place_latitude";
	public static final String PLACE_LONGITUDE = "place_longitude";
	public static final String PLACE_ACCURACY = "place_accuracy";
	public static final String DATABASE_NAME = "places.db";
	
	private static final int DATABASE_VERSION = 1;
	
	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_PLACES + "(" +PLACE_ID
			+ " integer primary key autoincrement, " + PLACE_NAME
			+ " text not null, " + PLACE_TARGET + " text null, " + PLACE_LATITUDE 
			+ " real null,  " + PLACE_LONGITUDE + " real null, "+ PLACE_ACCURACY + " integer null);";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	public void createDataBase()
	{
		
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
		onCreate(db);
	}
	

} 
