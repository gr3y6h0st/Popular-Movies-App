package com.android.popularmoviesapp.data;

import java.io.Serializable;


// implement Serializable for use of data between Main --> MovieDetail activities
public class MovieData implements Serializable{

    private String original_title;
    private String poster_path;
    private String overview;
    private String vote_average;
    private String release_date;

    private String trailer_name;
    private String trailer_key;
    private String trailer_site;
    private String trailer_id;
    private String trailer_type;
    private static boolean favorite_movie = false;

    /**
     * No args constructor, use in serialization
     */
    public MovieData() {
    }

    public MovieData(String name, String key, String site, String id, String type) {
        this.trailer_name = name;
        this.trailer_key = key;
        this.trailer_site = site;
        this.trailer_id = id;
        this.trailer_type= type;
    }

    //trailerSpecifics.put(MovieContract.MovieEntry.COLUMN_TRAILER_NAME, name);
    //                trailerSpecifics.put(MovieContract.MovieEntry.COLUMN_TRAILER_KEY, key);
    //                trailerSpecifics.put(MovieContract.MovieEntry.COLUMN_TRAILER_SITE, site);
    //                trailerSpecifics.put(MovieContract.MovieEntry.COLUMN_TRAILER_ID, id);
    //                trailerSpecifics.put(MovieContract.MovieEntry.COLUMN_TRAILER_TYPE, type);

    public MovieData (boolean check_favorite_movie){
        this.favorite_movie = favorite_movie;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public boolean checkFavorite_movie() {
        return favorite_movie;
    }

    public void setFavorite_movie(boolean new_fav_movie){
        favorite_movie = new_fav_movie;
    }
}
