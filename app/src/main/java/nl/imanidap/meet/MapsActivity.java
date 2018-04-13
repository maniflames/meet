package nl.imanidap.meet;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;

import android.net.Uri;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * MapsActivity
 *
 * This is the MainActivity of the app. It shows a map with all the MeetUp events based on the current user location &
 * categories selected in the user preferences.
 *
 * @note extendingAppCompatActivity to show the actionbar
 */

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
                    View.OnClickListener, LocationHandlerCallback, MeetupImageDownloadCallback {

    public static final String LOG = "thisapp";
    public static final String EVENT_DETAIL_DATA = "meetEvent";

    private GoogleMap mMap;
    private RelativeLayout rlEventInfo;
    private TextView tvEventName;
    private TextView tvGroupName;
    private ArrayList<Marker> locations = new ArrayList<Marker>();
    private MeetEvent clickedEvent;
    private LocationHandler locationHandler;
    private Location userLocation;
    public static Boolean settingsChanged = false;

    /**
     * onCreate
     *
     * Android Hook method, performed when the activity is created.
     * The layout is set, user preferences are initialized, the mapFragment is initialized
     * & items are added to the layout.
     *
     * @param savedInstanceState
     *      Parameters are inserted by the framework
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Set default values if user enters app for the first time
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationHandler = new LocationHandler(this, this);
        locationHandler.checkLocationPermissions();
        locationHandler.getUserLocation();

        rlEventInfo = findViewById(R.id.rl_event_info);
        tvEventName = findViewById(R.id.tv_event_name);
        tvGroupName = findViewById(R.id.tv_group_name);

        rlEventInfo.setOnClickListener(this);
    }

    /**
     * onPause
     *
     * Android Hook method, performed when the activity is paused (user navigates away).
     * The location manager from the LocationHandler is stopped.
     *
     * @see LocationHandler
     */

    @Override
    protected void onPause() {
        super.onPause();
        locationHandler.removeUpdates();
    }

    /**
     * onResume
     *
     * Android Hook Method, preformed when user navigates (back) to the activity.
     * Requests new events if the settings have changed.
     */

    @Override
    protected void onResume() {
        super.onResume();
        if(userLocation != null && settingsChanged){
            requestMeetEvents(userLocation);
            settingsChanged = false;
        }

    }

    /**
     * onCreateOptionsMenu
     *
     * Android Hook Method, performed when the options menu is created
     * This is where the actionbar menu from the resources is loaded into the layout
     *
     * @param menu
     *      Parameters are inserted by the framework, The menu
     *
     * @return true
     *      indicates we want to override the default behaviour
     *
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    /**
     * onOptionsItemSelected
     *
     * Android Hook Method, performed when an item on the actionbar is clicked.
     * This is where in intent towards the SettingsActivity is send.
     *
     * @param item
     *      Parameters are inserted by the framework, the selected menu item
     *
     * @return
     *      Indicates we want to override the default behaviour
     *
     * @See SettingsActivity
     *      The activity that is requested by the intent
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);

        startActivity(intent);

        return super.onOptionsItemSelected(item);
    }

    /**
     * onRequestPermissionsResult
     *
     * Android Hook Method, performed when the result of a permission request is in
     * This hook will either log that the permission is granted or close the activity
     *
     * @param requestCode
     *      Parameters are inserted by the framework, the request code send with the permissions
     * @param permissions
     *      Parameters are inserted by the framework, an array with the requested permissions
     * @param grantResults
     *      Parameters are inserted by the framework, an array with the results of the request
     *
     */

    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == LocationHandler.LOCATION_REQUEST_CODE){
            //I know I asked for one permission so I can just grab the first and check it
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d(LOG, "yaay, permission granted");

            } else  {
                Log.d(LOG, "App need location for basic functionality");
                finish();
                //TODO: this should eventually be a dialoge that then closes the app
            }
        }
    }

    /**
     * onMapReady
     *
     * Android Hook Method, manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     * Implementation of the onMapReady Interface.
     *
     * @param googleMap
     *      Parameters are inserted by the framework, instance of the google map
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
    }

    /**
     * onMarkerClick
     *
     * Android Hook Method, performed when a marker is clicked.
     * Implementation of the onMarkerClickListener Interface.
     *
     * @param marker
     *      Parameters are inserted by the framework, clicked marker
     *
     * @return true
     *      To override the default behaviour
     */

    @Override
    public boolean onMarkerClick(final Marker marker) {
        clickedEvent = (MeetEvent) marker.getTag();
        new MeetupImageDownloadTask(this).execute(clickedEvent);
        showEventInfo(marker);
        return true;
    }

    /**
     * showEventInfo
     *
     * This method appends a view with a preview of the event.
     *
     * @param marker
     *      Marker that has to be shown
     */

    private void showEventInfo(Marker marker){
        Log.d(LOG, "Marker was clicked");
        MeetEvent event = (MeetEvent) marker.getTag();
        tvEventName.setText(event.getName());
        tvGroupName.setText(event.getGroupName());
        rlEventInfo.setVisibility(View.VISIBLE);
    }

    /***
     * onClick
     *
     * Android Hook Method, performed when the preview view is clicked.
     * Implementation of the onClickListener.
     *
     * @param view
     */

    @Override
    public void onClick(View view) {
        Intent eventDetailIntent = new Intent(this, EventDetailActivity.class);
        eventDetailIntent.putExtra(EVENT_DETAIL_DATA, clickedEvent);
        startActivity(eventDetailIntent);
    }

    /**
     * onMapClick
     *
     * Android Hook Method, performed when the map is clicked.
     * Implementation of the onMapClickListener Interface.
     *
     * @param latLng
     *      Parameters are inserted by the framework, coordinates of the click
     */

    @Override
    public void onMapClick(LatLng latLng) {
        for (Marker mark: locations) {
            if(mark.getPosition() == latLng){
                return;
            }
        }

        hideEventInfo();
    }

    /**
     * hideEventInfo
     *
     * Hides the preview info of an event
     */

    private void hideEventInfo(){
        Log.d(LOG, "Click on map");
        rlEventInfo.setVisibility(View.GONE);
    }

    /**
     * onUserLocationSuccess
     *
     * Handles a succesfull user location update.
     * It will zoom in on the user location & request new events based on the new location.
     * Implementation of LocationHandlerCallback.
     *
     * @param location
     *      The current user location
     *
     * @see LocationHandler
     */

    @Override
    public void onUserLocationSuccess(Location location) {
        userLocation = location;
        zoomInOnUser(location);
        requestMeetEvents(location);
    }

    /**
     * zoomInOnUser
     *
     * A function that zooms the camera of the map in on the user.
     *
     * @param location
     *      The location where the zoom will focus on
     */

    private void zoomInOnUser(Location location){
        LatLng user = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(user));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(11));
    }

    /**
     * addEventsToMap
     *
     * adds a marker for each given event
     *
     * @param events
     *      Meetup events
     */

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

    /**
     * requestMeetEvents
     *
     * This method will build an URL based on user location & user preference to request nearby events of an specific category
     *
     * @param location
     *      The current user location
     */

    public void requestMeetEvents(Location location){
        Log.d(LOG, "request new events");
        String categories = new MeetupEventsDownloadTask(this).getMeetupCategoriesFromUserPreferences();

        Uri.Builder uriBuilder = Uri.parse(MeetupEventsDownloadTask.MEETUP_EVENTS_BASE_URL).buildUpon()
                .appendQueryParameter(MeetupEventsDownloadTask.KEY_PARAM, Secret.MEETUP_API_KEY)
                .appendQueryParameter(MeetupEventsDownloadTask.SIGN_PARAM, MeetupEventsDownloadTask.SIGN_VALUE)
                .appendQueryParameter(MeetupEventsDownloadTask.TEXT_FORMAT_PARAM, MeetupEventsDownloadTask.TEXT_FORMAT_VALUE)
                .appendQueryParameter(MeetupEventsDownloadTask.LAT_PARAM, String.valueOf(location.getLatitude()))
                .appendQueryParameter(MeetupEventsDownloadTask.LONG_PARAM, String.valueOf(location.getLongitude()));

        if(categories.length() > 0 ){
            uriBuilder.appendQueryParameter(MeetupEventsDownloadTask.CATEGORY_PARAM, categories).build();
        }

        try {
            URL url = new URL(uriBuilder.build().toString());
            Log.d(LOG, url.toString());
            new MeetupEventsDownloadTask(this).execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * loadImagePreview
     *
     * Implementation of MeetupImageDownloadCallback. This method is performed by the
     * MeetupImageDownloadTask. In this method the image is put in the layout.
     *
     * @param b
     *      The bitmap returned by the MeetupImageDownloadTask
     *
     * @see MeetupImageDownloadTask
     */

    @Override
    public void loadImagePreview(Bitmap b){
        ImageView img = (ImageView) findViewById(R.id.iv_preview_image);

        if(b != null) {
            img.setImageBitmap(b);
        } else {
            img.setImageDrawable(getDrawable(R.drawable.steak));
        }
    }
}
