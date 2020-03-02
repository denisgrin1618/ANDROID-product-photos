package ua.sunstones.sunstones_photo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {

    private static DataBase sInstance;
    private static final int DATABASE_VERSION    = 2;
    private static final String DATABASE_NAME    = "sunstones_photos";
    private static final String TABLE_SESSION    = "session_values";
    SQLiteDatabase db;

    public static DataBase getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new DataBase(context);
        }
        return sInstance;

    }

    private DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db, TABLE_SESSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion != newVersion) {
            onCreate(db);
        }

    }

    private void createTable(SQLiteDatabase db, String table_name){


        switch(table_name) {

            case TABLE_SESSION:
                db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SESSION + " ("
                        + "id integer primary key autoincrement,"
                        + "name text,"
                        + "value text"
                        + ");");

                break;



            default:
                // code block
        }


    }


    ///////////////////////////////////////////////////////////////////////////////////
    //Session

    public void setCredential(String credential){
        setSessionVariable("credential", credential);
    }
    public String getCredential(){
        return getSessionVariable("credential");
    }

    public void setLogin(String login){
        setSessionVariable("login", login);
    }
    public String getLogin(){
        return getSessionVariable("login");
    }

    public void setPassword(String password){
        setSessionVariable("password", password);
    }
    public String getPassword(){
        return getSessionVariable("password");
    }

    public void setAuthorizationStatus(String authorization_status){
        setSessionVariable("authorization_status", authorization_status);
    }
    public String getAuthorizationStatus(){
        return getSessionVariable("authorization_status");
    }

    public String getSessionVariable(String name){

        String value  = "";
//        SQLiteDatabase db = getWritableDatabase();

        Cursor userCursor = db.rawQuery("select value from " + TABLE_SESSION + " where  name = ?", new String[]{name});
        if (userCursor.moveToFirst()) {
            value  = userCursor.getString(0);
        }

        return value;

    }
    public void setSessionVariable(String name, String value){


        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("value", value);

        Cursor userCursor = db.rawQuery("select * from " + TABLE_SESSION + " where  name = ?", new String[]{name});
        if (userCursor.moveToFirst()) {
            db.update(TABLE_SESSION, cv, "name= ?", new String[]{name});
        } else {
            db.insert(TABLE_SESSION, null, cv);
        }

    }

    public void setPhontSize(int phont_size){
        setSessionVariable("phont_size", String.valueOf(phont_size));
    }
    public int getPhontSize(){

        String phontSize = getSessionVariable("phont_size");
        if(phontSize == null || phontSize.isEmpty() || phontSize.equals(""))
            return 50;
        else
            return Integer.valueOf(phontSize);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        db.close();
    }

}
