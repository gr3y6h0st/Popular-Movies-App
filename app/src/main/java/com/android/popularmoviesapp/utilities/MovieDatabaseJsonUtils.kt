package com.android.popularmoviesapp.utilities

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.android.popularmoviesapp.data.MovieContract
import com.android.popularmoviesapp.data.MovieData
import com.android.popularmoviesapp.data.MovieDbHelper
import org.json.JSONException
import org.json.JSONObject
import java.util.*

object MovieDatabaseJsonUtils {

    private val TAG = MovieDatabaseJsonUtils::class.java.simpleName
    private const val RESULTS = "results"
    private const val ORIGINAL_TITLE = "original_title"
    private const val OVERVIEW = "overview"
    private const val RELEASE_DATE = "release_date"
    private const val POSTER_PATH = "poster_path"
    private const val BACKDROP_PATH = "backdrop_path"
    private const val VOTE_AVERAGE = "vote_average"
    private const val MOVIE_ID = "id"
    private const val POPULARITY = "popularity"

    private val MOVIE_TRAILER_NAME = "name"
    private val MOVIE_TRAILER_ID = "id"
    private val MOVIE_TRAILER_KEY = "key"
    private val MOVIE_TRAILER_SITE = "site"
    private val MOVIE_TRAILER_TYPE = "type"
    private val MOVIE_TRAILER_COUNT = "count"

    private val MOVIE_REVIEW_AUTHOR = "author"
    private val MOVIE_REVIEW_CONTENT = "content"
    private val MOVIE_REVIEW_URL = "url"

    //public static List<String> trailer_Keys = new ArrayList<String>();


    /*public static ContentValues getContentValueReviewData (Context context, String movieJsonStr)
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
    }*/

    /*public static ContentValues getContentValueTrailerData (Context context, String trailerJsonStr)
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
    }*/

    fun getFavoriteContentValueData(context: Context, data: MovieData, checkFavBool: Boolean) {
        val mDb: SQLiteDatabase
        val movieDbHelper = MovieDbHelper(context)


        val originalTitle = data.original_title
        val posterPath = data.poster_path
        val backdropPath = data.backdrop_path
        val overview = data.overview
        val voteAverage = data.vote_average
        val releaseDate = data.release_date
        val id = data.movie_id
        val favBool = java.lang.Boolean.toString(checkFavBool)
        Log.d(TAG, favBool)


        val favoriteData = ContentValues()
        favoriteData.put(MovieContract.MovieEntry.COLUMN_TITLE, originalTitle)
        favoriteData.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath)
        favoriteData.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, backdropPath)
        favoriteData.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview)
        favoriteData.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage)
        favoriteData.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate)
        favoriteData.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id)
        favoriteData.put(MovieContract.MovieEntry.COLUMN_FAVORITE_BOOL, favBool)

        mDb = movieDbHelper.writableDatabase
        val rowCount = mDb.insert(MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN, null, favoriteData)
    }

    @Throws(JSONException::class)
    fun getContentValueMovieData(context: Context, movieJsonStr: String): Array<ContentValues?> {

        val moviesData = JSONObject(movieJsonStr)

        // check for an error
        //if (movieData.has())

        val results = moviesData.getJSONArray(RESULTS)
        val movieContentValues = arrayOfNulls<ContentValues>(results.length())

        for (i in 0 until results.length()) {

            val originalTitle: String
            val posterPath: String
            val backdropPath: String
            val overview: String
            val voteAverage: String
            val releaseDate: String
            val id: String
            // get current JSON object
            val currentMovie = results.getJSONObject(i)

            //extract movie details from current JSON object
            originalTitle = currentMovie.getString(ORIGINAL_TITLE)
            posterPath = currentMovie.getString(POSTER_PATH)
            backdropPath = currentMovie.getString(BACKDROP_PATH)
            overview = currentMovie.getString(OVERVIEW)
            voteAverage = currentMovie.getString(VOTE_AVERAGE)
            releaseDate = currentMovie.getString(RELEASE_DATE)
            id = currentMovie.getString(MOVIE_ID)

            val movieSpecifics = ContentValues()
            movieSpecifics.put(MovieContract.MovieEntry.COLUMN_TITLE, originalTitle)
            movieSpecifics.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath)
            movieSpecifics.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, backdropPath)
            movieSpecifics.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage)
            movieSpecifics.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate)
            movieSpecifics.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview)
            movieSpecifics.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id)

            movieContentValues[i] = movieSpecifics
        }

        return movieContentValues
    }

    @Throws(JSONException::class)
    fun getMovieData(json: String): ArrayList<MovieData> {
        val movieData = JSONObject(json)
        val results = movieData.getJSONArray(RESULTS)
        val moviesArray = ArrayList<MovieData>()
        for (i in 0 until results.length()) {
            val currentMovie = results.getJSONObject(i)
            val movieSpecifics = MovieData(
                    currentMovie.getString(MOVIE_ID),
                    currentMovie.getString(ORIGINAL_TITLE),
                    currentMovie.getString(POSTER_PATH),
                    currentMovie.getString(BACKDROP_PATH),
                    currentMovie.getString(VOTE_AVERAGE),
                    currentMovie.getString(RELEASE_DATE),
                    currentMovie.getString(OVERVIEW)

            )
            moviesArray.add(movieSpecifics)
            //Log.v(TAG, movieArray.get(i).getPoster_path());
        }
        return moviesArray
    }

    @Throws(JSONException::class)
    fun getMovieTrailerData(json: String): ArrayList<MovieData> {
        val movieTrailerData = JSONObject(json)
        val results = movieTrailerData.getJSONArray(RESULTS)
        val movieTrailerArray = ArrayList<MovieData>()
        for (i in 0 until results.length()) {
            val currentMovie = results.getJSONObject(i)
            val trailerData = MovieData(
                    currentMovie.getString(MOVIE_TRAILER_NAME),
                    currentMovie.getString(MOVIE_TRAILER_ID),
                    currentMovie.getString(MOVIE_TRAILER_KEY),
                    currentMovie.getString(MOVIE_TRAILER_SITE),
                    currentMovie.getString(MOVIE_TRAILER_TYPE)
            )
            movieTrailerArray.add(trailerData)
            Log.v(TAG, movieTrailerArray[i].trailer_name)
        }
        return movieTrailerArray
    }

    @Throws(JSONException::class)
    fun getMovieReviewData(json: String): ArrayList<MovieData> {
        val movieReviewData = JSONObject(json)
        val results = movieReviewData.getJSONArray(RESULTS)
        val movieReviewArray = ArrayList<MovieData>()
        for (i in 0 until results.length()) {
            val currentMovie = results.getJSONObject(i)
            val reviewData = MovieData(
                    currentMovie.getString(MOVIE_REVIEW_AUTHOR),
                    currentMovie.getString(MOVIE_REVIEW_CONTENT),
                    currentMovie.getString(MOVIE_REVIEW_URL)
            )
            movieReviewArray.add(reviewData)
            Log.v(TAG, movieReviewArray[i].review_author)
        }
        return movieReviewArray
    }
}
