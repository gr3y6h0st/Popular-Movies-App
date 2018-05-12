package com.android.popularmoviesapp.utilities;


import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    //insert your own API KEY
    private static final String API_KEY = "YOUR_API_KEY";


    private static final String STATIC_MOVIE_DATABASE_URL =
            "https://api.themoviedb.org/3/movie";

    private static final String MOVIEDB_BASE_URL = STATIC_MOVIE_DATABASE_URL;

    private static final String format = "json";

    private static String SORT_ORDER = "popular";

    private final static String APPID_PARAM = "api_key";

    public static URL buildUrl(String movieQuery) {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendPath(SORT_ORDER)
                .appendQueryParameter(APPID_PARAM, API_KEY)
                .build();

        URL url = null;
        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public void setSortOrder (String sortOrder){
        this.SORT_ORDER = sortOrder;
    }

    /** Method returns entire result from HTTP response.
     *
     * @param url URL to fetch the HTTP response from.
     * @return THe contents of HTTP response.
     * @throws IOException Related to network and stream reading
     * Handles Character encoding. Allocates/deallocates buffers as needed.
     */
    public static String getResponseFromHttpUrl (URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection)  url.openConnection();

        try{
            InputStream input = urlConnection.getInputStream();

            Scanner scanner = new Scanner(input);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
