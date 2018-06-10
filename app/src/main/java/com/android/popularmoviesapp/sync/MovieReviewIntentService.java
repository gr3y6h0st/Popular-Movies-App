package com.android.popularmoviesapp.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class MovieReviewIntentService extends IntentService {

    public MovieReviewIntentService() {
        super("MovieTrailerIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        MovieInfo.syncMovieReviewInfo(this);
    }
}
