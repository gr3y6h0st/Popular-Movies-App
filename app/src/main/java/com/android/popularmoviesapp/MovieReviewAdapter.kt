package com.android.popularmoviesapp

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.popularmoviesapp.data.MovieData
import java.util.*


class MovieReviewAdapter(private var mContext: Context?, private val reviewData: ArrayList<MovieData>?) : RecyclerView.Adapter<MovieReviewAdapter.MovieDetailHolder>() {

    private var mCount: Int = 0

    /*
     * Cache of children views for list item.
     */
    inner class MovieDetailHolder//Constructor for ViewHolder, references TextViews and sets onClickListener to listen for clicks
    //via onClick method.
    (view: View) : RecyclerView.ViewHolder(view) {
        internal val listReviewView: TextView = view.findViewById(R.id.movie_review_tv)
        internal val listReviewUrlView: TextView = view.findViewById(R.id.md_review_url)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MovieDetailHolder {
        mContext = viewGroup.context
        val layoutIdForListItem = R.layout.reviews_list_item

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

        val noReviewsAvailable = mContext!!.getString(R.string.No_Review_Text)

        if (reviewData == null) {
            holder.listReviewView.text = noReviewsAvailable
            holder.listReviewView.setTextColor(mContext!!.resources.getColor(R.color.gray_400))
            //holder.listReviewUrlView.setVisibility(View.INVISIBLE);
        } else {
            val reviewContent = reviewData[position].review_content
            /** Credit for hyperlink code in listview to: emmby from Stack Overflow.
             * Source: https://stackoverflow.com/questions/1697908/android-how-can-i-add-html-links-inside-a-listview
             * Creates a link using the review url taken from the MovieDataBase.
             */
            val reviewUrl = "<a href=" + reviewData[position].review_url + ">READ MORE</a>"
            //Log.v(TAG, review_url);
            //Log.v(TAG, review_content);

            if (reviewContent != null) {
                holder.listReviewView.text = reviewContent
                holder.listReviewView.setTextColor(mContext!!.resources.getColor(R.color.gray_400))
                holder.listReviewUrlView.visibility = View.VISIBLE
                holder.listReviewUrlView.movementMethod = LinkMovementMethod.getInstance()
                holder.listReviewUrlView.text = Html.fromHtml(reviewUrl)
            } else {
                holder.listReviewView.text = noReviewsAvailable
                holder.listReviewView.setTextColor(mContext!!.resources.getColor(R.color.gray_400))
                holder.listReviewUrlView.visibility = View.INVISIBLE
            }
        }
    }

    /**
     * Method returns number of items to display, used to help layout Views.
     * @return # of items available
     */
    override fun getItemCount(): Int {
        if (null == reviewData) return 1
        mCount = reviewData.size
        return mCount
    }

    companion object {

        private val TAG = MovieReviewAdapter::class.java.simpleName
    }
}
