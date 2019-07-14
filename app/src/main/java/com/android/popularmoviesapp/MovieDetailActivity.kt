package com.android.popularmoviesapp

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.LoaderManager
import android.support.v4.app.NavUtils
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.ContextCompat
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MenuItem
import com.android.popularmoviesapp.data.MovieContract
import com.android.popularmoviesapp.data.MovieData
import com.android.popularmoviesapp.data.MovieDbHelper
import com.android.popularmoviesapp.databinding.ActivityMovieDetailBinding
import com.android.popularmoviesapp.utilities.MovieDatabaseJsonUtils
import com.android.popularmoviesapp.utilities.NetworkUtils
import com.squareup.picasso.Picasso
import java.util.*


class MovieDetailActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<ArrayList<MovieData>> {

    internal val TAG = MovieDetailActivity::class.java.simpleName

    private val mContext = this@MovieDetailActivity

    private var mDetailBinding: ActivityMovieDetailBinding? = null

    private var mMovieTrailerList: RecyclerView? = null
    private var mMovieReviewList: RecyclerView? = null

    private var mTrailerAdapter: MovieTrailerAdapter? = null
    private var mReviewAdapter: MovieReviewAdapter? = null

    private var checkFavorite: Boolean = false
    private lateinit var favoriteFab: FloatingActionButton
    private lateinit var mDb: SQLiteDatabase

