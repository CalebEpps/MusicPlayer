package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TutorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        EditText tutOne = findViewById(R.id.tutText);
        EditText tutTwo = findViewById(R.id.tutText2);
        EditText tutThree = findViewById(R.id.tutText3);
        tutOne.setEnabled(false);
        tutTwo.setEnabled(false);
        tutThree.setEnabled(false);

        Button closeBtn = findViewById(R.id.closeTutBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });







    }
}