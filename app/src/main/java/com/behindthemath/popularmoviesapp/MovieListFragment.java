package com.behindthemath.popularmoviesapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by aryeh on 3/21/2016.
 */
public class MovieListFragment extends Fragment {
    private OnMovieSelectedListener mListener;
    private RecyclerView mRecyclerView;
    private MovieRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "MovieListFragment";
    private ArrayList<Movie> mMovieList;
    //private boolean mOnPostExecute;

    //TODO: Replace with API Key
    private final String apiKey = APIKey.getKey();


    public MovieListFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.
        getMovies();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        int scrollPosition = 0;

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((GridLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);

        mAdapter = new MovieRecyclerViewAdapter(getContext(), mMovieList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new MovieRecyclerViewAdapter.MovieClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Movie movie = mAdapter.getItem(position);
                mListener.onMovieSelected(movie);

            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (OnMovieSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnMovieSelectedListener");
        }
    }

    public interface OnMovieSelectedListener {
        public void onMovieSelected(Movie movie);
    }

    private void getMovies() {
        if (isOnline()) {
            HttpGet httpGet = new HttpGet(){
                @Override
                protected void onPostExecute(String response) {
                    // This method is executed in the UIThread
                    // with access to the result of the long running task
                    // DO SOMETHING WITH STRING RESPONSE
                    try {
                        //Log.i(LOG_TAG, "onPostExecute()");
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        mMovieList = Movie.parseJsonArray(jsonArray);
                    } catch (final JSONException e) { e.printStackTrace(); }
                }
            };
            httpGet.execute("http://api.themoviedb.org/3/movie/popular?api_key=" + apiKey);
        }
    }

    public boolean isOnline() {
        //TODO: https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-timeouts/27312494#27312494
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
          catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }
}
