package com.android.popularmoviesapp;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.popularmoviesapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private static final String TAG = MoviesAdapter.class.getSimpleName();

    final private MoviesAdapterOnClickListener mOnClickListener;

    public interface MoviesAdapterOnClickListener {
        void onItemClick (View view, String movie_id);
    }

    //private ArrayList<MovieData> data;

    private Context mContext;

    private Cursor mCursor;

    private int mCount;

    public MoviesAdapter(Context context, MoviesAdapterOnClickListener listener) {
        mContext = context;
        //mCount = count;
        this.mOnClickListener = listener;

    }

    /*
     * Cache of children views for list item.
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView listImageView;
        //Constructor for ViewHolder, references TextViews and sets onClickListener to listen for clicks
        //via onClick method.
        MovieViewHolder(View view){
            super(view);
            listImageView = view.findViewById(R.id.movies_iv);
            //Call setOnClickListener to the View passed into constructor
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            //keeps track of position of item being clicked
            int clickedPosition = this.getAdapterPosition();
            mCursor.moveToPosition(clickedPosition);
            String movie_id = mCursor.getString(MainActivity.INDEX_COLUMN_MOVIE_ID);
            //String trailer_key = mCursor.getString(MainActivity.INDEX_COLUMN_TRAILER_KEY);
            //Log.d(TAG, "url" + trailer_key);
            NetworkUtils.setMovieID(movie_id);

            mOnClickListener.onItemClick(view, movie_id);

            }

    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        int layoutIdForListItem = R.layout.movies_list_item;
        boolean shouldAttachToParentImmediately = false;

        View view = LayoutInflater
                .from(mContext)
                .inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);

        return new MovieViewHolder(view);
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
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        if(mCursor == null){
            return;
        }
        //move cursor to appropriate position
        mCursor.moveToPosition(position);

        //Log.d(TAG, "#" + position);

        final String IMAGE_BASE = "http://image.tmdb.org/t/p/";

        //image size on recyclerView, not in the MovieDetail page!
        final String IMAGE_SIZE = "w342";

        String IMAGE_URL = mCursor.getString(MainActivity.INDEX_POSTER_PATH);
        //Log.d(TAG, "url" + IMAGE_URL);
        String url = IMAGE_BASE + IMAGE_SIZE + IMAGE_URL;

        //display the images into the holder
        Picasso.get()
                .load(url)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.listImageView);


    }

    /**
     * Method returns number of items to display, used to help layout Views.
     * @return # of items available
     */
    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }


}
