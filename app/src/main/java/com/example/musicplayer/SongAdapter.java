package com.example.musicplayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder>{

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
        holder.genre.setText(song.getGenre());
        holder.artist.setText(song.getArtist());
    }

    @Override
    public int getItemCount() {
        ParseSongList parser = new ParseSongList();

        return 2;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView songTitle;
        public TextView genre;
        public TextView artist;
        public ConstraintLayout constraintLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.songTitle = (TextView)  itemView.findViewById(R.id.songTitle);
            this.genre = (TextView) itemView.findViewById(R.id.genre);
            this.artist = (TextView) itemView.findViewById(R.id.artist);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.constraintLayout);
        }
    }
}