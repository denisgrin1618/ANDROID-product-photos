package ua.sunstones.sunstones_photo;



import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PhotoView extends View {

    private float mLastTouchX;
    private float mLastTouchY;
    private float mPosX;
    private float mPosY;
    private Bitmap mBitmap;
    private float cX, cY;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.0f;
    private float lastScaleFactor = 1.0f;
    private float minScaleFactor;
    private float scalePointX;
    private float scalePointY;
    private float lastScalePointX;
    private float lastScalePointY;
    private int mToolbarHeight = 0;

    public PhotoView(Context context) {
        super(context);
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache(true);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());


    }

    public PhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache(true);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

    }

    public void setBitmap(Bitmap bitmap, float display_width){
       // bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic_1);

        mBitmap = bitmap;

        if(display_width > 0){
            minScaleFactor  = (float)display_width / (float)bitmap.getWidth();
            mScaleFactor    = minScaleFactor;
            lastScaleFactor = minScaleFactor;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        setDrawingCacheEnabled(true); // cache


        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor, scalePointX, scalePointY);

        ///////////////////////////////////////////
        //
        //запретим выход за границы картинки
        float posBorderLeft     = (0 - scalePointX)/ mScaleFactor + scalePointX;
        float posBorderTop      = (0 - scalePointY)/ mScaleFactor + scalePointY;
        float posBorderRight    = (getWidth()  - mBitmap.getWidth()*mScaleFactor  - scalePointX) /mScaleFactor + scalePointX;
        float posBorderBottom   = (getHeight() - mBitmap.getHeight()*mScaleFactor - scalePointY) /mScaleFactor + scalePointY - mToolbarHeight/mScaleFactor;

        mPosX = Math.min(Math.max(mPosX,posBorderRight), posBorderLeft);
        mPosY = Math.min(Math.max(mPosY,posBorderBottom), posBorderTop);

        ///////////////////////////////////////////

        canvas.translate(mPosX, mPosY);
        canvas.drawARGB(255, 125, 125, 125);
        canvas.drawBitmap(mBitmap, 0, 0, null);
        canvas.restore();

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {


        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);

        final int action = ev.getAction();

        float _x = ev.getX();
        float _y = ev.getY();

        switch(action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                touchStart(_x,_y);
                invalidate();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                touchMove(_x,_y);
                invalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                touchUp(_x,_y);
                invalidate();
                break;
            }



        }
        return true;
    }

    private void touchStart(float _x, float _y) {

        final float x = (_x - scalePointX)/mScaleFactor;
        final float y = (_y - scalePointY)/mScaleFactor;

        cX = x - mPosX + scalePointX; // canvas X
        cY = y - mPosY + scalePointY; // canvas Y

        mLastTouchX = x;
        mLastTouchY = y;
    }
    private void touchMove(float _x, float _y) {

        final float x = (_x - scalePointX)/mScaleFactor;
        final float y = (_y - scalePointY)/mScaleFactor;

        cX = x - mPosX + scalePointX; // canvas X
        cY = y - mPosY + scalePointY; // canvas Y

        // Only move if the ScaleGestureDetector isn't processing a gesture.
        if (!mScaleDetector.isInProgress()) {
            final float dx = x - mLastTouchX; // change in X
            final float dy = y - mLastTouchY; // change in Y

            mPosX += dx;
            mPosY += dy;
        }

        mLastTouchX = x;
        mLastTouchY = y;


    }
    private void touchUp(float _x, float _y) {

        final float x = (_x - scalePointX)/mScaleFactor;
        final float y = (_y - scalePointY)/mScaleFactor;

        cX = x - mPosX + scalePointX; // canvas X
        cY = y - mPosY + scalePointY; // canvas Y

        mLastTouchX = 0;
        mLastTouchY = 0;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            lastScaleFactor = mScaleFactor;
            mScaleFactor *= detector.getScaleFactor();
            lastScalePointX = scalePointX;
            lastScalePointY = scalePointY;
            scalePointX =  detector.getFocusX();
            scalePointY = detector.getFocusY();


            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(minScaleFactor, Math.min(mScaleFactor, 10.0f));


            invalidate();
            return true;
        }
    }


}
