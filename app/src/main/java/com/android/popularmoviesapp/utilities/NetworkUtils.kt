package com.android.popularmoviesapp.utilities


import android.net.Uri
import android.util.Log
import com.android.popularmoviesapp.BuildConfig
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

object NetworkUtils {

    private val TAG = NetworkUtils::class.java.simpleName

    //insert your own API KEY
    private const val API_KEY = BuildConfig.ApiKey

    private const val STATIC_MOVIE_DATABASE_URL = "https://api.themoviedb.org/3/movie/"

    private const val MOVIEDB_BASE_URL = STATIC_MOVIE_DATABASE_URL

    private const val APPID_PARAM = "api_key"

    private const val VIDEO_PATH = "videos"

    private const val REVIEW_PATH = "reviews"


    val movieId: String? = null

    fun buildUrl(sortOrder: String): URL? {
        val builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendPath(sortOrder)
                .appendQueryParameter(APPID_PARAM, API_KEY)
                .build()

        var url: URL? = null
        try {
            url = URL(builtUri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }

        Log.v(TAG, "Built URI " + url!!)

        return url
    }

    fun trailersBuildUrl(movie_ID: String): URL? {
        val builtUri = Uri.parse(MOVIEDB_BASE_URL + movie_ID).buildUpon()
                .appendPath(VIDEO_PATH)
                .appendQueryParameter(APPID_PARAM, API_KEY)
                .build()

        var url: URL? = null
        try {
            url = URL(builtUri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }

        Log.v(TAG, "Built URI " + url!!)
        return url
    }

    fun reviewsBuildUrl(movie_ID: String): URL? {
        val builtUri = Uri.parse(MOVIEDB_BASE_URL + movie_ID).buildUpon()
                .appendPath(REVIEW_PATH)
                .appendQueryParameter(APPID_PARAM, API_KEY)
                .build()

        var url: URL? = null
        try {
            url = URL(builtUri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }

        Log.v(TAG, "Built URI " + url!!)
        return url
    }

    /** Method returns entire result from HTTP response.
     *
     * @param url URL to fetch the HTTP response from.
     * @return THe contents of HTTP response.
     * @throws IOException Related to network and stream reading
     * Handles Character encoding. Allocates/deallocates buffers as needed.
     */
    @Throws(IOException::class)
    fun getResponseFromHttpUrl(url: URL): String? {
        val urlConnection = url.openConnection() as HttpURLConnection

        try {
            val input = urlConnection.inputStream

            val scanner = Scanner(input)
            scanner.useDelimiter("\\A")

            val hasInput = scanner.hasNext()

            return if (hasInput) {
                scanner.next()
            } else {
                null
            }
        } finally {
            urlConnection.disconnect()
        }
    }
}
