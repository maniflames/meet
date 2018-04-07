package nl.imanidap.meet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

//Class will also be a location listener so real gps data can be requested
public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
                    View.OnClickListener {

    public static final String LOG = "thisapp";
    public static final String EVENT_DETAIL_DATA = "meetEvent";
    public static final int LOCATION_REQUEST_CODE = 5;
    private GoogleMap mMap;
    private RelativeLayout rlEventInfo;
    private TextView tvEventName;
    private TextView tvGroupName;
    private ArrayList<Marker> locations = new ArrayList<Marker>();
    private MeetEvent clickedEvent;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Boolean firstLocationUpdate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //make sure you can get the last known location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            
            String[] wantedPermissions = { Manifest.permission.ACCESS_FINE_LOCATION };
            ActivityCompat.requestPermissions(this, wantedPermissions, LOCATION_REQUEST_CODE);

            return;
        }

        //create location manager
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        //didn't succeed in turning the class into a location listener
        //It has something to do with my google play services update or my imports
        //it would be neat if I could fix that or put this in a separate class
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                if(firstLocationUpdate){
                    Log.d(LOG, "New Location: " + location.toString());
                    zoomToUserLocation(location);
                    firstLocationUpdate = false;
                }

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(LOG, "status changed");
            }

            public void onProviderEnabled(String provider) {
                Log.d(LOG, "provider enabled");
            }

            public void onProviderDisabled(String provider) {
                Log.d(LOG, "provider disabled");
            }
        };

        rlEventInfo = findViewById(R.id.rl_event_info);
        tvEventName = findViewById(R.id.tv_event_name);
        tvGroupName = findViewById(R.id.tv_group_name);

        rlEventInfo.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);

        getUserLocation();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        clickedEvent = (MeetEvent) marker.getTag();
        showEventInfo(marker);
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        for (Marker mark: locations) {
            if(mark.getPosition() == latLng){
                return;
            }
        }

        hideEventInfo();
    }

    private void showEventInfo(Marker marker){
        Log.d(LOG, "Marker was clicked");
        MeetEvent event = (MeetEvent) marker.getTag();
        tvEventName.setText(event.getName());
        tvGroupName.setText(event.getGroupName());
        rlEventInfo.setVisibility(View.VISIBLE);
    }

    private void hideEventInfo(){
        Log.d(LOG, "Click on map");
        rlEventInfo.setVisibility(View.GONE);
    }

    //https://developer.android.com/training/location/retrieve-current.html
    //https://developer.android.com/guide/topics/location/strategies.html#Updates
    private void getUserLocation(){

        //get users current location either through last known location or through live GPS
        try {
             mFusedLocationClient.getLastLocation()
             .addOnSuccessListener(this, new OnSuccessListener<Location>() {

                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        // Logic to handle location object
                        Log.d(LOG, "last known location is " + location.toString());
                        zoomToUserLocation(location);
                    } else {
                        //for now log but in this case I should get the location myself
                        Log.d(LOG, "No last known location");

                        try {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }

                    }
                }
              });

        } catch (SecurityException e){
            e.printStackTrace();
        }
    }

    private void zoomToUserLocation(Location location){
        LatLng user = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(user));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12));

        new MeetupEventsDownloadTask(this).execute("");
    }


    @Override
    public void onClick(View view) {
        Intent eventDetailIntent = new Intent(this, EventDetailActivity.class);
        eventDetailIntent.putExtra(EVENT_DETAIL_DATA, clickedEvent);
        startActivity(eventDetailIntent);
    }

    public void addEventsToMap(ArrayList<MeetEvent> events){
        for ( MeetEvent event : events ) {
            //get latitude & longitude and create a new marker /w the MeetEvent as tag
            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(event.getLatitude(), event.getLongitude()))
            );
            m.setTag(event);
            locations.add(m);
        }

        LatLng firstEvent = new LatLng(events.get(0).getLatitude(), events.get(0).getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(firstEvent));
    }

    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == LOCATION_REQUEST_CODE){
            //I know I asked for one permission so I can just grab the first and check it
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //get user location and zoom
                Log.d(LOG, "yaay, permission granted");
                //do the user location function

            } else  {
                Log.d(LOG, "App need location for basic functionality");
                //this should eventually be a pop-up that then closes the app
            }
        }
    }

}
