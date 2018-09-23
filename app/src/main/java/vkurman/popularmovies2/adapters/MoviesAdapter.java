/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vkurman.popularmovies2.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import vkurman.popularmovies2.R;
import vkurman.popularmovies2.model.Movie;
import vkurman.popularmovies2.persistance.MoviesContract;
import vkurman.popularmovies2.utils.MovieUtils;

/**
 * Project Popular Movies stage 2.
 * Created by Vassili Kurman on 06/03/2018.
 * Version 2.0
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    /**
     * An on-click handler that allows for an Activity to interface with RecyclerView
     */
    final private MovieClickListener mMovieClickListener;
    /**
     * Reference to list of items.
     */
    private List<Movie> movies;
    private Map<Long, Long> favourites;

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieClickListener {
        void onMovieClicked(Movie movie);
    }

    /**
     * Constructor for Adapter that accepts a number of items to display and ClickListener.
     *
     * @param listOfItems list of items to display
     * @param listener Listener for list item clicks
     */
    public MoviesAdapter(List<Movie> listOfItems, MovieClickListener listener) {
        this.movies = listOfItems;
        mMovieClickListener = listener;
        favourites = new TreeMap<>();
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new MovieViewHolder that holds the View for each list item
     */
    @Override
    @NonNull
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_layout;

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new MovieViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the correct
     * indices in the list for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        if(position >= 0 && position < movies.size()) {
            final Movie movie = movies.get(position);
            final long movieId = movie.getMovieId();

            String imagePath = MovieUtils.createFullIconPath(movie.getMoviePoster());

            holder.itemView.setTag(movieId);

            Context context = holder.posterImageView.getContext();
            Picasso.get()
                    .load(imagePath)
                    .placeholder(R.drawable.ic_image_area)
                    .error(R.drawable.ic_error_image)
                    .into(holder.posterImageView);

            // Making sure that favourites not null
            if(favourites == null) {
                loadFavouriteMovieIds(context);
            }

            if (favourites.get(movieId) != null) {
                holder.favouriteImageView.setImageResource(R.drawable.ic_heart);
            } else {
                holder.favouriteImageView.setImageResource(R.drawable.ic_heart_outline);
            }
        }
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout Views and for animations.
     *
     * @return The number of items available
     */
    @Override
    public int getItemCount() {
        if(movies == null) {
            return 0;
        }
        return movies.size();
    }

    /**
     * Updating movies list.
     *
     * @param movies - list of movies
     */
    public void updateData(Context context, List<Movie> movies) {
        this.movies = movies;
        loadFavouriteMovieIds(context);
        notifyDataSetChanged();
    }

    private void favouriteClicked(View view, Movie movie) {
        if(favourites == null) {
            loadFavouriteMovieIds(view.getContext());
        }

        if(view.getId() == R.id.iv_favourite) {
            if(favourites.containsKey(movie.getMovieId())) {
                Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
                uri = uri.buildUpon().build();

                String where = MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(movie.getMovieId())};

                AppCompatActivity context = (AppCompatActivity) view.getContext();
                context.getContentResolver().delete(uri, where, selectionArgs);

                ((ImageView)view).setImageResource(R.drawable.ic_heart_outline);
                // Removing from favourites map
                favourites.remove(movie.getMovieId());
                // Displaying toast
                Toast.makeText(context, "Removed from favourites!", Toast.LENGTH_SHORT).show();
            } else {
                long id = movie.getMovieId();
                ContentValues cv = new ContentValues();
                cv.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, movie.getMovieId());
                cv.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER, movie.getMoviePoster());
                cv.put(MoviesContract.MoviesEntry.COLUMN_TITLE, movie.getTitle());
                cv.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                cv.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
                cv.put(MoviesContract.MoviesEntry.COLUMN_PLOT_SYNOPSIS, movie.getPlotSynopsis());

                view.getContext().getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, cv);

                ((ImageView)view).setImageResource(R.drawable.ic_heart);

                // Adding to favourites map
                favourites.put(id, id);
                // Displaying toast
                Toast.makeText(view.getContext(), "Added to favourites!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Loading Favourite movies from content provider
     * @param context - Context
     */
    private void loadFavouriteMovieIds(Context context) {
        if(favourites != null) {
            favourites.clear();
        }

        String[] projection = new String[] {MoviesContract.MoviesEntry.COLUMN_MOVIE_ID};
        Cursor cursor = context.getContentResolver().query(
                MoviesContract.MoviesEntry.CONTENT_URI,
                    projection,
                null,
                null,
                null
                );
        while (cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID));
            favourites.put(id, id);
        }
        cursor.close();
    }

    /**
     * Cache of the children views for a list item.
     */
    class MovieViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView posterImageView;
        ImageView favouriteImageView;

        /**
         * Constructor for ViewHolder.
         *
         * @param itemView - The View that inflated in {@link MoviesAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        MovieViewHolder(View itemView) {
            super(itemView);

            // Retrieve ImageView's from parent FrameLayout view group
            posterImageView = itemView.findViewById(R.id.iv_list_poster);
            favouriteImageView = itemView.findViewById(R.id.iv_favourite);

            // Set separate click listeners to poster and favourite image
            posterImageView.setOnClickListener(this);
            favouriteImageView.setOnClickListener(this);
        }

        /**
         * Called whenever a user clicks on an item in the list.
         *
         * @param view - The View that was clicked
         */
        @Override
        public void onClick(View view) {
            if(movies == null) {
                return;
            }
            int position = getAdapterPosition();
            if(position >= 0 && position < movies.size()) {
                Movie movie = movies.get(position);
                if(view.getId() == R.id.iv_favourite) {
                    // Sending message to adapter that image for favourite movie is clicked
                    favouriteClicked(view, movie);
                } else {
                    mMovieClickListener.onMovieClicked(movie);
                }
            }
        }
    }
}