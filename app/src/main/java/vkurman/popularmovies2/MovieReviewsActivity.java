package vkurman.popularmovies2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import vkurman.popularmovies2.model.Movie;

public class MovieReviewsActivity extends AppCompatActivity {

    private static final String TAG = "MovieReviewsActivity";

    @BindView(R.id.rv_reviews) RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;

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

        Movie movie = intent.getParcelableExtra("movie");
        // Return from method if movie not set
        if(movie == null) {
            return;
        }

        setTitle(movie.getTitle());

        requestForReviews(movie.getMovieId());
    }

    /**
     * Closes and displays message when error occurs
     */
    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void requestForReviews(long movieId) {

    }

}