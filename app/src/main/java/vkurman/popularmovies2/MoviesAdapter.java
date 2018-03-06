package vkurman.popularmovies2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import vkurman.popularmovies2.model.Movie;
import vkurman.popularmovies2.utils.MovieUtils;

/**
 * Project Popular Movies stage 2.
 * Created by Vassili Kurman on 06/03/2018.
 * Version 2.0
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private static final String TAG = "MovieAdapter";

    /**
     * An on-click handler that allows for an Activity to interface with RecyclerView
     */
    final private MovieClickListener mMovieClickListener;
    /**
     * Reference to list of items.
     */
    private List<Movie> movies;

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
    MoviesAdapter(List<Movie> listOfItems, MovieClickListener listener) {
        movies = listOfItems;
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
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
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
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        if(position >= 0 && position < movies.size()) {
            final Movie movie = movies.get(position);

            String imagePath = MovieUtils.createFullIconPath(movie.getMoviePoster());
            Log.d(TAG, "Movie poster full path: " + imagePath);

            Context context = holder.posterImageView.getContext();
            Picasso.with(context)
                    .load(imagePath)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.posterImageView);

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

    /**
     * Cache of the children views for a list item.
     */
    class MovieViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView posterImageView;

        /**
         * Constructor for ViewHolder.
         *
         * @param itemView - The View that inflated in {@link MoviesAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        MovieViewHolder(View itemView) {
            super(itemView);

            posterImageView = itemView.findViewById(R.id.iv_list_poster);

            itemView.setOnClickListener(this);
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
                mMovieClickListener.onMovieClicked(movie);
            }
        }
    }
}
