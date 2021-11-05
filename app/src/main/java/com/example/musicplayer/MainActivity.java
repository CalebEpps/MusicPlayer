package com.example.musicplayer;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
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
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import abhishekti7.unicorn.filepicker.UnicornFilePicker;
import abhishekti7.unicorn.filepicker.utils.Constants;

/*


 */


public class MainActivity extends AppCompatActivity {

    // Permission Check Variable
    private static final int STORAGE_PERMISSION_CODE = 101;

    // Declare our Media Player Early so it can be used everywhere in this class.
    MediaPlayer mp = new MediaPlayer();

    Toast toast;

    // Boolean for mediaplayer, currently not used due to no ability to play music (Removed for testing)
    boolean isMPStopped = false;

    // This is the arraylist of files the user is trying to import. It does NOT STORE SONGS THE USER HAS ALREADY SELECTED.
    ArrayList<String> songs;

    // The creates our parser so we can use it in the below if/else statements.

    ParseSongList parser = new ParseSongList();

    // This is our array of songs for the recyclerView
    ArrayList<Song> songArr;

    RecyclerView recyclerView;

    // Queried Songs from search :D
   ArrayList<Song> queriedResults;

   // NEW Class-Wide Adapter. Much More efficient. Declared
    // in the onCreate Method Below. Same as new Adapter (Search Results adapter)
    SongAdapter adapter;

    // This is our CDLL that we use to do... Most everything. :D
    // (Props to Alessa for making a great CDLL class)
    CyclicDouble CDLList = new CyclicDouble();
    Node currentSong;

    // Only allows the user to choose popular audio file types.
    String[] filters = {"mp3","ogg","wav","m4a"};

    // Here we declare our handler. It allows us to run things asyncronously on the same
    // or different threads.
    Handler handler = new Handler();

    // This is the image view of our banner. We'll initialize it a bit later.
    ImageView adBanner;
    // Classwide current time variable. Can be changed throughout the activity.
    TextView currentTimePlaying;
    // The seekbar is the bar you can see the song's current time on.
    SeekBar seekBar;

    // These variables are used to process and run the ad banners.
    // This array holds the integer references for our ads to put them into our ad banner CDLL.
    int[] adBannerPaths = {R.drawable.ad_one, R.drawable.ad_two, R.drawable.ad_three, R.drawable.ad_four,
                           R.drawable.ad_five, R.drawable.ad_seven, R.drawable.ad_eight, R.drawable.ad_nine};
    CyclicDoubleInt adBannerCDLL = new CyclicDoubleInt();

    // This is an integer node because R.*.* returns an int reference to the object.
    CyclicDoubleInt.IntNode currentAd;

    // This runnable is infinite and runs every few seconds to change our banner ad.
    Runnable adRunnable = new Runnable() {
        @Override
        public void run() {
    // Set our image to the next ad in the currentAd CDLL
            currentAd = currentAd.next;
            adBanner.setImageDrawable(getResources().getDrawable(currentAd.data));
            // This works kinda like recursion, the runnable infinitely calls itself because we want the ad to infinitely cycle
            // while the app is open :D
            handler.postDelayed(adRunnable, 8000);
        }
    };

    //This is a time Runnable. It asyncrounously loads the time for the mediaplayer every second and displays it on the screen in a textview.
    Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            int timeInSong = mp.getCurrentPosition();
            currentTimePlaying.setText((String.format("%02d", timeInSong / 3600000)) + ":" + String.format("%02d",(timeInSong / 1000 / 60)) + ":" + String.format("%02d", (timeInSong / 1000) % 60));
            handler.postDelayed(timeRunnable, 1000);

        }
    };
    // This Runnable prevents the search from querying too often. This is an optimization technique
    // because the search results change every time the search query is updated. We don't want that.
    boolean tooSoonToSearch = false;
    Runnable preventionRunnable = new Runnable() {
        @Override
        public void run() {
            if(tooSoonToSearch) {
                tooSoonToSearch = false;
            } else {
                tooSoonToSearch = true;
            }
            // Infinite Recursive Call
            handler.postDelayed(preventionRunnable, 500);
        }
    };
// This seekbar runnable updates the seek bar every second.
    Runnable seekBarRunnable = new Runnable() {
        @Override
        public void run() {
            if(mp != null) {
                int currentPlace = mp.getCurrentPosition() / 1000;
                seekBar.setProgress(currentPlace);
            }
            handler.postDelayed(seekBarRunnable, 1000);
        }
    };



