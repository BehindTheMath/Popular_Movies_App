package io.behindthemath.popularmoviesapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lb.auto_fit_textview.AutoResizeTextView;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by BehindTheMath on 3/21/2016.
 */
public class MovieDetailsFragment extends Fragment {
    private static final String ARGUMENT_MESSAGE = "message";
    @BindView(R.id.original_title) AutoResizeTextView originalTitle;
    @BindView(R.id.thumbnail) ImageView thumbnail;
    @BindView(R.id.release_year) TextView releaseYear;
    @BindView(R.id.vote_average) TextView voteAverage;
    @BindView(R.id.overview) TextView overview;
    private Unbinder unbinder;
    private Movie mMovie;
    private boolean mReadyForInitialization = false;
    private final static String LOG_TAG = MovieDetailsFragment.class.getName();

    public MovieDetailsFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreateView");
        final View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (savedInstanceState == null) {
            final Bundle arguments = getArguments();
            if (arguments != null) {
                mMovie = Parcels.unwrap(arguments.getParcelable(ARGUMENT_MESSAGE));
                load();
            }
        } else {
            if (mMovie == null) {
                mMovie = Parcels.unwrap(savedInstanceState.getParcelable("mMovie"));
                load();
            }
        }

        if (mReadyForInitialization){
            load();
        } else {
            mReadyForInitialization = true;
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("mMovie", Parcels.wrap(mMovie));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setMovie(Movie movie, boolean reSorted){
        mMovie = movie;
        if (mReadyForInitialization || reSorted) {
            load();
        } else {
            mReadyForInitialization = true;
        }

    }

    private void load(){
        originalTitle.setText(mMovie.getOriginalTitle());
        originalTitle.setLines(1);

        Picasso.with(getContext()).load(mMovie.getThumbnailPath()).into(thumbnail);

        releaseYear.setText(mMovie.getReleaseYear());

        voteAverage.setText(mMovie.getVoteAverage().toString().concat("/10"));
        overview.setText(mMovie.getOverview());
    }

    public static MovieDetailsFragment newInstance(Movie movie) {
        final Bundle args = new Bundle();
        args.putParcelable(ARGUMENT_MESSAGE, Parcels.wrap(movie));
        final MovieDetailsFragment fragment = new MovieDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
