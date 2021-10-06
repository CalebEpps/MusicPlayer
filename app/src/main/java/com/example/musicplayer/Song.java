package com.example.musicplayer;

public class Song {
    private String title;
    private String genre;

    private String path;

    public Song(String title, String genre, String artist) {
        this.path = path;
        this.title = title;
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
}