    private lateinit var movieDetails: MovieData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_movie_detail)

        mDetailBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_movie_detail)

        favoriteFab = mDetailBinding!!.favoriteFloatingActionButton

        val movieDbHelper = MovieDbHelper(this)
        mDb = movieDbHelper.writableDatabase

        val actionBar = this.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val layoutTrailerManager = LinearLayoutManager(applicationContext)

        val layoutReviewManager =

                LinearLayoutManager(applicationContext)

        mMovieTrailerList = mDetailBinding!!.mdTrailersRv
        mMovieReviewList = mDetailBinding!!.mdReviewsRv

        mMovieTrailerList!!.layoutManager = layoutTrailerManager
        mMovieReviewList!!.layoutManager = layoutReviewManager

        val intent = intent ?: throw NullPointerException("YOUR INTENT cannot be null")
        movieDetails = intent.getSerializableExtra("movieDeets") as MovieData

        val movieTitle = movieDetails.original_title

        val moviePoster = movieDetails.poster_path

        val movieBackdrop = movieDetails.backdrop_path

        val movieOverview = movieDetails.overview

        val movieReleaseDate = movieDetails.release_date

        val movieRating = movieDetails.vote_average

        //create the image url for poster and backdrop
        val  imageBaseURL = "http://image.tmdb.org/t/p/"
        val imageSizeW500 = "w500"
        val imageSizeOriginal = "original"

        //obtain poster path from MovieData object

        val poster_url = imageBaseURL + imageSizeW500 + moviePoster
        val backdrop_url = imageBaseURL + imageSizeOriginal + movieBackdrop

        //use Picasso to place url image into imageview
        Picasso.get()
                .load(poster_url)
                .placeholder(R.drawable.ic_blank_black)
                .error(R.drawable.ic_sentiment_very_dissatisfied_black_24dp)
                .into(mDetailBinding!!.movieDetailIv)
        Picasso.get()
                .load(backdrop_url)
                .placeholder(R.drawable.ic_blank_black)
                .error(R.drawable.ic_sentiment_very_dissatisfied_black_24dp)
                .into(mDetailBinding!!.movieDetailBackdropIv)

        if (movieDetails.check_favorite_movie != null) {
            checkFavorite = java.lang.Boolean.parseBoolean(movieDetails.check_favorite_movie)
        } else {
            checkFavorite = false
        }

        if (checkFavorite) {
            favoriteFab.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic__favorite_movie_action_on))

        } else {
            favoriteFab.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic__favorite_movie_action_off))
        }

        favoriteFab.setOnClickListener {
            if (checkFavorite) {
                favoriteFab.setImageDrawable(ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic__favorite_movie_action_off))
                //delete happens here
                mDb.delete(MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN, "id=?", arrayOf(movieDetails.movie_id))
                checkFavorite = !checkFavorite

            } else {
                favoriteFab.setImageDrawable(ContextCompat.getDrawable(applicationContext,
                        R.drawable.ic__favorite_movie_action_on))
                checkFavorite = !checkFavorite
                addFavoriteMovie()
            }
        }

        mDetailBinding!!.mdOriginalTitle.text = movieTitle
        mDetailBinding!!.mdVoteAvg.text = movieRating
        mDetailBinding!!.mdReleaseDate.text = movieReleaseDate
        mDetailBinding!!.mdPlotSynopsis.text = movieOverview

        //mDetailBinding.mdOriginalTitleLabel.setText(getString(R.string.md_original_title_label));
        mDetailBinding!!.mdPlotSynopsisLabel.text = getString(R.string.md_synopsis_label)
        mDetailBinding!!.mdReleaseDateLabel.text = getString(R.string.md_date_label)
        mDetailBinding!!.mdVoteAvgLabel.text = getString(R.string.md_rating_label)

        val movieId = movieDetails.movie_id
        println(movieId)

        supportLoaderManager.initLoader(ID_MOVIE_TRAILER_LOADER, null, this)
    }

    //will set BACK BUTTON to navigate back to previous activity w/o recreating it.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemClicked = item.itemId

        when (itemClicked) {

            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return super.onOptionsItemSelected(item)
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateLoader(loaderId: Int, args: Bundle?): Loader<ArrayList<MovieData>> {

        when (loaderId) {

            ID_MOVIE_TRAILER_LOADER -> {
                println(loaderId)

                return object : AsyncTaskLoader<ArrayList<MovieData>>(this) {
                    var mMovieData: ArrayList<MovieData>? = null

                    override fun onStartLoading() {
                        if (mMovieData != null) {
                            deliverResult(mMovieData)
                            println("TRAILER DATA! delivering data!")
                        } else {
                            forceLoad()
                            println("TRAILER DATA forceloading.")
                        }
                    }

                    override fun deliverResult(data: ArrayList<MovieData>?) {
                        mMovieData = data
                        super.deliverResult(data)
                    }

                    override fun loadInBackground(): ArrayList<MovieData>? {
                        val movieId = movieDetails.movie_id
                        println("ASYNC TRAILER LOAD STARTED: " + movieId!!)
                        try {
                            val trailerRequestUrl = NetworkUtils.trailersBuildUrl(movieId)

                            val jsonMovieDatabaseResponse = NetworkUtils.getResponseFromHttpUrl(trailerRequestUrl!!)

//Log.v(TAG, "ASYNCTASK: " + simpleJsonTrailerData.get(0).getTrailer_type());

                            return MovieDatabaseJsonUtils.getMovieTrailerData(jsonMovieDatabaseResponse!!)

                        } catch (e: Exception) {
                            e.printStackTrace()
                            return null
                        }

                    }
                }
            }

            ID_MOVIE_REVIEW_LOADER ->

                return object : AsyncTaskLoader<ArrayList<MovieData>>(this) {
                    var mMovieData: ArrayList<MovieData>? = null

                    override fun onStartLoading() {
                        if (mMovieData != null) {
                            deliverResult(mMovieData)
                            println("REVIEW DATA DELIVERED")
                        } else {
                            forceLoad()
                            println("REVIEW DATA FORCELOADED.")
                        }
                    }

                    override fun deliverResult(data: ArrayList<MovieData>?) {
                        mMovieData = data
                        super.deliverResult(data)
                    }

                    override fun loadInBackground(): ArrayList<MovieData>? {
                        val movieId = movieDetails.movie_id
                        println("ASYNC REVIEW LOAD STARTED: " + movieId!!)
                        try {
                            val movieRequestUrl = NetworkUtils.reviewsBuildUrl(movieId)
                            val jsonMDBResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl!!)

//Log.v(TAG, "ASYNCTASK: " + simpleJsonReviewData.get(0).getReview_author());

                            return MovieDatabaseJsonUtils.getMovieReviewData(jsonMDBResponse!!)

                        } catch (e: Exception) {
                            e.printStackTrace()
                            return null
                        }

                    }
                }

            else -> throw RuntimeException("Loader Not Implemented: $loaderId")
        }
    }

    override fun onLoadFinished(loader: Loader<ArrayList<MovieData>>, data: ArrayList<MovieData>) {

        if (loader.id == ID_MOVIE_TRAILER_LOADER) {
            //System.out.println("TRAILER load finished " + data.get(0).getTrailer_key());
            mTrailerAdapter = MovieTrailerAdapter(applicationContext, data)
            mMovieTrailerList!!.adapter = mTrailerAdapter
            supportLoaderManager.initLoader(ID_MOVIE_REVIEW_LOADER, null, this)
        }

        if (loader.id == ID_MOVIE_REVIEW_LOADER) {
            //System.out.println("REVIEW load finished " + data.get(0).getReview_author());

            mReviewAdapter = MovieReviewAdapter(applicationContext, data)
            mMovieReviewList!!.adapter = mReviewAdapter
        }

    }

    override fun onLoaderReset(loader: Loader<ArrayList<MovieData>>) {

    }

    private fun addFavoriteMovie() {
        val updateFavoriteMovie = ContentValues()
        updateFavoriteMovie.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieDetails.movie_id)
        updateFavoriteMovie.put(MovieContract.MovieEntry.COLUMN_TITLE, movieDetails.original_title)
        updateFavoriteMovie.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movieDetails.poster_path)
        updateFavoriteMovie.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movieDetails.backdrop_path)
        updateFavoriteMovie.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movieDetails.overview)
        updateFavoriteMovie.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieDetails.release_date)
        updateFavoriteMovie.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movieDetails.vote_average)
        updateFavoriteMovie.put(MovieContract.MovieEntry.COLUMN_FAVORITE_BOOL, java.lang.Boolean.toString(checkFavorite))
        Log.v(TAG, movieDetails.original_title + " " + checkFavorite)

        val insertCount = mDb.insert(MovieContract.MovieEntry.TABLE_NAME_MOVIE_MAIN, null, updateFavoriteMovie)
    }

    companion object {

        private const val ID_MOVIE_TRAILER_LOADER = 519
        private const val ID_MOVIE_REVIEW_LOADER = 119
    }

}
