package com.behindthemath.popularmoviesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by aryeh on 3/21/2016.
 */
public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.MovieHolder> {
    private static String LOG_TAG = "MovieRecyclerViewAdapter";
    private ArrayList<Movie> movieList;
    //private LayoutInflater inflater;
    private static MovieClickListener movieClickListener;
    private Context mContext;

    public MovieRecyclerViewAdapter(Context context, ArrayList<Movie> movieList) {
        //inflater = LayoutInflater.from(context);
        this.movieList = movieList;
        mContext = context;
    }

    public class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;

        public MovieHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            movieClickListener.onItemClick(getPosition(), v);
        }

    }

    public void setOnItemClickListener(MovieClickListener movieClickListener) {
        this.movieClickListener = movieClickListener;
    }

    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);

        MovieHolder movieHolder = new MovieHolder(view);
        return movieHolder;
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        Movie movie = movieList.get(position);

        Picasso.with(mContext)
                .load(movie.getPoster_path())
                .fit()
                .into(holder.imageView);
    }

    public void addItem(Movie movie, int index) {
        movieList.add(movie);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        movieList.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public interface MovieClickListener {
        void onItemClick(int position, View v);
    }

    public Movie getItem(int position) {
        return movieList.get(position);
    }

}