package com.android.popularmoviesapp.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.android.popularmoviesapp.data.MovieContract;
import com.android.popularmoviesapp.data.MovieData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.IDN;
import java.util.ArrayList;
import java.util.List;

public final class MovieDatabaseJsonUtils {

    final static String TAG = MovieDatabaseJsonUtils.class.getSimpleName();
    final static String RESULTS = "results";
    final static String ORIGINAL_TITLE = "original_title";
    final static String OVERVIEW = "overview";
    final static String RELEASE_DATE = "release_date";
    final static String POSTER_PATH = "poster_path";
    final static String VOTE_AVERAGE = "vote_average";
    final static String MOVIE_ID = "id";

    final static String MOVIE_TRAILER_ID = "id";
    final static String MOVIE_TRAILER_KEY = "key";
    final static String MOVIE_TRAILER_NAME = "name";
    final static String MOVIE_TRAILER_SITE = "site";
    final static String MOVIE_TRAILER_TYPE = "type";
    final static String MOVIE_TRAILER_COUNT = "count";

    final static String MOVIE_REVIEW_AUTHOR = "author";
    final static String MOVIE_REVIEW_CONTENT = "content";
    final static String MOVIE_REVIEW_URL = "url";

    //public static List<String> trailer_Keys = new ArrayList<String>();


    public static ArrayList<MovieData> getMovieData(String json) throws JSONException {


        JSONObject movieData = new JSONObject(json);
        JSONArray results = movieData.getJSONArray(RESULTS);

        ArrayList<MovieData> moviesArray = new ArrayList<MovieData>();

        for(int i = 0; i < results.length(); i++){
            JSONObject currentMovie = results.getJSONObject(i);
            MovieData movieSpecifics = new MovieData(
                    currentMovie.getString(ORIGINAL_TITLE),
                    currentMovie.getString(POSTER_PATH),
                    currentMovie.getString(VOTE_AVERAGE),
                    currentMovie.getString(RELEASE_DATE),
                    currentMovie.getString(OVERVIEW)
                    );
            moviesArray.add(movieSpecifics);
            //Log.v(TAG, moviesArray.get(i).getPoster_path());
        }

        return moviesArray;
    }

    public static ContentValues getContentValueReviewData (Context context, String movieJsonStr)
            throws JSONException {

        JSONObject reviewData = new JSONObject(movieJsonStr);

        // check for an error
        //if (movieData.has())

        JSONArray results = reviewData.getJSONArray(RESULTS);
        //Log.d(TAG, results.getJSONObject(0).getString("key"));
        //List<String> trailerKeys = new ArrayList<String>();


        ContentValues reviewContentValues = new ContentValues();
        //results.length gets the amount of reviews....
        for(int i = 0; i < results.length(); i++){

            String author;
            String content;
            String url;

            // get current JSON object
             JSONObject currentReview = results.getJSONObject(i);

            //extract movie details from current JSON object
            author = currentReview.getString(MOVIE_REVIEW_AUTHOR);
            content = currentReview.getString(MOVIE_REVIEW_CONTENT);
            url = currentReview.getString(MOVIE_REVIEW_URL);

            Log.v(TAG, author);
            ContentValues reviewSpecifics = new ContentValues();
            reviewSpecifics.put(MovieContract.MovieEntry.COLUMN_REVIEW_AUTHOR, author);
            reviewSpecifics.put(MovieContract.MovieEntry.COLUMN_REVIEW_CONTENT, content);
            reviewSpecifics.put(MovieContract.MovieEntry.COLUMN_REVIEW_URL, url);

            reviewContentValues = reviewSpecifics;
        }

        return reviewContentValues;
    }

