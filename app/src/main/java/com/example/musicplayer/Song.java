package com.example.musicplayer;

import android.media.MediaPlayer;
import android.provider.MediaStore;

import java.util.ArrayList;

public class Song {
    private String title;
    private String genre;
    private String artist;
    private String path;
    private CyclicDoubleInt traversal;

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
    public void splitSong(MediaPlayer mp) {

    }

    // Method to return the needed time in the traversal CDLL
    public int getTime(MediaPlayer mp) {
        return 0;

    }
}