// This string is important for setting up our title to display.
    String currentlyPlaying;


    // This is the variable for our search bar!
    SearchView searchBar;

    // onCreate Method for doing... Everything? The on create method
    // is everything that needs to happen when the app starts.
    // You'll notice it's quite large, and it's very typical for this
    // to be the case.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // We initialize our recycler view immediately
        recyclerView = findViewById(R.id.songList);
        // Next we initialize our adapter. See the Song Adapter
        // class for more information.
        adapter= new SongAdapter(songArr, new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song clickedSong) {
                // Test Logs Pre-Traversal of CDLL
                Log.e("CDLL Test1", currentSong.song.getTitle());
                Log.e("CDLL Test2", clickedSong.getTitle());
                // Compare the current song's title to the song that was clicked.
                while(!currentSong.song.getTitle().equals(clickedSong.getTitle())) {
                        currentSong = currentSong.next;

                }
                // Test Log Post Traversal
                Log.e("CDLL Test", currentSong.song.getTitle());
                // Reset and run our media player
                try {
                    mp.reset();
                    mp.setDataSource(currentSong.song.getPath());
                    mp.prepareAsync();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                }
        });

        // The song title and the currently playing text references.
        TextView songTitle = findViewById(R.id.songTitle);
        TextView currentlyPlayingText = findViewById(R.id.textView2);

        // Allowing the user to rotate their phone resets the activity, and we don't want that.
        // This line locks the user's phone orientation to portrait mode. Many apps do this.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // The permission check method is at the bottom of the Main Activity class.
        // It will see if the user has granted us permissions. If not, we prompt them.
        permissionCheck(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);

        // Just Grabbing our buttons for java.
        Button selectButton = findViewById(R.id.fileSelectBtn);
        ImageButton playButton = findViewById(R.id.playButton);
        ImageButton skipButton = findViewById(R.id.NextBtn);
        ImageButton prevButton = findViewById(R.id.previousBtn);
        ImageButton ffBtn = findViewById(R.id.FFBtn);
        ImageButton rewBtn = findViewById(R.id.rewBtn);

        // Initialize our ad banner variable
        adBanner = findViewById(R.id.rotatingAds);
        // This is the song timer.
        currentTimePlaying = findViewById(R.id.songTime);
        // this is the seekbar variable :D
        seekBar = findViewById(R.id.seekBar);

        // For loop that populates our like... 22nd CDLL at this point.
        // REMEMBER: R.id.* are all integers. We re-use our Int CDLL.
        for(int i = 0; i < adBannerPaths.length; i++) {
            adBannerCDLL.insertNode(adBannerPaths[i]);
        }
        // Set the first ad to the head of our CDLL.
        currentAd = adBannerCDLL.head;
        // This asyncronously runs our delayed code to cycle the CDLL of our ad banners.
        handler.postDelayed(adRunnable,0);
        // Gotta start ALL THE HANDLER RUNNABLES LET'S GOOOOO
        handler.postDelayed(seekBarRunnable,0);
        handler.postDelayed(preventionRunnable,0);


        // Initialize our search bar
        searchBar = findViewById(R.id.searchSongs);
        // This is a listener for our search bar. It
        // Overrides the methods that run when the query changes
        // or when the submit button (or enter) is pressed.
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                // If the query is empty, then we reset
                // the adapter because we want to show all songs again
                if (s.isEmpty()) {
                    Log.e("CLEARING", "CLEARING SEARCHES...");
                    adapter.updateList(songArr);
                } else if (s != null) {
                    // If the query isn't empty, we need to call our search method.
                    // see Parse Song List for more details.
                    queriedResults = parser.search(s);
                    adapter.updateList(queriedResults);
                }
                return false;
            }

