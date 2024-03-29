package com.backtohome.others;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class RouteOverlay extends Overlay {
	private ArrayList<GeoPoint> geoPoints;
	private int mRadius = 5;

	public RouteOverlay() {
		geoPoints = new ArrayList<GeoPoint>();
	}
	/**
	 * 
	 * @param gp
	 * 
	 * this methos add geo point to list geoPoints
	 */
	public void addGeoPoint(GeoPoint gp) {
		geoPoints.add(gp);	
	}
	
	/**
	 * 
	 * 
	 *  this method draw path
	 */
	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
			long when) {
		if (shadow == false) {
			Projection projection = mapView.getProjection();
			Paint paint = new Paint();
			paint.setAntiAlias(true);

			GeoPoint geoPointFrom = null;
			GeoPoint geoPointTo = null;
			Point pointFrom = new Point();
			Point pointTo = new Point();
				
			/**
			 * 
			 * foreach loop in first pass asign first point to variable geoPointFrom
			 * On the next steps of loop this method paint path between geoPointsFrom to 
			 * geoPointTo
			 */
			for (GeoPoint geoPoint : geoPoints) {
				if(geoPointFrom != null) {
					geoPointTo = geoPoint;

					projection.toPixels(geoPointFrom, pointFrom);
					projection.toPixels(geoPointTo, pointTo);

					paint.setColor(Color.GREEN);
					paint.setStrokeWidth(5);
					paint.setAlpha(120);

					canvas.drawLine(pointFrom.x, pointFrom.y, 
									pointTo.x, pointTo.y, 
									paint);

					geoPointFrom = geoPointTo;
				} else {
					geoPointFrom = geoPoint;
				}
			}

			//Start point
			paint.setColor(Color.BLUE);
			projection.toPixels(geoPoints.get(0), pointFrom);
			RectF ovalStart = new RectF(pointFrom.x - mRadius, 
										pointFrom.y - mRadius,
										pointFrom.x + mRadius, 
										pointFrom.y + mRadius);
			canvas.drawOval(ovalStart, paint);

			//Stop point
			paint.setColor(Color.RED);
			RectF ovalStop = new RectF(pointTo.x - mRadius, 
								   	   pointTo.y - mRadius,
								   	   pointTo.x + mRadius, 
								   	   pointTo.y + mRadius);
			canvas.drawOval(ovalStop, paint);
		}

		return super.draw(canvas, mapView, shadow, when);
	}

}