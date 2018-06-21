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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.android.popularmoviesapp.data.MovieContract;
import com.android.popularmoviesapp.data.MovieDbHelper;
import com.android.popularmoviesapp.sync.MovieInfoQueryIntentService;
import com.android.popularmoviesapp.sync.MovieInfoSyncIntentService;
import com.android.popularmoviesapp.sync.MovieReviewIntentService;
import com.android.popularmoviesapp.sync.MovieTrailerIntentService;
import com.android.popularmoviesapp.utilities.NetworkUtils;


public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        MoviesAdapter.MoviesAdapterOnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private final String TAG = MainActivity.class.getSimpleName();
    private NetworkUtils networkUtils = new NetworkUtils();
    private SQLiteDatabase mDb;

    public static final String[] MOVIE_DATA_ARRAY = {
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TRAILER_KEY,
            MovieContract.MovieEntry.COLUMN_TRAILER_TYPE,
            MovieContract.MovieEntry.COLUMN_TRAILER_NAME,
            MovieContract.MovieEntry.COLUMN_REVIEW_AUTHOR,
            MovieContract.MovieEntry.COLUMN_REVIEW_CONTENT,
            MovieContract.MovieEntry.COLUMN_REVIEW_URL,
            MovieContract.MovieEntry.COLUMN_FAVORITE_BOOL
    };

    public static final int INDEX_OG_TITLE = 0;
    public static final int INDEX_VOTE_AVG = 1;
    public static final int INDEX_POSTER_PATH = 2;
    public static final int INDEX_BACKDROP_PATH = 3;
    public static final int INDEX_RELEASE_DATE = 4;
    public static final int INDEX_OVERVIEW = 5;
    public static final int INDEX_COLUMN_MOVIE_ID = 6;

    public static final int INDEX_TRAILER_KEY = 7;
    public static final int INDEX_TRAILER_TYPE = 8;
    public static final int INDEX_TRAILER_NAME = 9;

    public static final int INDEX_REVIEW_AUTHOR = 10;
    public static final int INDEX_REVIEW_CONTENT = 11;
    public static final int INDEX_REVIEW_URL = 12;

    public static final int INDEX_FAV_BOOL = 13;

    public static final int ID_MOVIE_LOADER = 19;
    public static final int ID_FAVORITES_LOADER = 400;

    private RecyclerView mMovieList;
    private MoviesAdapter mAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //find the RecyclerView, set it to variable
        mMovieList = (RecyclerView) findViewById(R.id.movies_rv);
        //define variable for # of columns to display in GridLayoutManager
        int numberOfColumns = 2;
        RecyclerView.LayoutManager layoutManager =
                new GridLayoutManager(getApplicationContext(), numberOfColumns);
        mMovieList.setLayoutManager(layoutManager);
        mMovieList.setHasFixedSize(true);

        mAdapter = new MoviesAdapter(this, this);

        mMovieList.setAdapter(mAdapter);

        MovieDbHelper movieDbHelper = new MovieDbHelper(this);
        mDb = movieDbHelper.getWritableDatabase();

        Intent syncMovieInfo = new Intent(this, MovieInfoSyncIntentService.class);
        startService(syncMovieInfo);

        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupMB();

    }

    private void setupMB() {

        //create preferences object
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        //get values for shared preference
        loadMovieSortPreference(preferences);

        //set listener for changes in Preference data
        //preferences.registerOnSharedPreferenceChangeListener(this);

        Intent syncMovieInfo = new Intent(this, MovieInfoSyncIntentService.class);
        startService(syncMovieInfo);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.sort_key))) {
            loadMovieSortPreference(sharedPreferences);
        }
    }

    private void loadMovieSortPreference (SharedPreferences preferences) {
        String value = preferences.getString(getString(R.string.sort_key), getString(R.string.sort_default));
        switch (value) {
            case "popular": {

                getSupportLoaderManager().destroyLoader(ID_FAVORITES_LOADER);
                getSupportLoaderManager().restartLoader(ID_MOVIE_LOADER, null, this);


                networkUtils.setSortOrder(preferences.getString(getString(R.string.sort_key),
                        getString(R.string.sort_most_popular_value)));

                System.out.println(value);

                Intent queryPopularMovieInfo = new Intent(this, MovieInfoQueryIntentService.class);
                queryPopularMovieInfo.putExtra("query_type", value);
                //send this to Intent service to decide whether to call popular query or Top Rated query.
                startService(queryPopularMovieInfo);

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
                getSupportLoaderManager().restartLoader(ID_FAVORITES_LOADER, null, this);

                mDb.beginTransaction();
                Cursor cursor = mDb.query(MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN,
                        new String[]{MovieContract.MovieEntry.COLUMN_FAVORITE_BOOL},
                        MovieContract.MovieEntry.COLUMN_FAVORITE_BOOL + " = ? ",
                        new String[]{"true"},
                        null,
                        null,
                        null);

                //mAdapter.swapCursor(cursor);

                System.out.println(value);
                mDb.endTransaction();
                cursor.close();


                break;
            }



            case "top_rated": {

                getSupportLoaderManager().destroyLoader(ID_FAVORITES_LOADER);
                getSupportLoaderManager().restartLoader(ID_MOVIE_LOADER, null, this);

                networkUtils.setSortOrder(preferences.getString(getString(R.string.sort_key),
                        getString(R.string.sort_highest_rated_value)));

                System.out.println(value);

                Intent queryTopRatedMovieInfo = new Intent(this, MovieInfoQueryIntentService.class);

                queryTopRatedMovieInfo.putExtra("query_type", value);
                startService(queryTopRatedMovieInfo);

                /*Cursor cursor = mDb.query(MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);

                cursor.close();*/

                this.getContentResolver().notifyChange(MovieContract.MovieEntry.CONTENT_URI, null);


                break;
            }
        }
    }

    @NonNull
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle args) {

        switch (loaderId) {
            case ID_MOVIE_LOADER:

                Uri moviesQueryUri = MovieContract.MovieEntry.CONTENT_URI;

                return new android.support.v4.content.CursorLoader(this,
                        moviesQueryUri,
                        MOVIE_DATA_ARRAY,
                        null,
                        null,
                        null);

            case ID_FAVORITES_LOADER:

                Uri favoritesQueryUri = MovieContract.MovieEntry.buildFavoriteMovieUri();

                return new android.support.v4.content.CursorLoader(this,
                        favoritesQueryUri,
                        MOVIE_DATA_ARRAY,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader not implemented: " + loaderId);

        }
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        if(mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mMovieList.smoothScrollToPosition(mPosition);

        data.moveToFirst();
        Log.v(TAG , DatabaseUtils.dumpCursorToString(data));

        //if(cursor.getCount() != 0)
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(View view, String movie_id) {

        Intent syncMovieTrailer = new Intent(this, MovieTrailerIntentService.class);
        startService(syncMovieTrailer);

        Intent syncMovieReview = new Intent(this, MovieReviewIntentService.class);
        startService(syncMovieReview);

        Intent intentToStartMovieDetailActivity = new Intent(MainActivity.this, MovieDetailActivity.class);

        Uri movieClicked = MovieContract.MovieEntry.buildMovieDetailPageUri(movie_id);

        intentToStartMovieDetailActivity.setData(movieClicked);
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

}

