package ua.sunstones.sunstones_photo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;

public class Query1C extends AsyncTask<String,Void,Void> {

    public static final String TASK_AUTORIZATION     = "TASK_AUTORIZATION";
    public static final String TASK_DOWNLOAD_PHOTOS  = "TASK_DOWNLOAD_PHOTOS";
    public static final String TASK_UPLOAD_PHOTOS    = "TASK_UPLOAD_PHOTOS";


    private Activity act;
    private final String NameBase1C = "7km";
    private String answer = "";
    private String error = "";
    private String current_task;

    ArrayList<Photo> listPhotos = new ArrayList();


    DataBase data;

    public Query1C(Activity act){
        this.act  = act;
        this.data = DataBase.getInstance(act);
    }

    @Override
    protected Void doInBackground(String... params) {

        switch(params[0]) {

            case TASK_AUTORIZATION:
                current_task = TASK_AUTORIZATION;
                Authorization();
                break;

            case TASK_DOWNLOAD_PHOTOS:
                current_task = TASK_DOWNLOAD_PHOTOS;
                String articul_barcode = ((PhotoActivity)act).edit_text_articul_barcode.getText().toString();
                DownloadPhotos(articul_barcode);
                break;

            case TASK_UPLOAD_PHOTOS:
                current_task = TASK_UPLOAD_PHOTOS;
                listPhotos = ((PhotoActivity)act).listPhotos;
                UploadPhotos(listPhotos);
                break;

            default:
                break;

        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        switch(current_task) {

            case TASK_AUTORIZATION:
                ((AutorizationActivity)act).text_view_error.setText(answer);

                if(answer == "Authorization success"){
                    ((AutorizationActivity)act).openPhotos();
                }

                break;


            case TASK_DOWNLOAD_PHOTOS:
                ((PhotoActivity)act).gifImage_downloding.setVisibility(View.INVISIBLE);
                if(!error.isEmpty()){
                    ((PhotoActivity)act).showError(error);
                }else{
                    ((PhotoActivity)act).updatePhotos(listPhotos);
                }

                break;

            case TASK_UPLOAD_PHOTOS:

                ((PhotoActivity)act).gifImage_downloding.setVisibility(View.INVISIBLE);
                if(!error.isEmpty()){
                    ((PhotoActivity)act).showError(error);
                }else{
                    ((PhotoActivity)act).clearAll();
                }
                break;

            default:
                break;

        }


    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

        switch(current_task) {

            case TASK_AUTORIZATION:
                ((AutorizationActivity)act).text_view_error.setText(answer);
                break;


//            case TASK_GET_LAST_VENDOR_CODE:
//                ((SynchronizationActivity)act).synchronization_text_rezalt.setText(answer);
//                break;




            default:
                break;

        }
    }


    private String GET(String urlSpec, String login, String password) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();


        String userCredentials = login + ":" + password;
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
        connection.setRequestProperty ("Authorization", basicAuth);


        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);

            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return new String(out.toByteArray());
        }
        finally {
            connection.disconnect();
        }
    }

    private String POST(String urlSpec, String post_data, String login, String password) throws IOException {

        URL url;
        String response = "";
        try {
            url = new URL(urlSpec);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setReadTimeout(15000);
//            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            String userCredentials = login + ":" + password;
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
            conn.setRequestProperty ("Authorization", basicAuth);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(post_data);
            writer.flush();
            writer.close();
            os.close();

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="error " + String.valueOf(responseCode) ;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;

    }

    private void Authorization(){

        String AnswerJsonString = "";
//        DataBase data = new DataBase(act);

        try {

            String login    = ((AutorizationActivity)act).edit_login.getText().toString();
            String password = ((AutorizationActivity)act).edit_password.getText().toString();
            data.setLogin(login);
            data.setPassword(password);

            String url = Uri.parse("https://purchases.sunstones.ua/"+NameBase1C+"/hs/mobile/authorization")
                    .buildUpon()
//                    .appendQueryParameter("method", "flickr.photos.getRecent")
//                    .appendQueryParameter("api_key", API_KEY)
//                    .appendQueryParameter("format", "json")
//                    .appendQueryParameter("nojsoncallback", "1")
//                    .appendQueryParameter("extras", "url_s")
                    .build().toString();

            AnswerJsonString = GET(url, login, password);

        } catch (IOException ioe) {
            answer = "Authorization Failed";
        }




        JSONObject obj = null;
        try {

            obj = new JSONObject(AnswerJsonString);

            error            = obj.optString ( "error" );
//            JSONObject result       = obj.getJSONObject("result");
//            String name_user        = result.getString("name");

            if(error.isEmpty() || error == null){
                answer = "Authorization success";
                data.setAuthorizationStatus("success");
            } else{
                answer = "Authorization error. " + error;
                data.setAuthorizationStatus("error");
            }

        } catch (JSONException e) {
            answer = "Authorization error...";
            e.printStackTrace();
        }

    }

    private void DownloadPhotos(String articul_barcode){

        listPhotos.clear();
        String login     = data.getLogin();
        String password  = data.getPassword();
        File storageDir  = act.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        String url = Uri.parse("https://purchases.sunstones.ua/"+NameBase1C+"/hs/mobile/site_photos")
                .buildUpon()
                .appendQueryParameter("articul_barcode", articul_barcode)
                .build().toString();

//        publishProgress();

        try{
            answer = GET(url, login, password);
        } catch (IOException e) {
            return;
        }




        try {

            JSONObject obj          = new JSONObject(answer);
            String _error           = obj.optString ( "error" );
            JSONObject result       = obj.getJSONObject("result");

            if(_error.isEmpty() || _error == null){

                String articul        = result.getString("articul");
                ((PhotoActivity)act).articul = articul;

                JSONArray json_orders = result.getJSONArray("photos");

                for(int i=0; i<json_orders.length(); i++){
                    JSONObject json_order = json_orders.getJSONObject(i);

                    String file_name    = json_order.getString("name");

                    String encodedImage = json_order.getString("photo");
                    byte[] decodedString = android.util.Base64.decode(encodedImage, android.util.Base64.DEFAULT);
                    Bitmap bitmap_image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                    return decodedByte;

                    try {
                        File file = File.createTempFile(file_name, ".jpg", storageDir);
                        Photo photo = new Photo();
                        photo.downloded_from_1c     = true;
                        photo.articul               = articul;
                        photo.full_name             = file.getAbsolutePath();
                        photo.short_name            = file_name;
                        photo.articul_barcode       = articul_barcode;
                        listPhotos.add(photo);

                        FileOutputStream out = new FileOutputStream(file);
                        bitmap_image.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                }

            }else{
                error = error + _error + (_error.isEmpty() ? "" : "\n");
            }

        } catch (JSONException e) {

        }


    }

    private void UploadPhotos( ArrayList<Photo> _listPhotos){

        //1. fist delete photos
        for(int i=0; i<_listPhotos.size(); i++) {
            Photo photo = _listPhotos.get(i);
            if(photo.status == Photo.Status.DELETE_PHOTO && photo.downloded_from_1c){
                POST_delete_photo(photo);
            }
        }

        //2. upload new photos
        for(int i=0; i<_listPhotos.size(); i++) {
            Photo photo = _listPhotos.get(i);
            if(photo.status == Photo.Status.ADD_PHOTO){
                POST_upload_photo(photo);
            }
        }
    }

    private void POST_delete_photo(Photo photo){


        if(!photo.downloded_from_1c){
            return;
        }

        String login            = data.getLogin();
        String password         = data.getPassword();

        String url = Uri.parse("https://purchases.sunstones.ua/"+NameBase1C+"/hs/mobile/site_photos")
                .buildUpon()
//                .appendQueryParameter("articul_barcode", photo.articul_barcode)
                .build().toString();

        String json_photo  = get_json_photo_task_delete(photo).toString();
        try {
            answer = POST(url, json_photo, login, password);
        } catch (IOException e) {

        }


        try {
            JSONObject obj          = new JSONObject(answer);
            String _error           = obj.optString ( "error" );
            error                   = error + _error+ (_error.isEmpty() ? "" : "\n");
        } catch (JSONException e) {
            error = error + answer + (answer.isEmpty() ? "" : "\n");
        }


    }

    private void POST_upload_photo(Photo photo){

        String login            = data.getLogin();
        String password         = data.getPassword();

        String url = Uri.parse("https://purchases.sunstones.ua/"+NameBase1C+"/hs/mobile/site_photos")
                .buildUpon()
//                .appendQueryParameter("articul_barcode", photo.articul_barcode)
                .build().toString();



        String json_photo  = get_json_photo_task_add(photo).toString();
        try {
            answer = POST(url, json_photo, login, password);
        } catch (IOException e) {

        }

        try {
            JSONObject obj          = new JSONObject(answer);
            String _error           = obj.optString ( "error" );
            error                   =  error + _error+ (_error.isEmpty() ? "" : "\n");
        } catch (JSONException e) {
            error = error + answer + (answer.isEmpty() ? "" : "\n");
        }

    }


    public JSONObject get_json_photo_task_delete(Photo photo){

        try {
            JSONObject json = new JSONObject();
            json.put("task",     "delete_photo");
            json.put("articul",     photo.articul);
            json.put("photo_name",  photo.short_name);
            json.put("photo",       get_img_Base64(photo.full_name));
            return json;
        }

        catch(JSONException ex) {
            ex.printStackTrace();
        }

        return  new JSONObject();
    }

    public JSONObject get_json_photo_task_add(Photo photo){

        try {
            JSONObject json = new JSONObject();
            json.put("task",     "add_photo");
            json.put("articul",     photo.articul);
            json.put("photo_name",  photo.articul);
            json.put("photo",       get_img_Base64(photo.full_name));
            return json;
        }

        catch(JSONException ex) {
            ex.printStackTrace();
        }

        return  new JSONObject();
    }



    public String get_img_Base64(String  filePath){

        String img_str = "";

//        if(bitmap != null){
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
//            byte[] image=stream.toByteArray();
//            img_str = android.util.Base64.encodeToString(image, 0);
//        }

        File imgFile = new File(filePath);
        if (imgFile.exists() && imgFile.length() > 0) {
            Bitmap bm = BitmapFactory.decodeFile(filePath);
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bOut);
            img_str =  android.util.Base64.encodeToString(bOut.toByteArray(),  android.util.Base64.DEFAULT);
        }

        return img_str;
    }

}
