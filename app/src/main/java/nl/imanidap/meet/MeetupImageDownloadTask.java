package nl.imanidap.meet;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * MeetupEventsDownloadsTask
 *
 * An AsyncTask extention that takes care of downloading images from the web
 */

public class MeetupImageDownloadTask extends AsyncTask<MeetEvent, Void, Bitmap>{
    public static final String TEST_URL = "https://secure.meetupstatic.com/photos/event/1/6/9/0/global_466985776.jpeg";
    private MeetupImageDownloadCallback callback;

    /**
     * Constructor
     *
     * Saves the callback
     *
     * @param cb
     *      A MeetupImageDownloadCallback
     */

    MeetupImageDownloadTask(MeetupImageDownloadCallback cb){
        super();
        callback = cb;
    }


    /**
     * doInBackground
     *
     * AsyncTask Hook Method, preformed on the background thread.
     * This methods uses a downloadUtil to do an actual request.
     *
     * @param meetEvents
     *      MeetupEvents with an imgUrl
     *
     * @return bitmap
     *      A bitmap of the image
     *
     * @see DownloadUtils
     */

    @Override
    protected Bitmap doInBackground(MeetEvent... meetEvents) {
        try {
            MeetEvent meetEvent = meetEvents[0];
            URL url = new URL(meetEvent.getImageUrl());
            return DownloadUtils.getRequestImage(url);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * onPostExecute
     *
     * AsyncTask hook method, this method is performed on the UI thread.
     * The method that is performed is part of the MapsActivity Class.
     * (addEventsToMap)
     *
     * @param b
     *      A bitmap of the image that needs to be loaded
     *
     * @see MeetupImageDownloadCallback
     */

    @Override
    protected void onPostExecute(Bitmap b) {
        callback.loadImagePreview(b);
    }
}
