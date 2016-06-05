package io.behindthemath.popularmoviesapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieList.OnGetMoviesCompletedListener, MovieRecyclerViewAdapter.ItemClickListener, SortDialogFragment.OnSortChangedListener {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.frame_layout_list) FrameLayout listFrameLayout;
    @Nullable @BindView(R.id.frame_layout_details) FrameLayout detailsFrameLayout;
    private final static String LOG_TAG = MainActivity.class.getName();
    private MovieListFragment mMovieListFragment;
    private MovieDetailsFragment mMovieDetailsFragment;
    private MovieList mMovieList = new MovieList();
    private String mLastFragment;
    private static boolean mDualPane;
    private int mOrientation = Configuration.ORIENTATION_UNDEFINED;
    private boolean mRotated = false;
    private int mCurrentPosition = 0;
    private boolean mReSorted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        mDualPane = ((detailsFrameLayout != null) && (detailsFrameLayout.getVisibility() == View.VISIBLE));
        int currentOrientation = getResources().getConfiguration().orientation;

        mMovieListFragment = (MovieListFragment) getSupportFragmentManager().findFragmentByTag(MovieListFragment.class.getName());
        if (mDualPane) { mMovieDetailsFragment = (MovieDetailsFragment) getSupportFragmentManager().findFragmentByTag(MovieDetailsFragment.class.getName()); }

        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt("mCurrentPosition");
            mLastFragment = savedInstanceState.getString("mLastFragment");
            mOrientation = savedInstanceState.getInt("mOrientation", currentOrientation);
            if (mOrientation != currentOrientation) {
                Log.i(LOG_TAG, "Rotated");
                mRotated = true;
                mOrientation = currentOrientation;
            }

            if (mDualPane) {
                if (mMovieDetailsFragment == null) {
                    mMovieDetailsFragment = MovieDetailsFragment.newInstance(MovieList.getMovieAtPosition(mCurrentPosition));
                    getSupportFragmentManager().beginTransaction()
                            .replace(detailsFrameLayout.getId(), mMovieDetailsFragment, MovieDetailsFragment.class.getName())
                            .commit();
                } else {
                    if (mRotated && mLastFragment.equals(MovieDetailsFragment.class.getName())) {
                        //int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
                        //if ((backStackEntryCount != 0) && (getSupportFragmentManager().getBackStackEntryAt(backStackEntryCount - 1).getName().equals(MovieDetailsFragment.class.getName()))) {
                        //if (mLastFragment.equals(MovieDetailsFragment.class.getName())) {
                            getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            getSupportFragmentManager().beginTransaction()
                                    .remove(mMovieDetailsFragment)
                                    .commit();
                            getSupportFragmentManager().executePendingTransactions();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frame_layout_list, mMovieListFragment, MovieListFragment.class.getName())
                                    .commit();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(detailsFrameLayout.getId(), mMovieDetailsFragment, MovieDetailsFragment.class.getName())
                                    .commit();
                        //}
                    }
                }
            }
        } else {
            mLastFragment = MovieListFragment.class.getName();
            if (mMovieListFragment == null) {
                mMovieListFragment = new MovieListFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_layout_list, mMovieListFragment, MovieListFragment.class.getName())
                        .commit();
            }
            if (mDualPane && mMovieDetailsFragment == null) {
                mMovieDetailsFragment = new MovieDetailsFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(detailsFrameLayout.getId(), mMovieDetailsFragment, MovieDetailsFragment.class.getName())
                        .commit();
            }
        }

        if (!(mMovieList.isInitialized())) {
            mMovieList.setOnGetMoviesCompletedListener(this);
            mMovieList.setContext(this);
            if (!(mMovieList.getMovies())) {
                finish();
            }
        } else {
            onGetMoviesCompleted();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mOrientation", mOrientation);
        outState.putInt("mCurrentPosition", mCurrentPosition);
        outState.putString("mLastFragment", mLastFragment);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Log.i(LOG_TAG, "onBackPressed");
        if (!mDualPane){
            getSupportFragmentManager().popBackStack();
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            mLastFragment = MovieListFragment.class.getName();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onGetMoviesCompleted(){
        Log.i(LOG_TAG, "onGetMoviesCompleted");
        mMovieListFragment.onGetMoviesCompleted(mReSorted);
        mReSorted = false;

        if (mDualPane){ mMovieDetailsFragment.setMovie(MovieList.getMovieAtPosition(mCurrentPosition), mReSorted); }
    }

    public void onItemClick(Movie movie, int position) {
        if(mDualPane){
            if (mMovieDetailsFragment == null) { mMovieDetailsFragment = (MovieDetailsFragment) getSupportFragmentManager().findFragmentByTag("movieDetailsDualPaneFragment"); }
            mMovieDetailsFragment.setMovie(movie, false);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mCurrentPosition = position;
            mLastFragment = MovieDetailsFragment.class.getName();
            mMovieDetailsFragment = MovieDetailsFragment.newInstance(movie);
            getSupportFragmentManager().beginTransaction()
                    .hide(mMovieListFragment)
                    .add(R.id.frame_layout_list, mMovieDetailsFragment, MovieDetailsFragment.class.getName())
                    .addToBackStack(MovieDetailsFragment.class.getName())
                    .commit();
        }
    }

    public void onSortChanged(MovieList.SortType sortOrder){
        mReSorted = true;
        mCurrentPosition = 0;
        mMovieList.onSortChanged(sortOrder);
    }

    public MovieList getMovieList() {
        return mMovieList;
    }
}