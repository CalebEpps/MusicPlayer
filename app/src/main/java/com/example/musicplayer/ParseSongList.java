package com.example.musicplayer;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class ParseSongList extends AppCompatActivity {

    protected ArrayList<Song> songList = new ArrayList<>();

    public ParseSongList() {

    }

    public void populateListFirstTime(ArrayList paths, ArrayList names) throws IOException {
        // This is the AssetManager that allows us to open assets within the application's context.

        // Now we're making our file into a string so we can use it at JSON in a sec
        JSONObject jsonObj;

        try {
            Log.e("JSON FILE REACHED", "TRUE");
            jsonObj = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < paths.size(); i++) {
                Log.e("JSON LOOP", "TRUE");
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


        public Song getSong(String path) {


        return null;
        }

        public void deleteSong(String path) {

        }


    }



