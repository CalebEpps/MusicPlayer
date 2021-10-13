package com.example.musicplayer;

import android.media.MediaPlayer;
import android.provider.MediaStore;

import java.util.ArrayList;

public class Song {
    private String title;
    private String genre;
    private String artist;
    private String path;
    private CyclicDoubleInt traversal = new CyclicDoubleInt();

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
    public CyclicDoubleInt splitSong(MediaPlayer mp) {
        int totalTime = getTotalTime(mp);
        CyclicDoubleInt dividedSong = new CyclicDoubleInt();
        int temp = 0;
        while(temp <= totalTime) {
            if(temp + 30 > totalTime) {
                break;
            }
          dividedSong.insertNode(temp);
          temp += 30;
        }
        return dividedSong;
    }

    // Method to return the needed time in the traversal CDLL
    public int getTotalTime(MediaPlayer mp) {
        return mp.getDuration();

    }
}
