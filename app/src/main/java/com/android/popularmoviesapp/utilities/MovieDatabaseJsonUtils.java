package com.android.popularmoviesapp.utilities;

import android.util.Log;

import com.android.popularmoviesapp.data.MovieData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class MovieDatabaseJsonUtils {

    final static String TAG = MovieDatabaseJsonUtils.class.getSimpleName();

    public static ArrayList<MovieData> getMovieData(String json) throws JSONException {
        final String RESULTS = "results";
        final String ORIGINAL_TITLE = "original_title";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String POSTER_PATH = "poster_path";
        final String VOTE_AVERAGE = "vote_average";

        JSONObject movieData = new JSONObject(json);
        JSONArray results = movieData.getJSONArray(RESULTS);

        ArrayList<MovieData> moviesArray = new ArrayList<MovieData>();

        for(int i = 0; i < results.length(); i++){
            JSONObject currentMovie = results.getJSONObject(i);
            MovieData movieSpecifics = new MovieData(
                    currentMovie.getString(ORIGINAL_TITLE),
                    currentMovie.getString(POSTER_PATH),
                    currentMovie.getString(OVERVIEW),
                    currentMovie.getString(VOTE_AVERAGE),
                    currentMovie.getString(RELEASE_DATE)
            );
            moviesArray.add(movieSpecifics);
            Log.v(TAG, moviesArray.get(i).getPoster_path());
        }

        return moviesArray;
    }
}
