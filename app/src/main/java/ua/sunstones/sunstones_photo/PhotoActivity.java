package ua.sunstones.sunstones_photo;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PhotoActivity extends AppCompatActivity {

    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static final int REQUEST_CAPTURE_IMAGE      = 100;
    private static final int REQUEST_GALLERY            = 22;
    private int REQUEST_CODE_PERMISSIONS                = 121;

    ArrayList<Photo> listPhotos = new ArrayList();
    ViewPager view_image_pager;
    EditText edit_text_articul_barcode;
    String currentPhotoPath;
    public GifImageView gifImage_downloding;
    public String articul="";
    DataBase data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        if(!allPermissionsGranted()){
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        data = DataBase.getInstance(this);
        gifImage_downloding = (GifImageView) findViewById(R.id.gifImage_downloding);
        gifImage_downloding.setGifImageResource(R.drawable.downloding_5);


        DeleteAllPhoto();

        edit_text_articul_barcode = (EditText)findViewById(R.id.edit_text_articul_barcode);

        view_image_pager = (ViewPager) findViewById(R.id.view_image_pager);
        ImageAdapter adapterView = new ImageAdapter(this, listPhotos);
        view_image_pager.setAdapter(adapterView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setTitle("PHOTOS");
        menu.findItem(R.id.menu_main_login).setTitle(data.getLogin());


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        ///////////////////////////////////////////////////////////////////////////
        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            Photo photo = new Photo();
            photo.full_name             = currentPhotoPath;
            photo.status                = Photo.Status.ADD_PHOTO;
            photo.downloded_from_camera = true;
            photo.articul_barcode       = edit_text_articul_barcode.getText().toString();
            photo.articul               = articul;
            listPhotos.add(photo);

            Log.v("Photo", "onActivityResult ");
            ImageAdapter adapterView = new ImageAdapter(this, listPhotos);
            view_image_pager.setAdapter(adapterView);
            view_image_pager.setCurrentItem(listPhotos.size());
        }


        ///////////////////////////////////////////////////////////////////////////
        IntentResult Result = IntentIntegrator.parseActivityResult(requestCode , resultCode ,data);
        if(Result != null){
                if(Result.getContents() == null){
                    Log.d("MainActivity" , "cancelled scan");
//                    Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d("MainActivity" , "Scanned");
                    edit_text_articul_barcode.setText(Result.getContents().toString());

                    DeleteAllPhoto();
                    listPhotos.clear();
                    downloadPhotos();
                }
        }
        else {
            super.onActivityResult(requestCode , resultCode , data);
        }


        ///////////////////////////////////////////////////////////////////////////
        try {
            // When an Image is picked
            if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && null != data) {

                if(data.getData()!=null){

                    File targetLocation     = createImageFile();
                    copyFiles(data.getData(), targetLocation);

                    Photo photo = new Photo();
                    photo.full_name             = targetLocation.getAbsolutePath();
                    photo.status                = Photo.Status.ADD_PHOTO;
                    photo.downloded_from_camera = true;
                    photo.articul_barcode       = edit_text_articul_barcode.getText().toString();
                    photo.articul               = articul;
                    listPhotos.add(photo);


                }
                else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();

                            File targetLocation     = createImageFile();
                            copyFiles(uri, targetLocation);

                            Photo photo = new Photo();
                            photo.full_name             = targetLocation.getAbsolutePath();
                            photo.status                = Photo.Status.ADD_PHOTO;
                            photo.downloded_from_camera = true;
                            photo.articul_barcode       = edit_text_articul_barcode.getText().toString();
                            photo.articul               = articul;
                            listPhotos.add(photo);

                        }

                    }
                }


                ImageAdapter adapterView = new ImageAdapter(this, listPhotos);
                view_image_pager.setAdapter(adapterView);
                view_image_pager.setCurrentItem(listPhotos.size());

