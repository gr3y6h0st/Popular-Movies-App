package com.android.popularmoviesapp.data;

import java.io.Serializable;


// implement Serializable for use of data between Main --> MovieDetail activities
public class MovieData implements Serializable{

    private String original_title;
    private String poster_path;
    private String backdrop_path;
    private String overview;
    private String vote_average;
    private String release_date;
    private String movie_id;

    private String trailer_name;
    private String trailer_key;
    private String trailer_site;
    private String trailer_id;
    private String trailer_type;
    private String trailer_count;

    private String review_url;
    private String review_author;
    private String review_content;

    private boolean favorite_movie = false;

    /**
     * No args constructor, use in serialization
     */
    public MovieData() {
    }

    public MovieData(String movieID, String originalTitle, String posterPath, String backdropPath,
                     String voteAverage, String releaseDate, String overview) {

        this.movie_id = movieID;
        this.original_title = originalTitle;
        this.poster_path = posterPath;
        this.backdrop_path = backdropPath;
        this.vote_average = voteAverage;
        this.release_date = releaseDate;
        this.overview = overview;
    }

    public MovieData(String name, String id, String key, String site,
                     String type) {

        this.trailer_name = name;
        this.trailer_id = id;
        this.trailer_key = key;
        this.trailer_site = site;
        this.trailer_type = type;
    }

    public MovieData(String author, String content, String url) {

        this.review_author = author;
        this.review_content = content;
        this.review_url = url;
    }

    public MovieData (boolean check_favorite_movie){
        this.favorite_movie = check_favorite_movie;
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

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
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

    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    public String getTrailer_name() {
        return trailer_name;
    }

    public void setTrailer_name(String trailer_name) {
        this.trailer_name = trailer_name;
    }

    public String getTrailer_key() {
        return trailer_key;
    }

    public void setTrailer_key(String trailer_key) {
        this.trailer_key = trailer_key;
    }

    public String getTrailer_site() {
        return trailer_site;
    }

    public void setTrailer_site(String trailer_site) {
        this.trailer_site = trailer_site;
    }

    public String getTrailer_id() {
        return trailer_id;
    }

    public void setTrailer_id(String trailer_id) {
        this.trailer_id = trailer_id;
    }

    public String getTrailer_type() {
        return trailer_type;
    }

    public void setTrailer_type(String trailer_type) {
        this.trailer_type = trailer_type;
    }

    public String getTrailer_count() {
        return trailer_count;
    }

    public void setTrailer_count(String trailer_count) {
        this.trailer_count = trailer_count;
    }

    public String getReview_url() {
        return review_url;
    }

    public void setReview_url(String review_url) {
        this.review_url = review_url;
    }

    public String getReview_author() {
        return review_author;
    }

    public void setReview_author(String review_author) {
        this.review_author = review_author;
    }

    public String getReview_content() {
        return review_content;
    }

    public void setReview_content(String review_content) {
        this.review_content = review_content;
    }
}
