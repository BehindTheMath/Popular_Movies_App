package com.behindthemath.popularmoviesapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

//import butterknife.Bind;
//import butterknife.ButterKnife;

/**
 * Created by aryeh on 3/21/2016.
 */
public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.MovieHolder> {
    private static String LOG_TAG = "MovieRecyclerViewAdapter";
    private ArrayList<Movie> movieList;
    private static MovieClickListener movieClickListener;
    protected View mItemView;
    protected RecyclerView mRecyclerView;
    protected int mWidth;

    public MovieRecyclerViewAdapter(ArrayList<Movie> movieList) {
        this.movieList = movieList;
    }

    public class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected @BindView(R.id.thumbnail_image_view) ImageView imageView;

        public MovieHolder(View itemView) {
            super(itemView);

            mItemView = itemView;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            movieClickListener.onItemClick(getLayoutPosition(), v);
        }

    }

    public void setOnItemClickListener(MovieClickListener movieClickListener) {
        MovieRecyclerViewAdapter.movieClickListener = movieClickListener;
    }

    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        if(mRecyclerView == null) {
            mRecyclerView = (RecyclerView) parent;
            mWidth = mRecyclerView.getWidth() / 2;
        }

        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieHolder holder, int position) {
        Movie movie = movieList.get(position);
        final String url = movie.getThumbnailPath();

        Picasso.with(mRecyclerView.getContext())
            .load(movie.getThumbnailPath())
            .error(R.drawable.ic_warning_black_24dp)
            //TODO: java.lang.IllegalArgumentException: At least one dimension has to be positive number.
            .resize(mWidth, 0)
            .into(holder.imageView);
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