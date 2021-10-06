package com.example.musicplayer;

import android.content.res.AssetManager;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ParseSongList extends AppCompatActivity {

    public ParseSongList() {

    }

    public void populateList(ArrayList songs, ArrayList paths) throws IOException {
        AssetManager am = getApplicationContext().getAssets();
        InputStream is = am.open("songs.json");

        for(int i = 0; i < paths.size(); i++) {
            String tempPath = (String) paths.get(i);
            Song song = new Song(null,null,null, tempPath);


            }
        }
    }



