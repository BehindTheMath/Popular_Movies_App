package com.behindthemath.popularmoviesapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

/**
 * Created by aryeh on 3/21/2016.
 */
public class MovieDetailsFragment extends Fragment {
    private static final String ARGUMENT_MESSAGE = "message";

    public MovieDetailsFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_movie_details, container, false);

        final Bundle args = getArguments();
        Movie movie = (Movie) Parcels.unwrap(args.getParcelable(ARGUMENT_MESSAGE));
        //movie = ((MainActivity) getActivity()).mSelectedMovie;

        TextView originalTitle = (TextView) view.findViewById(R.id.original_title);
        ImageView poster = (ImageView) view.findViewById(R.id.poster);
        TextView releaseDate = (TextView) view.findViewById(R.id.release_date);
        TextView voteAverage = (TextView) view.findViewById(R.id.vote_average);
        TextView overview = (TextView) view.findViewById(R.id.overview);

        originalTitle.setText(movie.getOriginal_title());
        Picasso.with(getContext()).load(movie.getPoster_path()).into(poster);
        //holder.poster.setText(movie.getPoster_path());
        releaseDate.setText(movie.getRelease_date().toString());
        voteAverage.setText(movie.getVote_average().toString());
        overview.setText(movie.getOverview());

        return view;
    }

    public void onAttach(Context context){
        //Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        super.onAttach(context);
    }

    public static MovieDetailsFragment newInstance(Movie movie) {
        final Bundle args = new Bundle();
        args.putParcelable(ARGUMENT_MESSAGE, Parcels.wrap(movie));
        final MovieDetailsFragment fragment = new MovieDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
