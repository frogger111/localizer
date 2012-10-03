package com.backtohome;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

public class Map extends MapActivity{

	public static final String MY_PREFERENCES = "localization";
	
	private SharedPreferences preferences;
	
	private MapView mapView;
	
	private MapController mapController;
	
	private LocationManager locationManager;
	
	private MyLocationOverlay myLocationOverlay;
	
	public GeoPoint p;
	
	private String placeName = "";
	
	private String placeTarget = "";
	
	private String placeDesc = "";
	
	private Button buttonSave, buttonlayers;

	private Boolean map;
	
	private int itemSelected;;
	
	MyItemizedOverlay myItemizedOverlay;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        mapView = (MapView)findViewById(R.id.mapview_point);
        buttonSave = (Button)findViewById(R.id.button_save_point);
        buttonlayers = (Button)findViewById(R.id.button_layers);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
		mapController.setZoom(15);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, new GeoUpdateHandler());
		preferences = getSharedPreferences(MY_PREFERENCES, Activity.MODE_PRIVATE);
		map = preferences.getBoolean("mapView", true);
		if(map == true)
			mapView.setSatellite(true);
		else
			mapView.setSatellite(false);
		
		
		myLocationOverlay = new MyLocationOverlay(this, mapView);
		mapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				mapView.getController().animateTo(
						myLocationOverlay.getMyLocation());
			}
		});
	
        Drawable marker=getResources().getDrawable(R.drawable.pin);
        int markerWidth = marker.getIntrinsicWidth();
        int markerHeight = marker.getIntrinsicHeight();
        marker.setBounds(0, markerHeight, markerWidth, 0);
        myItemizedOverlay = new MyItemizedOverlay(marker);
        mapView.getOverlays().add(myItemizedOverlay);
        
        
		/*
		 * get intent's values
		 * 
		 */
        Bundle extras = getIntent().getExtras();
        if(extras != null){
        	placeName = extras.getString("name");
        	itemSelected = extras.getInt("itemSelected");
			placeTarget = extras.getString("target");
			placeDesc = extras.getString("desc");
			buttonSave.setOnClickListener(setButtonSaveListener);
	    	buttonlayers.setOnClickListener(setButtonLayerListener);
	    }
		
	}
	private OnClickListener setButtonSaveListener = new OnClickListener() {
		
		public void onClick(View v) {
			//show_dialog_save();
			
			int lat = preferences.getInt("mapLatitude", 999999999);
			int longi = preferences.getInt("mapLongitude", 999999999);
			if(lat != 999999999 && longi != 999999999){
				Intent intent = new Intent(Map.this, MarkPoint.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("name", placeName);
				intent.putExtra("target", placeTarget);
				intent.putExtra("latitud", lat);
				intent.putExtra("longitud",longi);
				intent.putExtra("afterMap", true);
				intent.putExtra("itemSelected", itemSelected);
				SharedPreferences.Editor preferencesEditor = preferences.edit();
				preferencesEditor.clear();
				preferencesEditor.commit();
				startActivity(intent);
			}
			else
				Toast.makeText(Map.this, "Tap point on map and click \'back\'", Toast.LENGTH_SHORT).show();
		}
	};
	private OnClickListener setButtonLayerListener = new OnClickListener() {
		
		public void onClick(View v) {
			map = preferences.getBoolean("mapView", true);
			if(map == true)
			{
				mapView.setSatellite(false);
				SharedPreferences.Editor preferencesEditor = preferences.edit();
				preferencesEditor.putBoolean("mapView", false);
				
				preferencesEditor.commit();
			}
			else
			{
				mapView.setSatellite(true);
				SharedPreferences.Editor preferencesEditor = preferences.edit();
				preferencesEditor.putBoolean("mapView", true);
				
				preferencesEditor.commit();
			}
		}
	};
	
	
	
	
	public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem>{
		 
		
		private ArrayList<OverlayItem> overlayItemList = new ArrayList<OverlayItem>();
		 
			
			public MyItemizedOverlay(Drawable marker) {
					super(boundCenterBottom(marker));
					populate();
			}
		 
		public void addItem(GeoPoint p, String title, String snippet){
		OverlayItem newItem = new OverlayItem(p, title, snippet);
		if(overlayItemList.size() > 0){
			overlayItemList.clear();
			overlayItemList.add(newItem);
			}
		else
			overlayItemList.add(newItem);
		populate();
		}
		 
		@Override
		protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return overlayItemList.get(i);
		}
		 
		@Override
		public int size() {
		// TODO Auto-generated method stub
		return overlayItemList.size();
		}
		 
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
		}
		@Override
		public boolean onTap(GeoPoint p, MapView mapView) {
		 String title = "pt:" + String.valueOf(overlayItemList.size() + 1);
		 String snippet = "geo:\n"
		   + String.valueOf(p.getLatitudeE6()) + "\n"
		   + String.valueOf(p.getLongitudeE6());
		 SharedPreferences.Editor preferencesEditor = preferences.edit();
			preferencesEditor.putInt("mapLatitude",p.getLatitudeE6());
			preferencesEditor.putInt("mapLongitude",p.getLongitudeE6());
			preferencesEditor.commit();
		 addItem(p, title, snippet);
		 return true;
		}
			
		}

	public class GeoUpdateHandler implements LocationListener {

		public void onLocationChanged(Location location) {
			Log.d(this.getClass().getName(),"onLocationChanged : lat = "+location.getLatitude()+" lon = "+location.getLongitude());
			//if(azimute != 0)
			//	works = true;
            int lat = (int) Math.round(location.getLatitude()*1.0e6);
            int lng = (int) Math.round(location.getLongitude()*1.0e6);
            p= new GeoPoint(lat, lng);
            //itemizedoverlay.addOverlay(new OverlayItem(geoPoint, "", ""));
            mapController.animateTo(p);

		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			//if(azimute == 0)
			//	works = false;
		}

		
	}
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	protected void onResume() {
		super.onResume();
		myLocationOverlay.enableMyLocation();
		myLocationOverlay.enableCompass();
	}
	/**
	 * 
	 * called when activity is on pause state
	 */
	@Override
	protected void onPause() {
		super.onResume();
		myLocationOverlay.disableMyLocation();
		myLocationOverlay.disableCompass();
	}
	@Override
	public void onStop()
	{
		super.onStop();
		finish();
	}
	@Override
	public void onBackPressed() {
		int lat = preferences.getInt("mapLatitude", 999999999);
		int longi = preferences.getInt("mapLongitude", 999999999);
		if(lat != 999999999 && longi != 999999999){
			Intent intent = new Intent(Map.this, MarkPoint.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("name", placeName);
			intent.putExtra("target", placeTarget);
			intent.putExtra("desc", placeDesc);
			intent.putExtra("latitud", lat);
			intent.putExtra("longitud",longi);
			SharedPreferences.Editor preferencesEditor = preferences.edit();
			preferencesEditor.clear();
			preferencesEditor.commit();
			startActivity(intent);
		}
		else
			Toast.makeText(Map.this, "Tap point on map and click \'back\'", Toast.LENGTH_SHORT).show();
	
	}

}
