package com.android.popularmoviesapp.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.android.popularmoviesapp.utilities.NetworkUtils


class MovieProvider : ContentProvider() {

    internal val TAG = MovieProvider::class.java.simpleName

    private var mOpenHelper: MovieDbHelper? = null

    override fun onCreate(): Boolean {
        mOpenHelper = MovieDbHelper(context)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val cursor: Cursor
        val db = mOpenHelper!!.readableDatabase
        when (sUriMatcher.match(uri)) {
            CODE_MOVIE -> cursor = db.query(
                    MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN,
                    projection,
                    selection,
                    selectionArgs, null, null,
                    sortOrder, "20")

            CODE_MOVIE_FAVORITE ->

                cursor = db.query(null, null, null, null, null, null,
                        MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }
    override fun getType(uri: Uri): String? {
        return null
    }

    override fun bulkInsert(uri: Uri, contentValues: Array<ContentValues>?): Int {
        val db = mOpenHelper!!.writableDatabase
        when (sUriMatcher.match(uri)) {
            CODE_MOVIE -> {
                db.beginTransaction()
                var rowsInserted = 0
                try {
                    for (value in contentValues!!) {

                        val _id = db.insert(MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN, null, value)
                        if (_id >= 0) {
                            rowsInserted++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }

                if (rowsInserted > 0) {
                    context!!.contentResolver.notifyChange(uri, null)
                }
                return rowsInserted
            }

            else -> return super.bulkInsert(uri, contentValues!!)
        }
    }
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        var selection = selection
        var numRowsDeleted: Int
        if (null == selection) selection = "1"
        when (sUriMatcher.match(uri)) {

            CODE_MOVIE_FAVORITE -> {
                val id = uri.lastPathSegment
                numRowsDeleted = mOpenHelper!!.writableDatabase.delete(
                        MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN,
                        "id=?",
                        arrayOf(id))
            }

            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }

        if (numRowsDeleted != 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return numRowsDeleted

    }

    override fun update(uri: Uri, contentValues: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        var selection = selection
        var selectionArgs = selectionArgs
        var rowsUpdated = 0

        if (null == selection) selection = "1"
        selectionArgs = arrayOf(NetworkUtils.movieId.toString())

        when (sUriMatcher.match(uri)) {

            CODE_MOVIE -> rowsUpdated = mOpenHelper!!.writableDatabase.update(
                    MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN,
                    contentValues,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                    selectionArgs
            )

            CODE_MOVIE_FAVORITE ->

                rowsUpdated = mOpenHelper!!.writableDatabase.update(
                        MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN,
                        contentValues,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArgs
                )
        }
        if (rowsUpdated != 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return rowsUpdated
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        val db = mOpenHelper!!.writableDatabase
        var retUri: Uri? = null
        val id: Long
        when (sUriMatcher.match(uri)) {
            CODE_MOVIE_FAVORITE -> {

                db.beginTransaction()
                //insert into FAVORITE TABLE
                try {
                    id = db.insert(MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN, null, contentValues)
                    if (id >= 0) {
                        //database has changed
                        retUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id)
                        println("Successful insert!")
                    } else {
                        println("SORRY insert FAILED!")
                    }
                    db.setTransactionSuccessful()

                } finally {
                    db.endTransaction()
                    context!!.contentResolver.notifyChange(uri, null)
                }
            }
        }
        return retUri
    }
    companion object {

        const val CODE_MOVIE = 100
        const val CODE_MOVIE_FAVORITE = 143

        private val sUriMatcher = buildUriMatcher()

        private fun buildUriMatcher(): UriMatcher {

            val matcher = UriMatcher(UriMatcher.NO_MATCH)
            val authority = MovieContract.CONTENT_AUTHORITY

            matcher.addURI(authority, MovieContract.PATH_MOVIES, CODE_MOVIE)
            matcher.addURI(authority, MovieContract.PATH_MOVIES + "/*", CODE_MOVIE_FAVORITE)

            return matcher
        }
    }
}
