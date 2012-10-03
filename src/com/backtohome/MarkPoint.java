package com.backtohome;

import java.util.Iterator;

import com.backtohome.BackToHomeActivity.GeoUpdateHandler;
import com.backtohome.others.Place;
import com.backtohome.others.PlacesDataSource;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MarkPoint extends Activity{

	private String placeName;
	private String placeTarget;
	private String placeDesc;
	private EditText edTextName;
	private EditText edTextDesc;
	private Button btnMap;
	private Button btnFind;
	private Button btnMarkPoint;
	private Spinner spiTarget;
	private int placeLatitude;
	private int placeLongitude;
	private static LocationManager locationManager;
	private static final String TAG = "GPS";

	private static Boolean works = false;
	protected static float azimute;
	private PlacesDataSource datasource;
	private Place place;
	private Boolean afterMap = false;
	public GeoPoint point;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
	        setContentView(R.layout.mark_point);
	        edTextName = (EditText)findViewById(R.id.editText_dialog_save);
	        edTextDesc = (EditText)findViewById(R.id.editText_description);
	        btnMap = (Button)findViewById(R.id.button_map);
	        btnFind = (Button)findViewById(R.id.button_go);
	        spiTarget = (Spinner)findViewById(R.id.spinner_target) ;
	        btnMarkPoint = (Button)findViewById(R.id.button_mark_point);
	        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	        
	        btnMap.setOnClickListener(setBtnMapListener);
	        btnFind.setOnClickListener(setBtnFindListener);
	        btnMarkPoint.setOnClickListener(setBtnMarkListener);
	        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
					0, new GeoUpdateHandler());
			checkLocationProviders();
			locationManager.addGpsStatusListener(gpsListener);
	        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
	 			   R.array.place, android.R.layout.simple_spinner_item);
	 		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	 		spiTarget.setAdapter(adapter);
	 		datasource = new PlacesDataSource(this);
	 		Bundle extras = getIntent().getExtras();
	 		if(extras != null)
	 		{
	 			placeName = extras.getString("name");
	 			placeTarget = extras.getString("target");
	 			placeDesc = extras.getString("desc");
	 			placeLatitude = extras.getInt("latitud");
	 			placeLongitude = extras.getInt("longitud");
	 			afterMap = extras.getBoolean("afterMap");
	 			edTextName.setText(placeName);
	 			edTextDesc.setText(placeDesc);
	 			spiTarget.setSelection(extras.getInt("itemSelected"));
	 			
	 		}
			
	}
	private OnClickListener setBtnMapListener = new OnClickListener() {
		
		public void onClick(View v) {
			Intent myIntent = new Intent(MarkPoint.this, BackToHomeActivity.class);
	        startActivity(myIntent);
		}
	};
	private OnClickListener setBtnFindListener = new OnClickListener() {
		
		public void onClick(View v) {
			Intent myIntent = new Intent(MarkPoint.this, Find.class);
	        startActivity(myIntent);
		}
	};
	
	private OnClickListener setBtnMarkListener = new OnClickListener() {
		
		public void onClick(View v) {
			if(works == false || afterMap == true){
				if(afterMap == false){
				/**
				 * dialog show, gps is unavaliable, mark on map......
				 * 
				 */
				String name = edTextName.getText().toString();
				String desc = edTextDesc.getText().toString();
				String target = spiTarget.getSelectedItem().toString();
				Intent intent = new Intent(MarkPoint.this,Map.class);
				intent.putExtra("name", name);
				intent.putExtra("target", target);
				intent.putExtra("desc", desc);
				intent.putExtra("itemSelected", spiTarget.getSelectedItemPosition());
				startActivity(intent);
				}
				else
				{
					place = new Place();
					place.setPlace(placeName);
					place.setTarget(placeTarget);
					place.setAccuracy(0);
			        datasource.open();
			       
			        	place.setLatitude(placeLatitude);
			        	place.setLongitude(placeLongitude);
						Toast.makeText(getApplicationContext(), "Save", Toast.LENGTH_LONG).show();
					
			        datasource.createPlace(place);
			        datasource.close();
			        afterMap = false;
			        finish();
				}
				
			}
			else{
				place = new Place();
				
				if(point != null)
				{
					int lat = point.getLatitudeE6();
					int longi = point.getLongitudeE6();
					if(lat == 0 || longi == 0)
						Toast.makeText(MarkPoint.this, "przelacz na mape", Toast.LENGTH_SHORT).show();
					else{
						Toast.makeText(getApplicationContext(), "Savead", Toast.LENGTH_LONG).show();
						place.setPlace(edTextName.getText().toString());
						place.setTarget(spiTarget.getSelectedItem().toString());
						place.setLatitude(lat);
						place.setLongitude(longi);
						place.setAccuracy(0);
						datasource.open();
						datasource.createPlace(place);
						datasource.close();
						finish();
			        }
				}
				else{
					if(afterMap == false){
						/**
						 * dialog show, gps is unavaliable, mark on map......
						 * 
						 */
						String name = edTextName.getText().toString();
						String desc = edTextDesc.getText().toString();
						String target = spiTarget.getSelectedItem().toString();
						Intent intent = new Intent(MarkPoint.this,Map.class);
						intent.putExtra("name", name);
						intent.putExtra("target", target);
						intent.putExtra("desc", desc);
						intent.putExtra("itemSelected", spiTarget.getSelectedItemPosition());
						startActivity(intent);
						}
				}
		        
			}
		}
	};
	
	
	public class GeoUpdateHandler implements LocationListener {

		public void onLocationChanged(Location location) {
			if(azimute != 0)
				works = true;
			else
				works = false;
			int lat = (int) Math.round(location.getLatitude()*1.0e6);
            int lng = (int) Math.round(location.getLongitude()*1.0e6);
            point = new GeoPoint(lat, lng);
		}
		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			if(azimute == 0)
				works = false;
			else
				works = true;
		}

		
	}
	/**
	 *
	 * check location providers 
	 */
	private void checkLocationProviders(){
	    if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
	    	 AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	   builder.setMessage("Location providers are not available. Enable GPS and network providers.")
	    	          .setCancelable(false)
	    	          .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	    	              public void onClick(DialogInterface dialog, int id) {
	    	            	  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	    	   	          startActivityForResult(intent, 1);
	    	              }
	    	          })
	    	          .setNegativeButton("No", new DialogInterface.OnClickListener() {
	    	              public void onClick(DialogInterface dialog, int id) {
	    	            	  MarkPoint.this.finish();
	    	              }
	    	          }).show();
	    }
	    
	   

	   }
	private static final GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            GpsStatus gpsStatus = locationManager.getGpsStatus(null);
            switch (event) {
                case GpsStatus.GPS_EVENT_STARTED: 
                    Log.i(TAG, "Start");
                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX: 
                    Log.i(TAG, "Jest");
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS: 
                   
                    Iterable<GpsSatellite>satellites = gpsStatus.getSatellites();
                    Iterator<GpsSatellite>satI = satellites.iterator();
                    while (satI.hasNext()) {
                        GpsSatellite satellite = satI.next();
                        Log.d(TAG, "onGpsStatusChanged(): " + satellite.getPrn() + "," + satellite.usedInFix() + "," + satellite.getSnr() + "," + satellite.getAzimuth() + "," + satellite.getElevation()); 
                        works = satellite.usedInFix();
                        azimute = satellite.getAzimuth();
                    }
                    break;
                case GpsStatus.GPS_EVENT_STOPPED: 
                    Log.i(TAG, "STOP");
                    break;
            }       
        }
    };
	@Override
	public void onStop()
	{
		super.onStop();
		finish();
	}
	
}
