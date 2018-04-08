package nl.imanidap.meet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Date;

public class EventDetailActivity extends AppCompatActivity {

    private TextView tvEventName;
    private TextView tvEventTime;
    private TextView tvEventDescription;
    private TextView tvGroupName;
    private TextView tvRSVPCount;

    private MeetEvent event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Intent eventDetailIntent = getIntent();
        event = (MeetEvent) eventDetailIntent.getSerializableExtra(MapsActivity.EVENT_DETAIL_DATA);

        populateView();
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

    private void populateView(){
        tvEventName = (TextView) findViewById(R.id.tv_event_name);
        tvGroupName = (TextView) findViewById(R.id.tv_group_name);
        tvEventDescription = (TextView) findViewById(R.id.tv_event_description);
        tvEventTime = (TextView) findViewById(R.id.tv_time);
        tvRSVPCount = (TextView) findViewById(R.id.tv_rsvp_count);

        tvEventName.setText(event.getName());
        tvGroupName.setText(event.getGroupName());
        tvEventDescription.setText(event.getDescription());


        tvEventTime.setText(new Date(event.getTime()).toString());

        String rsvpSubstring;

        if (event.getRsvpCount() == 1){
            rsvpSubstring = " person is going!";
        } else {
            rsvpSubstring = " people are going!";
        }

        String rsvp = String.valueOf(event.getRsvpCount()) + rsvpSubstring;
        tvRSVPCount.setText(rsvp);
    }
}
