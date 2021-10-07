package com.example.musicplayer;

import java.util.ArrayList;

public class Song {
    private String title;
    private String genre;
    private String artist;
    private String path;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Song(String title, String genre, String artist) {
        this.path = path;
        this.title = title;
        this.genre = genre;
        this.artist = artist;

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
}