    public static ArrayList<MovieData> getMovieTrailerData(String json) throws JSONException {


        JSONObject movieTrailerData = new JSONObject(json);
        JSONArray results = movieTrailerData.getJSONArray(RESULTS);

        ArrayList<MovieData> movieTrailerArray = new ArrayList<MovieData>();

        for(int i = 0; i < results.length(); i++){
            JSONObject currentMovie = results.getJSONObject(i);
            MovieData trailerData = new MovieData(
                    currentMovie.getString(ORIGINAL_TITLE),
                    currentMovie.getString(POSTER_PATH),
                    currentMovie.getString(VOTE_AVERAGE),
                    currentMovie.getString(RELEASE_DATE),
                    currentMovie.getString(OVERVIEW)
            );
            movieTrailerArray.add(trailerData);
            //Log.v(TAG, moviesArray.get(i).getPoster_path());
        }

        return movieTrailerArray;
    }

    public static ContentValues getContentValueTrailerData (Context context, String trailerJsonStr)
            throws JSONException {

        JSONObject trailerData = new JSONObject(trailerJsonStr);

        // check for an error
        //if (movieData.has())

        JSONArray results = trailerData.getJSONArray(RESULTS);
        //Log.d(TAG, results.getJSONObject(0).getString("key"));
        ArrayList<String> trailerKeys = new ArrayList<String>();


        ContentValues trailerContentValues = new ContentValues();

        for(int i = 0; i < results.length(); i++){

            String name;
            String site;
            String type;
            String key;
            String id;

            // get current JSON object
            JSONObject currentTrailer = results.getJSONObject(i);

            //extract movie details from current JSON object
            name = currentTrailer.getString(MOVIE_TRAILER_NAME);
            site = currentTrailer.getString(MOVIE_TRAILER_SITE);
            key = currentTrailer.getString(MOVIE_TRAILER_KEY);
            trailerKeys.add(key);
            Log.d(TAG, trailerKeys.get(i));
            id = currentTrailer.getString(MOVIE_TRAILER_ID);
            type = currentTrailer.getString(MOVIE_TRAILER_TYPE);

            Log.v(TAG, type);

            if(type.equals("Trailer")) {
                ContentValues trailerSpecifics = new ContentValues();
                trailerSpecifics.put(MovieContract.MovieEntry.COLUMN_TRAILER_NAME, name);
                trailerSpecifics.put(MovieContract.MovieEntry.COLUMN_TRAILER_KEY, key);
                trailerSpecifics.put(MovieContract.MovieEntry.COLUMN_TRAILER_SITE, site);
                trailerSpecifics.put(MovieContract.MovieEntry.COLUMN_TRAILER_ID, id);
                trailerSpecifics.put(MovieContract.MovieEntry.COLUMN_TRAILER_TYPE, type);

                trailerContentValues = trailerSpecifics;
            }
        }

        return trailerContentValues;
    }

    public static ContentValues[] getContentValueMovieData (Context context, String movieJsonStr)
            throws JSONException {

        JSONObject moviesData
                = new JSONObject(movieJsonStr);

        // check for an error
        //if (movieData.has())

        JSONArray results = moviesData.getJSONArray(RESULTS);
        ContentValues[] movieContentValues = new ContentValues[results.length()];

        for(int i = 0; i < results.length(); i++){

            String original_title;
            String posterPath;
            String overview;
            String voteAverage;
            String releaseDate;
            String id;

            // get current JSON object
            JSONObject currentMovie = results.getJSONObject(i);

            //extract movie details from current JSON object
            original_title = currentMovie.getString(ORIGINAL_TITLE);
            posterPath = currentMovie.getString(POSTER_PATH);
            overview = currentMovie.getString(OVERVIEW);
            voteAverage = currentMovie.getString(VOTE_AVERAGE);
            releaseDate = currentMovie.getString(RELEASE_DATE);
            id = currentMovie.getString(MOVIE_ID);

            ContentValues movieSpecifics = new ContentValues();
            movieSpecifics.put(MovieContract.MovieEntry.COLUMN_TITLE, original_title);
            movieSpecifics.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
            movieSpecifics.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
            movieSpecifics.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            movieSpecifics.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
            movieSpecifics.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);

            movieContentValues[i] = movieSpecifics;
        }

        return movieContentValues;
    }
}
