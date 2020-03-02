package ua.sunstones.sunstones_photo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AutorizationActivity extends AppCompatActivity {

    public TextView text_view_error;
    public EditText edit_login;
    public EditText edit_password;

    Query1C query;
    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autorization);

        text_view_error = (TextView) findViewById(R.id.autorization_text_view_error);
        edit_login      = (EditText) findViewById(R.id.autorization_edit_login);
        edit_password   = (EditText) findViewById(R.id.autorization_edit_password);

        query = new Query1C(this);
        db = DataBase.getInstance(this);
        String login = db.getLogin();
        edit_login.setText(login);

        String password = db.getLogin();
        edit_password.setText(password);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setTitle("AUTHORIZATION");
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Boolean rezalt = MainMenu.onOptionsItemSelected(item, this);
        if(rezalt)
            return true;
        else
            return super.onOptionsItemSelected(item);

    }


    public void onClickButtonlogin(View view){
        new Query1C(this).execute(Query1C.TASK_AUTORIZATION);
    }

    public void openPhotos(){
        Intent intent = new Intent(this, PhotoActivity.class);
        startActivity(intent);
    }
}
