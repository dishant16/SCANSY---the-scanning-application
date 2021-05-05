package com.example.scansy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class pdffile extends AppCompatActivity {
    private  static final int CAMERA_REQUEST_CODE=200;
    private  static final int STORAGE_REQUEST_CODE=400;
    private  static final int IMAGE_PICK_GALLERY_CODE=1000;
    private  static final int IMAGE_PICK_CAMERA_CODE=1001;
    String cameraPermission[];
    String storagePermission[];
    String fname;
    Uri image_uri;


    LinearLayout ly;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdffile);
        cameraPermission=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(pdffile.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        ly=(LinearLayout)findViewById(R.id.layout);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id== R.id.addImage){
            showImageimportDialog();
        }
/*        switch (item.getItemId()){
            case R.id.addImage:

                break;
        }*/
        if(id== R.id.save){

            AlertDialog.Builder mydialog=new AlertDialog.Builder(pdffile.this);
            mydialog.setTitle("Enter File name:");
            final EditText filename=new EditText(pdffile.this);
            mydialog.setView(filename);
            mydialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    fname=filename.getText().toString();
                    Createpdf(fname);
                }
            });
            mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });

            mydialog.show();

        }
        return super.onOptionsItemSelected(item);
    }
    private void Createpdf(String fname) {
      //  String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        String name=fname+".pdf";
        PdfDocument myDocument=new PdfDocument();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(ly.getWidth(),ly.getHeight(),1).create();
        PdfDocument.Page myPage = myDocument.startPage(myPageInfo);
        Paint myPaint = new Paint();
        Bitmap screen;
        ly.buildDrawingCache();
        ly.setDrawingCacheEnabled(true);
        screen = Bitmap.createBitmap(ly.getDrawingCache());
        ly.setDrawingCacheEnabled(false);


        myPage.getCanvas().drawBitmap(screen,0,0,myPaint);
        myDocument.finishPage(myPage);

        String filePath = Environment.getExternalStorageDirectory().getPath();
        File dir=new File(filePath+"/Scansy/pdf/");
        dir.mkdirs();
        String fil=Environment.getExternalStorageDirectory().getPath()+"/Scansy/pdf/"+name;
        File myFile = new File(fil);
        try {
            myDocument.writeTo(new FileOutputStream(myFile));
            Toast.makeText(pdffile.this,"Created in Internal Storage",Toast.LENGTH_LONG).show();

        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(pdffile.this,"Error",Toast.LENGTH_LONG).show();
        }
        myDocument.close();

    }



    private void showImageimportDialog() {
        String[] items={"Camera","Gallery"};
        AlertDialog.Builder dialog =new AlertDialog.Builder(this);
        dialog.setTitle("Select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){

                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else{
                        pickCamera();
                    }

                }
                if(i==1){
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }

                    else{
                        pickaGallery();
                    }



                }
            }
        })  ;
        dialog.create().show();

    }

    private void pickaGallery() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image to Text");
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);
    }
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1=ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if (grantResults.length>0){
                    boolean cameraAccepted=grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted =grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted &&  writeStorageAccepted){
                        pickCamera();
                    }
                    else {
                        Toast.makeText(pdffile.this,"Permission Denidedd",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case  STORAGE_REQUEST_CODE:
                if (grantResults.length>0){
                    boolean writeStorageAccepted =grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (   writeStorageAccepted){
                        pickaGallery();
                    }
                    else {
                        Toast.makeText(pdffile.this,"Permission Denidedd",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).start(pdffile.this);
            }
            if(requestCode ==IMAGE_PICK_CAMERA_CODE){
                CropImage.activity(image_uri).setGuidelines(CropImageView.Guidelines.ON).start(pdffile.this);
            }
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                Uri resulturi =result.getUri();
                ImageView imageView=new ImageView(pdffile.this);
                imageView.setImageURI(resulturi);
                addView(imageView, 595,842);




            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error =result.getError();
                Toast.makeText(pdffile.this,""+error,Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void addView(ImageView imageView,int width,int height){
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(width,height);
        layoutParams.setMargins(0,10,0,10);
        imageView.setLayoutParams(layoutParams);
        ly.addView(imageView);
    }

}
