package com.example.musicplayer;

import static android.os.Build.VERSION.SDK_INT;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.lang3.ArrayUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ParseSongList extends AppCompatActivity {

    String filePathEnd = "";

    public ParseSongList() {

    }

    public void populateListFirstTime(ArrayList paths, ArrayList names, ArrayList artists, ArrayList genres) throws IOException {

        // Check to see if we've reached this part of the method.
        Log.e("NEW JSON FILE?", "TRUE");
        // Initialize Jsonarray and populate it with the parameters in the method's signature.
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < paths.size(); i++) {
            Log.e("NEW JSON LOOP?", "TRUE");
            // Temp JSON Object to hold our data
            JSONObject newEntry = new JSONObject();
            newEntry.put("songTitle", (String) names.get(i));
            newEntry.put("songPath", (String) paths.get(i));
            newEntry.put("artist", "No Artist Found");
            newEntry.put("genre", "No Genre Assigned");
            // Put newEntry into our jsonarray
            jsonArray.add(newEntry);

        }
        // If API version is > 30, we need to write to docs folder, NOT our own.
        if (SDK_INT >= Build.VERSION_CODES.Q) {
            filePathEnd = "/Documents";
        } else {
            filePathEnd = "/WGACA";

        }
        // IF API version is higher than 30, we need to NOT create our folder. If it is lower than 30,
        // we can create one.
        if (SDK_INT >= Build.VERSION_CODES.Q) {
            File directoryToCreate = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath());
            //directoryToCreate.mkdirs();
            // Initialize our JSON file and output stream. This will allow us to write to our file.
            File initJSONFile = new File(directoryToCreate.getPath());
            FileOutputStream outputStream = new FileOutputStream(initJSONFile + "/songs.json");
            byte[] strToBytes = jsonArray.toString().getBytes("utf-8");
            // Here we just write the bytes of our jsonarray to our output file.
            outputStream.write(strToBytes);
            // Close the output file. :)
            outputStream.close();
        } else {
            File directoryToCreate = new File(Environment.getExternalStorageDirectory(), filePathEnd);
            directoryToCreate.mkdirs();
            // Initialize our JSON file and output stream. This will allow us to write to our file.
            File initJSONFile = new File(Environment.getExternalStorageDirectory(), filePathEnd);
            FileOutputStream outputStream = new FileOutputStream(initJSONFile + "/songs.json");
            byte[] strToBytes = jsonArray.toString().getBytes("utf-8");
            // Here we just write the bytes of our jsonarray to our output file.
            outputStream.write(strToBytes);
            // Close the output file. :)
            outputStream.close();

        }
    }

    // This method is run if the file 'songs.json' already exists in our user's directory.
    public void populateExistingList(ArrayList paths, ArrayList names, ArrayList artists, ArrayList genres) {

        Log.e("RUNNING PEL", "RUNNING...");
        Song[] existingSongs = getEntries();

        // If API version is > 30, we need to write/read to docs folder, NOT our own.
        if (SDK_INT >= Build.VERSION_CODES.R) {
            filePathEnd = "Documents";
        } else {
            filePathEnd = "WGACA";
        }

        JSONParser parser = new JSONParser();

        try {
            Reader reader = new FileReader(new File(Environment.getExternalStorageDirectory(), filePathEnd + "/songs.json"));
            Log.e("PATH EXSTING: ", new File(Environment.getExternalStorageDirectory(), filePathEnd + "/songs.json").getPath());
            JSONArray jsonArray = (JSONArray) parser.parse(reader);
            //Log.e("OBJECT TEST:", String.valueOf(jsonArray));

            // This for loop iterates over our existing entries and is for testing purposes.
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.get(i).toString();
                JSONObject jsonObj = (JSONObject) jsonArray.get(i);
                String songTitle = jsonObj.get("songTitle").toString();
                //   Log.e("SONG NAME TEST:",songTitle);
                //    Log.e("OBJECT TEST:", name);
            }


            // This for loop iterates over our NEW entries and adds them to our jsonarray
            for (int i = 0; i < paths.size(); i++) {
                // Log.e("NEW JSON LOOP?", "TRUE");
                // Temp JSON Object to hold our data
                JSONObject newEntry = new JSONObject();
                newEntry.put("songTitle", names.get(i));
                newEntry.put("songPath", paths.get(i));
                newEntry.put("artist",  artists.get(i));
                newEntry.put("genre", genres.get(i));
                boolean alreadyAdded = false;
                // Nested for loop checks for duplicates.
                for(int j = 0; j < existingSongs.length; j++) {
                    if(newEntry.get("songPath").equals(existingSongs[j].getPath())) {
                        alreadyAdded = true;
                        break;
                    }
                }

                // Put newEntry into our jsonarray
                if(!alreadyAdded) {
                    jsonArray.add(newEntry);
                }


            }
// This method is currently not implemented, but it checks for duplicates in our json array
            // before writing it to the file.
            File initJSONFile = new File(Environment.getExternalStorageDirectory(), filePathEnd);
            FileOutputStream outputStream = new FileOutputStream(initJSONFile + "/songs.json");
            byte[] strToBytes = jsonArray.toString().getBytes("utf-8");
            // Here we just write the bytes of our jsonarray to our output file.
            outputStream.write(strToBytes);
            // Close the output file. :)
            outputStream.close();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    public int getLength() {
        JSONArray jsonArray;
        int counter = 0;
     //   Log.e("RUNNING GETLENGTH", "RUNNING...");

        // If API version is > 30, we need to write/read to docs folder, NOT our own.
        if (SDK_INT >= Build.VERSION_CODES.R) {
            filePathEnd = "Documents";
        } else {
            filePathEnd = "WGACA";
        }

        JSONParser parser = new JSONParser();

        try {
            Reader reader = new FileReader(new File(Environment.getExternalStorageDirectory(), filePathEnd + "/songs.json"));
            jsonArray = (JSONArray) parser.parse(reader);
            //Log.e("OBJECT TEST:", String.valueOf(jsonArray));
            for (int i = 0; i < jsonArray.size(); i++) {
                counter++;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return counter;
    }

    public Song[] getEntries()  {
        // If API version is > 30, we need to write/read to docs folder, NOT our own.
        if (SDK_INT >= Build.VERSION_CODES.R) {
            filePathEnd = "Documents";
        } else {
            filePathEnd = "WGACA";
        }

        JSONParser parser = new JSONParser();

        Reader reader = null;
        try {
            reader = new FileReader(new File(Environment.getExternalStorageDirectory(), filePathEnd + "/songs.json"));
        } catch (FileNotFoundException e) {
            return null;
        }
        JSONArray jsonArray = null;
        try {
            jsonArray = (JSONArray) parser.parse(reader);
        } catch (IOException e) {
            return null;
        } catch (ParseException e) {
            return null;
        }
        //      Log.e("SIZE GETENTRIES:", String.valueOf(jsonArray.size()));
            Song[] songs = new Song[jsonArray.size()];
            //Log.e("OBJECT TEST:", String.valueOf(jsonArray));
            for (int i = 0; i < songs.length; i++) {
                JSONObject jsonObj = (JSONObject) jsonArray.get(i);
                songs[i] = new Song(jsonObj.get("songTitle").toString(),jsonObj.get("songPath").toString(),jsonObj.get("artist").toString(),jsonObj.get("genre").toString());
               //    Log.e("PARSE METHOD:", songs[i].getTitle());
                //    Log.e("OBJECT TEST:", name);
            }
         //   Log.e("RETURNED SONGS:", songs[0].toString());
            return songs;

    }

    public void deleteAll() {

        if (SDK_INT >= Build.VERSION_CODES.Q) {
            filePathEnd = "Documents";
        } else {
            filePathEnd = "WGACA";
        }

        File initJSONFile;
        File directoryToCreate;
        if (SDK_INT >= Build.VERSION_CODES.Q) {
            directoryToCreate = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath());
            initJSONFile = new File(directoryToCreate.getPath() + "/songs.json");
        } else {
            directoryToCreate = new File(Environment.getExternalStorageDirectory(), "/WGACA");
            initJSONFile = new File(Environment.getExternalStorageDirectory(), filePathEnd + "/songs.json");
        }

        if(initJSONFile.exists()) {
            initJSONFile.delete();
            if(SDK_INT >= Build.VERSION_CODES.Q) {
                directoryToCreate.delete();
            }

        }
    }
// Search method for returning an arraylist of songs that contain
    // the search string
    public Song[] search(String searchStr) {
        Song[] allSongs = getEntries();
        if(allSongs != null) {
            ArrayList<Song> queriedSongs = new ArrayList<>();

            for (int i = 0; i < allSongs.length; i++) {
                if (allSongs[i].getTitle().contains(searchStr) ||
                        allSongs[i].getArtist().contains(searchStr) ||
                        allSongs[i].getGenre().contains(searchStr)) {
                    Log.e("SEARCHING...", allSongs[i].getTitle() + " FOUND");
                    queriedSongs.add(allSongs[i]);
                }
            }
            Song[] toReturn = new Song[queriedSongs.size()];
            toReturn = queriedSongs.toArray(toReturn);
            // Return the songs containing the key words
            return toReturn;
            // Else statement to return null if needed
        } else {
            return null;
        }
    }
    // Method deletes a single song.
    public void deleteSong(String songTitle) {

        Song[] allSongs = getEntries();
        boolean foundSong = false;

        if(allSongs != null) {
            Log.e("REACHED IF DELTE", "REACHED IF DELETE");
        for(int i = 0; i < allSongs.length; i++) {
            if (allSongs[i].getTitle().equals(songTitle)) {
                Log.e("SONG TO DELETE FOUND", "SONG TO DELETE FOUND");
                Log.e("Song to Delete", allSongs[i].getTitle());
                allSongs = ArrayUtils.remove(allSongs, i);

                foundSong = true;
            }
        }

        for(int i = 0; i < allSongs.length; i++) {
            Log.e("Song is gone?", allSongs[i].getTitle());
        }

        }

        if(foundSong) {
            ArrayList<Song> tempSongList = new ArrayList<>(Arrays.asList(allSongs));
            ArrayList<String> tempTitles = new ArrayList<>();
            ArrayList<String> tempPaths = new ArrayList<>();
            ArrayList<String> tempGenres = new ArrayList<>();
            ArrayList<String> tempArtists = new ArrayList<>();

            for (int i = 0; i < tempSongList.size(); i++) {
                tempTitles.add(tempSongList.get(i).getTitle());
                tempPaths.add(tempSongList.get(i).getPath());
                tempGenres.add(tempSongList.get(i).getGenre());
                tempArtists.add(tempSongList.get(i).getArtist());
            }

            try {
                if(!tempPaths.isEmpty()) {
                    populateListFirstTime(tempPaths, tempTitles, tempArtists, tempGenres);
                } else {
                    deleteAll();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



}



