package nl.imanidap.meet;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, View.OnClickListener {

    public static final String LOG = "thisapp";
    public static final String EVENT_DETAIL_DATA = "meetEvent";
    private GoogleMap mMap;
    private RelativeLayout rlEventInfo;
    private TextView tvEventName;
    private TextView tvGroupName;
    private ArrayList<Marker> locations = new ArrayList<Marker>();
    private MeetEvent clickedEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        rlEventInfo = findViewById(R.id.rl_event_info);
        tvEventName = findViewById(R.id.tv_event_name);
        tvGroupName = findViewById(R.id.tv_group_name);

        rlEventInfo.setOnClickListener(this);
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
        new MeetupEventsDownloadTask(this).execute("");

        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);

        //move camera to current position of user
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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
}
