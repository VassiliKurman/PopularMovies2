package vkurman.popularmovies2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vkurman.popularmovies2.adapters.ReviewsAdapter;
import vkurman.popularmovies2.loaders.ReviewsLoader;
import vkurman.popularmovies2.model.Movie;
import vkurman.popularmovies2.model.Review;

/**
 * Activity that displays movie reviews
 */
public class MovieReviewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Review>> {

    @BindView(R.id.tv_reviews_title) TextView tvTitle;
    @BindView(R.id.rv_reviews) RecyclerView mRecyclerView;

    private static final String TITLE = "Reviews";
    private ReviewsAdapter mAdapter;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_reviews);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Binding views
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

        tvTitle.setText(movie.getTitle());
        setTitle(TITLE);

        // Setting recycle view for reviews
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ReviewsAdapter(new ArrayList<Review>());
        mRecyclerView.setAdapter(mAdapter);

        // Setting loaders
        getSupportLoaderManager().initLoader(0, null, this).forceLoad();
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

    @NonNull
    @Override
    public Loader<List<Review>> onCreateLoader(int id, @Nullable Bundle args) {
        return new ReviewsLoader(this, String.valueOf(movie.getMovieId()));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Review>> loader, List<Review> data) {
        if(data == null) {
            return;
        }

        if(mAdapter == null) {
            mAdapter = new ReviewsAdapter(data);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.updateReviews(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Review>> loader) {}
}