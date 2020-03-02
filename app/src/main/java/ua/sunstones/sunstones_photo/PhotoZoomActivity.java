package ua.sunstones.sunstones_photo;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;

public class PhotoZoomActivity extends AppCompatActivity {

    private PhotoView photoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_photo_zoom);

        String photo_full_name = getIntent().getStringExtra("photo_full_name");


        Display display = getWindowManager().getDefaultDisplay();
        Point sizeWindow = new Point();
        display.getSize(sizeWindow);
        float display_width = sizeWindow.x;

        photoView = findViewById(R.id.photoView);
        try {
            File imgFile  = new File(photo_full_name);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver() , Uri.fromFile(imgFile));
            photoView.setBitmap(bitmap, display_width);
        }
        catch (Exception e) { }


    }

    public void onClickClose(View view){
        finish();
    }

}
