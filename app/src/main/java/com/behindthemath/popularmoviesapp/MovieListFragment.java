package com.behindthemath.popularmoviesapp;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by aryeh on 3/21/2016.
 */
public class MovieListFragment extends Fragment /*implements SortDialogFragment.OnSortSelectedListener*/ {
    private RecyclerView mRecyclerView;
    private MovieRecyclerViewAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private final String LOG_TAG = getClass().toString();
    private ArrayList<Movie> mMovieList;
    public static SortType mSortOrder = SortType.SORT_MOST_POPULAR;
    private static final String BUNDLE_RECYCLER_STATE = "MovieListFragment.mRecyclerView.state";
    private int index = -1;
    private int top = -1;
    //TODO: https://github.com/codepath/android_guides/wiki/Implementing-Pull-to-Refresh-Guide#recyclerview-with-swiperefreshlayout
    private SwipeRefreshLayout swipeRefreshLayout;

    //TODO: Replace with API Key
    private final String apiKey = APIKey.getKey();

    public enum SortType {
        SORT_MOST_POPULAR, SORT_HIGHEST_RATED
    }

    public MovieListFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        setHasOptionsMenu(true);

        // Lookup the swipe container view
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        // Setup refresh listener which triggers new data loading
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeRefreshLayout.setRefreshing(false)
                // once the network request has completed successfully.
                getMovies();
            }
        });
        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(LOG_TAG, "onResume");
        getMovies();
        //set recyclerview position
        if(index != RecyclerView.NO_POSITION) {
            mLayoutManager.scrollToPositionWithOffset(index, top);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //read current recyclerview position
        index = mLayoutManager.findFirstVisibleItemPosition();
        View view = mRecyclerView.getChildAt(0);
        top = (view == null) ? 0 : (view.getTop() - mRecyclerView.getPaddingTop());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (swipeRefreshLayout != null) {
            outState.putParcelable(BUNDLE_RECYCLER_STATE, mRecyclerView.getLayoutManager().onSaveInstanceState());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null)
        {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_STATE);
            if (mRecyclerView != null) {
                mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.sort) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            SortDialogFragment sortDialogFragment = new SortDialogFragment();
            sortDialogFragment.setTargetFragment(this, 0);
            sortDialogFragment.show(fragmentManager, "fragment_sort_dialog");

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void OnSortSelected(SortType sortOrder) {
        if(!(sortOrder.equals(mSortOrder))) {
            mSortOrder = sortOrder;
            getMovies();
        }
    }

    public void onMovieSelected(Movie movie){
        final MovieDetailsFragment fragment = MovieDetailsFragment.newInstance(movie);
        fragment.setRetainInstance(true);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void getMovies() {
        if (isOnline()) {
            HttpGet httpGet = new HttpGet(){
                @Override
                protected void onPostExecute(ArrayList<Movie> movieList) {
                    Log.i(LOG_TAG, "OnPostExecute");
                    swipeRefreshLayout.setRefreshing(false);
                    mMovieList = movieList;
                    initializeRecyclerView();
                }
            };

            switch(mSortOrder) {
                case SORT_MOST_POPULAR:
                httpGet.execute("http://api.themoviedb.org/3/movie/popular?api_key=" + apiKey);
                    break;
                case SORT_HIGHEST_RATED:
                httpGet.execute("http://api.themoviedb.org/3/movie/top_rated?api_key=" + apiKey);
                    break;
            }
        } else {
            Toast.makeText(getActivity(), "No connection!", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    private boolean isOnline() {
        //TODO: https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-timeouts/27312494#27312494
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          {
            Log.i(LOG_TAG, "IOException");
            e.printStackTrace(); }
          catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    private void initializeRecyclerView() {
        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        //TODO: add logic for various widths based on screen size
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MovieRecyclerViewAdapter(mMovieList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new MovieRecyclerViewAdapter.MovieClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Movie movie = mAdapter.getItem(position);
                onMovieSelected(movie);
            }
        });

    }
}
