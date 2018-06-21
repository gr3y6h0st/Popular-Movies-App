package com.android.popularmoviesapp.sync;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.android.popularmoviesapp.data.MovieContract;
import com.android.popularmoviesapp.utilities.MovieDatabaseJsonUtils;
import com.android.popularmoviesapp.utilities.NetworkUtils;

public class FavoritesMovieIntentService extends IntentService {

    public FavoritesMovieIntentService() {
        super("FavoriteMovieIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            MovieInfo.update_Favorite_Movie(this);

            String movieTitle = intent.getStringExtra("movieTitle");

            String moviePoster = intent.getStringExtra("moviePoster");

            String movieBackdrop = intent.getStringExtra("movieBackdrop");

            String movieOverview = intent.getStringExtra("movieOverview");

            String movieReleaseDate = intent.getStringExtra("movieReleaseDate");

            String movieRating = intent.getStringExtra("movieRating");

            String movie_ID = intent.getStringExtra("movie_ID");

            String trailer_type = intent.getStringExtra("trailer_type");

            String movieTrailerName = intent.getStringExtra("movieTrailerName");

            String movieReviewAuthor = intent.getStringExtra("movieReviewAuthor");

            String favorite_movie = intent.getStringExtra("favorite_movie");

            String trailer_KEY = intent.getStringExtra("trailer_KEY");

            ContentValues favoriteData = new ContentValues();
            favoriteData.put(MovieContract.MovieEntry.COLUMN_TITLE, movieTitle);
            favoriteData.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, moviePoster);
            favoriteData.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movieBackdrop);
            favoriteData.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movieOverview);
            favoriteData.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movieRating);
            favoriteData.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieReleaseDate);
            favoriteData.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie_ID);
            favoriteData.put(MovieContract.MovieEntry.COLUMN_TRAILER_TYPE, trailer_type);
            favoriteData.put(MovieContract.MovieEntry.COLUMN_TRAILER_NAME, movieTrailerName);
            favoriteData.put(MovieContract.MovieEntry.COLUMN_REVIEW_AUTHOR, movieReviewAuthor);
            favoriteData.put(MovieContract.MovieEntry.COLUMN_TRAILER_KEY, trailer_KEY);
            favoriteData.put(MovieContract.MovieEntry.COLUMN_FAVORITE_BOOL, favorite_movie);

            ContentResolver favoritesContentResolver = this.getContentResolver();

            String[] sel = {movie_ID};

            favoritesContentResolver.update(
                    MovieContract.MovieEntry.buildFavoriteMovieUri(),
                    favoriteData,
                    "id = ?",
                    sel);

        } else {
            System.out.println("ERROR adding movie to Favorites: FAVORITE INTENT NULL");
        }
    }
}
