package com.example.musicplayer;

import android.media.MediaPlayer;
import android.provider.MediaStore;

import java.util.ArrayList;

public class Song {
    private String title;
    private String genre;
    private String artist;
    private String path;
    protected int currentTime = 0;
    protected CyclicDoubleInt.IntNode seekToNode;
    protected CyclicDoubleInt skipTimeCDLL = new CyclicDoubleInt();
    boolean checkFF = false;
    boolean checkRW = false;

// LITERALLY GENERATED THE GETTERS AND SETTERS  NOT SURE WHY IT PUT THESE TWO UP HERE :D
    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
// This constructor is great when we just need the title and path. :)
    // More optimized.
    public Song(String title, String path) {
        this.path = path;
        this.title = title;

    }
// This is the full constructor.
    public Song(String title, String path, String artist, String genre) {
        this.title = title;
        this.path = path;
        this.artist = artist;
        this.genre = genre;
    }

    // OH YEAH REAL PROGRAMMING TIME GETTERS AND SETTERS.
    // LOOK MOM, I MADE SOMETHING
    // "GET A REAL JOB!"
    // MOM YOU CAN'T STOP ME FROM LIVING MY BEST LIFE
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // Method to populates our traversal CDLL.
    public void populateFastFoward(MediaPlayer mp) {
        // ONLY DO ANY OF THIS METHOD IF THE CDLL IS NULL.
        // See the populateRewind method below for more detailed comments.
        // This code is basically the same because I'm dumb and commented the wrong block.
        if(skipTimeCDLL.head == null) {
        FormulaDivide fd = new FormulaDivide();
        currentTime = mp.getCurrentPosition();
        ArrayList<Integer> tempList = fd.FFFormula(mp.getDuration(),currentTime, 15000);
        for(int i = 0; i < tempList.size(); i++) {
           skipTimeCDLL.insertNode(tempList.get(i));
            }
           // ffNode = fastForward.head;
        seekToNode = skipTimeCDLL.head;
        }
    }

    public void populateRewind(MediaPlayer mp) {

        if(skipTimeCDLL.head == null) {
            // Create New Formula Object
            FormulaDivide fd = new FormulaDivide();
            // Get the media player parameter's current time.
            currentTime = mp.getCurrentPosition();
            // Create an arraylist that makes use of our mathy goodness to divide
            // the songs into 30 second intervals.
            ArrayList<Integer> tempList = fd.RRformula(mp.getDuration(),currentTime, 15000);
            // This for loop populates our CDLL with the results from the above.
            for(int i = 0; i < tempList.size(); i++) {
               skipTimeCDLL.insertNode(tempList.get(i));
            }
            seekToNode = skipTimeCDLL.head;
        }
    }
    // Using a separate method to set the seekToNode is more efficient
    // because if we did it in the above method, we'd need to check the condition first.
    // Calling these two below methods separately allows us more flexibility in our other files.
    public void fastFoward(MediaPlayer mp) {
                seekToNode = seekToNode.next;
    }

    public void rewind() {
        seekToNode = seekToNode.previous;
    }
}
