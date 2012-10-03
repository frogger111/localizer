package com.backtohome.others;

import java.util.ArrayList;
import java.util.List;

import com.backtohome.sqlite.MySQLiteHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class PlacesDataSource {
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.PLACE_ID,
			MySQLiteHelper.PLACE_NAME,MySQLiteHelper.PLACE_TARGET,MySQLiteHelper.PLACE_LATITUDE,
			MySQLiteHelper.PLACE_LONGITUDE };
	private static PlacesDataSource mInstance = null;
	
	//private Context mCxt;
	  
    public static PlacesDataSource getInstance(Context ctx) {
        /** 
         * use the application context, which will ensure that you 
         * don't accidentally leak an Activity's context.
         * see this article for more information: 
         * developer.android.com/resources/articles/avoiding-memory-leaks.html
         */
        if (mInstance == null) {
            mInstance = new PlacesDataSource(ctx.getApplicationContext());
        }
        return mInstance;
    }
	
	
	
	public PlacesDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public long createPlace(Place place) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.PLACE_NAME,place.getPlace());
		values.put(MySQLiteHelper.PLACE_TARGET,place.getTarget());
		values.put(MySQLiteHelper.PLACE_LATITUDE, place.getLatitude());
		values.put(MySQLiteHelper.PLACE_LONGITUDE, place.getLongitude());
		values.put(MySQLiteHelper.PLACE_ACCURACY, place.getAccuracy());
		
		//return newComment;
		return database.insert(MySQLiteHelper.TABLE_PLACES, null, values);
	}
	public boolean updateHome(long index, Place place) {
	    //Warunek wstawiany do klauzuli WHERE
	    String where = MySQLiteHelper.PLACE_ID+ "=" + index;
	    //Tak samo jak przy metodzie insert
	    ContentValues updateHomeValues = new ContentValues();
	    updateHomeValues.put(MySQLiteHelper.PLACE_NAME, place.getPlace());
	    updateHomeValues.put(MySQLiteHelper.PLACE_TARGET,place.getTarget());
		updateHomeValues.put(MySQLiteHelper.PLACE_LATITUDE, place.getLatitude());
		updateHomeValues.put(MySQLiteHelper.PLACE_LONGITUDE, place.getLongitude());
		updateHomeValues.put(MySQLiteHelper.PLACE_ACCURACY, place.getAccuracy());
	    //Aktualizujemy dane wiersza zgodnego ze zmienn¹ where
	    return database.update(MySQLiteHelper.TABLE_PLACES, updateHomeValues, where, null) > 0;
	}
	public void deletePlace(long id) {
		
		database.delete(MySQLiteHelper.TABLE_PLACES, MySQLiteHelper.PLACE_ID
				+ " = " + id, null);
	}
	public void deleteAll() {
		database.delete(MySQLiteHelper.TABLE_PLACES, null, null);
	}

	public List<Place> getAllComments() {
		List<Place> comments = new ArrayList<Place>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_PLACES,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Place comment = cursorToPlace(cursor);
			comments.add(comment);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return comments;
	}

	private Place cursorToPlace(Cursor cursor) {
		Place place = new Place();
		place.setId(cursor.getLong(0));
		place.setPlace(cursor.getString(1));
		place.setTarget(cursor.getString(2));
		place.setLatitude(cursor.getInt(3));
		place.setLongitude(cursor.getInt(4));
		return place;
	}

	public long findHomeIndex() {
		long index = 1;
		String where = MySQLiteHelper.PLACE_TARGET+ "=Home";
		Cursor cursor = database.query(MySQLiteHelper.TABLE_PLACES,
				allColumns, where, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Place comment = cursorToPlace(cursor);
			index = comment.getId();
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return index;
		
	}
	
	public Place getEntry(int index) {
		 String where = "place_accuracy="+index;
		 Cursor cursor = database.query(true, MySQLiteHelper.TABLE_PLACES, allColumns, where, 
		     null, null, null, null, null);
		 
		cursor.moveToFirst();
		Place comment = cursorToPlace(cursor);
		cursor.close();
		 
		 return comment;
		}



	public void createDataBase() {
		// TODO Auto-generated method stub
		
	}
}
