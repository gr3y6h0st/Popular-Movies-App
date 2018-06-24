package com.android.popularmoviesapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.popularmoviesapp.utilities.NetworkUtils;


public class MovieProvider extends ContentProvider {

    final String TAG = MovieProvider.class.getSimpleName();


    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_POPULAR = 101;
    public static final int CODE_MOVIE_DETAIL = 119;
    public static final int CODE_MOVIE_FAVORITE = 143;
    public static final int CODE_MOVIE_UNFAVORITE = 144;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MovieDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIES, CODE_MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/popular", CODE_MOVIE_POPULAR);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/#", CODE_MOVIE_DETAIL);

        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/favorite", CODE_MOVIE_FAVORITE);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/favorite/#", CODE_MOVIE_UNFAVORITE);

        return matcher;


    }

    @Override
    public boolean onCreate() {

        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIE:
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder, "20");

                break;

           /* case CODE_MOVIE_POPULAR:
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder + " ASC",
                        "20");

                break;*/

            case CODE_MOVIE_DETAIL:

                String movie_id = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{movie_id};

                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN,
                        projection,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        null);

                break;


            case CODE_MOVIE_FAVORITE:

                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN,
                        projection,
                        MovieContract.MovieEntry.COLUMN_FAVORITE_BOOL + " = ? ",
                        new String[]{"true"},
                        null,
                        null,
                        MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE );

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        //getContext().getContentResolver().notifyChange(uri, null);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public int bulkInsert(@NonNull Uri uri, @Nullable ContentValues[] contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIE:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : contentValues) {

                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;

            /*case CODE_MOVIE_FAVORITE:

                db.beginTransaction();
                int favoritesInserted = 0;
                try {
                    for (ContentValues value : contentValues) {

                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME_FAVORITES, null, value);
                        if(_id != -1) {
                            favoritesInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if(favoritesInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return favoritesInserted;*/


            case CODE_MOVIE_DETAIL:
                db.beginTransaction();
                int trailerInfoRowsInserted = 0;
                try {
                    for (ContentValues value : contentValues) {

                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN, null, value);
                        if (_id != -1) {
                            trailerInfoRowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (trailerInfoRowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return trailerInfoRowsInserted;
            default:
                return super.bulkInsert(uri, contentValues);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsDeleted = 0;
        if (null == selection) selection = "1";
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN,
                        null,
                        null);
                break;

            case CODE_MOVIE_DETAIL:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN,
                        null,
                        null);
                break;

            case CODE_MOVIE_UNFAVORITE:
                String id = uri.getLastPathSegment();
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN,
                        "id=?",
                        new String[]{id});
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsDeleted;

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsUpdated = 0;

        if (null == selection) selection = "1";
        selectionArgs = new String[]{NetworkUtils.getMovieId()};

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIE:
                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN,
                        contentValues,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArgs
                );
                break;


            case CODE_MOVIE_DETAIL:
                //String[] selectionArguments = {uri.getLastPathSegment()};

                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN,
                        contentValues,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArgs
                );
                break;

            case CODE_MOVIE_FAVORITE:

                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN,
                        contentValues,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArgs
                );
                break;


        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);

        }
        return rowsUpdated;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri retUri = null;
        long _id;


        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIE_FAVORITE:

                db.beginTransaction();
                //insert into FAVORITE TABLE
                try {
                    _id = db.insert(MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN, null, contentValues);

                    /* if _id == -1 means insertion failed */
                    if (_id != -1) {
                        //database has changed
                        retUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, _id);
                        System.out.println("Successful insert!");
                    } else {
                        System.out.println("SORRY insert FAILED!");
                    }
                    db.setTransactionSuccessful();

                } finally {
                    db.endTransaction();

                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;

        }
        return MovieContract.MovieEntry.buildFavoriteMovieUri();

    }
}
