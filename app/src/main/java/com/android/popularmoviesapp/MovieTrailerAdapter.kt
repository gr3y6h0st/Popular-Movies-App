package com.android.popularmoviesapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.android.popularmoviesapp.data.MovieData
import java.util.*


class MovieTrailerAdapter(private var mContext: Context?, private val data: ArrayList<MovieData>?) : RecyclerView.Adapter<MovieTrailerAdapter.MovieDetailHolder>() {

    /*
     * Cache of children views for list item.
     */

    inner class MovieDetailHolder//Constructor for ViewHolder, references TextViews and sets onClickListener to listen for clicks
    //via onClick method.
    (view: View) : RecyclerView.ViewHolder(view) {
        internal val listTrailerView: Button = view.findViewById(R.id.movie_trailer_button)

        init {
            //Call setOnClickListener to the View passed into constructor
            listTrailerView.setOnClickListener {
                //keeps track of position of item being clicked
                val clickedPosition = adapterPosition
                if (data == null) {
                    Toast.makeText(mContext, mContext!!.getString(R.string.No_trailer_text), Toast.LENGTH_SHORT).show()
                } else {
                    val trailerKey = data[clickedPosition].trailer_key
                    val openYTApp = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerKey!!))
                    mContext!!.startActivity(openYTApp)
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MovieDetailHolder {
        mContext = viewGroup.context
        //Log.d("onCreateViewHolder", "onCreateViewHolder Started");

        val layoutIdForListItem = R.layout.trailers_list_item
        val view = LayoutInflater
                .from(mContext)
                .inflate(layoutIdForListItem, viewGroup, false)

        return MovieDetailHolder(view)
    }

    /**
     * onBindViewHolder displays data at specified position.
     * Method below updates contents of ViewHolder to display correct indices in list for particular position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of item at given position
     * in data set.
     * @param position The position of item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: MovieDetailHolder, position: Int) {
        val noTrailersAvailable = mContext!!.getString(R.string.No_trailer_text)
        if (data == null) {
            holder.listTrailerView.text = noTrailersAvailable
        } else {
            val trailerName = data[position].trailer_name
            if (trailerName != null) {
                holder.listTrailerView.text = trailerName
            } else {
                holder.listTrailerView.text = noTrailersAvailable
            }
        }
    }

    /**
     * Method returns number of items to display, used to help layout Views.
     * @return # of items available
     */
    override fun getItemCount(): Int {
        return data?.size ?: 1
    }

    companion object {

        private val TAG = MovieTrailerAdapter::class.java.simpleName
    }
}
