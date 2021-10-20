package com.example.musicplayer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder>{

    /*
    OI WHERE THE HECK ARE THE COMMENTS?

    GOOD QUESTION, SO ADAPTERS ARE USUALLY LIKE... WORD FOR WORD THE SAME LAYOUT
    YOU GET YOUR VARIABLES, YOU INFLATE THE VIEW, YOU SET WHERE THE DATA IS COMING
    FROM, AND YOU GET A BEAUTIFUL RECYCLER VIEW THAT'S BASICALLY A LIST
    BUT MORE CONVOLUTED AND OPTIMIZED.

    THE CODE IS KINDA STRAIGHTFORWARD SO I DID NOT REALLY WRITE ANY COMMENTS IN THIS ONE.
    IF YOU HAVE QUESTIONS ABOUT HOW IT WORKS, HMU.
     */

    protected Song[] songs;

    public SongAdapter(Song[] songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View song = inflater.inflate(R.layout.song_item, parent, false);
        ViewHolder vh = new ViewHolder(song);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songs[position];
        holder.songTitle.setText(song.getTitle());




    }

    @Override
    public int getItemCount() {
        ParseSongList parser = new ParseSongList();

        return parser.getLength();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView songTitle;
        public ConstraintLayout constraintLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.songTitle = (TextView)  itemView.findViewById(R.id.songTitle);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.constraintLayout);
        }
    }
}