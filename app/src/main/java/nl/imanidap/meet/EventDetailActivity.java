package nl.imanidap.meet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

public class EventDetailActivity extends AppCompatActivity implements MeetupImageDownloadCallback{

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

        new MeetupImageDownloadTask(this).execute(event);

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

    @Override
    public void loadImagePreview(Bitmap b){
        ImageView img = (ImageView) findViewById(R.id.iv_preview_image);

        if(b != null) {

            Bitmap filter = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Bitmap.Config.ARGB_8888);
            filter.setHasAlpha(true);

            Canvas canvas = new Canvas(filter);
            //TODO: This could be different depenting on the theme
            int lightColor = Color.argb(255, 106, 17, 203);
            int darkColor = Color.argb(255, 37, 117, 252);

            filter = this.duoToneFilter(b, filter, lightColor, darkColor);

            canvas.drawBitmap(filter, 0, 0, null);
            img.setImageBitmap(filter);

        } else {
            img.setImageDrawable(getDrawable(R.drawable.steak));
            img.setPadding(25,0,25,0);
        }
    }

    //Algorithm Concept: https://stackoverflow.com/questions/2442391/computer-graphics-programatically-create-duotone-or-separations
    private Bitmap duoToneFilter(Bitmap b, Bitmap filter, int colorLight, int colorDark){
        for(int x = 0; x < b.getWidth(); x++){
            for(int y = 0; y < b.getHeight(); y++){

                int originalPixel = b.getPixel(x, y);
                int red = Color.red(originalPixel);
                int green = Color.green(originalPixel);
                int blue = Color.blue(originalPixel);

                int gray = (red + green + blue) / 3;
                double relativeGray = (double)gray / (double)255;

                int tone;

                if(relativeGray < 0.3) {
                    tone = colorLight;

                } else if (relativeGray >= 0.3 && relativeGray < 0.7){
                    int mixRed = (Color.red(colorLight) + Color.red(colorDark)) / 2;
                    int mixGreen = (Color.green(colorLight) + Color.green(colorDark)) / 2;
                    int mixBlue = (Color.blue(colorLight) + Color.blue(colorDark)) / 2;

                    tone = Color.argb(255, mixRed, mixGreen, mixBlue);

                } else {
                    tone = colorDark;
                }

                int toneRed = (int)Math.round((relativeGray * (double)Color.red(tone)));
                int toneGreen = (int)Math.round((relativeGray * (double)Color.green(tone)));
                int toneBlue = (int)Math.round((relativeGray * (double)Color.blue(tone)));


                filter.setPixel(x, y, Color.argb(255, toneRed, toneGreen, toneBlue));
            }
        }

        return filter;
    }

}
