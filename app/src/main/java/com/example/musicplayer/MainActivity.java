package com.example.musicplayer;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import abhishekti7.unicorn.filepicker.UnicornFilePicker;
import abhishekti7.unicorn.filepicker.utils.Constants;

public class MainActivity extends AppCompatActivity {


    // Declare our Media Player Early so it can be used everywhere in this class.
    MediaPlayer mp = new MediaPlayer();
    // Boolean for mediaplayer, currently not used due to no ability to play music (Removed for testing)
    boolean isMPPlaying = false;
    // This is the arraylist of files the user is trying to import. It does NOT STORE SONGS THE USER HAS ALREADY SELECTED.
    ArrayList<String> songs;

    // Just a code to make sure no errors are returned.
    private final int REQUEST_CODE_PERMISSIONS = 101;

    // We are storing these to use later.
    private final String[] REQUIRED_PERMISSIONS = new String[]{
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
    };



    // onCreate Method for doing... Everything?
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Just Grabbing our buttons for java code
        Button selectButton = findViewById(R.id.button);
        ImageButton stopButton = findViewById(R.id.StopButton);
        ImageButton playButton = findViewById(R.id.playButton);



        // This is what happens when our play button is pressed.
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mp.isPlaying()) {
                        isMPPlaying = false;
                        mp.pause();
                    } else {
                        mp.start();
                    }

                } catch (Exception e) {
             //       toastNothingPlaying("There is nothing playing, try selecting a file!");
                }
            }
        });


        // Currently unused method for stop button. Ability to Play Sound Removed.
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mp.isPlaying()) {
                        mp.stop();
                        mp.reset();
                    } else {
                 //      toastNothingPlaying("There is nothing playing, try selecting a file!");
                    }

                } catch (Exception e) {
              //      toastNothingPlaying("There is nothing playing, try selecting a file!");
                }
            }
        });




        // Sets our button listener
       selectButton.setOnClickListener((v)->{
           // Unicorn Picker is a third party library that allows a unified method of
           // selecting files and folders. The reason we decided to go with this method
           // is to preserve compatibility across our app with little issue.
           // In Android, due to their sweeping API changes, much code
           // that was written for API 30 won't work with 21-28.
           // Our solution to this is to use a third party library that is updated
           // to work with files across most API versions >21.
            UnicornFilePicker.from(MainActivity.this)
                    .addConfigBuilder()
                    .selectMultipleFiles(true)
                    .showOnlyDirectory(false)
                    .setRootDirectory(Environment.getExternalStorageDirectory().getAbsolutePath())
                    .showHiddenFiles(false)
                    .addItemDivider(true)
                    .theme(R.style.UnicornFilePicker_Default)
                    .build()
                    .forResult(Constants.REQ_UNICORN_FILE);
        });

        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // Okay so this will start our audio ONCE IT'S READY
                mp.start();
                isMPPlaying = true;
            }
        });


    }


    // Here is our onActivityResult method. This is now separated out of the onCreate method because
    // it was causing issues. Basically This is overidden for the CLASS, not the method itself.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.REQ_UNICORN_FILE && resultCode == RESULT_OK){
            if(data!=null){
                ArrayList<String> titles = new ArrayList<String>();
                ArrayList<String> paths = new ArrayList<String>();
                songs = data.getStringArrayListExtra("filePaths");
                int counter = 0;
                // this loop iterates over EVERY SELECTED FILE. It will be used to
                // store our song's information in its respective JSON file.
                for(String file : songs){
                    Log.e(TAG, file);
                    File realFile = new File(songs.get(counter));
                    paths.add(file);
                    String songName = realFile.getName().replace(".mp3", "");
                    titles.add(songName);
                    Log.e("FILE NAME: ", songName);
                    counter++;


                }
                ParseSongList parser = new ParseSongList();
                try {
                    parser.populateListFirstTime(paths,titles);
                    Log.e("SUCCESS", "SUCCESS");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("path","empty");
            }
        }
    }

    // So this is just checking for the correct permission. It spits out an error log with a toast message.
    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    // This gets the results of the allPermissionsGranted() method to check them.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                Toast.makeText(MainActivity.this, "Permissions granted by the user.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}



