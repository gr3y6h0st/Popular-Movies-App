package com.android.popularmoviesapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.databinding.DataBindingUtil;
import android.view.View;
import android.widget.ImageButton;

import com.android.popularmoviesapp.data.MovieData;
import com.android.popularmoviesapp.data.MovieDbHelper;
import com.android.popularmoviesapp.databinding.ActivityMovieDetailBinding;

import com.android.popularmoviesapp.data.MovieContract;
import com.android.popularmoviesapp.sync.FavoritesMovieIntentService;
import com.android.popularmoviesapp.sync.MovieInfo;
import com.android.popularmoviesapp.sync.MovieReviewIntentService;
import com.android.popularmoviesapp.utilities.MovieDatabaseJsonUtils;
import com.android.popularmoviesapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

import static com.android.popularmoviesapp.utilities.NetworkUtils.getResponseFromHttpUrl;

public class MovieDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ArrayList<MovieData>>{

    final String TAG = MovieDetailActivity.class.getSimpleName();

    public static final String[] MOVIE_DETAIL_PROJECTION = {
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

    public static final int INDEX_MOVIE_TITLE = 0;
    public static final int INDEX_MOVIE_VOTE_AVERAGE = 1;
    public static final int INDEX_MOVIE_POSTER_PATH = 2;
    public static final int INDEX_MOVIE_BACKDROP_PATH = 3;
    public static final int INDEX_MOVIE_RELEASE_DATE = 4;
    public static final int INDEX_MOVIE_OVERVIEW = 5;
    public static final int INDEX_MOVIE_ID = 6;

    public static final int INDEX_TRAILER_KEY = 7;
    public static final int INDEX_TRAILER_TYPE = 8;
    public static final int INDEX_TRAILER_NAME = 9;

    public static final int INDEX_REVIEW_AUTHOR = 10;
    public static final int INDEX_REVIEW_CONTENT = 11;
    public static final int INDEX_REVIEW_URL = 12;
    public static final int INDEX_FAVORITE_BOOL = 13;

    private static final int ID_MOVIE_TRAILER_LOADER = 519;
    private static final int ID_MOVIE_REVIEW_LOADER = 119;


    //private Uri mUri;
    //private Cursor mCursor;
    private Context mContext = MovieDetailActivity.this;

    private ActivityMovieDetailBinding mDetailBinding;

    private RecyclerView mMovieTrailerList;
    private RecyclerView mMovieReviewList;

    private MovieTrailerAdapter mTrailerAdapter;
    private MovieReviewAdapter mReviewAdapter;

    private int mPosition = RecyclerView.NO_POSITION;

    private boolean checkFavorite = false;

    FloatingActionButton favoriteFab;
    SQLiteDatabase mDb;

    MovieData movie_details;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_detail);

        mDetailBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_movie_detail);

        favoriteFab = mDetailBinding.favoriteFloatingActionButton;

        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView.LayoutManager layoutTrailerManager =
                new LinearLayoutManager(getApplicationContext());

        RecyclerView.LayoutManager layoutReviewManager =

                new LinearLayoutManager(getApplicationContext());

        mMovieTrailerList = mDetailBinding.mdTrailersRv;
        mMovieReviewList = mDetailBinding.mdReviewsRv;

        mMovieTrailerList.setLayoutManager(layoutTrailerManager);
        mMovieReviewList.setLayoutManager(layoutReviewManager);

        Intent intent = getIntent();
        if (intent == null) throw new NullPointerException("YOUR INTENT cannot be null");
        movie_details = (MovieData) intent.getSerializableExtra("movieDeets");

        String movieTitle = movie_details.getOriginal_title();

        String moviePoster = movie_details.getPoster_path();

        String movieBackdrop = movie_details.getBackdrop_path();

        String movieOverview = movie_details.getOverview();

        String movieReleaseDate = movie_details.getRelease_date();

        String movieRating = movie_details.getVote_average();

        //create the image url for poster and backdrop
        final String IMAGE_BASE = "http://image.tmdb.org/t/p/";
        final String IMAGE_SIZE_W500 = "w500";
        final String IMAGE_SIZE_ORIGINAL = "original";

        //obtain poster path from MovieData object
        String POSTER_IMAGE_URL = moviePoster;
        String BACKDROP_IMAGE_URL = movieBackdrop;

        String poster_url = IMAGE_BASE + IMAGE_SIZE_W500 + POSTER_IMAGE_URL;
        String backdrop_url = IMAGE_BASE + IMAGE_SIZE_ORIGINAL + BACKDROP_IMAGE_URL;
        //output the url into Log for debug purposes
        //Log.d(TAG, trailer_KEY);

        //use Picasso to place url image into imageview
        Picasso.get()
                .load(poster_url)
                .into(mDetailBinding.movieDetailIv);
        Picasso.get()
                .load(backdrop_url)
                .into(mDetailBinding.movieDetailBackdropIv);

        if (checkFavorite){
            checkFavorite = !checkFavorite;
            favoriteFab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic__favorite_movie_action_on));
        } else {
            checkFavorite = !checkFavorite;
            favoriteFab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic__favorite_movie_action_off));
        }

        favoriteFab.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (checkFavorite) {
                   checkFavorite = !checkFavorite;
                   //TODO: find a way to write boolean value to cursor
                   favoriteFab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                           R.drawable.ic__favorite_movie_action_off));
               } else {
                   checkFavorite = !checkFavorite;
                   favoriteFab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                           R.drawable.ic__favorite_movie_action_on));

               }
           }
        });

        mDetailBinding.mdOriginalTitle.setText(movieTitle);
        mDetailBinding.mdVoteAvg.setText(movieRating);
        mDetailBinding.mdReleaseDate.setText(movieReleaseDate);
        mDetailBinding.mdPlotSynopsis.setText(movieOverview);

        //mDetailBinding.mdOriginalTitleLabel.setText(getString(R.string.md_original_title_label));
        mDetailBinding.mdPlotSynopsisLabel.setText(getString(R.string.md_synopsis_label));
        mDetailBinding.mdReleaseDateLabel.setText(getString(R.string.md_date_label));
        mDetailBinding.mdVoteAvgLabel.setText(getString(R.string.md_rating_label));

        String movie_ID = movie_details.getMovie_id();
        System.out.println(movie_ID);

        getSupportLoaderManager().initLoader(ID_MOVIE_TRAILER_LOADER, null, this);

    }

    //will set BACK BUTTON to navigate back to previous activity w/o recreating it.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemClicked = item.getItemId();
        Context context = MovieDetailActivity.this;


        switch (itemClicked) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @NonNull
    @Override
    public Loader<ArrayList<MovieData>> onCreateLoader(int loaderId, @Nullable Bundle args) {

        switch (loaderId) {

            case ID_MOVIE_TRAILER_LOADER:
                System.out.println(loaderId);

                return new AsyncTaskLoader<ArrayList<MovieData>>(this) {
                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        forceLoad();
                    }

                    @Nullable
                    @Override
                    public ArrayList<MovieData> loadInBackground() {
                        String movie_ID = movie_details.getMovie_id();
                        System.out.println("ASYNC LOAD STARTED: " + movie_ID);
                        try{
                            URL trailerRequestUrl = NetworkUtils.trailersBuildUrl(movie_ID);

                            String jsonMovieDatabaseResponse = getResponseFromHttpUrl(trailerRequestUrl);

                            ArrayList<MovieData> simpleJsonTrailerData = MovieDatabaseJsonUtils.getMovieTrailerData(jsonMovieDatabaseResponse);

                            Log.v(TAG, "ASYNCTASK: " + simpleJsonTrailerData.get(0).getTrailer_type());

                            return simpleJsonTrailerData;

                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };

            case ID_MOVIE_REVIEW_LOADER:

                return new AsyncTaskLoader<ArrayList<MovieData>>(this) {
                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        forceLoad();
                    }

                    @Nullable
                    @Override
                    public ArrayList<MovieData> loadInBackground() {
                        String movie_ID = movie_details.getMovie_id();
                        System.out.println("ASYNC LOAD STARTED: " + movie_ID);
                        try {
                            URL movieRequestUrl = NetworkUtils.reviewsBuildUrl(movie_ID);
                            String jsonMDBResponse = getResponseFromHttpUrl(movieRequestUrl);

                            ArrayList<MovieData> simpleJsonReviewData = MovieDatabaseJsonUtils.getMovieReviewData(jsonMDBResponse);

                            Log.v(TAG, "ASYNCTASK: " + simpleJsonReviewData.get(0).getReview_author());

                            return simpleJsonReviewData;

                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            default:
                throw new RuntimeException("Loader Not Implemented: "  + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<MovieData>> loader, ArrayList<MovieData> data) {

        if(loader.getId() == ID_MOVIE_TRAILER_LOADER) {
            getSupportLoaderManager().restartLoader(ID_MOVIE_REVIEW_LOADER, null, this);
            //System.out.println("TRAILER load finished " + data.get(0).getTrailer_key());
            mTrailerAdapter = new MovieTrailerAdapter(5,
                    getApplicationContext(), data);
            mMovieTrailerList.setAdapter(mTrailerAdapter);
        }


        if (loader.getId() == ID_MOVIE_REVIEW_LOADER){
            //System.out.println("REVIEW load finished " + data.get(0).getReview_author());

            mReviewAdapter = new MovieReviewAdapter(getApplicationContext(), data);
            mMovieReviewList.setAdapter(mReviewAdapter);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<MovieData>> loader) {
        //mTrailerAdapter.swapCursor(null);
        //mReviewAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_detail_menu, menu);
        return true;
    }

}
