package com.backtohome;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.backtohome.adapters.ArrayFeedAdapter;
import com.backtohome.others.KMLHandler;
import com.backtohome.others.Place;
import com.backtohome.others.PlacesDataSource;
import com.backtohome.others.RouteOverlay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class BackToHomeActivity extends MapActivity{
	
    protected static final int SAVE_DIALOG = 0;

	protected static final int ALERT_DIALOG_HOME = 1;
	
	private static final int ALERT_DIALOG_CLEARALL = 2;
	
	private static final int ALERT_DIALOG_EX = 3;

	public static final String MY_PREFERENCES = "myPreferences";

	private static final int ALERT_DIALOG_GOODLUCK = 4;
	
	private static final int ALERT_DIALOG_DELETE = 5;

	private static LocationManager locationManager;

    private Button save, go, settings,imdrunk;
    
    private MapView mapView;
    
    public GeoPoint point;
    public GeoPoint point2;

	public MyOverlays itemizedoverlay;

	private MapController mapController;
	
	private MyLocationOverlay myLocationOverlay;
	
	private PlacesDataSource datasource;
	
	private Dialog dialog_new;
	
	private Place place;
	
	private ListView dialog_ListView;
	
	private SharedPreferences preferences;
	
	private Boolean firstTimeRun;
	
	Location loc;
	
	private static final String TAG = "GPS";

	private static Boolean works = false;
	
	
	private List<Place> list;

	private String targetLongitude;

	private String targetLatitude;
	private String startLatitude;
	private String startLongitude;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        save = (Button)findViewById(R.id.button_save);
        go = (Button)findViewById(R.id.button_go);
        settings = (Button)findViewById(R.id.button_settings);
        imdrunk = (Button)findViewById(R.id.button_imdrunk);
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
		mapController.setZoom(16);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, new GeoUpdateHandler());
		preferences = getSharedPreferences(MY_PREFERENCES, Activity.MODE_PRIVATE);
		
		myLocationOverlay = new MyLocationOverlay(this, mapView);
		mapView.getOverlays().add(myLocationOverlay);
		
		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				mapView.getController().animateTo(
						myLocationOverlay.getMyLocation());
			}
		});
		Drawable drawable = this.getResources().getDrawable(R.drawable.point);
		itemizedoverlay = new MyOverlays(this, drawable);
        checkLocationProviders();
        
        datasource = new PlacesDataSource(this);
        
        
        save.setOnClickListener(setBtnSaveListener);
        go.setOnClickListener(setBtnGoListener);
        settings.setOnClickListener(setBtnSettingsListener);
        imdrunk.setOnClickListener(setBtnImdrunkListener);
        
        locationManager.addGpsStatusListener(gpsListener);
       
        mapView.invalidate();
        Bundle extras = getIntent().getExtras();
 		if(extras != null)
 		{
 			
 			targetLatitude = extras.getString("latitude");
 			targetLongitude = extras.getString("longitude");
 			int lat = preferences.getInt("homeLat", 0);
 			int longi = preferences.getInt("homeLong", 0);
 			//Toast.makeText(BackToHomeActivity.this, preferences.getInt("homeLat", 0)  + " " + preferences.getInt("homeLong", 0), Toast.LENGTH_SHORT).show();
 			if(lat == 0|| longi == 0)
 			{
 				Intent intent = new Intent(BackToHomeActivity.this, Map.class);
 				
 				startActivity(intent);
 			}
 			GeoPoint destGeoPoint = new GeoPoint(Integer.parseInt(targetLatitude),Integer.parseInt(targetLongitude));
 			GeoPoint point2 = new GeoPoint(preferences.getInt("homeLat", 0), preferences.getInt("homeLong", 0));
// 			if(point2.getLatitudeE6() == 0 || point.getLongitudeE6() == 0)
// 				point2 = new GeoPoint(preferences.getInt("homeLat", 0), preferences.getInt("homeLong", 0));
			// we have to check that point isn't null if it's null thats mean that we aren't connected with satelites
			if(point2 != null)
			{
				// draw path from actual point to destanation point
				drawPath(point2, destGeoPoint, mapView);
				//animate map to actual position
				mapView.getController().animateTo(point2);
				// set zoom on the map
		        mapView.getController().setZoom(12);
			}
 			
 		}
    }
    private OnClickListener setBtnSaveListener = new OnClickListener() {
		
		public void onClick(View v) {
			//show_dialog_save();
			Intent intent = new Intent(BackToHomeActivity.this, MarkPoint.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			//myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	        startActivity(intent);
		}
	};
	private OnClickListener setBtnGoListener = new OnClickListener() {
		
		public void onClick(View v) {
//			show_dialog_go();
			Intent intent = new Intent(BackToHomeActivity.this, Find.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(intent);
		}
	};
	private OnClickListener setBtnSettingsListener = new OnClickListener() {
		
		public void onClick(View v) {
			//show_dialog_settings();
			if(works == true)
	        	Toast.makeText(BackToHomeActivity.this, "GPS works", Toast.LENGTH_SHORT).show();
	        else
	        	Toast.makeText(BackToHomeActivity.this, " no fixed", Toast.LENGTH_SHORT).show();
		}
	};
	private OnClickListener setBtnImdrunkListener = new OnClickListener() {
		
		public void onClick(View v) {
			show_dialog_imdrunk();
		}
	};


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
	    	            	  BackToHomeActivity.this.finish();
	    	              }
	    	          }).show();
	    }
	    
	   

	   }

	private  ArrayFeedAdapter adapter;

	protected static float azimute;
	private void show_dialog_save()
	{
	   dialog_new = new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar);
	   
	   ImageView closeImage = new ImageView(this);
	   
	   
	   closeImage.setOnClickListener(new OnClickListener() {
	              public void onClick(View v) {   
	            	  
	               dialog_new.dismiss();                
	              }
	          });
	    
	   Drawable closeDrawable = this.getResources().getDrawable(R.drawable.close);
	   closeImage.setImageDrawable(closeDrawable);         
	          
	   FrameLayout contentLayout = new FrameLayout(this);    
	   contentLayout.setPadding(5,5,5,5);   
	   
	   LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);    
	   RelativeLayout new_layout = (RelativeLayout) inflater.inflate(R.layout.dialog_save, (ViewGroup) findViewById(R.id.dialog_lay));
	   Button btnProfile = (Button)new_layout.findViewById(R.id.button_dialog_save);
	   final EditText edTxtPlace = (EditText)new_layout.findViewById(R.id.editText_dialog_save);
	   final Spinner spSave = (Spinner)new_layout.findViewById(R.id.spinner_dialog_save);
	   
	   
	   ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			   R.array.place, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spSave.setAdapter(adapter);
		
		final String name = edTxtPlace.getText().toString();
		final String origVal = getResources().getText(R.string.NameDefault).toString();
		edTxtPlace.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(name.equals(origVal));
				{
				edTxtPlace.setText("");

				}
				
			}
		});
		
		
	   btnProfile.setOnClickListener(new OnClickListener() {
		
		public void onClick(View v) {
			place = new Place();
			place.setPlace(edTxtPlace.getText().toString());
			place.setTarget(spSave.getSelectedItem().toString());
			
			place.setAccuracy(0);
			firstTimeRun = preferences.getBoolean("firstTimeRun", true);
			if(spSave.getSelectedItemPosition() == 0)
			{
				if(firstTimeRun == false)
					showDialog(ALERT_DIALOG_HOME);
				else
				{

						place.setAccuracy(1);
						BackToHomeActivity.this.datasource.open();
						BackToHomeActivity.this.datasource.createPlace(place);
        				SharedPreferences.Editor preferencesEditor = preferences.edit();
    					preferencesEditor.putBoolean("firstTimeRun", false);
    					preferencesEditor.commit();
    					Toast.makeText(BackToHomeActivity.this, "Saved", Toast.LENGTH_SHORT).show();
					}
			}
			else
			{
				
				BackToHomeActivity.this.datasource.open();
				BackToHomeActivity.this.datasource.createPlace(place);
				Toast.makeText(BackToHomeActivity.this, "Saved", Toast.LENGTH_SHORT).show();
	      	  	BackToHomeActivity.this.datasource.close();
			}
			
			
			dialog_new.dismiss();
		}
	   });
	   
	   contentLayout.addView(new_layout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));     
	   contentLayout.addView(closeImage, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	   
	   dialog_new.addContentView(contentLayout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	  
	   dialog_new.show();
	}
	private void show_dialog_go()
	{
	   dialog_new = new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar);
	   list = new ArrayList<Place>();
	  
	   
	   BackToHomeActivity.this.datasource.open();
	   list = BackToHomeActivity.this.datasource.getAllComments();
	   BackToHomeActivity.this.datasource.close(); 
	   ImageView closeImage = new ImageView(this);
	   
	   
	   closeImage.setOnClickListener(new OnClickListener() {
	              public void onClick(View v) {                   
	               dialog_new.dismiss();                
	              }
	          });
	    
	   Drawable closeDrawable = this.getResources().getDrawable(R.drawable.close);
	   closeImage.setImageDrawable(closeDrawable);         
	          
	   FrameLayout contentLayout = new FrameLayout(this);    
	   contentLayout.setPadding(5,5,5,5);   
	   
	   LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);    
	   final RelativeLayout new_layout = (RelativeLayout) inflater.inflate(R.layout.dialog_find, (ViewGroup) findViewById(R.id.dialog_lay));
	   
	   
	   contentLayout.addView(new_layout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));     
	   contentLayout.addView(closeImage, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	   
	   dialog_new.addContentView(contentLayout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	   dialog_ListView = (ListView)dialog_new.findViewById(R.id.listView1);
	   
	   adapter= new ArrayFeedAdapter(this, R.layout.list_row, list);
	   dialog_ListView.setAdapter(adapter);
	   dialog_ListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	   Button btnTrashAll = (Button)new_layout.findViewById(R.id.button_trashall);
	   
	   btnTrashAll.setOnClickListener(new OnClickListener() {
		
		public void onClick(View v) {
			showDialog(ALERT_DIALOG_CLEARALL);
			dialog_new.dismiss();
		}
	   });
	   
	   dialog_ListView.setOnItemClickListener(new OnItemClickListener(){
		   
	        public void onItemClick(AdapterView<?> arg0, View arg1,
	                int position, long arg3) {
	        	//Toast.makeText(getApplicationContext(), "item " + position, Toast.LENGTH_SHORT).show();
	        	
	        }
	 
	    });
	   dialog_ListView.setOnItemLongClickListener(new OnItemLongClickListener() {

		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			TextView txId = (TextView)arg1.findViewById(R.id.textView_id);
			TextView txName = (TextView)arg1.findViewById(R.id.title);
			String StringTarget = (String)txName.getText();
			String stringId = (String) txId.getText();
			int aInt = Integer.parseInt(stringId);
			if(StringTarget.equals("Home"))
			{
				SharedPreferences.Editor preferencesEditor = preferences.edit();
				preferencesEditor.putBoolean("firstTimeRun", true);
				preferencesEditor.commit();
			}
			BackToHomeActivity.this.datasource.open();
     	  	BackToHomeActivity.this.datasource.deletePlace(aInt);
     	  	Toast.makeText(BackToHomeActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
     	  	BackToHomeActivity.this.datasource.close();
			
			dialog_new.dismiss();
			show_dialog_go();
			return false;
		}
	});
	   dialog_new.show();
	}
	
	private void show_dialog_settings()
	{
	   dialog_new = new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar);
	   
	   ImageView closeImage = new ImageView(this);
	   
	   
	   closeImage.setOnClickListener(new OnClickListener() {
	              public void onClick(View v) {                   
	               dialog_new.dismiss();                
	              }
	          });
	    
	   Drawable closeDrawable = this.getResources().getDrawable(R.drawable.close);
	   closeImage.setImageDrawable(closeDrawable);         
	          
	   FrameLayout contentLayout = new FrameLayout(this);    
	   contentLayout.setPadding(5,5,5,5);   
	   
	   LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);    
	   RelativeLayout new_layout = (RelativeLayout) inflater.inflate(R.layout.dialog_save, (ViewGroup) findViewById(R.id.dialog_lay));
	   Button btnProfile = (Button)new_layout.findViewById(R.id.button_dialog_save);
	   final EditText edTxtNick = (EditText)new_layout.findViewById(R.id.editText_dialog_save);
	   
	   btnProfile.setOnClickListener(new OnClickListener() {
		
		public void onClick(View v) {
			Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
			dialog_new.dismiss();
		}
	   });
	   
	   contentLayout.addView(new_layout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));     
	   contentLayout.addView(closeImage, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	   
	   dialog_new.addContentView(contentLayout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	  
	   dialog_new.show();
	}
	private void show_dialog_imdrunk()
	{
	   dialog_new = new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar);
	   
	   ImageView closeImage = new ImageView(this);
	   closeImage.setOnClickListener(new OnClickListener() {
	              public void onClick(View v) {                   
	               dialog_new.dismiss();                
	              }
	          });
	    
	   Drawable closeDrawable = this.getResources().getDrawable(R.drawable.close);
	   closeImage.setImageDrawable(closeDrawable);         
	   
	   FrameLayout contentLayout = new FrameLayout(this);    
	   contentLayout.setPadding(5,5,5,5);   
	  
	   LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);    
	   RelativeLayout new_layout = (RelativeLayout) inflater.inflate(R.layout.dialog_imdrunk, (ViewGroup) findViewById(R.id.dialog_lay));
	   Button btnHome = (Button)new_layout.findViewById(R.id.button_imdrunk_home);
	   Button btnGirlfriend = (Button)new_layout.findViewById(R.id.button_imdrunk_girlfriend);
	   Button btnExGirlfriend = (Button)new_layout.findViewById(R.id.button_imdrunk_ex);
	   btnHome.setOnClickListener(new OnClickListener() {
		
		public void onClick(View v) {
			Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
			dialog_new.dismiss();
		}
	   });
	   btnGirlfriend.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Girlfriend", Toast.LENGTH_SHORT).show();
				dialog_new.dismiss();
			}
		   });
	   btnExGirlfriend.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				showDialog(ALERT_DIALOG_EX);
				dialog_new.dismiss();
			}
		   });
	   
	   contentLayout.addView(new_layout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));     
	   contentLayout.addView(closeImage, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	   
	   dialog_new.addContentView(contentLayout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	  
	   dialog_new.show();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
	        final Dialog dialog;
	        switch (id) {
	       
	            
	        case ALERT_DIALOG_HOME:
	        	Context context4=BackToHomeActivity.this;
		 	    dialog=new Dialog(context4);
		 	    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 	    dialog.setContentView(R.layout.alert_home);
		 	    Button btn1Home = (Button)dialog.findViewById(R.id.button1_home);
		 	    Button btn2Home = (Button)dialog.findViewById(R.id.button2_home);
		 	     
		 	    btn1Home.setOnClickListener(new OnClickListener() {
			 	       public void onClick(View v) {
			 	    	   	BackToHomeActivity.this.datasource.open();
			 	    	   	Place a = BackToHomeActivity.this.datasource.getEntry(1);
			 	    	  	long index = a.getId();
			 	    	  	place.setAccuracy(1);
		              	  	BackToHomeActivity.this.datasource.updateHome(index, place);
		        			Toast.makeText(BackToHomeActivity.this, "Saved", Toast.LENGTH_SHORT).show();
		              	  	BackToHomeActivity.this.datasource.close();
		                
			 	          dialog.dismiss();
			 	       }
			 	       });
		 	    btn2Home.setOnClickListener(new OnClickListener() {
		 	       public void onClick(View v) {
		 	    	  
		 	          dialog.dismiss();
		 	       }
		 	       });
		 	    
	            break;
	        case ALERT_DIALOG_CLEARALL:
	             Context context3=BackToHomeActivity.this;
		 	     dialog=new Dialog(context3);
		 	     dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 	     dialog.setContentView(R.layout.alert_deleteall);
		 	     Button btn1Remove = (Button)dialog.findViewById(R.id.button1_remove);
		 	     Button btn2Remove = (Button)dialog.findViewById(R.id.button2_remove);
		 	     
		 	    btn1Remove.setOnClickListener(new OnClickListener() {
			 	       public void onClick(View v) {
			 	    	  BackToHomeActivity.this.datasource.open();
		              	  	BackToHomeActivity.this.datasource.deleteAll();
		              	    SharedPreferences.Editor preferencesEditor = preferences.edit();
		    				preferencesEditor.putBoolean("firstTimeRun", true);
		    				preferencesEditor.commit();
		        			Toast.makeText(BackToHomeActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
		              	  	BackToHomeActivity.this.datasource.close();
			 	          dialog.dismiss();
			 	         show_dialog_go();
			 	       }
			 	       });
		 	    btn2Remove.setOnClickListener(new OnClickListener() {
		 	       public void onClick(View v) {
		 	    	  
		 	          dialog.dismiss();
		 	         show_dialog_go();
		 	       }
		 	       });
	            
	            break;
	        case ALERT_DIALOG_DELETE:
	             Context context5=BackToHomeActivity.this;
		 	     dialog=new Dialog(context5);
		 	     dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 	     dialog.setContentView(R.layout.alert_deleteall);
		 	     Button btn1Delete = (Button)dialog.findViewById(R.id.button1_remove);
		 	     Button btn2Delete = (Button)dialog.findViewById(R.id.button2_remove);
		 	     
		 	    btn1Delete.setOnClickListener(new OnClickListener() {
			 	       public void onClick(View v) {
			 	    	  BackToHomeActivity.this.datasource.open();
		              	  	BackToHomeActivity.this.datasource.deleteAll();
		              	    SharedPreferences.Editor preferencesEditor = preferences.edit();
		    				preferencesEditor.putBoolean("firstTimeRun", true);
		    				preferencesEditor.commit();
		        			Toast.makeText(BackToHomeActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
		              	  	BackToHomeActivity.this.datasource.close();
			 	          dialog.dismiss();
			 	         show_dialog_go();
			 	       }
			 	       });
		 	    btn2Delete.setOnClickListener(new OnClickListener() {
		 	       public void onClick(View v) {
		 	    	  
		 	          dialog.dismiss();
		 	         show_dialog_go();
		 	       }
		 	       });
	            
	            break;
	        case ALERT_DIALOG_EX:
	        	Context context=BackToHomeActivity.this;
		 	     dialog=new Dialog(context);
		 	     dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 	     dialog.setContentView(R.layout.alert_ex);
		 	     Button btn1Ex = (Button)dialog.findViewById(R.id.button1_ex);
		 	     Button btn2Ex = (Button)dialog.findViewById(R.id.button2_ex);
		 	     
		 	    btn1Ex.setOnClickListener(new OnClickListener() {
			 	       public void onClick(View v) {
			 	    	   showDialog(ALERT_DIALOG_GOODLUCK);
			 	          dialog.dismiss();
			 	       }
			 	       });
		 	    btn2Ex.setOnClickListener(new OnClickListener() {
		 	       public void onClick(View v) {
		 	    	  
		 	          dialog.dismiss();
		 	       }
		 	       });
	            break;
	        case ALERT_DIALOG_GOODLUCK:
	        	Context context2=BackToHomeActivity.this;
		 	     dialog=new Dialog(context2);
		 	     dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 	     dialog.setContentView(R.layout.goodluck);
		 	     Button btnGoodluck = (Button)dialog.findViewById(R.id.button_googluck);
		 	     btnGoodluck.setOnClickListener(new OnClickListener() {
			 	       public void onClick(View v) {
			 	          dialog.dismiss();
			 	       }
			 	       });
		 	   
	            break;
	        default:
	            dialog = null;
	            break;
	        }
	        return dialog;
	    }
	public class GeoUpdateHandler implements LocationListener {

		public void onLocationChanged(Location location) {
			Log.d(this.getClass().getName(),"onLocationChanged : lat = "+location.getLatitude()+" lon = "+location.getLongitude());
			if(azimute != 0)
				works = true;
            int lat = (int) Math.round(location.getLatitude()*1.0e6);
            int lng = (int) Math.round(location.getLongitude()*1.0e6);
            point = new GeoPoint(lat, lng);
            SharedPreferences.Editor preferencesEditor = preferences.edit();
			preferencesEditor.putInt("homeLat",point.getLatitudeE6());
			preferencesEditor.putInt("homeLong",point.getLongitudeE6());
			preferencesEditor.commit();
            itemizedoverlay.addOverlay(new OverlayItem(point, "", ""));
            mapController.animateTo(point);

		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			if(azimute == 0)
				works = false;
		}

		
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	/**
	 * 
	 * called when activity is on resume state
	 */
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
                        //Log.d(TAG, "onGpsStatusChanged(): " + satellite.getPrn() + "," + satellite.usedInFix() + "," + satellite.getSnr() + "," + satellite.getAzimuth() + "," + satellite.getElevation()); 
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
    public void onBackPressed() {
       Log.d("CDA", "onBackPressed Called");
       Intent setIntent = new Intent(Intent.ACTION_MAIN);
       setIntent.addCategory(Intent.CATEGORY_HOME);
       setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       startActivity(setIntent);
    }
    private void drawPath(GeoPoint src, GeoPoint dest, MapView mapView) {
        String strUrl = "http://maps.google.com/maps?";
        // from
        strUrl += "saddr=" +
               (src.getLatitudeE6()/1.0E6) + 
               "," +
               (src.getLongitudeE6()/1.0E6);
        // to
        strUrl += "&daddr=" +
               (dest.getLatitudeE6()/1.0E6) + 
               "," + 
               (dest.getLongitudeE6()/1.0E6);
        // walk attribute (for walk path)
        strUrl += "&dirflg=w";
        // file format
        strUrl += "&output=kml";
        
        try {
            // parse KML
            URL url = new URL(strUrl.toString());
            
            SAXParserFactory saxFactory = SAXParserFactory.newInstance();
            SAXParser parser = saxFactory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            
            KMLHandler kmlHandler = new KMLHandler();
            reader.setContentHandler(kmlHandler);
            
            InputSource inputSource = new InputSource(url.openStream());
            reader.parse(inputSource);

            String path = kmlHandler.getPathCoordinates();
            // draw path
            if(path != null) {
                RouteOverlay routeOverlay = new RouteOverlay();
                
                String pairs[] = path.split(" ");
                
                for (String pair : pairs) {
                    String coordinates[] = pair.split(",");
                    GeoPoint geoPoint = new GeoPoint(
                            (int) (Double.parseDouble(coordinates[1]) * 1E6),
                            (int) (Double.parseDouble(coordinates[0]) * 1E6));
                    routeOverlay.addGeoPoint(geoPoint);
                }
                
                mapView.getOverlays().add(routeOverlay);
            }
        } catch (Exception e) {
            Log.w("RoutePath", e.toString());
        }
    }
	
}