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

//changed to AppCompatActivity to show the actionbar
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

        rlEventInfo = findViewById(R.id.rl_event_info);
        tvEventName = findViewById(R.id.tv_event_name);
        tvGroupName = findViewById(R.id.tv_group_name);

        rlEventInfo.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationHandler.removeUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationHandler.removeUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationHandler.getUserLocation();
        if(userLocation != null && settingsChanged){
            requestMeetEvents(userLocation);
            settingsChanged = false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);

        startActivity(intent);

        return super.onOptionsItemSelected(item);
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

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        clickedEvent = (MeetEvent) marker.getTag();
        new MeetupImageDownloadTask(this).execute(clickedEvent);
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
        if(requestCode == LocationHandler.LOCATION_REQUEST_CODE){
            //I know I asked for one permission so I can just grab the first and check it
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d(LOG, "yaay, permission granted");

            } else  {
                Log.d(LOG, "App need location for basic functionality");
                //TODO: this should eventually be a dialoge that then closes the app
            }
        }
    }

    @Override
    public void onUserLocationSuccess(Location location) {
        userLocation = location;
        zoomInOnUser(location);
        requestMeetEvents(location);
    }

    private void zoomInOnUser(Location location){
        LatLng user = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(user));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(11));
    }

    public void requestMeetEvents(Location location){
        Log.d(LOG, "request new events");
        String categories = new MeetupEventsDownloadTask(this).getMeetupCategoriesFromUserPreferences();
        Log.d(MapsActivity.LOG, categories);



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
