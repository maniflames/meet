package nl.imanidap.meet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Download Utils
 *
 * This Class contains static methods that serve as Utilities for making HTTP Requests
 */

public class DownloadUtils {

    /**
     * getRequest
     * A method that performs a GET request
     *
     * @param url
     *     The url that will be requested
     *
     * @return String content or Null
     *    A string containing the contents of the results
     *    or null when something went wrong
     */

    public static String getRequest(URL url){

        HttpURLConnection cURL = null;
        try {
            cURL = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream in = null;
        try {
            in = cURL.getInputStream();


            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            StringBuilder json = new StringBuilder();

            while (scanner.hasNextLine()) {
                Log.d(MapsActivity.LOG, "running tha while loop");
                json.append(scanner.nextLine());
            }

            return json.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cURL.disconnect();
        }

        return null;
    }

    /**
     * getRequestImage
     * A method that performs a GET request optimized for images
     *
     * @param url
     *    The url that will be requested
     *
     * @return Bitmap image or Null
     *    Returns the Bitmap of the requested image
     *    or null if something went wrong
     */

    public static Bitmap getRequestImage(URL url){
        HttpURLConnection cURL = null;
        try {
            cURL = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream in = null;
        try {
            in = cURL.getInputStream();
            Bitmap mBitmap = BitmapFactory.decodeStream(in);
            return mBitmap;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cURL.disconnect();
        }

        return null;
    }

}
