package com.android.popularmoviesapp

import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.LoaderManager
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.android.popularmoviesapp.data.MovieContract
import com.android.popularmoviesapp.data.MovieData
import com.android.popularmoviesapp.data.MovieDbHelper
import com.android.popularmoviesapp.utilities.MovieDatabaseJsonUtils
import com.android.popularmoviesapp.utilities.NetworkUtils
import java.util.*


class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<ArrayList<MovieData>>, MoviesAdapter.MoviesAdapterOnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private val TAG = MainActivity::class.java.simpleName
    private var mDb: SQLiteDatabase? = null

    private var mMovieList: RecyclerView? = null
    private var mAdapter: MoviesAdapter? = null
    private var mLayoutmanager: RecyclerView.LayoutManager? = null
    private var mPosition: Parcelable? = null
    private val mContext = this
    private val sortPrefBundle = Bundle()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //find the RecyclerView, set it to variable
        mMovieList = findViewById(R.id.movies_rv)
        //define variable for # of columns to display in GridLayoutManager
        val numberOfColumns = 2

        mLayoutmanager = GridLayoutManager(applicationContext, numberOfColumns)
        mMovieList!!.layoutManager = mLayoutmanager
        mMovieList!!.setHasFixedSize(true)

        mAdapter = MoviesAdapter(mContext, movieDataArrayList, this)

        mMovieList!!.adapter = mAdapter

        val movieDbHelper = MovieDbHelper(mContext)
        mDb = movieDbHelper.writableDatabase
        //PreferenceManager.setDefaultValues(mContext, R.xml.pref_movies, false);

        LoaderManager.enableDebugLogging(true)

        setupDBPref()

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)

        supportLoaderManager.initLoader(ID_MOVIE_LOADER, sortPrefBundle, this)

    }

    override fun onStart() {
        setupDBPref()
        super.onStart()
    }

    private fun setupDBPref() {

        //create preferences object
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        loadMovieSortPreference(sharedPreferences)

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == getString(R.string.sort_key)) {
            loadMovieSortPreference(sharedPreferences)
        }

    }

    private fun loadMovieSortPreference(sharedPreferences: SharedPreferences) {

        val value = sharedPreferences.getString(getString(R.string.sort_key), getString(R.string.sort_default))

        when (value) {

            "popular" -> {
                sortPrefBundle.putString(SORT_QUERY_URL_EXTRA, sharedPreferences.getString(getString(R.string.sort_key),
                        getString(R.string.sort_most_popular_value)))

                println(" changed preference to $value")
            }

            "favorites" -> {

                supportLoaderManager.destroyLoader(ID_MOVIE_LOADER)
                displayFavoriteMovies()
            }

            "top_rated" -> {

                sortPrefBundle.putString(SORT_QUERY_URL_EXTRA, sharedPreferences.getString(getString(R.string.sort_key),
                        getString(R.string.sort_highest_rated_value)))

                println(" changed preference to $value")
            }
        }
    }

    override fun onCreateLoader(loaderId: Int, args: Bundle?): android.support.v4.content.Loader<ArrayList<MovieData>> {

        when (loaderId) {
            ID_MOVIE_LOADER -> {
                println("Creating Loader with ID#: $loaderId")


                return object : AsyncTaskLoader<ArrayList<MovieData>>(this) {
                    internal var mMovieData: ArrayList<MovieData>? = null

                    override fun onStartLoading() {
                        if (args == null) {
                            println(" ARGS NULL!")
                            return
                        }
                        //GOTTT TO MAKE HTIS HAPPEN
                        if (mMovieData != null) {
                            deliverResult(mMovieData)
                            println("movie data isn't null, delivering data!")
                        } else {
                            forceLoad()
                            println("forceloading.")
                        }
                    }

                    override fun loadInBackground(): ArrayList<MovieData>? {
                        val sortOrderQueryString = args!!.getString(SORT_QUERY_URL_EXTRA)
                        println(sortOrderQueryString!! + " from asyc load.")
                        if (sortOrderQueryString == null || TextUtils.isEmpty(sortOrderQueryString)) {
                            return null
                        }


                        try {
                            val movieRequestUrl = NetworkUtils.buildUrl(sortOrderQueryString)

                            val jsonMovieDatabaseResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl!!)

                            val simpleJsonMovieData = MovieDatabaseJsonUtils.getMovieData(jsonMovieDatabaseResponse!!)

                            Log.v("READ", simpleJsonMovieData[0].poster_path)

                            return simpleJsonMovieData

                        } catch (e: Exception) {
                            e.printStackTrace()
                            return null
                        }

                    }

                    override fun deliverResult(data: ArrayList<MovieData>?) {
                        println(" delivery! Right HERE.")
                        mMovieData = data
                        super.deliverResult(data)
                    }

                }
            }

            else -> throw RuntimeException("Loader not implemented: $loaderId")
        }
    }

    override fun onLoadFinished(loader: Loader<ArrayList<MovieData>>, data: ArrayList<MovieData>?) {

        if (data != null) {
            //populate the global ArrayList for use elsewhere
            movieDataArrayList = data

            mMovieList!!.adapter = mAdapter
            mMovieList!!.setHasFixedSize(true)
            mLayoutmanager!!.onRestoreInstanceState(mPosition)
            mAdapter!!.notifyMovieDataChange(movieDataArrayList)
            //Log.v("README", data.get(0).getPoster_path());

        } else {
            Log.v("README", "MovieData Arraylist is null or empty.")

        }
    }

    override fun onLoaderReset(loader: android.support.v4.content.Loader<ArrayList<MovieData>>) {

    }

    override fun onItemClick(clickedItemIndex: Int) {

        val intentToStartMovieDetailActivity = Intent(this@MainActivity, MovieDetailActivity::class.java)
        intentToStartMovieDetailActivity.putExtra("movieDeets", movieDataArrayList[clickedItemIndex])
        startActivity(intentToStartMovieDetailActivity)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    override fun onRestart() {
        supportLoaderManager.restartLoader(ID_MOVIE_LOADER, sortPrefBundle, this)
        setupDBPref()
        super.onRestart()
    }

    override fun onPause() {
        super.onPause()
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemClicked = item.itemId
        val context = this@MainActivity
        if (itemClicked == R.id.settings_menu_action) {
            val settingsMenuIntent = Intent(context, SettingsActivity::class.java)
            startActivity(settingsMenuIntent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun displayFavoriteMovies() {
        movieDataArrayList.clear()

        val cursor = mDb!!.query(MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN, null, null, null, null, null, null)

        try {
            while (cursor.moveToNext()) {
                val item_id = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID))

                val movieData = MovieData(
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE_BOOL)))
                println(item_id + " " + movieData.backdrop_path)
                movieDataArrayList.add(movieData)
            }
        } finally {
            cursor.close()
        }
        mAdapter!!.notifyMovieDataChange(movieDataArrayList)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        mPosition = mMovieList!!.layoutManager.onSaveInstanceState()
        outState.putParcelable(RVIEW_POSITION, mPosition)
        val savedAdapterPosition = mLayoutmanager!!.onSaveInstanceState().toString()
        Log.d("SAVING STATE NOW:", savedAdapterPosition)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {

        if (savedInstanceState != null) {

            val restoreSavedState = savedInstanceState.getParcelable<Parcelable>(RVIEW_POSITION)
            if (restoreSavedState != null) {
                mLayoutmanager!!.onRestoreInstanceState(restoreSavedState)
            } else {
                Log.d("ERROR: ", "SAVED STATE NULL.")
            }
        }

        super.onRestoreInstanceState(savedInstanceState)
    }

    companion object {

        private val RVIEW_POSITION = "Recycler Position"

        val ID_MOVIE_LOADER = 19
        val ID_FAVORITES_LOADER = 400

        var movieDataArrayList = ArrayList<MovieData>()
        private val SORT_QUERY_URL_EXTRA = "sort_me"
    }
}

