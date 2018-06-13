package com.android.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieDetailHolder> {

    private static final String TAG = MovieReviewAdapter.class.getSimpleName();

    //final private MovieDetailAdapterOnClickListener mOnClickListener;

    /*public interface MovieDetailAdapterOnClickListener {
        void onItemClick (View view, String movie_id);
    }*/

    //private ArrayList<MovieData> data;

    private Context mContext;

    private Cursor mCursor;

    private int mCount;

    public MovieReviewAdapter(int review_count) {
        //mContext = context;

        mCount = review_count;
        //this.mOnClickListener = listener;

    }

    /*
     * Cache of children views for list item.
     */
    public class MovieDetailHolder extends RecyclerView.ViewHolder {
        private final TextView listReviewView;
        private final TextView listReviewUrlView;
        //Constructor for ViewHolder, references TextViews and sets onClickListener to listen for clicks
        //via onClick method.
        public MovieDetailHolder(View view){
            super(view);
            listReviewView = (TextView) view.findViewById(R.id.movie_review_tv);
            listReviewUrlView = (TextView) view.findViewById(R.id.md_review_url);
            //view.setOnClickListener(this);
            //Call setOnClickListener to the View passed into constructor
            /*listReviewView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //keeps track of position of item being clicked
                    int clickedPosition = getAdapterPosition();
                    mCursor.moveToPosition(clickedPosition);
                    String trailer_key = mCursor.getString(MovieDetailActivity.INDEX_TRAILER_KEY);

                    Log.d(TAG, trailer_key);
                    Intent openYTApp = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer_key));
                    mContext.startActivity(openYTApp);
                }
            });*/
        }
    }

    @NonNull
    @Override
    public MovieDetailHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        Log.d("onCreateViewHolder", "onCreateViewHolder for REVIEW Started");

        int layoutIdForListItem = R.layout.reviews_list_item;
        boolean shouldAttachToParentImmediately = false;

        View view = LayoutInflater
                .from(mContext)
                .inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);

        return new MovieDetailHolder(view);
    }

    /**
     * onBindViewHolder displays data at specified position.
     * Method below updates contents of ViewHolder to display correct indices in list for particular position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of item at given position
     *               in data set.
     * @param position The position of item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MovieDetailHolder holder, int position) {
        String no_reviews_available = mContext.getString(R.string.No_Review_Text);
        //Log.d(TAG, mCursor.getString(MovieDetailActivity.INDEX_REVIEW_AUTHOR));

        if(mCursor == null){
            holder.listReviewView.setText(mContext.getString(R.string.Cursor_is_null_adapter));
            holder.listReviewUrlView.setVisibility(View.INVISIBLE);
        }
         else {
            mCursor.moveToPosition(position);
            String review_content = mCursor.getString(MovieDetailActivity.INDEX_REVIEW_CONTENT);


            /** Credit for hyperlink code in listview to: emmby from Stack Overflow.
             * Source: https://stackoverflow.com/questions/1697908/android-how-can-i-add-html-links-inside-a-listview
             * Creates a link using the review url taken from the MovieDataBase.
             */
            String review_url = "<a href=" + mCursor.getString(MovieDetailActivity.INDEX_REVIEW_URL) + ">READ MORE</a>";
            Log.v(TAG, review_url);
            if (review_content != null) {

                holder.listReviewView.setText(review_content);
                holder.listReviewView.setTextColor(mContext.getResources().getColor(R.color.gray_400));
                holder.listReviewUrlView.setVisibility(View.VISIBLE);
                holder.listReviewUrlView.setMovementMethod(LinkMovementMethod.getInstance());
                holder.listReviewUrlView.setText(Html.fromHtml(review_url));
            } else {
                holder.listReviewView.setText(no_reviews_available);
                holder.listReviewUrlView.setVisibility(View.INVISIBLE);
            }
        //move cursor to appropriate position
        Log.d(TAG, "#" + position);

        }Log.d("onBindViewHolder", "onBindViewHolder"+ position);

    }

    /**
     * Method returns number of items to display, used to help layout Views.
     * @return # of items available
     */
    @Override
    public int getItemCount() {
        if (null == mCursor) return 1;
        return mCursor.getCount();
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }


}
