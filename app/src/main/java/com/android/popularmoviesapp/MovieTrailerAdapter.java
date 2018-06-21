package com.android.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.MovieDetailHolder> {

    private static final String TAG = MovieTrailerAdapter.class.getSimpleName();

    //final private MovieDetailAdapterOnClickListener mOnClickListener;

    /*public interface MovieDetailAdapterOnClickListener {
        void onItemClick (View view, String movie_id);
    }*/

    //private ArrayList<MovieData> data;

    private Context mContext;

    private Cursor mCursor;

    private int mCount;

    public MovieTrailerAdapter(int trailer_count) {
        //mContext = context;

        mCount = trailer_count;
        //this.mOnClickListener = listener;

    }

    /*
     * Cache of children views for list item.
     */
    public class MovieDetailHolder extends RecyclerView.ViewHolder {
        private final Button listTrailerView;
        //Constructor for ViewHolder, references TextViews and sets onClickListener to listen for clicks
        //via onClick method.
        public MovieDetailHolder(View view){
            super(view);
            listTrailerView = (Button) view.findViewById(R.id.movie_trailer_button);
            //Call setOnClickListener to the View passed into constructor
            listTrailerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //keeps track of position of item being clicked
                    int clickedPosition = getAdapterPosition();
                    mCursor.moveToPosition(clickedPosition);
                    String trailer_key = mCursor.getString(MovieDetailActivity.INDEX_TRAILER_KEY);

                    //Log.d(TAG, trailer_key);
                    Intent openYTApp = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer_key));
                    mContext.startActivity(openYTApp);
                }
            });
        }
    }

    @NonNull
    @Override
    public MovieDetailHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        //Log.d("onCreateViewHolder", "onCreateViewHolder Started");

        int layoutIdForListItem = R.layout.trailers_list_item;
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
        String no_trailers_available = mContext.getString(R.string.No_trailer_text);
        if(mCursor == null){
            holder.listTrailerView.setText(no_trailers_available);
        }
         else {
            mCursor.moveToPosition(position);
            /**
             * change code to reflect MOVIE TRAILER NAME
             **/

            String trailer_name = mCursor.getString(MovieDetailActivity.INDEX_TRAILER_NAME);
            if (trailer_name!= null){
                holder.listTrailerView.setText(trailer_name);

            } else {
                holder.listTrailerView.setText(no_trailers_available);
            }
        //move cursor to appropriate position
        //Log.d(TAG, "#" + position);

        }//Log.d("onBindViewHolder", "onBindViewHolder"+ position);

        //final String TRAILER_BASE = "http://image.tmdb.org/t/p/";

        //image size on recyclerView, not in the MovieDetail page!
        //final String IMAGE_SIZE = "w342";

        //String IMAGE_URL = mCursor.getString(MainActivity.INDEX_POSTER_PATH);
        //Log.d(TAG, "url" + IMAGE_URL);
        //String url = IMAGE_BASE + IMAGE_SIZE + IMAGE_URL;

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
