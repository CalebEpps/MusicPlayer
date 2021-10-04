package com.example.musicplayer;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {



    // Declare our Media Player Early so it can be used everywhere in this class.
    MediaPlayer mp = new MediaPlayer();
    boolean isMPPlaying = false;
    String currentlyPlayingSong;
    // onCreate Method for doing... Everything?
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

// Okay so this is what allows our activity to return something. You'll see in this case it returns
        // the intent.
        ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(
                // Starts the activity and looks for a result. You'll notice that
                // in our on click launcher for the button, we call mGetContent.launch().
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    // So this sets what happens after our activity is closed (The file chooser)
                    // Think of it like a return statement.
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // So this gets the returned item from our selection and converts
                            // its path to a readable form. VERY VERY hacky but it works.
                            Intent data = result.getData();
                            Uri uri = data.getData();
                            String realPath = getRealPathFromURI(uri);
                            Log.e("PATH", realPath);
                            String currentlyPlayingSong = realPath.substring(realPath.lastIndexOf("/")+1);
                            TextView testTitle = findViewById(R.id.textView2);
                            testTitle.setText("Currently Playing:" + currentlyPlayingSong);
                            try {
                                // This sets the listener and tells the
                                // the program that "Hey, we need to wait until the song
                                // is loaded into memory before we can start it."
                                // Otherwise we're going to get an error because it
                                // tries to play a song that isn't loaded into memory.
                                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        // Okay so this will start our audio ONCE IT'S READY
                                        mp.start();
                                        isMPPlaying = true;
                                    }
                                });
                                // This just sets our data source
                                mp.setDataSource(realPath);
                                // Prepares audio (Asynced for performance)
                                mp.prepareAsync();
                                // Errors (Note: These are auto generated blocks for catching errors.
                                // It might be worth personalizing them.
                            } catch (IllegalArgumentException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (SecurityException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IllegalStateException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }


                        }
                    }
                });

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
                    toastNothingPlaying("There is nothing playing, try selecting a file!");
                }
            }
        });

       stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mp.isPlaying()) {
                        mp.stop();
                        mp.reset();
                    } else {
                        toastNothingPlaying("There is nothing playing, try selecting a file!");
                    }

                } catch (Exception e) {
                    toastNothingPlaying("There is nothing playing, try selecting a file!");
                }
            }
        });


        // Sets our button listener
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // This is launching the activity to select a file
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                // ONLY allows audio files (Any kind)
                intent.setType("audio/*");
                // Launches our file choosing intent
                mGetContent.launch(intent);
            }
        });







    }


    //  it works and that's enough for me. Will be more detailed later. I'm tired.
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    protected void toastNothingPlaying(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.show();
    }


}
