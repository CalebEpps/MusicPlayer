package com.example.musicplayer;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class ParseSongList extends AppCompatActivity {

    protected ArrayList<Song> songList = new ArrayList<>();

    public ParseSongList() {

    }

    public void populateListFirstTime(ArrayList paths, ArrayList names) throws IOException {
        // This is the AssetManager that allows us to open assets within the application's context.

        // Now we're making our file into a string so we can use it at JSON in a sec
        JSONObject jsonObj;

        try {
            Log.e("NEW JSON FILE?", "TRUE");
            jsonObj = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < paths.size(); i++) {
                Log.e("NEW JSON LOOP?", "TRUE");
                JSONObject newEntry = new JSONObject();
                newEntry.put("songTitle", (String) names.get(i));
                newEntry.put("songPath", (String) paths.get(i));
                jsonArray.put(newEntry);

            }


            String folder_main = "WGACA";

            File directoryToCreate = new File(Environment.getExternalStorageDirectory(), folder_main);
            directoryToCreate.mkdirs();

            File initJSONFile = new File(Environment.getExternalStorageDirectory(),"WGACA");
            FileOutputStream outputStream = new FileOutputStream(initJSONFile + "/songs.txt");
            for(int i = 0; i < jsonArray.length(); i++) {
                byte[] strToBytes = jsonArray.get(i).toString().getBytes("utf-8");
                outputStream.write(strToBytes);
            }
            byte[] strToBytes = jsonObj.toString().getBytes("utf-8");
            outputStream.write(strToBytes);

            outputStream.close();
        } catch (JSONException e) {
            Log.e("JSON FILE REACHED", "FALSE");
            e.printStackTrace();
        }


    }

    public void populateExistingList(ArrayList paths, ArrayList names) {

        JSONObject jsonObj;

        try {
            Log.e("EXISTING JSON?", "TRUE");
            jsonObj = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < paths.size(); i++) {
                Log.e("EXISTING LOOP?", "TRUE");
                JSONObject newEntry = new JSONObject();
                try {
                    newEntry.put("songTitle", (String) names.get(i));
                    newEntry.put("songPath", (String) paths.get(i));
                    jsonArray.put(newEntry);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            String folder_main = "WGACA";

            File directoryToCreate = new File(Environment.getExternalStorageDirectory(), folder_main);
            directoryToCreate.mkdirs();

            File initJSONFile = new File(Environment.getExternalStorageDirectory(),"WGACA");
            FileOutputStream outputStream = new FileOutputStream(initJSONFile + "/songs.txt",true);
            for(int i = 0; i < jsonArray.length(); i++) {
                byte[] strToBytes = jsonArray.get(i).toString().getBytes("utf-8");
                outputStream.write(strToBytes);
            }
            byte[] strToBytes = jsonObj.toString().getBytes("utf-8");
            outputStream.write(strToBytes);

            outputStream.close();
        } catch (JSONException | IOException e) {
            Log.e("ERROR", "FALSE");
            e.printStackTrace();
        }
    }


        public int getLength() {
            JSONArray jsonArray = null;
            int counter = 0;

                File initJSONFile = new File(Environment.getExternalStorageDirectory(),"WGACA");
            try {
                FileInputStream inputStream = new FileInputStream(initJSONFile + "/songs.txt");
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



