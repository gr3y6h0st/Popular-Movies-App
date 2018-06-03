package com.android.popularmoviesapp.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.android.popularmoviesapp.data.MovieContract;
import com.android.popularmoviesapp.utilities.MovieDatabaseJsonUtils;
import com.android.popularmoviesapp.utilities.NetworkUtils;

import java.net.URL;

import static com.android.popularmoviesapp.utilities.NetworkUtils.getMovieId;
import static com.android.popularmoviesapp.utilities.NetworkUtils.getResponseFromHttpUrl;

public class MovieInfo {

    public static void syncMovieInfo(Context context) {

        try{
            URL movieRequestUrl = NetworkUtils.buildUrl(context);

            String jsonMovieDatabaseResponse =
                    getResponseFromHttpUrl(movieRequestUrl);

            ContentValues[] movieInfoValues = MovieDatabaseJsonUtils
                    .getContentValueMovieData(context, jsonMovieDatabaseResponse);

            if(movieInfoValues != null && movieInfoValues.length != 0){
                ContentResolver moviesContentResolver = context.getContentResolver();

                //delete any old information, then create/insert brand new data to sync.
                moviesContentResolver.delete(
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
    }

    /* OBTAIN THE ID VALUE OF MOVIE TO CREATE MOVIE TRAILER URL
    */
    public static void syncMovieTrailerInfo(Context context) {

        try{

            URL movieTrailerRequestUrl = NetworkUtils.trailersBuildUrl(context);

            String jsonMovieDatabaseResponse =
                    getResponseFromHttpUrl(movieTrailerRequestUrl);

            ContentValues movieTrailerInfoValues = MovieDatabaseJsonUtils
                    .getContentValueTrailerData(context, jsonMovieDatabaseResponse);

            if(movieTrailerInfoValues != null){
                ContentResolver movieTrailersContentResolver = context.getContentResolver();
                //delete any old information, then create/insert brand new data to sync.
                /*movieTrailersContentResolver.delete(
                        MovieContract.MovieEntry.CONTENT_URI,
                        MovieContract.MovieEntry.COLUMN_TRAILER_TYPE + "LIKE ?",
                        selectionArgs);*/
                //UPDATE?
                Log.v("HEREEEEE: ", NetworkUtils.getMovieId());

                String[] sel = {NetworkUtils.getMovieId()};
                movieTrailersContentResolver.update(
                        MovieContract.MovieEntry.CONTENT_URI,
                        movieTrailerInfoValues,
                        null,
                        sel
                        //MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        //sel
                );



                /*for(ContentValues value: movieTrailerInfoValues){
                    //get each Content Value somehow...instead of updating multiple times
                    movieTrailersContentResolver.update(
                            MovieContract.MovieEntry.CONTENT_URI,
                            value,
                            null,
                            null
                    );
                    }*/
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
