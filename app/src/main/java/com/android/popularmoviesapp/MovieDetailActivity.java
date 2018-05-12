package com.android.popularmoviesapp;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.popularmoviesapp.data.MovieData;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {
    final String TAG = MovieDetailActivity.class.getSimpleName();
    MovieData movie_details;
    ImageView imageViewPoster;
    TextView orig_Title;
    TextView release_date_tv;
    TextView vote_avg_tv;
    TextView plot_synopsis_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //find and set views to respective ids
        imageViewPoster = findViewById(R.id.movie_detail_iv);
        orig_Title = findViewById(R.id.md_original_title);
        release_date_tv = findViewById(R.id.md_release_date);
        vote_avg_tv = findViewById(R.id.md_vote_avg);
        plot_synopsis_tv = findViewById(R.id.md_plot_synopsis);

        //get the Intent from Parent Activity
        Intent intent =  getIntent();
        //create variable from the paired data from intent, which should be the MovieDataArrayList
        movie_details = (MovieData) intent.getSerializableExtra("movie_deets");

        //create the image url for poster
        final String IMAGE_BASE = "http://image.tmdb.org/t/p/";
        final String IMAGE_SIZE = "w500";
        //obtain poster path from MovieData object
        String IMAGE_URL = movie_details.getPoster_path();
        String url = IMAGE_BASE + IMAGE_SIZE + IMAGE_URL;
        //output the url into Log for debug purposes
        Log.d(TAG, url);
        //use Picasso to place url image into imageview
        Picasso.get().load(url).into(imageViewPoster);

        //get original title and set into proper TextView
        String original_title = movie_details.getOriginal_title();
        orig_Title.setText(original_title);
        //get and set release date
        String release_date = movie_details.getRelease_date();
        release_date_tv.setText(release_date);
        //get and set vote rating
        String vote_avg = movie_details.getVote_average();
        vote_avg_tv.setText(vote_avg);
        //get and set synopsis
        String overview = movie_details.getOverview();
        plot_synopsis_tv.setText(overview);

    }

    //will set BACK BUTTON to navigate back to previous activity w/o recreating it.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if( id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
