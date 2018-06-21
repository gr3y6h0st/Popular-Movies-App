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
            URL movieRequestUrl = NetworkUtils.buildUrl(context);

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

    public static void queryTopRatedMovieInfo(Context context){
        //currently using MOVIE LOADER CONTENTURI

        /*MovieDbHelper movieDbHelper = new MovieDbHelper(context);
        final SQLiteDatabase mdb = movieDbHelper.getReadableDatabase();*/


        ContentResolver movieContentResolver = context.getContentResolver();
        movieContentResolver.query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);

        /*Cursor cursor = mdb.query(MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN,
                null,
                null,
                null,
                null,
                null,
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC", "10");*/


        //cursor.setNotificationUri(movieContentResolver, MovieContract.MovieEntry.CONTENT_URI);
        movieContentResolver.notifyChange(MovieContract.MovieEntry.CONTENT_URI, null);
        //cursor.close();
    }

    public static void queryPopularMovieInfo(Context context){
        //CONTENT POPULAR URI

        /*MovieDbHelper movieDbHelper = new MovieDbHelper(context);
        final SQLiteDatabase mdb = movieDbHelper.getReadableDatabase();*/

        ContentResolver movieContentResolver = context.getContentResolver();
        movieContentResolver.query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE);

        /*Cursor cursor = mdb.query(MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN,
                null,
                null,
                null,
                null,
                null,
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC", "2");


        cursor.setNotificationUri(movieContentResolver, MovieContract.MovieEntry.CONTENT_URI);*/
        movieContentResolver.notifyChange(MovieContract.MovieEntry.CONTENT_URI, null);

        //cursor.close();


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

    public static void syncMovieReviewInfo(Context context) {

        try{
            URL movieRequestUrl = NetworkUtils.reviewsBuildUrl(context);

            String jsonMovieDatabaseResponse =
                    getResponseFromHttpUrl(movieRequestUrl);

            ContentValues movieReviewValues = MovieDatabaseJsonUtils
                    .getContentValueReviewData(context, jsonMovieDatabaseResponse);

            if(movieReviewValues != null){
                ContentResolver moviesContentResolver = context.getContentResolver();

                //delete any old information, then create/insert brand new data to sync.
                /*moviesContentResolver.delete(
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        null);*/
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
    }

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
