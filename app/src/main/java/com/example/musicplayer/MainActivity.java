package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

// Method that is called when our select a folder button is pressed. It will open a file browser on the user's phone.
    public void selectFolderBtn(View view) {
        // Create an intent. See the Android docs for more info.
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Here we're saying to ignore files that aren't local. For example, Google Drive, One Drive, etc.
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        // this is setting the type of file we want the user to be able to select (NEED TO BE ABLE TO SELECT FOLDER)
        intent.setType("audio/*");
        // Start the activity.
        startActivity(intent);

    }
}