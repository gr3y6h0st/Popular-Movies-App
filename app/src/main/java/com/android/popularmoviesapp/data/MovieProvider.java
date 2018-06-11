package com.android.popularmoviesapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.popularmoviesapp.utilities.NetworkUtils;


public class MovieProvider extends ContentProvider{
    final String TAG = MovieProvider.class.getSimpleName();


    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_DETAIL = 119;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MovieDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIES, CODE_MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/#", CODE_MOVIE_DETAIL);

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

            case CODE_MOVIE: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);

                break;
            }

            case CODE_MOVIE_DETAIL: {

                String movie_id = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{movie_id};

                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        null);

                break;

            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

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

        switch (sUriMatcher.match(uri)){

            case CODE_MOVIE:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : contentValues) {

                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if(_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if(rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;

            case CODE_MOVIE_DETAIL:
                db.beginTransaction();
                int trailerInfoRowsInserted = 0;
                try {
                    for (ContentValues value : contentValues) {

                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            trailerInfoRowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if(trailerInfoRowsInserted > 0) {
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
        if(null == selection) selection = "1";
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        null,
                        null);
                break;
            //case for CODE_MOVIE_DETAIL?!!!!
            case CODE_MOVIE_DETAIL:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        null,
                        null);
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

        if(null ==  selection) selection = "1";
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                selectionArgs[0] = NetworkUtils.getMovieId();
                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        MovieContract.MovieEntry.TABLE_NAME,
                        contentValues,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArgs
                );
                break;


            case CODE_MOVIE_DETAIL:
                //String[] selectionArguments = {uri.getLastPathSegment()};

                //UPDATE to the column that matches MOVIE_ID (found in URI)
                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        MovieContract.MovieEntry.TABLE_NAME,
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
        throw new RuntimeException(
                "Not in use yet. See bulkInsert first."
        );
    }
}
