package com.android.popularmoviesapp.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class MovieInfoQueryIntentService extends IntentService{

    public MovieInfoQueryIntentService() {
        super("MovieInfoQueryIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        if(intent.getStringExtra("query_type").equals("popular")){
            MovieInfo.queryPopularMovieInfo(this);
            System.out.println("starting pop-query");
        } else {
            MovieInfo.queryTopRatedMovieInfo(this);
            System.out.println("starting top_rated-query");

        }
    }
}
