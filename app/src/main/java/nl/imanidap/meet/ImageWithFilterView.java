package nl.imanidap.meet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by maniflames on 13/04/2018.
 */


@SuppressLint("AppCompatCustomView")
public class ImageWithFilterView extends ImageView {

    public ImageWithFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setDrawingCacheEnabled(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Paint p = new Paint(0);
        p.setColor(0xff101010);
        Rect r = new Rect(50, 50, 50, 50);

        try{
//            Bitmap b = this.getDrawingCache();
//
//            for(int x = 0; x < b.getWidth(); x++){
//                for(int y = 0; y < b.getHeight(); y++){
//
//                    b.setPixel(0, 0, Color.rgb(255, 0, 0));
//                }
//            }

//            canvas.drawBitmap(b, new Matrix(), null);
            canvas.drawRect(r, p);

        } catch (Exception e) {
            e.printStackTrace();
        }


        super.onDraw(canvas);

        Log.d(MapsActivity.LOG, "taking over the world");
        this.postInvalidate();
    }
}
