package com.ericmguimaraes.gaso.maps;
/*
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import java.util.ArrayList;
 */
/**
 * Class used to place marker or any overlay items on Map
 * */
public class AddItemizedOverlay {
 /*
       private ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();
 
       private Context context;
 
       public AddItemizedOverlay(Drawable defaultMarker) {
            super(boundCenterBottom(defaultMarker));
       }
 
       public AddItemizedOverlay(Drawable defaultMarker, Context context) {
            this(defaultMarker);
            this.context = context;
       }
       
       @Override
       public boolean onTouchEvent(MotionEvent event, MapView mapView)
       {   
 
           if (event.getAction() == 1) {
               GeoPoint geopoint = mapView.getProjection().fromPixels(
                   (int) event.getX(),
                   (int) event.getY());
               // latitude
               double lat = geopoint.getLatitudeE6() / 1E6;
               // longitude
               double lon = geopoint.getLongitudeE6() / 1E6;
               //Toast.makeText(context, "Lat: " + lat + ", Lon: "+lon, Toast.LENGTH_SHORT).show();
           }
           return false;
       } 
 
       @Override
       protected OverlayItem createItem(int i) {
          return mapOverlays.get(i);
       }
 
       @Override
       public int size() {
          return mapOverlays.size();
       }
 
       @Override
       protected boolean onTap(int index) {
         OverlayItem item = mapOverlays.get(index);
         AlertDialog.Builder dialog = new AlertDialog.Builder(this.context);
         dialog.setTitle(item.getTitle());
         dialog.setMessage(item.getSnippet());
         dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
 			public void onClick(DialogInterface dialog, int which) {
 			}
 		});
         dialog.show();
         return true;
       }
 
       public void addOverlay(OverlayItem overlay) {
          mapOverlays.add(overlay);
       }
       
       public void populateNow(){
    	   this.populate();
       }
 */
    }