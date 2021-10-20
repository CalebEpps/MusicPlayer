package com.example.musicplayer;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import abhishekti7.unicorn.filepicker.UnicornFilePicker;
import abhishekti7.unicorn.filepicker.utils.Constants;

public class MainActivity extends AppCompatActivity {


    // Declare our Media Player Early so it can be used everywhere in this class.
    MediaPlayer mp = new MediaPlayer();
    // Boolean for mediaplayer, currently not used due to no ability to play music (Removed for testing)
    boolean isMPStopped = false;
    // This is the arraylist of files the user is trying to import. It does NOT STORE SONGS THE USER HAS ALREADY SELECTED.
    ArrayList<String> songs;

    // The creates our parser so we can use it in the below if/else statements.
    ParseSongList parser = new ParseSongList();
    // This is our array of songs for the recyclerView
    Song[] songArr;

    // This is our CDLL that we use to do... Most everything. :D
    CyclicDouble CDLList = new CyclicDouble();
    Node nextSong;
    CyclicDoubleInt.IntNode tempNode;
    // Only allows the user to choose popular audio file types.
    String[] filters = {"mp3","ogg","wav","m4a"};
    // This boolean is no longer used. It WAS being used for testing purposes regarding the rw and ff functionality
    boolean progressEnable = true;


    Handler handler = new Handler();
    ImageView adBanner;

    // These variables are used to process and run the ad banners. :)
    int[] adBannerPaths = {R.mipmap.ad_banner_one_foreground, R.mipmap.ad_banner_three_foreground, R.mipmap.ad_four_foreground, R.mipmap.ad_five_foreground,
                           R.mipmap.ad_six_foreground, R.mipmap.ad_seven_foreground, R.mipmap.ad_eight_foreground, R.mipmap.ad_nine_foreground};
    CyclicDoubleInt adBannerCDLL = new CyclicDoubleInt();
    CyclicDoubleInt.IntNode currentAd;
    // This runnable is infinite and runs every 7 seconds to change our banner ad.
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
    // Set our image to the next ad in the currentAd CDLL
            currentAd = currentAd.next;
            adBanner.setImageDrawable(getResources().getDrawable(currentAd.data));
            // This works kinda like recursion, the runnable infinitely calls itself because we want the ad to infinitely cycle
            // while the app is open :D
            handler.postDelayed(runnable, 8000);
        }
    };





    // onCreate Method for doing... Everything?
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This code locks the phone in portrait mode because FUCK THE INSTANCE STATE STUFF I DON'T HAVE TIME TO IMPLEMENT IT
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialize our ad banner variable
        adBanner = findViewById(R.id.rotatingAds);

        // For loop that populates our like... 22nd CDLL at this point.
        // REMEMBER: R.id.* are all integers. We re-use our Int CDLL.
        for(int i = 0; i < adBannerPaths.length; i++) {
            adBannerCDLL.insertNode(adBannerPaths[i]);
        }
        // Set the first ad to the head of our CDLL.
        currentAd = adBannerCDLL.head;
        // This asyncronously runs our delayed code to cycle the CDLL of our ad banners.
        handler.postDelayed(runnable,0);



        // WARNING THIS CODE HAS NOT BEEN TESTED YET. NO USB CABLE AVAILABLE



        // Quickly Populates our recycler view song list... IIIIIIIF there are entries
        songArr = parser.getEntries();
        if(songArr != null) {
            RecyclerView recyclerView = findViewById(R.id.songList);
            SongAdapter adapter = new SongAdapter(songArr);
            recyclerView.setHasFixedSize(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            // Populates our CDLL With songs at startup.
            for (Song song : songArr) {
                CDLList.insertNode(song);
            }
            // Is there already a song in our CDLL? Cool fuckin' play that shit.
            nextSong = CDLList.head;

            Log.e("DID POPULATE?:",CDLList.toString());

            recyclerView.setAdapter(adapter);
        } else {
            // RIP There aren't any songs which means this is a first time load.
            // Nothing will happen here leaving our CDLL null;

        }

        // Set our music player to play on launch if possible.
        if(CDLList.head!= null) {
            try {
                mp.setDataSource(CDLList.head.song.getPath());
                // We don't need to call mp.start because we do that fancy shit in our onPrepared listener below.
                // MAYBE SOME OF THIS SPAGHETTI CODE IS EFFICIENT AFTER ALL.
                mp.prepareAsync();
            } catch (IOException e) {
            }
        }

        // Our onPrepared listener allows  the mediaplayer to function
        // without having to worry about it states.
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // Okay so this will start our audio ONCE IT'S READY
                toastGeneric("The Total Time for this Track is: " + mp.getDuration() / 1000 + " Seconds.");
                mp.start();
              //  isMPPlaying = true;
            }
        });





        // What happens when the song is done.
        mp.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mp.reset();
                        try {
                            if (nextSong.next != null) {
                                nextSong = nextSong.next;

                            }

                            String pathToPlay = nextSong.song.getPath();

                            try {
                                mp.setDataSource(pathToPlay);
                                mp.prepareAsync();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } catch (Exception e) {

                        }
                    }
                }

        );

        // Just Grabbing our buttons for java code. Don't mind it.
        Button selectButton = findViewById(R.id.fileSelectBtn);
        // STOP BUTTON IS INACTIVE. CAN BE ADDED BACK BY UNCOMMENTING RELEVANT CODE
       // ImageButton stopButton = findViewById(R.id.StopButton);
        ImageButton playButton = findViewById(R.id.playButton);
        ImageButton skipButton = findViewById(R.id.NextBtn);
        ImageButton prevButton = findViewById(R.id.previousBtn);
        ImageButton ffBtn = findViewById(R.id.FFBtn);
        ImageButton rewBtn = findViewById(R.id.rewBtn);

