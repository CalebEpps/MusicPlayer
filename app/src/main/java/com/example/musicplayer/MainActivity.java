package com.example.musicplayer;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.parser.ParseException;

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

    // The creates our parser so we can use it in the below if/else statements.
    ParseSongList parser = new ParseSongList();
    // This is our array of songs for the recyclerView
    Song[] songArr;
    CyclicDouble CDLList = new CyclicDouble();
    Node nextSong;
    //   Log.e("TEST CDLL:", CDLList.toString());

    int songCounter = 0;






    // onCreate Method for doing... Everything?
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Quickly Populates our recycler view song list.
        songArr = parser.getEntries();
        if(songArr != null) {
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.songList);
            SongAdapter adapter = new SongAdapter(songArr);
            recyclerView.setHasFixedSize(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            // Populates our CDLL With songs at startup.
            for(int i = 0; i < songArr.length; i++) {
                CDLList.insertNode(songArr[i]);
            }

            nextSong = CDLList.head;

            Log.e("DID POPULATE?:",CDLList.toString());

            recyclerView.setAdapter(adapter);
        } else {
            // Nothing will happen here leaving our CDLL null;

        }





        // What happens when the song is done.
        mp.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mp.reset();
                        nextSong = nextSong.next;
                        String pathToPlay =  nextSong.song.getPath();
                        try {
                            mp.setDataSource(pathToPlay);
                            mp.prepareAsync();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

        );



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
        selectButton.setOnClickListener((v) -> {
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
        if (requestCode == Constants.REQ_UNICORN_FILE && resultCode == RESULT_OK) {
            if (data != null) {
                // Two important arraylists for populating our JSON file.
                ArrayList<String> titles = new ArrayList<String>();
                ArrayList<String> paths = new ArrayList<String>();
                // This just gets all of the files that were returned from the previous activity.
                songs = data.getStringArrayListExtra("filePaths");
                // Counter. Unused Variable
                int counter = 0;
                // this loop iterates over EVERY SELECTED FILE. It will be used to
                // store our song's information in its respective JSON file.
                for (String file : songs) {
                    Log.e(TAG, file);
                    File realFile = new File(songs.get(counter));
                    // this adds the path to our paths list. ALSO VERY IMPORTANT.
                    paths.add(file);
                    // Does the song end in MP3? Gross, let's remove it.
                    String songName = realFile.getName().replace(".mp3", "");
                    // Adds the song's name to our titles list. VERY important.
                    titles.add(songName);
                    Log.e("FILE NAME: ", songName);
                    // Unused Incremented Variable
                    counter++;
                }
                // END OF FOR LOOP


                // This is just setting the directory of the folder so we can see if it exists or not.



                // This is the name of the folder we've either created or need to create.
                String sdkunder29 = "/WGACA/songs.json";
                File directoryToCreate = new File(Environment.getExternalStorageDirectory(), sdkunder29);

                // Does the directory exist? If so, we will populate the existing file.
                Log.e("DIRECTORY: ",directoryToCreate.getPath());
                if (directoryToCreate.exists()) {
                    // Populates an existing Directory if it's necessary.
                    Log.e("DIRECTORY EXISTS?: ", "True");
                    parser.populateExistingList(paths, titles);

                    // Ensure that we add our newly selected songs to our CDLL.
                    for(int i = 0; i < paths.size(); i++) {
                        CDLList.insertNode(new Song(titles.get(i), paths.get(i)));
                    //    Log.e("CDLL POPULATED:",CDLList.head.song.getTitle());
                    }

                    songArr = parser.getEntries();
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.songList);
                    SongAdapter adapter = new SongAdapter(songArr);
                    recyclerView.setAdapter(adapter);


                    // Log.e("POPULATELISTERROR: ", "FOLDER ALREADY EXISTS.");

                } else {
                    // This executes the FIRST TIME our user runs the app and selects files.
                    try {
                        parser.populateListFirstTime(paths, titles);
                        Log.e("ITEMS: ", String.valueOf(parser.getLength()));
                        Log.e("POPULATELISTSUCCESS", "FILES ADDED TO JSON FILE SUCCESSFULLY (FIRST TIME RUN)");

                        // This will populate and initialize our recycleview our FIRST ENTRIES once they've been added.
                        songArr = parser.getEntries();
                        if(songArr != null) {
                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.songList);
                            SongAdapter adapter = new SongAdapter(songArr);
                            recyclerView.setHasFixedSize(false);
                            recyclerView.setLayoutManager(new LinearLayoutManager(this));
                            recyclerView.setAdapter(adapter);
                        } else {
                            // This prolly means something went wrong RIP :(
                            // You should NOT hit this else block, it's here for reference
                        }
                    } catch (IOException e) {
                        Log.e("ERROR IN IO", "IO ERROR IN POPULATE FIRST TIME");
                        Log.e(TAG, e.getMessage());
                    }



                // This is our for loop to add the new songs to our CDLL.
                for(int i = 0; i < paths.size(); i++) {
                    CDLList.insertNode(new Song(titles.get(i), paths.get(i)));
                    Log.e("CDLL POPULATED:",CDLList.head.song.getTitle());
                }
                nextSong = CDLList.head;
                String pathToPlay =  nextSong.song.getPath();
                    try {
                        mp.setDataSource(pathToPlay);
                        mp.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            } else {
                // Lets us know our path was RIP Empty. THIS SHOULD NOT REALLY HAPPEN UNLESS THE USER CLOSES THE APP
                Log.e("path", "empty");
            }
        }
}



