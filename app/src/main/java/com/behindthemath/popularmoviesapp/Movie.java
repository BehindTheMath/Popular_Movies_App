package com.behindthemath.popularmoviesapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by aryeh on 3/21/2016.
 */
@Parcel
public class Movie {
    Integer id;
    String original_title;
    String poster_path;
    String overview;
    Double vote_average;
    Date release_date;

    public Integer getId() { return id; }

    public String getOriginal_title() {
        return original_title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public Double getVote_average() {
        return vote_average;
    }

    public Date getRelease_date() {
        return release_date;
    }

    public static Movie fromJson(JSONObject jsonObject) {
        Movie movie = new Movie();
        // Deserialize json into object fields
        try {
            movie.id = jsonObject.getInt("id");
            movie.original_title = jsonObject.getString("original_title");
            movie.poster_path = "http://image.tmdb.org/t/p/w185" + jsonObject.getString("poster_path");
            movie.overview = jsonObject.getString("overview");
            movie.vote_average = jsonObject.getDouble("vote_average");

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                movie.release_date = simpleDateFormat.parse(jsonObject.getString("release_date"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return movie;
    }

    public static ArrayList<Movie> parseJsonArray(JSONArray jsonArray) {
        JSONObject movieJson;
        ArrayList<Movie> movies = new ArrayList<Movie>(jsonArray.length());
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
    }
}