// Fast forward button... Fast forwards. BUT IT ALSO USES A CDLL TO DO SO, COOL STUFF HUH?
        // ONLY TOOK LIKE 12 HOURS OF BLOOD, TEARS, AND HIGH CHOLESTEROL TO GET IT TO WORK.
        // FINNICKY ASS JAVA.
        ffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextSong.song.populateFastFoward(mp);
               // Log.e("CURRENT MP TIME", String.valueOf(mp.getCurrentPosition()));
                Log.e("FF NODE TIME", String.valueOf(nextSong.song.seekToNode.data));
              //  Log.e("FFBTN", String.valueOf(mp.getDuration()));
                nextSong.song.fastFoward();
                 mp.seekTo(nextSong.song.seekToNode.data);
                Log.e("CURRENT MP TIME", String.valueOf(mp.getCurrentPosition()));
                toastGeneric("The Current Time of the Song is: " + String.valueOf(mp.getCurrentPosition() / 1000) + " seconds.");
            }
        });
// this is the loooooong click listener. press the button, it clears the fast forward CDLL and makes a new one.
        // CDLLs reproduce by long clicking.
        ffBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                nextSong.song.skipTimeCDLL.deleteAllNodes();
                nextSong.song.populateFastFoward(mp);
                nextSong.song.fastFoward();
                mp.seekTo(nextSong.song.seekToNode.data);
                return true;
            }

        });
// See above, I'm not typing it again.
        rewBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                nextSong.song.skipTimeCDLL.deleteAllNodes();
                nextSong.song.populateRewind(mp);
                nextSong.song.rewind();
                mp.seekTo(nextSong.song.seekToNode.data);
                return true;
            }

        });


