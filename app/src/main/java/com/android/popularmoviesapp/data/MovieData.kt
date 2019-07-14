package com.android.popularmoviesapp.data

import java.io.Serializable


// implement Serializable for use of data between Main --> MovieDetail activities
class MovieData : Serializable {

    var original_title: String? = null
    var poster_path: String? = null
    var backdrop_path: String? = null
    var overview: String? = null
    var vote_average: String? = null
    var release_date: String? = null
    var movie_id: String? = null

    var trailer_name: String? = null
    var trailer_key: String? = null
    var trailer_site: String? = null
    var trailer_id: String? = null
    var trailer_type: String? = null
    var trailer_count: String? = null

    var review_url: String? = null
    var review_author: String? = null
    var review_content: String? = null

    var check_favorite_movie: String? = null

    /**
     * No args constructor, use in serialization
     */
    constructor() {}

    constructor(movieID: String, originalTitle: String, posterPath: String, backdropPath: String,
                voteAverage: String, releaseDate: String, overview: String) {

        this.movie_id = movieID
        this.original_title = originalTitle
        this.poster_path = posterPath
        this.backdrop_path = backdropPath
        this.vote_average = voteAverage
        this.release_date = releaseDate
        this.overview = overview
    }

    constructor(movieID: String, originalTitle: String, posterPath: String, backdropPath: String,
                voteAverage: String, releaseDate: String, overview: String, checkfavorite: String) {

        this.movie_id = movieID
        this.original_title = originalTitle
        this.poster_path = posterPath
        this.backdrop_path = backdropPath
        this.vote_average = voteAverage
        this.release_date = releaseDate
        this.overview = overview
        this.check_favorite_movie = checkfavorite
    }

    constructor(name: String, id: String, key: String, site: String,
                type: String) {

        this.trailer_name = name
        this.trailer_id = id
        this.trailer_key = key
        this.trailer_site = site
        this.trailer_type = type
    }

    constructor(author: String, content: String, url: String) {

        this.review_author = author
        this.review_content = content
        this.review_url = url
    }
}
