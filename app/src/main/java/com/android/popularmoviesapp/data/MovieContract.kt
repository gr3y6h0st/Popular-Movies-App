package com.android.popularmoviesapp.data

import android.net.Uri
import android.provider.BaseColumns

object MovieContract {

    const val CONTENT_AUTHORITY = "com.android.popularmoviesapp"

    val BASE_CONTENT_URI: Uri = Uri.parse("content://$CONTENT_AUTHORITY")

    const val PATH_MOVIES = "movies"

    class MovieEntry : BaseColumns {
        companion object {

            val CONTENT_URI: Uri = BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_MOVIES)
                    .build()

            fun buildFavoriteMovieUri(movie_id: String): Uri {
                return CONTENT_URI.buildUpon()
                        .appendPath(movie_id)
                        .build()
            }

            const val TABLE_NAME_MOVIE_MAIN = "movie_data"
            const val COLUMN_TITLE = "original_title"
            const val COLUMN_POSTER_PATH = "poster_path"
            const val COLUMN_BACKDROP_PATH = "backdrop_path"
            const val COLUMN_VOTE_AVERAGE = "vote_average"
            const val COLUMN_RELEASE_DATE = "release_date"
            const val COLUMN_OVERVIEW = "overview"
            const val COLUMN_MOVIE_ID = "id"
            const val COLUMN_FAVORITE_BOOL = "favorite"
        }
    }
}
