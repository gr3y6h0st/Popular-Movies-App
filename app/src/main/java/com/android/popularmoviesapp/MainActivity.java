package com.android.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.android.popularmoviesapp.data.MovieContract;
import com.android.popularmoviesapp.data.MovieData;
import com.android.popularmoviesapp.data.MovieDbHelper;
import com.android.popularmoviesapp.utilities.MovieDatabaseJsonUtils;
import com.android.popularmoviesapp.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.android.popularmoviesapp.utilities.NetworkUtils.getResponseFromHttpUrl;


public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ArrayList<MovieData>>,
        MoviesAdapter.MoviesAdapterOnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private final String TAG = MainActivity.class.getSimpleName();
    private SQLiteDatabase mDb;

    public static final String[] MOVIE_DATA_ARRAY = {
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_FAVORITE_BOOL
    };

    public static final int INDEX_OG_TITLE = 0;
    public static final int INDEX_VOTE_AVG = 1;
    public static final int INDEX_POSTER_PATH = 2;
    public static final int INDEX_BACKDROP_PATH = 3;
    public static final int INDEX_RELEASE_DATE = 4;
    public static final int INDEX_OVERVIEW = 5;
    public static final int INDEX_COLUMN_MOVIE_ID = 6;
    public static final int INDEX_FAV_BOOL = 7;

    public static final int ID_MOVIE_LOADER = 19;
    public static final int ID_FAVORITES_LOADER = 400;

    private RecyclerView mMovieList;
    private MoviesAdapter mAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    public static ArrayList<MovieData> movieDataArrayList = new ArrayList<>();
    private static final String SORT_QUERY_URL_EXTRA = "sort_me";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create movie data ArrayList
        final ArrayList<MovieData> movieData = movieDataArrayList;

        //find the RecyclerView, set it to variable
        mMovieList = (RecyclerView) findViewById(R.id.movies_rv);
        //define variable for # of columns to display in GridLayoutManager
        int numberOfColumns = 2;
        RecyclerView.LayoutManager layoutManager =
                new GridLayoutManager(getApplicationContext(), numberOfColumns);
        mMovieList.setLayoutManager(layoutManager);
        mMovieList.setHasFixedSize(true);

        //setupMB();
        mAdapter = new MoviesAdapter(this, movieData, this);

        mMovieList.setAdapter(mAdapter);

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(ID_MOVIE_LOADER, null, this);

        MovieDbHelper movieDbHelper = new MovieDbHelper(this);
        mDb = movieDbHelper.getWritableDatabase();

        //Intent syncMovieInfo = new Intent(this, MovieInfoSyncIntentService.class);
        //startService(syncMovieInfo);

    }

    @Override
    protected void onStart() {
        setupMB();
        super.onStart();

    }

    private void setupMB() {

        //create preferences object
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //get values for shared preference
        loadMovieSortPreference(preferences);

        //set listener for changes in Preference data
        //preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.sort_key))) {
            loadMovieSortPreference(sharedPreferences);
        }
    }

    private void loadMovieSortPreference (SharedPreferences preferences) {
        String value = preferences.getString(getString(R.string.sort_key), getString(R.string.sort_default));
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList<MovieData>> movie_query_loader = loaderManager.getLoader(ID_MOVIE_LOADER);
        Bundle sortPrefBundle = new Bundle();

        switch (value) {
            case "popular": {
                sortPrefBundle.putString(SORT_QUERY_URL_EXTRA, preferences.getString(getString(R.string.sort_key),
                        getString(R.string.sort_most_popular_value)));

                System.out.println(value);

                if (movie_query_loader == null) {
                    loaderManager.initLoader(ID_MOVIE_LOADER, sortPrefBundle, this);
                } else
                    loaderManager.restartLoader(ID_MOVIE_LOADER, sortPrefBundle, this);

                //Intent queryPopularMovieInfo = new Intent(this, MovieInfoQueryIntentService.class);
                //queryPopularMovieInfo.putExtra("query_type", value);
                //send this to Intent service to decide whether to call popular query or Top Rated query.
                //startService(queryPopularMovieInfo);

                /*Cursor cursor = mDb.query(MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
                cursor.close();
                this.getContentResolver().notifyChange(MovieContract.MovieEntry.CONTENT_URI, null);*/

                break;
            }

            case "favorites": {
                getSupportLoaderManager().destroyLoader(ID_MOVIE_LOADER);

                displayFavoriteMovies();
                //getSupportLoaderManager().restartLoader(ID_FAVORITES_LOADER, null, this);

                /*Cursor cursor = mDb.query(MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN,
                        new String[]{MovieContract.MovieEntry.COLUMN_FAVORITE_BOOL},
                        MovieContract.MovieEntry.COLUMN_FAVORITE_BOOL + " = ? ",
                        new String[]{"true"},
                        null,
                        null,
                        null);*/
                break;
            }



            case "top_rated": {
                sortPrefBundle.putString(SORT_QUERY_URL_EXTRA, preferences.getString(getString(R.string.sort_key),
                        getString(R.string.sort_highest_rated_value)));

                System.out.println(value);

                if (movie_query_loader == null) {

                    loaderManager.initLoader(ID_MOVIE_LOADER, sortPrefBundle, this);
                } else {

                    loaderManager.restartLoader(ID_MOVIE_LOADER, sortPrefBundle, this);
                }
                break;
            }
        }
    }

    // Should return type be not defined to accept two different loaders???

    @NonNull
    @Override
    public android.support.v4.content.Loader<ArrayList<MovieData>> onCreateLoader(int loaderId, final Bundle args) {

        switch (loaderId) {
            case ID_MOVIE_LOADER:
                System.out.println(loaderId);

                return new AsyncTaskLoader<ArrayList<MovieData>>(this) {
                    ArrayList<MovieData> mMovieData;

                    @Override
                    public void deliverResult(@Nullable ArrayList<MovieData> data) {
                        mMovieData = data;
                        super.deliverResult(data);
                    }

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        if (args == null) {
                            return;
                        }
                        if (mMovieData != null) {
                            deliverResult(mMovieData);
                        } else{
                            forceLoad();
                        }
                    }

                    @Override
                    public ArrayList<MovieData> loadInBackground() {
                        String sortOrderQueryString = args.getString(SORT_QUERY_URL_EXTRA);
                        System.out.println(sortOrderQueryString);
                        if (sortOrderQueryString == null || TextUtils.isEmpty(sortOrderQueryString)){
                            return null;
                        }
                        try{
                            URL movieRequestUrl = NetworkUtils.buildUrl(sortOrderQueryString);

                            String jsonMovieDatabaseResponse = getResponseFromHttpUrl(movieRequestUrl);

                            ArrayList<MovieData> simpleJsonMovieData = MovieDatabaseJsonUtils.getMovieData(jsonMovieDatabaseResponse);

                            Log.v("READ", simpleJsonMovieData.get(0).getPoster_path());

                            return simpleJsonMovieData;

                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }


                };

            /*case ID_FAVORITES_LOADER:
                Uri favoritesQueryUri = MovieContract.MovieEntry.buildFavoriteMovieUri();

                return new android.support.v4.content.CursorLoader(this,
                        favoritesQueryUri,
                        MOVIE_DATA_ARRAY,
                        null,
                        null,
                        null);*/
            default:
                throw new RuntimeException("Loader not implemented: " + loaderId);

        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<MovieData>> loader, ArrayList<MovieData> data) {

        if (data != null) {
            mAdapter = new MoviesAdapter(getApplicationContext(), data, MainActivity.this);

            //populate the global ArrayList for use elsewhere
            movieDataArrayList = data;

            mMovieList.setAdapter(mAdapter);
            Log.v("README", data.get(0).getPoster_path());
        } else {
            Log.v("README", "MovieData Arraylist is null or empty.");
        }

        /*if(loader.getId() == ID_FAVORITES_LOADER){
            // Verify data as Cursor first
            //mAdapter.swapCursor(data);
            if(mPosition == RecyclerView.NO_POSITION) mPosition = 0;
            mMovieList.smoothScrollToPosition(mPosition);

            data.moveToFirst();
            Log.v(TAG , DatabaseUtils.dumpCursorToString(data));
        }
        else{
            if (data != null) {
                mAdapter = new MoviesAdapter(getApplicationContext(), data, MainActivity.this);

                //populate the global ArrayList for use elsewhere
                movieDataArrayList = data;

                mMovieList.setAdapter(mAdapter);
                Log.v("README", data.get(0).getPoster_path());
            } else {
                Log.v("README", "MovieData Arraylist is null or empty.");
            }
        }*/


        //if(cursor.getCount() != 0)
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<ArrayList<MovieData>> loader) {
        //mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(int clickedItemIndex) {

        Intent intentToStartMovieDetailActivity = new Intent(MainActivity.this, MovieDetailActivity.class);
        intentToStartMovieDetailActivity.putExtra("movieDeets", movieDataArrayList.get(clickedItemIndex));
        intentToStartMovieDetailActivity.putExtra("moviePosition", clickedItemIndex);
        startActivity(intentToStartMovieDetailActivity);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemClicked = item.getItemId();
        Context context = MainActivity.this;
        if (itemClicked == R.id.settings_menu_action){
            Intent settingsMenuIntent = new Intent(context, SettingsActivity.class);
            startActivity(settingsMenuIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayFavoriteMovies() {

        Cursor cursor = mDb.query(MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN,
                null,
                null,
                null,
                null,
                null,
                null);
        /*Cursor cursor = mDb.query(MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN,
                new String[]{MovieContract.MovieEntry.COLUMN_FAVORITE_BOOL},
                MovieContract.MovieEntry.COLUMN_FAVORITE_BOOL + " = ? ",
                new String[]{"true"},
                null,
                null,
                null);*/

        ArrayList<MovieData> result = new ArrayList<>();

        try {
            while(cursor.moveToNext()){
                String item_id = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));

                MovieData movieData = new MovieData(
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE_BOOL)));
                System.out.println(item_id + " " + movieData.getBackdrop_path());
                result.add(movieData);
            }
        } finally {
            cursor.close();
        }
        mAdapter.notifyMovieDataChange(result);
        //mMovieList.setAdapter(mAdapter);
                /*Uri favoritesQueryUri = MovieContract.MovieEntry.buildFavoriteMovieUri();

                /*return new android.support.v4.content.CursorLoader(this,
                        favoritesQueryUri,
                        MOVIE_DATA_ARRAY,
                        null,
                        null,
                        null);*/

    }


}

