package com.example.scansy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splashscreen extends AppCompatActivity {
    ImageView im1,im2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        im1=(ImageView)findViewById(R.id.img1);
        im2=(ImageView)findViewById(R.id.img2);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
       Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.splash_anim);
       Animation animation1 =AnimationUtils.loadAnimation(getApplicationContext(),R.anim.splash_anim2);
       im2.setAnimation(animation);
       im1.setAnimation(animation1);
       animation.setAnimationListener(new Animation.AnimationListener() {
           @Override
           public void onAnimationStart(Animation animation) {

           }

           @Override
           public void onAnimationEnd(Animation animation) {
                finish();
                startActivity(new Intent(Splashscreen.this,MainActivity.class));
           }

           @Override
           public void onAnimationRepeat(Animation animation) {

           }
       });
    }
}
