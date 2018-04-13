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
 * LocationHandler
 *
 * This class takes care of all functionality related to handling Location.
 */

public class LocationHandler implements LocationListener {
    private WeakReference<Activity> activity;
    private LocationManager locationManager;
    private LocationHandlerCallback callback;
    private Location lastLocation;
    public static final int LOCATION_REQUEST_CODE = 5;

    /**
     * Constructor
     *
     * Creates a Weakreference to the active activity, saves the callback & creates a LocationManager.
     *
     * @param a
     *      A reference to the active activity
     * @param cb
     *      A locationHandlerCallback
     */

    LocationHandler(Activity a, LocationHandlerCallback cb){
        activity = new WeakReference<Activity>(a);
        callback = cb;

        if(activity.get() != null){
            locationManager = (LocationManager) activity.get().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    /**
     * checkLocationPermissions
     *
     * Checks if the user has the right permissions if not they are requested.
     * The results appear in the onRequestPermissionsResult() method in MapsActivity.
     *
     * @see MapsActivity
     */

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

    /**
     * getUserLocation
     *
     * Checks the current user location using ACCESS_FINE_LOCATION.
     * Has a suppressLint since userPermissions are checked on MapsActivity.onCreate()
     *
     * @see MapsActivity
     */

    @SuppressLint("MissingPermission")
    public void getUserLocation() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    /**
     * onLocationChanged
     *
     * Android Hook method, performed when the user location has updated.
     * My emulator keeps sending data so a history of the user location is checked to make sure
     * there is an actual location change. LocationListener implementation.
     *
     * @param location
     *      Parameters are inserted by the framework, the current user location from the GPS
     */

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

    /**
     * onStatusChanged
     *
     * Android Hook method, performed when the status of the Location Provider changes.
     * LocationListener implementation.
     *
     * @param s
     *      Parameters are inserted by the framework, The name of the provider
     * @param i
     *      Parameters are inserted by the framework, Int value of provider Status
     * @param bundle
     *      Parameters are inserted by the framework, Specific information about the provider
     */

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d(MapsActivity.LOG, "status changed: provider " + s);
    }

    /**
     * onProviderEnabled
     *
     * Android Hook method, performed when a Location Provider is enabled.
     * LocationListener implementation.
     *
     * @param s
     *       Parameters are inserted by the framework, The name of the provider
     */

    @Override
    public void onProviderEnabled(String s) {
        Log.d(MapsActivity.LOG, "provider enabled");
    }

    /**
     * onProviderDisabled
     *
     * Android Hook method, performed when a Location Provider is disabled.
     * LocationListener implementation.
     *
     * @param s
     *       Parameters are inserted by the framework, The name of the provider
     */

    @Override
    public void onProviderDisabled(String s) {
        Log.d(MapsActivity.LOG, "provider disabled");
    }

    /**
     * removeUpdates
     *
     * Stops the Location Manager
     */

    public void removeUpdates(){
        locationManager.removeUpdates(this);
    }
}