// See above, I'm not typing it again.
        rewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Log.e("CURRENT MP TIME", String.valueOf(mp.getCurrentPosition()));
                nextSong.song.populateRewind(mp);
                Log.e("RW NODE TIME", String.valueOf(nextSong.song.seekToNode.data));
              //  Log.e("RWBTN", String.valueOf(mp.getDuration()));
                nextSong.song.rewind();
                mp.seekTo(nextSong.song.seekToNode.data);
                Log.e("CURRENT MP TIME", String.valueOf(mp.getCurrentPosition()));
                toastGeneric("The Current Time of the Song is: " + String.valueOf(mp.getCurrentPosition() / 1000) + " seconds.");
            }
        });




        // This is what happens when our play button is pressed.
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mp.isPlaying()) {
                        mp.pause();
                        isMPStopped = false;
                    } else {
                        if(!isMPStopped) {
                         //   Log.e("MP PLAY BTN", "MP PLAY BUTTON KNEW MP WAS NOT STOPPED");
                            mp.start();
                        } else {
                         //   Log.e("MP PLAY BUTTON","MP PLAY BUTTON SAYS MP WAS STOPPED");
                            String pathToPlay = nextSong.song.getPath();
                            mp.setDataSource(pathToPlay);
                            mp.prepareAsync();
                            isMPStopped = false;
                        }

                    }

                } catch (Exception e) {

                }
            }
        });

        //This will skip the song currently playing in our CDLL or throw an error.
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mp.isPlaying()) {
                        mp.reset();
                        // It will throw an error if it can't skip,
                        // so we need to make sure that the next node exists. :)
                        if(nextSong.next != null) {
                            nextSong = nextSong.next;
                        }
                        String pathToPlay = nextSong.song.getPath();
                        mp.setDataSource(pathToPlay);
                        mp.prepareAsync();
                    } else {
                        toastGeneric("There is no song playing right now.");
                    }

                } catch (Exception e) {
                    toastGeneric("There was an error RIP hope it wasn't during a presentation.");
                }
            }
        });
        // This will play the previous song.
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mp.isPlaying()) {
                        mp.reset();
                        // Same as above, we need to make sure the previous node
                        // is not null.
                        if(nextSong.previous != null) {
                            nextSong = nextSong.previous;
                        }
                        String pathToPlay = nextSong.song.getPath();
                        mp.setDataSource(pathToPlay);
                        mp.prepareAsync();
                    } else {
                        toastGeneric("There is no song playing right now.");
                    }

                } catch (Exception e) {
                    toastGeneric("There was an error RIP hope it wasn't during a presentation.");
                }
            }
        });

