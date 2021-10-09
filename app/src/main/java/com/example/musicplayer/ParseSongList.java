package com.example.musicplayer;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.ArrayList;
import java.util.Iterator;

public class ParseSongList extends AppCompatActivity {

    protected ArrayList<Song> songList = new ArrayList<>();
    String filePathEnd = "";

    public ParseSongList() {

    }

    public void populateListFirstTime(ArrayList paths, ArrayList names) throws IOException {

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
            // Put newEntry into our jsonarray
            jsonArray.add(newEntry);

        }
        // If API version is > 30, we need to write to docs folder, NOT our own.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            filePathEnd = "/Documents";
        } else {
            filePathEnd = "/WGACA";

        }
        // IF API version is higher than 30, we need to NOT create our folder. If it is lower than 30,
        // we can create one.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
/*        // Initialize our JSON file and output stream. This will allow us to write to our file.
        File initJSONFile = new File(Environment.getExternalStorageDirectory(), filePathEnd);
        FileOutputStream outputStream = new FileOutputStream(initJSONFile + "/songs.json");
        byte[] strToBytes = jsonArray.toString().getBytes("utf-8");
        // Here we just write the bytes of our jsonarray to our output file.
        outputStream.write(strToBytes);
        // Close the output file. :)
        outputStream.close();*/


    }

    // This method is run if the file 'songs.json' already exists in our user's directory.
    public void populateExistingList(ArrayList paths, ArrayList names) {

        Log.e("RUNNING PEL", "RUNNING...");

        // If API version is > 30, we need to write/read to docs folder, NOT our own.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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

            // This for loop iterates over our existing entries andis for testing purposes.
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
                newEntry.put("songTitle", (String) names.get(i));
                newEntry.put("songPath", (String) paths.get(i));
                // Put newEntry into our jsonarray
                jsonArray.add(newEntry);

            }

/*            for(int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObj = (JSONObject) jsonArray.get(i);
                String jsonStr = (String) jsonObj.get("songPath");
                for(int j = 0; j < jsonArray.size(); j++ ) {
                    JSONObject jsonObj2 = (JSONObject) jsonArray.get(j);
                    String jsonStr2 = (String) jsonObj2.get("songPath");
                    if(jsonStr.equals(jsonStr2)) {
                        jsonArray.remove(jsonObj2);
                    }
                }
            }*/

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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


        public void deleteSong(String path) {

    }

    public Song[] getEntries()  {
        // If API version is > 30, we need to write/read to docs folder, NOT our own.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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
                songs[i] = new Song(jsonObj.get("songTitle").toString(),jsonObj.get("songPath").toString());
               //    Log.e("PARSE METHOD:", songs[i].getTitle());
                //    Log.e("OBJECT TEST:", name);
            }
         //   Log.e("RETURNED SONGS:", songs[0].toString());
            return songs;

    }

}



