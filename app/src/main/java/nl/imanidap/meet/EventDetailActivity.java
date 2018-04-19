package nl.imanidap.meet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

/**
 * EvenDetailActivity
 *
 * This Activitiy shows an event in detail.
 */

public class EventDetailActivity extends AppCompatActivity implements MeetupImageDownloadCallback{

    private TextView tvEventName;
    private TextView tvEventTime;
    private TextView tvEventDescription;
    private TextView tvGroupName;
    private TextView tvRSVPCount;
    private MeetEvent event;

    /**
     * onCreate
     *
     * Android Hook method, performed when the activity is created.
     * The layout is set, default preferences set, eventData received from an intent and the image downloaded
     *
     * @param savedInstanceState
     *      Parameters are inserted by the framework
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        Intent eventDetailIntent = getIntent();
        event = (MeetEvent) eventDetailIntent.getSerializableExtra(MapsActivity.EVENT_DETAIL_DATA);

        populateView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new MeetupImageDownloadTask(this).execute(event);
    }

    /**
     * populateView
     *
     * A method that inserts the event data into the views of the layout
     */

    private void populateView(){
        tvEventName = (TextView) findViewById(R.id.tv_event_name);
        tvGroupName = (TextView) findViewById(R.id.tv_group_name);
        tvEventDescription = (TextView) findViewById(R.id.tv_event_description);
        tvEventTime = (TextView) findViewById(R.id.tv_time);
        tvRSVPCount = (TextView) findViewById(R.id.tv_rsvp_count);

        tvEventName.setText(event.getName());
        tvGroupName.setText(event.getGroupName());
        tvEventDescription.setText(event.getDescription());

        Date eventStart = new Date(event.getTime());
        tvEventTime.setText(eventStart.toString());

        String rsvpSubstring;

        if (event.getRsvpCount() == 1){
            rsvpSubstring = " person is going!";
        } else {
            rsvpSubstring = " people are going!";
        }

        String rsvp = String.valueOf(event.getRsvpCount()) + rsvpSubstring;
        tvRSVPCount.setText(rsvp);
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
     *      Parameters are inserted by the framework, The selected menu item
     *
     * @return
     *      Indicates we want to override the default behaviour
     *
     * @See SettingsActivity
     *      The activity that is requested by the intent
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.open_preferences){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else {
            finish();
        }

        return true;
    }


    /**
     * loadImagePreview
     *
     * Implementation of MeetupImageDownloadCallback. This method is performed by the
     * MeetupImageDownloadTask. In this method the image is being replaced by a canvas with a filtered
     * version of the image is drawn on the canvas and put in the layout.
     *
     * @param b
     *      The bitmap returned by the MeetupImageDownloadTask
     *
     * @see MeetupImageDownloadTask
     */

    @Override
    public void loadImagePreview(Bitmap b){
        ImageView img = (ImageView) findViewById(R.id.iv_preview_image);
        SharedPreferences prefManager = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean filterActivated = prefManager.getBoolean(SettingsActivity.KEY_THEME_FILTER, SettingsActivity.DEFAULT_THEME_FILTER);

        if(b != null && filterActivated) {

            Bitmap filter = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Bitmap.Config.ARGB_8888);
            filter.setHasAlpha(true);

            Canvas canvas = new Canvas(filter);
            int lightColor = Color.argb(255, 106, 17, 203);
            int darkColor = Color.argb(255, 37, 117, 252);

            filter = this.duoToneFilter(b, filter, lightColor, darkColor);
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);

            canvas.drawBitmap(filter, 0, 0, null);
            canvas.drawText(getString(R.string.app_name), b.getWidth() - 35, b.getHeight() - 10, paint);
            img.setImageBitmap(filter);

        } else if (b != null && !filterActivated) {
            img.setImageBitmap(b);
        } else {
            img.setImageDrawable(getDrawable(R.drawable.steak));
            img.setPadding(25,0,25,0);
        }
    }



    /**
     * duoToneFilter
     *
     * The filter used over the images.
     *
     * @param b
     *      A bitmap of the original image
     * @param filter
     *      An empty bitmap that can be used to put in the new pixels
     *      (Make sure setHasAlpha is true)
     * @param colorLight
     *      The lightest color in the duoTone combination. An int containing the color value.
     * @param colorDark
     *      The darkest color in the duoTone combination. An int containing the color value.
     *
     * @return filter
     *      A bitmap with the filtered image
     *
     * @credits Duotone Algorithm concept
     * https://stackoverflow.com/questions/2442391/computer-graphics-programatically-create-duotone-or-separations
     */

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
