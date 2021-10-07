package com.example.musicplayer;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ParseSongList extends AppCompatActivity {

    protected ArrayList<Song> songList = new ArrayList<>();
    String filePathEnd = "";

    public ParseSongList() {

    }

    public void populateListFirstTime(ArrayList paths, ArrayList names) throws IOException {
        // This is the AssetManager that allows us to open assets within the application's context.

        // Now we're making our file into a string so we can use it at JSON in a sec
        JSONObject jsonObj;

        Log.e("NEW JSON FILE?", "TRUE");
        jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < paths.size(); i++) {
            Log.e("NEW JSON LOOP?", "TRUE");
            JSONObject newEntry = new JSONObject();
            newEntry.put("songTitle", (String) names.get(i));
            newEntry.put("songPath", (String) paths.get(i));
            jsonArray.add(newEntry);

        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            filePathEnd = "Documents";
        } else {
            filePathEnd = "WGACA";
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

        } else {
            File directoryToCreate = new File(Environment.getExternalStorageDirectory(), filePathEnd);
            directoryToCreate.mkdirs();
        }

        File initJSONFile = new File(Environment.getExternalStorageDirectory(),filePathEnd);
        FileOutputStream outputStream = new FileOutputStream(initJSONFile + "/songs.json");
        byte[] strToBytes = jsonArray.toString().getBytes("utf-8");
        outputStream.write(strToBytes);

        outputStream.close();


    }

    public void populateExistingList(ArrayList paths, ArrayList names) {

        Log.e("RUNNING PEL", "RUNNING...");



    }



    public int getLength() {
            JSONArray jsonArray = null;
            int counter = 0;
             if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                 filePathEnd = "Documents";
             } else {
                 filePathEnd = "WGACA";
             }

                File initJSONFile = new File(Environment.getExternalStorageDirectory(),filePathEnd);
            try {
                FileInputStream inputStream = new FileInputStream(initJSONFile + "/songs.json");
                Log.e("COUNTER PATH: ", inputStream.toString());
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                for (String line; (line = br.readLine()) != null; ) {
                    sb.append(line);
                }

                String toParse = sb.toString();
                Log.e("SB: ", toParse);


            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return counter;

    }


        public void deleteSong(String path) {

        }


    }



