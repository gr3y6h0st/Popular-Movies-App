package com.android.popularmoviesapp.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MovieDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        //create table
        val movieTable = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN + " (" +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL PRIMARY KEY, " +
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_FAVORITE_BOOL + " TEXT DEFAULT 'false', " +
                " UNIQUE (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);"
        //execute SQL w/ execSQL method of SQL database obj
        sqLiteDatabase.execSQL(movieTable)
    }
    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN)
        onCreate(sqLiteDatabase)
    }
    companion object {
        private const val DATABASE_NAME = "movie_data.db"
        private const val DATABASE_VERSION = 24
    }
}
