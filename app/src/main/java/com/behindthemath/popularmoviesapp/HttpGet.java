package com.behindthemath.popularmoviesapp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by BehindTheMath on 3/21/2016.
 */
class HttpGet extends AsyncTask<String, Void, ArrayList<Movie>> {

    @Override
    protected ArrayList<Movie> doInBackground(String... params) {
        //TODO: https://gist.github.com/udacityandroid/d6a7bb21904046a91695
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String LOG_TAG = "HttpGet";
        ArrayList<Movie> movieList;

        // Will contain the raw JSON response as a string.
        String responseJSON;

        //Log.i(LOG_TAG, "doInBackground()");

        try {
            // Construct the URL
            URL url = new URL(params[0]);

            // Create the request, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            responseJSON = buffer.toString();
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }

        //responseJSON = APIKey.getJSON();
        //return responseJSON;
        try {
            //Log.i(LOG_TAG, "onPostExecute()");
            JSONObject jsonObject = new JSONObject(responseJSON);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            movieList = Movie.parseJsonArray(jsonArray);
            return movieList;
        } catch (final JSONException e) {
            Log.e(LOG_TAG, e.toString());
            e.printStackTrace(); }
        return null;
    }
}