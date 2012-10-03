package com.backtohome.adapters;


import java.util.List;


import com.backtohome.R;
import com.backtohome.others.Place;
import com.backtohome.others.PlacesDataSource;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ArrayFeedAdapter extends ArrayAdapter<Place> {
	
	PlacesDataSource placeDataSource;
	
	private Context appContext = null;
    private int resource,resource2;
    private ImageView image;
    public ArrayFeedAdapter(Context context, int textViewResourceId,List<Place> objects) {
            super(context, textViewResourceId, objects);
            resource = textViewResourceId;
            this.appContext = context;
            resource2 = R.layout.test2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
            final RelativeLayout feedView;
            Place places = getItem(position);
            
            final String placeName = places.getPlace();
            String placeTarget = places.getTarget();
            long placeId = places.getId();
            int placeLatitude = places.getLatitude();
            int placeLongitude = places.getLongitude();
            appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
            if(convertView == null) {
                    feedView = new RelativeLayout(getContext());
                    LayoutInflater inflater = 
                            (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    inflater.inflate(resource2, feedView, true);
            } else {
                    feedView = (RelativeLayout)convertView;
            }
            
            TextView tvPlaceTarget = (TextView)feedView.findViewById(R.id.title);
            TextView tvPlaceName = (TextView)feedView.findViewById(R.id.artist);
            TextView tvPlaceId = (TextView)feedView.findViewById(R.id.textView_id);
            TextView tvPlaceLatitude = (TextView)feedView.findViewById(R.id.textView_latitude);
            TextView tvPlaceLongitude = (TextView)feedView.findViewById(R.id.textView_longitud);
            image = (ImageView)feedView.findViewById(R.id.imageView1);
            
           
            tvPlaceId.setText(placeId + "");
            tvPlaceTarget.setText(placeTarget);
            tvPlaceName.setText(placeName);
            tvPlaceLatitude.setText(placeLatitude+"");
            tvPlaceLongitude.setText(placeLongitude+"");
            if(placeTarget.equals("Car"))
            	image.setImageResource(R.drawable.car);
            if(placeTarget.equals("Pub"))
            	image.setImageResource(R.drawable.pub);
            if(placeTarget.equals("Girlfriend"))
            	image.setImageResource(R.drawable.girlfriend);
            
            
            
            
            return feedView;
    }
   
}