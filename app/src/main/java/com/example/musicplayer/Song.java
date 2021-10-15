package com.example.musicplayer;

import android.media.MediaPlayer;
import android.util.Log;

import java.util.ArrayList;

public class Song {
    private String title;
    private String genre;
    private String artist;
    private String path;
    protected CyclicDoubleInt fastForward = new CyclicDoubleInt();
    protected CyclicDoubleInt rewind = new CyclicDoubleInt();
    protected CyclicDoubleInt.IntNode ffNode;
    protected CyclicDoubleInt.IntNode rewindNode;
    protected int currentTime = 0;
    ParseSongList parser = new ParseSongList();

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Song(String title, String path) {
        this.path = path;
        this.title = title;

    }

    public Song(String title, String path, String artist, String genre) {
        this.title = title;
        this.path = path;
        this.artist = artist;
        this.genre = genre;
    }


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
        if(fastForward.head == null) {
        FormulaDivide fd = new FormulaDivide();
        currentTime = mp.getCurrentPosition();
        ArrayList<Integer> tempList = fd.FFFormula(mp.getDuration(),currentTime);
        for(int i = 0; i < tempList.size(); i++) {
            fastForward.insertNode(tempList.get(i));
            }
            ffNode = fastForward.head;
        }
    }

    public void populateRewind(MediaPlayer mp) {

        if(rewind.head == null) {
            FormulaDivide fd = new FormulaDivide();
            currentTime = mp.getCurrentPosition();
            ArrayList<Integer> tempList = fd.RRformula(mp.getDuration(),currentTime);
            for(int i = 0; i < tempList.size(); i++) {
                rewind.insertNode(tempList.get(i));
            }
            rewindNode= rewind.head;
        }
    }


    // Method to return the needed time in the traversal CDLL
    public int getTime(MediaPlayer mp) {
        return 0;

    }

    public void fastFoward() {
    //    Log.e("FAST FORWARD TO", String.valueOf(ffNode.data));
        ffNode = ffNode.next;
        //ffNode.next.data
    }

    public void rewind() {
        rewindNode = rewindNode.previous;
    }
}
