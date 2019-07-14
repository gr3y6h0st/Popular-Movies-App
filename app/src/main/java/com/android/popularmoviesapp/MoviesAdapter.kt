package com.android.popularmoviesapp

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.android.popularmoviesapp.data.MovieData
import com.squareup.picasso.Picasso
import java.util.*


class MoviesAdapter(private val mContext: Context, private var data: ArrayList<MovieData>?, private val mOnClickListener: MoviesAdapterOnClickListener) : RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {

    interface MoviesAdapterOnClickListener {

        fun onItemClick(clickedPosition: Int)
    }

    /*
     * Cache of children views for list item.
     */
    inner class MovieViewHolder//Constructor for ViewHolder, references TextViews and sets onClickListener to listen for clicks
    //via onClick method.
    internal constructor(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        internal val listImageView: ImageView = view.findViewById(R.id.movies_iv)

        init {
            //Call setOnClickListener to the View passed into constructor
            view.setOnClickListener(this)

        }

        override fun onClick(view: View) {
            //keeps track of position of item being clicked
            val clickedPosition = adapterPosition
            mOnClickListener.onItemClick(clickedPosition)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MovieViewHolder {

        val layoutIdForListItem = R.layout.movies_list_item

        val view = LayoutInflater
                .from(viewGroup.context)
                .inflate(layoutIdForListItem, viewGroup, false)

        return MovieViewHolder(view)
    }

    /**
     * onBindViewHolder displays data at specified position.
     * Method below updates contents of ViewHolder to display correct indices in list for particular position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of item at given position
     * in data set.
     * @param position The position of item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        if (data == null) {
            return
        }
        Log.d(TAG, "#$position")

        val IMAGE_BASE = "http://image.tmdb.org/t/p/"

        //image size on recyclerView
        val IMAGE_SIZE = "w342"

        val IMAGE_URL = data!![position].poster_path
        //Log.d(TAG, "url" + IMAGE_URL);
        val url = IMAGE_BASE + IMAGE_SIZE + IMAGE_URL

        //display the images into the holder
        Picasso.get()
                .load(url)
                .placeholder(R.color.gray_900)
                .error(R.drawable.ic_launcher_background)
                .into(holder.listImageView)
    }

    /**
     * Method returns number of items to display, used to help layout Views.
     * @return # of items available
     */
    override fun getItemCount(): Int {
        return if (null == data) 0 else data!!.size
    }

    fun notifyMovieDataChange(movieData: ArrayList<MovieData>) {
        data = ArrayList()
        data = movieData

        // data.addAll(movieData);
        notifyDataSetChanged()
    }

    companion object {
        private val TAG = MoviesAdapter::class.java.simpleName
    }


}