//                grid_photo_view.setAdapter(new GridPhotoViewAdapter(this, offer.Photos));

            } else {
//                showError("Не выбрано ни одно фото");
            }
        } catch (Exception e) {
            showError("Что то пошло не так");
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(!allPermissionsGranted()){
                showError("Нет прав на работу с камерой");
            }
        }
    }

    private boolean allPermissionsGranted(){

        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }


    public void onClickButtonReadBarcode(View view) {

        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setDesiredBarcodeFormats(intentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setCameraId(0);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setPrompt("SCAN");
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.setCaptureActivity(CaptureActivityPortrait.class);
        intentIntegrator.initiateScan();
    }

    public void onClickButtonFindArticul(View view) {

        DeleteAllPhoto();
        listPhotos.clear();
        downloadPhotos();

    }

    public void onClickButtonCamera(View view) {

        if(articul.isEmpty()){
            showError("Не выбран артикул");
            return;
        }

//        if(allPermissionsGranted() == false){
//            showError("Нет прав работу с камерой");
//            return;
//        }


        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(cameraIntent, REQUEST_CAPTURE_IMAGE);

            File pictureFile = null;
            try {
                pictureFile = createImageFile();
            } catch (IOException ex) {
                showError("Нельзя использовать камеру. Попробуйте еще раз.");
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ua.sunstones.sunstones_photo.fileprovider",
                        pictureFile);

                Log.v("Photo", "photoURI: " + photoURI.toString());

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_CAPTURE_IMAGE);
            }
        }
    }

    public void onClickButtonGalary(View view){

        if(articul.isEmpty()){
            showError("Не выбран артикул");
            return;
        }

//        if(!allPermissionsGranted()){
//            showError("Нет прав работу с галереей");
//            return;
//        }

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), REQUEST_GALLERY);

    }

    public void onClickButtonZoom(View view){

        int index = view_image_pager.getCurrentItem();
        Photo photo = listPhotos.get(index);

        Intent intent = new Intent(this, AutorizationActivity.class);
        intent = new Intent(this, PhotoZoomActivity.class);
        intent.putExtra("photo_full_name", photo.full_name);

        startActivity(intent);

    }

    public void onClickButtonDeletePhoto(View view) {

        int position = view_image_pager.getCurrentItem();
        Photo photo  = listPhotos.get(position);

        if(photo.downloded_from_1c && photo.status == Photo.Status.DELETE_PHOTO){
            photo.status = null;
        }else if(photo.downloded_from_camera && photo.status == Photo.Status.DELETE_PHOTO){
            photo.status = Photo.Status.ADD_PHOTO;
        }else{
            photo.status = Photo.Status.DELETE_PHOTO;
        }

        ImageAdapter adapterView = new ImageAdapter(this, listPhotos);
        view_image_pager.setAdapter(adapterView);
        view_image_pager.setCurrentItem(position);

    }

    public void onClickButtonSavePhotos(View view) {
        savePhotos();
    }



    private void savePhotos(){

        gifImage_downloding.setVisibility(View.VISIBLE);
        new Query1C(this).execute(Query1C.TASK_UPLOAD_PHOTOS);

    }

    private void downloadPhotos(){

        gifImage_downloding.setVisibility(View.VISIBLE);
        ImageAdapter adapterView = new ImageAdapter(this, listPhotos);
        view_image_pager.setAdapter(adapterView);

        new Query1C(this).execute(Query1C.TASK_DOWNLOAD_PHOTOS);

    }

    private File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "SunStones_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        return image;

    }

    public void DeletePhotosRecursive(String strPath) {

        File fileOrDirectory = new File(strPath);

        if (fileOrDirectory.isDirectory()){
            for (File child : fileOrDirectory.listFiles())
                DeletePhotosRecursive(child.getPath());
            fileOrDirectory.delete();
        }else{

            fileOrDirectory.delete();
        }
    }

    void DeleteAllPhoto(){
        String dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        DeletePhotosRecursive(dir);
    }

    public void updatePhotos(ArrayList<Photo> _listPhotos){

        listPhotos = _listPhotos;
        ImageAdapter adapterView = new ImageAdapter(this, listPhotos);
        view_image_pager.setAdapter(adapterView);

    }

    public void showError(String erorr){


//        final Dialog dialog_erorr = new Dialog(this);
//        dialog_erorr.setContentView(R.layout.dialog_erorr);
//
//
//        TextView text_view_erorr = (TextView) dialog_erorr.findViewById(R.id.text_view_erorr);
//        text_view_erorr.setText(erorr);
//
//        Button button_ok = (Button) dialog_erorr.findViewById(R.id.button_ok);
//        button_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog_erorr.dismiss();
//            }
//        });
//
//        dialog_erorr.setCancelable(false);
//        dialog_erorr.setTitle("ERORR");
//        dialog_erorr.show();



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ошибка!")
                .setMessage(erorr)
                .setIcon(R.drawable.delete)
                .setCancelable(false)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();


    }

    public void clearAll(){

        edit_text_articul_barcode.setText("");
        articul = "";
        listPhotos.clear();
        ImageAdapter adapterView = new ImageAdapter(this, listPhotos);
        view_image_pager.setAdapter(adapterView);
    }

    void copyFiles(Uri sourceLocation, File targetLocation){

        try{

//            File sourceLocation = new File (source);
//
//            // make sure your target location folder exists!
//            File targetLocation = new File (target);

//            if(sourceLocation.exists()){

//                InputStream in = new FileInputStream(sourceLocation);
                InputStream in = getContentResolver().openInputStream(sourceLocation);
                OutputStream out = new FileOutputStream(targetLocation);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();

                Log.v("test", "Copy file successful.");

//            }else{
//                Log.v("test", "Copy file failed. Source file missing.");
//            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
