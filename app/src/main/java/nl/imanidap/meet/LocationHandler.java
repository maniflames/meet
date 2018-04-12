package nl.imanidap.meet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


import com.google.android.gms.maps.GoogleMap;


import java.lang.ref.WeakReference;


/**
 * Created by maniflames on 12/04/2018.
 */

public class LocationHandler implements LocationListener {
    private WeakReference<Activity> activity;
    private LocationManager locationManager;
    private GoogleMap mMap;
    private LocationHandlerCallback callback;
    private Location lastLocation;
    public static final int LOCATION_REQUEST_CODE = 5;

    LocationHandler(Activity a, LocationHandlerCallback cb){
        activity = new WeakReference<Activity>(a);
        callback = cb;

        if(activity.get() != null){
            locationManager = (LocationManager) activity.get().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void checkLocationPermissions(){
        if(activity.get() == null){
            return;
        }

        if (ActivityCompat.checkSelfPermission(activity.get().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            String[] wantedPermissions = { Manifest.permission.ACCESS_FINE_LOCATION };
            ActivityCompat.requestPermissions(activity.get(), wantedPermissions, LOCATION_REQUEST_CODE);
        }
    }

    //Permission are checked in the main activity
    @SuppressLint("MissingPermission")
    public void getUserLocation() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {

        try{
            if(location.getLatitude() != lastLocation.getLatitude() && location.getLongitude() != lastLocation.getLongitude()){
                Log.d(MapsActivity.LOG, "New Location: " + location.toString());
                callback.onUserLocationSuccess(location);
                lastLocation = location;
            }
        } catch (NullPointerException e) {
            callback.onUserLocationSuccess(location);
            lastLocation = location;
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d(MapsActivity.LOG, "status changed");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d(MapsActivity.LOG, "provider enabled");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d(MapsActivity.LOG, "provider disabled");
    }

    public void removeUpdates(){
        locationManager.removeUpdates(this);
    }
}
