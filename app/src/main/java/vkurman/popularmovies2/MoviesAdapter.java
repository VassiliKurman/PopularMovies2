package vkurman.popularmovies2;

import android.app.LoaderManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import vkurman.popularmovies2.model.Movie;
import vkurman.popularmovies2.persistance.MoviesContract;
import vkurman.popularmovies2.persistance.MoviesPersistenceManager;
import vkurman.popularmovies2.utils.MovieUtils;

/**
 * Project Popular Movies stage 2.
 * Created by Vassili Kurman on 06/03/2018.
 * Version 2.0
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private static final String TAG = "MovieAdapter";
    private static final int MOVIE_LOADER_ID = 0;

    /**
     * An on-click handler that allows for an Activity to interface with RecyclerView
     */
    final private MovieClickListener mMovieClickListener;
    /**
     * Reference to list of items.
     */
    private Cursor mCursor;
    private List<Movie> movies;
    private Map<Long, Long> favourites;

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieClickListener {
        void onMovieClicked(Movie movie);
    }

    public interface FavouriteClickListener {
        void onFavouriteMovieClicked(View view, Movie movie);
    }

    /**
     * Constructor for Adapter that accepts a number of items to display and ClickListener.
     *
     * @param listOfItems list of items to display
     * @param listener Listener for list item clicks
     */
    MoviesAdapter(List<Movie> listOfItems, MovieClickListener listener) {
        this.movies = listOfItems;
        mMovieClickListener = listener;
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

            Log.d(TAG, "Movie poster full path: " + imagePath);

            holder.itemView.setTag(movieId);

            Context context = holder.posterImageView.getContext();
            Picasso.with(context)
                    .load(imagePath)
                    .placeholder(R.drawable.ic_image_area)
                    .error(R.drawable.ic_error_image)
                    .into(holder.posterImageView);

            // Making sure that favourites not null
            if(favourites == null) {
                favourites = MoviesPersistenceManager.getInstance(context).getFavouriteMovieIds();
            }

            if (favourites.get(new Long(movieId)) != null) {
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
    void updateMovies(List<Movie> movies) {
        this.movies.clear();
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

    void favouriteClicked(View view, Movie movie) {
        if(view.getId() == R.id.iv_favourite) {
            if(favourites == null) {
                favourites = MoviesPersistenceManager.getInstance(view.getContext()).getFavouriteMovieIds();
            }
            if(favourites.containsKey(movie.getMovieId())) {
                long id = movie.getMovieId();
                String stringId = Long.toString(id);
                Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                AppCompatActivity context = (AppCompatActivity) view.getContext();
                context.getContentResolver().delete(uri, null, null);
                context.getLoaderManager().restartLoader(MOVIE_LOADER_ID,
                        null,
                        (LoaderManager.LoaderCallbacks<Cursor>)context);

                favourites.remove(movie.getMovieId());
                ((ImageView)view).setImageResource(R.drawable.ic_heart_outline);
            } else {
                long id = movie.getMovieId();
                ContentValues cv = new ContentValues();
                cv.put(MoviesContract.MoviesEntry._ID, movie.getMovieId());
                cv.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER, movie.getMoviePoster());
                cv.put(MoviesContract.MoviesEntry.COLUMN_TITLE, movie.getTitle());
                cv.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                cv.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
                cv.put(MoviesContract.MoviesEntry.COLUMN_PLOT_SYNOPSIS, movie.getPlotSynopsis());

                view.getContext().getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, cv);

                // Adding to favourites map
                favourites.put(id, id);
                ((ImageView)view).setImageResource(R.drawable.ic_heart);
            }
        }
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor cursor) that is passed in.
     */
    public Cursor swapCursor(Cursor cursor) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == cursor) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = cursor;

        //check if this is a valid cursor, then update the cursor
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return temp;
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
