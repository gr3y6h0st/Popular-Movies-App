package com.android.popularmoviesapp.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.popularmoviesapp.MainActivity;
import com.android.popularmoviesapp.MoviesAdapter;
import com.android.popularmoviesapp.data.MovieContract;
import com.android.popularmoviesapp.data.MovieDbHelper;
import com.android.popularmoviesapp.utilities.MovieDatabaseJsonUtils;
import com.android.popularmoviesapp.utilities.NetworkUtils;

import java.net.URL;

import static com.android.popularmoviesapp.utilities.NetworkUtils.getMovieId;
import static com.android.popularmoviesapp.utilities.NetworkUtils.getResponseFromHttpUrl;

public class MovieInfo {

    public static void syncMovieInfo(Context context) {

        try{
            URL movieRequestUrl = NetworkUtils.buildUrl("temp");

            String jsonMovieDatabaseResponse =
                    getResponseFromHttpUrl(movieRequestUrl);

            ContentValues[] movieInfoValues = MovieDatabaseJsonUtils
                    .getContentValueMovieData(context, jsonMovieDatabaseResponse);

            if(movieInfoValues != null && movieInfoValues.length != 0){
                ContentResolver moviesContentResolver = context.getContentResolver();

                //delete any old information, then create/insert brand new data to sync.
                /*moviesContentResolver.delete(
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        null); */

                moviesContentResolver.bulkInsert(
                        MovieContract.MovieEntry.CONTENT_URI,
                        movieInfoValues);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public static void syncAsyncMovieInfo(Context context) {

        try{
            URL movieRequestUrl = NetworkUtils.buildUrl(context);

            String jsonMovieDatabaseResponse =
                    getResponseFromHttpUrl(movieRequestUrl);

            String[] movieInfoValues = MovieDatabaseJsonUtils
                    .getContentValueMovieData(context, jsonMovieDatabaseResponse);

            if(movieInfoValues != null && movieInfoValues.length != 0){
                ContentResolver moviesContentResolver = context.getContentResolver();

                //delete any old information, then create/insert brand new data to sync.
                /*moviesContentResolver.delete(
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        null);

                moviesContentResolver.bulkInsert(
                        MovieContract.MovieEntry.CONTENT_URI,
                        movieInfoValues);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    /*public static void syncMovieReviewInfo(Context context) {

        try{
            URL movieRequestUrl = NetworkUtils.reviewsBuildUrl(context);

            String jsonMovieDatabaseResponse =
                    getResponseFromHttpUrl(movieRequestUrl);

            ContentValues movieReviewValues = MovieDatabaseJsonUtils
                    .getContentValueReviewData(context, jsonMovieDatabaseResponse);

            if(movieReviewValues != null){
                ContentResolver moviesContentResolver = context.getContentResolver();

                //delete any old information, then create/insert brand new data to sync.
                moviesContentResolver.delete(
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        null);
                String[] sel = {NetworkUtils.getMovieId()};


                moviesContentResolver.update(
                        MovieContract.MovieEntry.CONTENT_URI,
                        movieReviewValues,
                        null,
                        sel);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static void update_Favorite_Movie(Context context){
    }

    public static void remove_Favorite_Movie(Context context){
        ContentResolver moviesContentResolver = context.getContentResolver();
        moviesContentResolver.delete(
                MovieContract.MovieEntry.CONTENT_URI.buildUpon()
                        .appendPath("favorite")
                        .appendPath(NetworkUtils.getMovieId())
                        .build(),
                null,
                null);
    }

}
