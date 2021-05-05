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
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class txtfile extends AppCompatActivity {
    String mText;
    EditText mResultEt;
    ImageView mPriviewIv;
    String fname;
    private  static final int CAMERA_REQUEST_CODE=200;
    private  static final int STORAGE_REQUEST_CODE=400;
    private  static final int IMAGE_PICK_GALLERY_CODE=1000;
    private  static final int IMAGE_PICK_CAMERA_CODE=1001;
    private static final int WRITE_EXTERNAL_STORAGE_CODE=1;

    String cameraPermission[];
    String storagePermission[];
    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_txtfile);
        mResultEt=(EditText)findViewById(R.id.resultEt);
        mPriviewIv=(ImageView)findViewById(R.id.ImageIv);


        cameraPermission=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
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

            AlertDialog.Builder mydialog=new AlertDialog.Builder(txtfile.this);
            mydialog.setTitle("Enter File name:");
            final EditText filename=new EditText(txtfile.this);
            mydialog.setView(filename);
            mydialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    fname=filename.getText().toString();

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                            String[] permissions={Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permissions,WRITE_EXTERNAL_STORAGE_CODE);
                        }
                        else {
                            mText=mResultEt.getText().toString();
                            Createandsavefile(fname,mText);
                        }
                    }
                    else {
                        mText=mResultEt.getText().toString();
                        Createandsavefile(fname,mText);
                    }


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
    private void Createandsavefile(String fname,String text) {

      /*  Intent intent=new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE,fname+".txt");
        startActivityForResult(intent,1);*/
      //  String timstmp=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        try {
            String filepath= Environment.getExternalStorageDirectory().getPath();
            File dir=new File(filepath+"/Scansy/txt/");
            dir.mkdirs();
            String path=dir.toString();
            String filename=fname+".txt";

            String file_path=Environment.getExternalStorageDirectory().getPath()+"/Scansy/txt/"+filename;
            File file=new File(file_path);

            FileWriter fw=new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw=new BufferedWriter(fw);
            bw.write(text);
            bw.close();
            Toast.makeText(txtfile.this,"Save file",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(txtfile.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }



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
                        Toast.makeText(txtfile.this,"Permission Denidedd",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(txtfile.this,"Permission Denidedd",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case WRITE_EXTERNAL_STORAGE_CODE:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    mText=mResultEt.getText().toString();
                    Createandsavefile(fname,mText);
                }
                else {
                    Toast.makeText(txtfile.this,"Permission Required",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode== RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).start(txtfile.this);
            }
            if(requestCode ==IMAGE_PICK_CAMERA_CODE){
                CropImage.activity(image_uri).setGuidelines(CropImageView.Guidelines.ON).start(txtfile.this);
            }
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                Uri resulturi =result.getUri();
                mPriviewIv.setImageURI(resulturi);

                BitmapDrawable bitmapDrawable =(BitmapDrawable)mPriviewIv.getDrawable();
                Bitmap bitmap=bitmapDrawable.getBitmap();

                TextRecognizer recognizer =new TextRecognizer.Builder(getApplicationContext()).build();
                if (!recognizer.isOperational()){
                    Toast.makeText(txtfile.this,"Error",Toast.LENGTH_SHORT).show();
                }
                else {
                    Frame frame=new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items =recognizer.detect(frame);
                    StringBuilder sb=new StringBuilder();

                    for(int i=0;i<items.size();i++){
                        TextBlock myItem =items.valueAt(i);
                        sb.append(myItem.getValue());
                        sb.append("\n");
                    }
                    mResultEt.setText(sb.toString());
                }
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error =result.getError();
                Toast.makeText(txtfile.this,""+error,Toast.LENGTH_SHORT).show();
            }
        }


        if(requestCode==1){
            if(resultCode == RESULT_OK){
                try {
                    String mtext=mResultEt.getText().toString().trim();

                    Uri uri=data.getData();
                    OutputStream outputStream=getContentResolver().openOutputStream(uri);
                    outputStream.write(mtext.getBytes());
                    outputStream.close();
                    Toast.makeText(txtfile.this,"file save Successfully ",Toast.LENGTH_SHORT).show();

                }
                catch (IOException e){
                    Toast.makeText(txtfile.this,"Failed to save",Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(txtfile.this,"File not save",Toast.LENGTH_SHORT).show();
            }
        }


    }
}