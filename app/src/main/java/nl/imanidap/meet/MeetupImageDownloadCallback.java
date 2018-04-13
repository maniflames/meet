package nl.imanidap.meet;

import android.graphics.Bitmap;

/**
 * MeetupImageDownloadCallback
 *
 * An interface to ensure the loadImagePreview method can be executed
 * @see MeetupImageDownloadTask
 */

public interface MeetupImageDownloadCallback {
    public void loadImagePreview(Bitmap b);
}
