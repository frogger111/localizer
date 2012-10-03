package com.backtohome;

import java.util.ArrayList;
import java.util.List;

import com.backtohome.adapters.ArrayFeedAdapter;
import com.backtohome.others.Place;
import com.backtohome.others.PlacesDataSource;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;



public class Find extends Activity{

	protected static final int ALERT_DIALOG_CLEARALL = 0;
	private static LocationManager locationManager;
	private List<Place> list;
	private PlacesDataSource datasource;
	private ListView listView;
	private  ArrayFeedAdapter adapter;
	private Button btnTrashAll;
	ProgressDialog pDialog;
	@Override
	public void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
	        setContentView(R.layout.find);
	        
	        Button btnMap = (Button)findViewById(R.id.button_map);
	        Button btnSave = (Button)findViewById(R.id.button_save);
//	        Button btnShare = (Button)findViewById(R.id.button_share);
	        btnTrashAll = (Button)findViewById(R.id.button_trashall);
	        listView = (ListView)findViewById(R.id.listView1);
	        btnMap.setOnClickListener(setBtnMapListener);
	        btnSave.setOnClickListener(setBtnSaveListener);
	        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	        checkLocationProviders();
	        datasource = new PlacesDataSource(this);
	        list = new ArrayList<Place>();
	        try{
	        	datasource.open();
	 	   		list = datasource.getAllComments();
	        }
	        catch (Exception e) {
	        	;
			}
	        finally{
	 	    	datasource.close();
	        }
			adapter = new ArrayFeedAdapter(this, R.layout.list_row, list);
			adapter.notifyDataSetChanged();
	 	    listView.setAdapter(adapter);
	 	    btnTrashAll.setOnClickListener(new OnClickListener() {
	 			
	 			public void onClick(View v) {
	 				showDialog(ALERT_DIALOG_CLEARALL);
	 				
	 			}
	 		   });
	 	   listView.setOnItemClickListener(new OnItemClickListener() {  
			

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
//				TextView txId = (TextView)arg1.findViewById(R.id.textView_id);
				//TextView txName = (TextView)arg1.findViewById(R.id.title);
				TextView txLatitude = (TextView)arg1.findViewById(R.id.textView_latitude);
				TextView txLongitude = (TextView)arg1.findViewById(R.id.textView_longitud);
//				String stringId = (String) txId.getText();
				String stringLatitude = (String)txLatitude.getText();
				String stringLongitude = (String)txLongitude.getText();
//				int aInt = Integer.parseInt(stringId);
				Intent intent = new Intent(Find.this, BackToHomeActivity.class);
				intent.putExtra("latitude",stringLatitude);
				intent.putExtra("longitude", stringLongitude);
				startActivity(intent);
				
			}

			
			        
			      });
	}
	
	private OnClickListener setBtnMapListener = new OnClickListener() {
		
		public void onClick(View v) {
			Intent myIntent = new Intent(Find.this, BackToHomeActivity.class);
	        startActivity(myIntent);
		}
	};
	private OnClickListener setBtnSaveListener = new OnClickListener() {
		
		public void onClick(View v) {
			Intent myIntent = new Intent(Find.this, MarkPoint.class);
	        startActivity(myIntent);
		}
	};
	@Override
	protected Dialog onCreateDialog(int id) {
	        final Dialog dialog;
	        switch (id) {
	        case ALERT_DIALOG_CLEARALL:
	             Context context1=Find.this;
		 	     dialog=new Dialog(context1);
		 	     dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 	     dialog.setContentView(R.layout.alert_deleteall);
		 	     Button btn1Remove = (Button)dialog.findViewById(R.id.button1_remove);
		 	     Button btn2Remove = (Button)dialog.findViewById(R.id.button2_remove);
		 	     
		 	    btn1Remove.setOnClickListener(new OnClickListener() {
			 	       public void onClick(View v) {
			 	    	  Find.this.datasource.open();
		              	  	Find.this.datasource.deleteAll();
		        			Toast.makeText(Find.this, "Deleted", Toast.LENGTH_SHORT).show();
		              	  	Find.this.datasource.close();
			 	          dialog.dismiss();
			 	          Intent intent = new Intent(Find.this, Find.class);
			 	          startActivity(intent);
			 	         }
			 	       });
		 	    btn2Remove.setOnClickListener(new OnClickListener() {
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
	    	              }
	    	          }).show();
	    }
	    
	   

	   }
	@Override
	public void onStop()
	{
		super.onStop();
		finish();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
	}
}
