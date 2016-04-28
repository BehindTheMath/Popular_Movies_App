package com.behindthemath.popularmoviesapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by BehindTheMath on 3/21/2016.
 */
@Parcel
public class Movie {
    Integer id;
    String originalTitle;
    String thumbnailPath;
    String overview;
    Double voteAverage;
    String releaseYear;

    public Movie(){}

    public Movie(Integer id, String originalTitle, String thumbnailPath, String overview, Double voteAverage, String releaseYear) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.thumbnailPath = thumbnailPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseYear = releaseYear;
    }

    public Integer getId() { return id; }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public String getOverview() {
        return overview;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public static Movie fromJson(JSONObject jsonObject) {
        Movie movie = new Movie();
        // Deserialize json into object fields
        try {
            movie.id = jsonObject.getInt("id");
            movie.originalTitle = jsonObject.getString("original_title");
            movie.thumbnailPath = "http://image.tmdb.org/t/p/w185" + jsonObject.getString("poster_path");
            movie.overview = jsonObject.getString("overview");
            movie.voteAverage = jsonObject.getDouble("vote_average");

            movie.releaseYear = jsonObject.getString("release_date").substring(0, 4);
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
