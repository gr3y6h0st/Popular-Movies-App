package com.android.popularmoviesapp.sync;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.android.popularmoviesapp.data.MovieContract;

public class FavoritesMovieIntentService extends IntentService{

    public FavoritesMovieIntentService() {
        super("FavoriteMovieIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        MovieInfo.add_Favorite_Movie(this);

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

        final String trailer_KEY = intent.getStringExtra("trailer_KEY");

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

        ContentResolver favoritesContentResolver = this.getContentResolver();
        favoritesContentResolver.insert(MovieContract.MovieEntry.buildFavoriteMovieUri(),
                favoriteData);

    }
}
