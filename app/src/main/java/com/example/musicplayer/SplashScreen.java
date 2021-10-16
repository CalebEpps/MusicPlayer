package com.example.musicplayer;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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
    MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
// This code locks the phone in portrait mode because FUCK THE INSTANCE STATE STUFF I DON'T HAVE TIME TO IMPLEMENT IT
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        // This is the code for the fade out animation
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(500);
        ImageView tttLogo = (ImageView) findViewById(R.id.tttLogo);

        // This is the code for the Fade In Animation that plays first.
        // Notice we start this animation right away.
        // The fade out animation is delayed for a few seconds and then started.
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

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mp.release();
            }
        });




        handler = new Handler();
// This code tells our program to delay the animation starting for a few seconds.
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                tttLogo.startAnimation(fadeOut);
            }
        }, 6000);

// This code tells our program to delay the main activity's launch for a few seconds.
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 7000);

    }
}

