package ua.sunstones.sunstones_photo;

import android.graphics.Bitmap;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class Photo {

    public static enum Status {
        ADD_PHOTO, DELETE_PHOTO
    }

    public Boolean downloded_from_1c=false;
    public Boolean downloded_from_camera=false;

    public String articul_barcode="";
    public String articul="";
    public Status status;
    public String full_name="";
    public String short_name="";

    public Photo(){}


    public JSONObject get_json(){

        try {
            JSONObject json = new JSONObject();
            json.put("articul_barcode", articul_barcode);
//            json.put("photo", get_img_Base64(photo_original));
            return json;
        }

        catch(JSONException ex) {
            ex.printStackTrace();
        }

        return  new JSONObject();
    }



    public String get_img_Base64(Bitmap bitmap){

        String img_str = "";

        if(bitmap != null){
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            byte[] image=stream.toByteArray();
            img_str = Base64.encodeToString(image, 0);
        }

        return img_str;
    }

}
