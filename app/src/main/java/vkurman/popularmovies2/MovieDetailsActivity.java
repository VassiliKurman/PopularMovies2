package vkurman.popularmovies2;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import vkurman.popularmovies2.model.Movie;
import vkurman.popularmovies2.persistance.MoviesContract;
import vkurman.popularmovies2.persistance.MoviesPersistenceManager;
import vkurman.popularmovies2.utils.MovieUtils;

/**
 * Project Popular Movies stage 2.
 * Created by Vassili Kurman on 25/02/2018.
 * Version 1.0
 */
public class MovieDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    // Binding views
    @BindView(R.id.poster_iv) ImageView ivMoviePoster;
    @BindView(R.id.iv_details_favourite) ImageView ivFavourite;
    @BindView(R.id.title_tv) TextView tvTitle;
    @BindView(R.id.release_date_tv) TextView tvReleaseDate;
    @BindView(R.id.vote_average_tv) TextView tvVoteAverage;
    @BindView(R.id.plot_synopsis_tv) TextView tvPlotSynopsis;

    @BindView(R.id.btn_reviews) Button btnReviews;
    @BindView(R.id.btn_trailers) Button btnTrailers;

    private Movie movie;
    private Map<Long, Long> favourites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        movie = intent.getParcelableExtra("movie");
        // Return from method if movie not set
        if(movie == null) {
            return;
        }
        Long movieId = movie.getMovieId();
        String poster = movie.getMoviePoster();
        String title = movie.getTitle();
        String releaseDate = movie.getReleaseDate();
        String voteAverage = movie.getVoteAverage();
        String plotSynopsis = movie.getPlotSynopsis();

        // Setting text to text views
        tvTitle.setText(title);
        tvReleaseDate.setText(releaseDate.substring(0, 4));
        tvVoteAverage.setText(voteAverage);
        tvPlotSynopsis.setText(plotSynopsis);
        // Setting OnClickListener
        btnReviews.setOnClickListener(this);
        btnTrailers.setOnClickListener(this);
        ivFavourite.setOnClickListener(this);

        Picasso.with(this)
                .load(MovieUtils.createFullIconPath(poster))
                .placeholder(R.drawable.ic_image_area)
                .error(R.drawable.ic_error_image)
                .into(ivMoviePoster);

        if(favourites == null) {
            favourites = MoviesPersistenceManager.getInstance(this).getFavouriteMovieIds();
        }

        if (favourites.get(new Long(movieId)) != null) {
            ivFavourite.setImageResource(R.drawable.ic_heart);
        } else {
            ivFavourite.setImageResource(R.drawable.ic_heart_outline);
        }

        setTitle(getString(R.string.activity_title_movie_details));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Closes and displays message when error occurs
     */
    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    // TODO fix issue with favourite in details
    void favouriteClicked(View view, Movie movie) {
        if(view.getId() == R.id.iv_details_favourite) {
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

    @Override
    public void onClick(View view) {
        if(view == btnReviews) {
            if(movie != null) {
                Intent intent = new Intent(MovieDetailsActivity.this, MovieReviewsActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        } else if (view == btnTrailers) {
            // TODO display trailers
            Toast.makeText(this, "Trailers clicked!", Toast.LENGTH_LONG).show();
        } else if(view == ivFavourite) {
            // TODO change favourite
            Toast.makeText(this, "Favourites clicked!", Toast.LENGTH_LONG).show();
            // Sending message to adapter that image for favourite movie is clicked
            favouriteClicked(view, movie);
        }
    }
}