package com.behindthemath.popularmoviesapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by BehindTheMath on 5/2/2016.
 */
public class MovieList {
    private static final String LOG_TAG = MovieList.class.getName();
    private static ArrayList<Movie> mMovieList = new ArrayList<>();
    private OnGetMoviesCompletedListener mOnGetMoviesCompletedListener;
    private static SortType mSortOrder = SortType.SORT_MOST_POPULAR;
    private Context mContext;

    public enum SortType {
        SORT_MOST_POPULAR, SORT_HIGHEST_RATED
    }

    //TODO: Replace with API Key
    private final static String apiKey = APIKey.getKey();

    interface OnGetMoviesCompletedListener {
        void onGetMoviesCompleted();
    }

    public void setOnGetMoviesCompletedListener(OnGetMoviesCompletedListener onGetMoviesCompletedListener){
        mOnGetMoviesCompletedListener = onGetMoviesCompletedListener;
    }

    public void setContext(Context context){
        mContext = context;
    }

    public void onSortChanged(SortType sortOrder) {
        mSortOrder = sortOrder;
        getMovies();
    }

    public boolean getMovies() {
        if (isOnline()) {
            HttpGet httpGet = new HttpGet(){
                @Override
                protected void onPostExecute(String responseJSON) {
                    Log.i(LOG_TAG, "OnPostExecute");
                    mMovieList = parseJSON(responseJSON);
                    if (mOnGetMoviesCompletedListener != null){ mOnGetMoviesCompletedListener.onGetMoviesCompleted(); }
                }
            };

            switch(mSortOrder) {
                case SORT_MOST_POPULAR:
                    //TODO: convert to URI/URL? https://stackoverflow.com/questions/19167954/use-uri-builder-in-android-or-create-url-with-variables
                    httpGet.execute("http://api.themoviedb.org/3/movie/popular?api_key=" + apiKey);
                    break;
                case SORT_HIGHEST_RATED:
                    httpGet.execute("http://api.themoviedb.org/3/movie/top_rated?api_key=" + apiKey);
                    break;
            }
            return true;
        }
        Toast.makeText(mContext, "No connection!", Toast.LENGTH_SHORT).show();
        return false;
    }

    private static boolean isOnline() {
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

    private static ArrayList<Movie> parseJSON(String responseJSON){
        JSONObject movieJson;

        try {
            JSONObject jsonObject = new JSONObject(responseJSON);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            ArrayList<Movie> movies = new ArrayList<>(jsonArray.length());
            // Process each result in json array, decode and convert to business object
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    movieJson = jsonArray.getJSONObject(i);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }

                Movie movie = Movie.fromJson(movieJson);
                if (movie != null) {
                    movies.add(movie);
                }
            }
            return movies;
        } catch (final JSONException e) {
            Log.e(LOG_TAG, e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Movie> getMovieList(){
        return mMovieList;
    }

    public static Movie getMovieAtPosition(int position){
        return mMovieList.get(position);
    }

    public boolean isInitialized(){
        return (mOnGetMoviesCompletedListener != null) && (mMovieList != null);
    }

    public static SortType getSortOrder() {
        return mSortOrder;
    }
}