// Same as above except we want this to run if the user presses submit.
            @Override
            public boolean onQueryTextChange(String s) {
                if(s.isEmpty()) {
                    Log.e("CLEARING", "CLEARING SEARCHES...");
                    adapter.updateList(songArr);
                } else if(s != null && !tooSoonToSearch) {
                    queriedResults = parser.search(s);
                    adapter.updateList(queriedResults);
                }
                return false;
            }
        });


        // Quickly Populates our recycler view song list. If there are entries.
        songArr = parser.getEntries();
        if(songArr != null) {
            RecyclerView recyclerView = findViewById(R.id.songList);
            recyclerView.setHasFixedSize(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            // Populates our CDLL With songs at startup.
            for (Song song : songArr) {
                CDLList.insertNode(song);
            }
            // If there are songs in our CDLL, we will queue and play them.
            // Remember this is only called if the song array isn't empty
            // which in turn means our CDLList isn't empty either.
            currentSong = CDLList.head;
            // Just a testing Log.
            Log.e("DID POPULATE?:",CDLList.toString());
            adapter.updateList(songArr);
            recyclerView.setAdapter(adapter);
        }
        // Set our music player to play on launch if possible.
        if(CDLList.head != null) {
            try {
                mp.setDataSource(CDLList.head.song.getPath());
                handler.postDelayed(timeRunnable,0);
                // We don't need to call mp.start because we do that in our onPrepared Listener.
                mp.prepareAsync();
            } catch (IOException e) {
            }
        }

        // Our onPrepared listener allows  the mediaplayer to function
        // without having to worry about its states.
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // Okay so this will start our audio ONCE IT'S READY
                playButton.setImageDrawable(getResources().getDrawable(R.drawable.pause_btn));
                currentlyPlaying = currentSong.song.getTitle();
                songTitle.setText(currentlyPlaying);
                currentlyPlayingText.setText("Currently Playing:");
                seekBar.setMax(mp.getDuration() / 1000);
             //   toastGeneric("The Total Time for this Track is: " + mp.getDuration() / 1000 + " Seconds.");
                mp.start();
            }
        });


        // What happens when the song is done.
        mp.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mp.reset();
                        try {
                            if (currentSong.next != null) {
                                currentSong = currentSong.next;

                            }
                            String pathToPlay = currentSong.song.getPath();


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
        // Our seek bars on change listener. We only utilize the onStopTrackingTouch method.
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mp != null) {
                    mp.seekTo(seekBar.getProgress() * 1000);
                }
            }
        });




        // Fast forward button Code.
        ffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // We populate the current song's CDLL of timestamps.
                if (currentSong != null) {
                    currentSong.song.populateFastFoward(mp);
                    // Log.e("CURRENT MP TIME", String.valueOf(mp.getCurrentPosition()));
                    Log.e("FF NODE TIME", String.valueOf(currentSong.song.seekToNode.data));
                    //  Log.e("FFBTN", String.valueOf(mp.getDuration()));
                    // Remember fast forward is a method that does seekToNode = seekToNode.next;
                    currentSong.song.fastFoward(mp);
                    // So this is a little weird.... Let's go piece by piece shall we? Yes.
                    // mp.seekTo goes to that time in the song.
                    // nextSong.song gets the song object that currently exists in the nextSong node.
                    // .seekToNode is our node within the song object.
                    // .data gets that number for us.
                    mp.seekTo(currentSong.song.seekToNode.data);
                    // So these are just some logs that will let everyone know it is indeed skipping
                    // into the future.
                    Log.e("CURRENT MP TIME", String.valueOf(mp.getCurrentPosition()));
                //    toastGeneric("The Current Time of the Song is: " + String.valueOf(mp.getCurrentPosition() / 1000) + " seconds.");
                } else {
                    toastGeneric("There is no song playing right now.");
                }
            }
        });
        // This is the long click listener. press the button, it clears the fast forward CDLL and makes a new one.
        // CDLLs reproduce by long clicking.
        ffBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Delete all of the current nodes.
                if (currentSong != null) {
                    currentSong.song.skipTimeCDLL.deleteAllNodes();
                    // Repopulate the song's CDLL
                    currentSong.song.populateFastFoward(mp);
                    // Sett the time  to where it needs to go in the future
                    // (We're assuming the user wanted to fast forward on the
                    // long press.
                    currentSong.song.fastFoward(mp);
                    // Go to that point in the song
                    mp.seekTo(currentSong.song.seekToNode.data);
                    // So apparently you have to return a boolean with on long click listeners.
                } else {
                    toastGeneric("There is no song playing right now.");
                }
                return true;
            }

        });

        // See above, I'm not typing it again.
        rewBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(currentSong != null) {
                    currentSong.song.skipTimeCDLL.deleteAllNodes();
                    currentSong.song.populateRewind(mp);
                    currentSong.song.rewind();
                    mp.seekTo(currentSong.song.seekToNode.data);
                } else {
                    toastGeneric("There is no song playing right now.");
                }
                return true;
            }

        });


        // See above, I'm not typing it again.
        rewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentSong != null) {
                    //  Log.e("CURRENT MP TIME", String.valueOf(mp.getCurrentPosition()));
                    currentSong.song.populateRewind(mp);
                    Log.e("RW NODE TIME", String.valueOf(currentSong.song.seekToNode.data));
                    //  Log.e("RWBTN", String.valueOf(mp.getDuration()));
                    currentSong.song.rewind();
                    mp.seekTo(currentSong.song.seekToNode.data);
                    Log.e("CURRENT MP TIME", String.valueOf(mp.getCurrentPosition()));
                  //  toastGeneric("The Current Time of the Song is: " + String.valueOf((mp.getCurrentPosition() / 1000)) + " seconds.");
                } else {
                    toastGeneric("There is no song playing right now.");
                }
            }
        });




        // This is what happens when our play button is pressed.
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSong != null) {
                    try {
                        // This is our pause functionality here
                        if (mp.isPlaying()) {
                            playButton.setImageDrawable(getDrawable(R.drawable.play_btn));
                            mp.pause();
                            // This variable needed to be changed here for some reason
                            // I forgot why
                            isMPStopped = false;
                        } else {
                            if (!isMPStopped) {
                                //   Log.e("MP PLAY BTN", "MP PLAY BUTTON KNEW MP WAS NOT STOPPED");
                                playButton.setImageDrawable(getDrawable(R.drawable.pause_btn));
                                mp.start();
                            } else {
                                //   Log.e("MP PLAY BUTTON","MP PLAY BUTTON SAYS MP WAS STOPPED");
                                String pathToPlay = currentSong.song.getPath();
                                mp.setDataSource(pathToPlay);
                                mp.prepareAsync();
                                isMPStopped = false;
                            }

                        }

                    } catch (Exception e) {

                    }
                } else {
                    toastGeneric("There is no song playing right now.");
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
                        if(currentSong.next != null) {
                            currentSong = currentSong.next;

                        }
                        String pathToPlay = currentSong.song.getPath();
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
        // This will play the previous song. Who would've guessed?
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mp.isPlaying()) {
                        mp.reset();
                        // Same as above, we need to make sure the previous node
                        // is not null.
                        if(currentSong.previous != null) {
                            currentSong = currentSong.previous;
                        }
                        String pathToPlay = currentSong.song.getPath();
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
                // And two we don't use at all.
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
                   ArrayList<Song> getAllSongsBefore = parser.getEntries();
                    // This method populates our JSON file
                    parser.populateExistingList(paths, titles, artists, genres);


                    // This boolean will help us identify duplicates and keep them out
                    // of our circular doubly linked list.
                    boolean alreadyExists = false;


                    // Ensure that we add our newly selected songs to our CDLL.
                    // This code should also skip duplicates.
                    for (int i = 0; i < paths.size(); i++) {
                        for (int j = 0; j < getAllSongsBefore.size(); j++) {
                            // This inner loop checks our previous json file against the
                            // songs that were just selected and changes our boolean for us.
                            if (getAllSongsBefore.get(j).getPath().equals(paths.get(i))) {
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
                    ArrayList<Song> getAllSongsAfter = parser.getEntries();
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.songList);
                    adapter.updateList(getAllSongsAfter);
                 //   recyclerView.setAdapter(adapter);



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
                            recyclerView.setHasFixedSize(false);
                            recyclerView.setLayoutManager(new LinearLayoutManager(this));
                            adapter.updateList(songArr);
                        //    recyclerView.setAdapter(adapter);
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
                        currentSong = CDLList.head;
                        String pathToPlay = currentSong.song.getPath();
                        try {
                            mp.setDataSource(pathToPlay);
                            mp.prepareAsync();
                            handler.postDelayed(timeRunnable,0);
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
// This is the same as our regular recyclerView code. This one just changes the array.
    // Currently does NOT work, everything needs to be changed to arraylists.
        public void resetSongsAfterSearch(ArrayList<Song> songArr) {
            RecyclerView recyclerView = findViewById(R.id.songList);
            Log.e("RV RESET", "RECYCLER VIEW RESET IN RESET SONGS AFTER SEARCH");
            adapter.updateList(songArr);
        }

// I love love love this little method. It allows me to basically throw any text to the screen
    // really fast.
        public void toastGeneric(String textToShow) {
        // IF a toast is on the screen, it won't throw an error lmao
        try{ toast.getView().isShown();
            // Sets the text of our currently shown toast.
                toast.setText(textToShow);
                // This is a doozy. If the above method throws an excpetion,
            // Then we will actually create the toast. THEN if we press the button again,
            // there won't be an exception until the toast disappears.
            } catch (Exception e) {
                toast = Toast.makeText(this, textToShow, Toast.LENGTH_SHORT);
            }
        // Gotta show that toast.
            toast.show();
        }
// This is the permission checker. It opens a popup that will ask the user for access to write to their storage.
       // The request code is declared at the top of our class. It can be any number :D
    // If the permissions aren't granted, the program will return a different number, I think
    // it's like -1 or something.
        public void permissionCheck(String permissionType, int reqCode) {
            if(ContextCompat.checkSelfPermission(MainActivity.this, permissionType) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {permissionType }, reqCode);
            } else {
               // toastGeneric("You're all set with your permission, thank you for trusting Circle Player");
            }
        }
// This method is like a return statement for our permission checker method. It let's the user know that we really need access.
    @Override
    public void onRequestPermissionsResult(int reqCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(reqCode, permissions, grantResults);
        if (reqCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toastGeneric("You granted storage permission, thank you! <3");
            } else {
                toastGeneric("Why don't you trust us, this is a school project. :(");
            }
        }
    }

}



