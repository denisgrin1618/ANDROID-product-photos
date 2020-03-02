package ua.sunstones.sunstones_photo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    DataBase data;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);




        data = DataBase.getInstance(this);

        Intent intent = new Intent(this, AutorizationActivity.class);
        String status = data.getAuthorizationStatus();
        if(status.equals("success")){
            intent = new Intent(this, PhotoActivity.class);
        }else{
            intent = new Intent(this, AutorizationActivity.class);
        }
        startActivity(intent);

    }




}
