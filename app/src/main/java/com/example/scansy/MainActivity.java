package com.example.scansy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    Button bt;
    Adapter adapter;
    List<Model> models;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt=(Button)findViewById(R.id.btn);

        models = new ArrayList<>();


        models.add(new Model(R.drawable.pdffinal, "CONVERT INTO PDF FORMAT",""));
    //    models.add(new Model(R.drawable.gallery,"PDF Gallery",""));
        models.add(new Model(R.drawable.txtfinal, "CONVERT INTO TXT FORMAT",""));
      //  models.add(new Model(R.drawable.gallery,"TXT Gallery",""));
        adapter = new Adapter(models, this);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130,0, 130, 0);

        Integer[] colors_temp = {getResources().getColor(R.color.color1),getResources().getColor(R.color.color2)};

        colors = colors_temp;



        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {

                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(position==0){
                            Intent intent=new Intent(MainActivity.this,pdffile.class);
                            startActivity(intent);
                        }
                        else if(position==1){
                            Intent intent=new Intent(MainActivity.this,txtfile.class);
                            startActivity(intent);

                        }
                      /*  else if(position==1){
                            Intent intent=new Intent(MainActivity.this,pdfgallery.class);
                            startActivity(intent);
                        }
                        else if(position==3){
                            Intent intent=new Intent(MainActivity.this,txtgallery.class);
                            startActivity(intent);
                        }
*/

                    }
                });
                if(position < (adapter.getCount() -1) && position < (colors.length -1)){
                    viewPager.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset, colors[position], colors[position + 1]));
                }

                else {
                    viewPager.setBackgroundColor(colors[colors.length - 1]);
                }

            }



            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id =item.getItemId();
        if(id == R.id.info){
            Intent intent=new Intent(MainActivity.this,info.class);
            startActivity(intent);


        }

        return super.onOptionsItemSelected(item);
    }
}