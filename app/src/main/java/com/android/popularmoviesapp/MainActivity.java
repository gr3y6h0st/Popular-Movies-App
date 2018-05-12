package com.android.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.popularmoviesapp.data.MovieData;
import com.android.popularmoviesapp.utilities.MovieDatabaseJsonUtils;
import com.android.popularmoviesapp.utilities.NetworkUtils;
import java.net.URL;
import java.util.ArrayList;

import static com.android.popularmoviesapp.utilities.NetworkUtils.*;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ListItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener  {

    private RecyclerView mMovieList;
    private MoviesAdapter mAdapter;
    private NetworkUtils networkUtils = new NetworkUtils();
    //create the movie data ArrayList
    public static ArrayList<MovieData> movieDataArrayList = new ArrayList<>();

    @Override
    protected void onStart() {
        refreshMovieDB();
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create the movie data ArrayList
        final ArrayList<MovieData> movieData = movieDataArrayList;

        //find the RecyclerView, set it to variable
        mMovieList = (RecyclerView) findViewById(R.id.movies_rv);

        //define variable for # of columns to display in GridLayoutManager
        int numberOfColumns = 2;
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), numberOfColumns);
        mMovieList.setLayoutManager(layoutManager);
        mMovieList.setHasFixedSize(true);
    }

    public void refreshMovieDB() {
        //create asynctask object
        FetchMoviesDataTask fetchMoviesDataTask = new FetchMoviesDataTask();
        //create preferences object
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //set default sort
        String sortInfo = preferences.getString(getString(R.string.sort_key), getString(R.string.sort_default));
        //execute asynctask
        fetchMoviesDataTask.execute(sortInfo);

        //set listener for changes in Preference data
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        // COMPLETED: add intent to MovieDetailsActivity here
        Intent startMovieDetailActivity = new Intent(MainActivity.this, MovieDetailActivity.class);

        //use putExtra to transfer movieData to child Activity
        startMovieDetailActivity.putExtra("movie_deets", movieDataArrayList.get(clickedItemIndex));
        startActivity(startMovieDetailActivity);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.sort_key))) {
            networkUtils.setSortOrder(sharedPreferences.getString(key, getResources().getString(R.string.sort_default)));
        }
    }

    class FetchMoviesDataTask extends AsyncTask<String, Void, ArrayList<MovieData>>{

        @Override
        protected ArrayList<MovieData> doInBackground(String... params) {
            if(params.length == 0){
                return null;
            }

            String movie = params[0];
            URL movieRequestUrl = NetworkUtils.buildUrl(movie);

            try {
                String jsonMovieDatabaseResponse =
                        getResponseFromHttpUrl(movieRequestUrl);

                ArrayList<MovieData> simpleJsonMovieData = MovieDatabaseJsonUtils
                        .getMovieData(jsonMovieDatabaseResponse);
                Log.v("READ", simpleJsonMovieData.get(0).getPoster_path());
                return simpleJsonMovieData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MovieData> movieData) {
            super.onPostExecute(movieData);
            if (movieData != null) {
                mAdapter = new MoviesAdapter(getApplicationContext(), movieData, MainActivity.this);

                //populate the global ArrayList for use elsewhere
                movieDataArrayList = movieData;

                mMovieList.setAdapter(mAdapter);
                Log.v("README", movieData.get(0).getPoster_path());
            } else {
                Log.v("README", "MovieData Arraylist is null or empty.");
            }
        }
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

