package com.android.popularmoviesapp.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

public class MovieTrailerIntentService extends IntentService {

    public MovieTrailerIntentService() {
        super("MovieTrailerIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //MovieInfo.syncMovieTrailerInfo(this);
    }
}
