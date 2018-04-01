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
    private GoogleMap mMap;
    private RelativeLayout rlEventInfo ;
    private TextView tvEventName;
    private TextView tvGroupName;
    private ArrayList<Marker> locations = new ArrayList<Marker>();

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
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        Marker mark = mMap.addMarker(new MarkerOptions().position(sydney).title("Sydney"));
        locations.add(mark);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        showEventInfo();
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

    private void showEventInfo(){
        Log.d(LOG, "Marker was clicked");
        rlEventInfo.setVisibility(View.VISIBLE);
    }

    private void hideEventInfo(){
        Log.d(LOG, "Click on map");
        rlEventInfo.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        Intent eventDetailIntent = new Intent(this, EventDetailActivity.class);
        startActivity( eventDetailIntent );
    }
}
