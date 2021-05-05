package com.example.scansy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class pdfgallery extends AppCompatActivity {
    ListView lv_pdf;
    public static ArrayList<File> filelist =new ArrayList<File>();
    PDFadapter obj_adapter;
    File dir;

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfgallery);
        init();
    }
    private void init() {
        lv_pdf=(ListView)findViewById(R.id.lv_pdf);
        dir=new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"Scansy/pdf/");
        getfile(dir);
        obj_adapter=new PDFadapter(getApplicationContext(),filelist);
        lv_pdf.setAdapter(obj_adapter);

    }

    private ArrayList<File> getfile(File dir) {
        File listFile[]=dir.listFiles();
        if(listFile!=null && listFile.length > 0){
            for (int i=0;i<listFile.length;i++){
                if (listFile[i].isDirectory()){
                    getfile(listFile[i]);
                }
                else {
                    boolean booleanpdf=false;
                    if (listFile[i].getName().endsWith(".pdf")){
                        for(int j=0;j<filelist.size();j++){
                            if (filelist.get(j).getName().equals(listFile[i].getName())){
                                booleanpdf=true;
                            }
                        }
                        if (booleanpdf){
                            booleanpdf=false;
                        }
                        else {
                            filelist.add(listFile[i]);
                        }
                    }
                }
            }
        }
        return filelist;
    }
}
