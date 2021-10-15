package com.example.musicplayer;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


public class SplashScreen extends AppCompatActivity {
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(500);
        ImageView tttLogo = (ImageView) findViewById(R.id.tttLogo);


        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1500);
        tttLogo.startAnimation(fadeIn);







        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Nothing Needs to Happen Here
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tttLogo.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Nothing needs to happen here again
            }
        });


        MediaPlayer mp = new MediaPlayer();
        try {
            // This code opens our mp3 to play at startup from the assets folder.
            AssetFileDescriptor descriptor = this.getAssets().openFd("startup_sound.mp3");
            mp.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();
            mp.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, "Error loading sound, check path");
        }

        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mp.start();
            }
        });




        handler = new Handler();

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                tttLogo.startAnimation(fadeOut);
            }
        }, 4000);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);

    }
}

