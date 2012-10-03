package com.backtohome;
import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
 
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem>{
	 
	
private ArrayList<OverlayItem> overlayItemList = new ArrayList<OverlayItem>();
 
	
	public MyItemizedOverlay(Drawable marker) {
			super(boundCenterBottom(marker));
			populate();
	}
 
public void addItem(GeoPoint p, String title, String snippet){
OverlayItem newItem = new OverlayItem(p, title, snippet);
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
// TODO Auto-generated method stub
super.draw(canvas, mapView, shadow);
//boundCenterBottom(marker);
}
@Override
public boolean onTap(GeoPoint p, MapView mapView) {
 // TODO Auto-generated method stub
 String title = "pt:" + String.valueOf(overlayItemList.size() + 1);
 String snippet = "geo:\n"
   + String.valueOf(p.getLatitudeE6()) + "\n"
   + String.valueOf(p.getLongitudeE6());
  
 addItem(p, title, snippet);
 return true;
}
	
}