// OLD STOP BUTTON CODE. UNCOMMENT AND ADD START BUTTON INITIALIZER ABOVE TO REMAKE
        // Currently unused method for stop button. Ability to Play Sound Removed.
    //    stopButton.setOnClickListener(new View.OnClickListener() {
      //      @Override
      //      public void onClick(View view) {
         //       try {
           //         if (mp.isPlaying()) {
          ////              mp.reset();
            //            isMPStopped = true;
            //            nextSong = CDLList.head;
             //       } else {
            //        }

            //    } catch (Exception e) {
                    //      toastNothingPlaying("There is nothing playing, try selecting a file!");
          //      }
       //     }
     //   });


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
                    .setFilters(filters)
                    .theme(R.style.UnicornFilePicker_Default)
                    .build()
                    .forResult(Constants.REQ_UNICORN_FILE);
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
                ArrayList<String> titles = new ArrayList<>();
                ArrayList<String> paths = new ArrayList<>();
                ArrayList<String> artists = new ArrayList<>();
                ArrayList<String> genres = new ArrayList<>();
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
                    // These just add the necessary artist and genre fields so we can create our song objects.
                    artists.add("No Artist Assigned");
                    genres.add("No Genre Assigned");
                    Log.e("FILE NAME: ", songName);


                    // Unused Incremented Variable
                    counter++;
                }
                // END OF FOR LOOP


                // This is just setting the directory of the folder so we can see if it exists or not.
                // This is the name of the folder we've either created or need to create.
                String sdkunder29 = "/WGACA/songs.json";
                String sdkOver29 = "/Documents/songs.json";
                File directoryToCreate;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    directoryToCreate = new File(Environment.getExternalStorageDirectory(), sdkOver29);
                } else {
                    directoryToCreate = new File(Environment.getExternalStorageDirectory(), sdkunder29);
                }


                // Does the directory exist? If so, we will populate the existing file.
                Log.e("DIRECTORY: ", directoryToCreate.getPath());
                if (directoryToCreate.exists()) {
                    // Populates an existing Directory if it's necessary.
                    Log.e("DIRECTORY EXISTS?: ", "True");


                    // This array holds our entries BEFORE we repopulate our list
                    // with the new entries. This allows us to check for duplicates
                    // in the following nested for loop below. It's not the most optimized
                    // thing in the world, but it is relatively straightforward.
                    Song[] getAllSongsBefore = parser.getEntries();
                    // This method populates our JSON file
                    parser.populateExistingList(paths, titles, artists, genres);


                    // This boolean will help us identify duplicates and keep them out
                    // of our circular doubly linked list.
                    boolean alreadyExists = false;


                    // Ensure that we add our newly selected songs to our CDLL.
                    // This code should also skip duplicates.
                    for (int i = 0; i < paths.size(); i++) {
                        for (int j = 0; j < getAllSongsBefore.length; j++) {
                            // This inner loop checks our previous json file against the
                            // songs that were just selected and changes our boolean for us.
                            if (getAllSongsBefore[j].getPath().equals(paths.get(i))) {
                                alreadyExists = true;
                                Log.e("INNER LOOP: ", "SONG ALREADY EXISTS");
                            } else {
                                Log.e("INNER LOOP:", "SONG DOES NOT EXIST");
                            }

                        }
                        // If that song already exists in our file, we don't add it.
                        // Otherwise, we add it to our CDLL.
                        if (!alreadyExists) {
                            Log.e("ALREADY EXISTS?: ", "FALSE");
                            CDLList.insertNode(new Song(titles.get(i), paths.get(i)));
                        }
                        // Here we need to reset the boolean variable after the inner loop
                        // has run so we can check the next song.
                        alreadyExists = false;


                    }

                    //    Log.e("CDLL POPULATED:",CDLList.head.song.getTitle());

                    // This code will parse our JSON file and reset the recycler view to include all of our added songs.
                    Song[] getAllSongsAfter = parser.getEntries();
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.songList);
                    SongAdapter adapter = new SongAdapter(getAllSongsAfter);
                    recyclerView.setAdapter(adapter);

                    // Log.e("POPULATELISTERROR: ", "FOLDER ALREADY EXISTS.");
                } else {
                    // This executes the FIRST TIME our user runs the app and selects files.
                    try {
                        parser.populateListFirstTime(paths, titles, artists, genres);
                        Log.e("ITEMS: ", String.valueOf(parser.getLength()));
                        Log.e("POPULATELISTSUCCESS", "FILES ADDED TO JSON FILE SUCCESSFULLY (FIRST TIME RUN)");

                        // This will populate and initialize our recycleview our FIRST ENTRIES once they've been added.
                        songArr = parser.getEntries();
                        if (songArr != null) {
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
                    if (paths != null) {
                        // This is our for loop to add the new songs to our CDLL.
                        for (int i = 0; i < paths.size(); i++) {
                            CDLList.insertNode(new Song(titles.get(i), paths.get(i)));
                            Log.e("CDLL POPULATED:", CDLList.head.song.getTitle());
                        }
                        nextSong = CDLList.head;
                        String pathToPlay = nextSong.song.getPath();
                        try {
                            mp.setDataSource(pathToPlay);
                            mp.prepareAsync();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

            } else {
                // Lets us know our path was RIP Empty. THIS SHOULD NOT REALLY HAPPEN UNLESS THE USER CLOSES THE APP
                Log.e("path", "empty");
            }
        }

        public void toastGeneric(String textToShow) {
            Toast.makeText(this, textToShow,
                    Toast.LENGTH_SHORT).show();
        }



}



