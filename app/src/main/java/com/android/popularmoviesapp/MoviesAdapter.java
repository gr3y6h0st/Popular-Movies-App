package com.android.popularmoviesapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.popularmoviesapp.data.MovieData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private static final String TAG = MoviesAdapter.class.getSimpleName();

    final private ListItemClickListener mOnClickListener;

    private ArrayList<MovieData> data;

    private Context context;

    MoviesAdapter(Context context, ArrayList<MovieData> movieData, ListItemClickListener listener) {
        this.context = context;
        this.data = movieData;
        this.mOnClickListener = listener;

    }

    public interface ListItemClickListener {
        void onListItemClick (int clickedItemIndex);
    }

    /*
     * Cache of children views for list item.
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView listImageView;
        //Constructor for ViewHolder, references TextViews and sets onClickListener to listen for clicks
        //via onClick method.
        MovieViewHolder(View view){
            super(view);
            listImageView = (ImageView) view.findViewById(R.id.movies_iv);
            //Call setOnClickListener to the View passed into constructor
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            //keeps track of position of item being clicked
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
            }

    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movies_list_item;

        LayoutInflater inflater = LayoutInflater.from(context);

        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);

        MovieViewHolder viewHolder = new MovieViewHolder(view);

        return viewHolder;
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
        Log.d(TAG, "#" + position);
        final String IMAGE_BASE = "http://image.tmdb.org/t/p/";

        //image size on recyclerView, not in the MovieDetail page!
        final String IMAGE_SIZE = "w342";
        if(data == null){
            return;
        }
        String IMAGE_URL = data.get(position).getPoster_path();
        Log.d(TAG, "url" + IMAGE_URL);
        String url = IMAGE_BASE + IMAGE_SIZE + IMAGE_URL;

        Picasso.get().load(url).into(holder.listImageView);
    }

    /**
     * Method returns number of items to display, used to help layout Views.
     * @return # of items available
     */
    @Override
    public int getItemCount() {
        return data.size();
    }


}
