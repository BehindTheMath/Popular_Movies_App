package com.behindthemath.popularmoviesapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieListFragment.MoviesLoadedListener{
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.frame) FrameLayout frameLayout;
    @Nullable @BindView(R.id.linear_layout) LinearLayout linearLayout;
    @Nullable @BindView(R.id.frame2) FrameLayout frameLayout2;
    private MovieListFragment fragment = new MovieListFragment();
    private boolean mDualPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (savedInstanceState == null) {
            MovieListFragment movieListFragment = new MovieListFragment();
            movieListFragment.setRetainInstance(true);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, movieListFragment)
                    .commit();
            movieListFragment.setMoviesLoadedListener(this);

            mDualPane = ((frameLayout2 != null) && (frameLayout2.getVisibility() == View.VISIBLE));
        }
    }

    public void onMoviesLoaded(){
        if (frameLayout2 != null) {
            MovieDetailsFragment movieDetailsFragment = MovieDetailsFragment.newInstance(fragment.getFirstMovie());
            movieDetailsFragment.setRetainInstance(true);
            getSupportFragmentManager().beginTransaction()
                    .replace(frameLayout2.getId(), movieDetailsFragment, MovieDetailsFragment.class.getName())
                    .commit();
        }
    }

    public boolean isDualPane() {
        return mDualPane;
    }
}