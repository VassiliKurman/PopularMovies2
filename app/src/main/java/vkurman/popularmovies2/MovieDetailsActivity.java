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
package vkurman.popularmovies2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import vkurman.popularmovies2.adapters.VideosAdapter;
import vkurman.popularmovies2.loaders.VideosLoader;
import vkurman.popularmovies2.model.Movie;
import vkurman.popularmovies2.model.Video;
import vkurman.popularmovies2.persistance.MoviesContract;
import vkurman.popularmovies2.utils.MovieUtils;

/**
 * MovieDetailsActivity is displaying movie details and allows movie to add to favourites,
 * to read movie reviews and to watch movie trailers.
 *
 * Created by Vassili Kurman on 25/02/2018.
 * Version 1.0
 */
public class MovieDetailsActivity extends AppCompatActivity
        implements View.OnClickListener, LoaderManager.LoaderCallbacks<List<Video>> {

    // Binding views
    @BindView(R.id.header_image) ImageView ivMoviePoster;

    @BindView(R.id.poster_iv) ImageView ivBackdrop;
    @BindView(R.id.iv_details_favourite) ImageView ivFavourite;
    @BindView(R.id.tv_details_title) TextView tvTitle;
    @BindView(R.id.release_date_tv) TextView tvReleaseDate;
    @BindView(R.id.vote_average_tv) TextView tvVoteAverage;
    @BindView(R.id.plot_synopsis_tv) TextView tvPlotSynopsis;
    @BindView(R.id.rv_videos) RecyclerView mRecyclerView;

    @BindView(R.id.btn_reviews) Button btnReviews;

    private final static String TAG = "MovieDetailsActivity";

    // Reference to Movie object
    private Movie movie;
    // MovieAdapter for Trailers RecycleView
    private VideosAdapter mAdapter;
    // Map of favourite movies
    private Map<Long, Long> favourites = new TreeMap<>();;
    // Indicator that data changed for result intent
    private boolean dataChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        // Binding views
        ButterKnife.bind(this);
        // Setting Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            ActionBar actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        dataChanged = false;

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
        ivFavourite.setOnClickListener(this);

        Picasso.get()
                .load(MovieUtils.createFullBackdropPath(poster))
                .placeholder(R.drawable.ic_image_area)
                .error(R.drawable.ic_error_image)
                .into(ivBackdrop);

        Picasso.get()
                .load(MovieUtils.createFullPosterPath(poster))
                .placeholder(R.drawable.ic_image_area)
                .error(R.drawable.ic_error_image)
                .into(ivMoviePoster);

        loadFavourites();

        if (favourites.get(movieId) != null) {
            ivFavourite.setImageResource(R.drawable.ic_heart);
        } else {
            ivFavourite.setImageResource(R.drawable.ic_heart_outline);
        }

        setTitle(getString(R.string.activity_title_movie_details));

        // Setting recycle view for trailers
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new VideosAdapter(new ArrayList<Video>());
        mRecyclerView.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(VideosLoader.ID, null, this).forceLoad();
    }

    /**
     * Loading favourites into the map
     */
    private void loadFavourites() {
        if(favourites != null) {
            favourites.clear();
        }

        String[] projection = new String[] {MoviesContract.MoviesEntry.COLUMN_MOVIE_ID};
        Cursor cursor = getContentResolver().query(
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
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult((dataChanged) ? 1 : 2);
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
            boolean added = true;

            if(favourites.containsKey(movie.getMovieId())) {
                added = false;
                dataChanged = !dataChanged;

                long id = movie.getMovieId();
//                String stringId = Long.toString(id);
                Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
//                uri = uri.buildUpon().appendPath(stringId).build();
                uri = uri.buildUpon().build();

                // New 2 lines
                String where = MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(movie.getMovieId())};

                AppCompatActivity context = (AppCompatActivity) view.getContext();
//                context.getContentResolver().delete(uri, null, null);
                context.getContentResolver().delete(uri, where, selectionArgs);

                // Changing favourite button appearance
                favourites.remove(id);
                ((ImageView)view).setImageResource(R.drawable.ic_heart_outline);
            } else {
                dataChanged = !dataChanged;

                long id = movie.getMovieId();
                ContentValues cv = new ContentValues();
                cv.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, movie.getMovieId());
                cv.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER, movie.getMoviePoster());
                cv.put(MoviesContract.MoviesEntry.COLUMN_TITLE, movie.getTitle());
                cv.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                cv.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
                cv.put(MoviesContract.MoviesEntry.COLUMN_PLOT_SYNOPSIS, movie.getPlotSynopsis());

                view.getContext().getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, cv);

                // Changing favourite button appearance
                favourites.put(id, id);
                ((ImageView)view).setImageResource(R.drawable.ic_heart);
            }

            if(added) {
                Toast.makeText(this, "Added to favourites!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Removed from favourites!", Toast.LENGTH_SHORT).show();
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
        } else if(view == ivFavourite) {
            // Sending message to adapter that image for favourite movie is clicked
            favouriteClicked(view, movie);
        }
    }

    @NonNull
    @Override
    public Loader<List<Video>> onCreateLoader(int id, @Nullable Bundle args) {
        return new VideosLoader(this, String.valueOf(movie.getMovieId()));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Video>> loader, List<Video> data) {
        if(data == null) {
            return;
        }

        if(mAdapter == null) {
            mAdapter = new VideosAdapter(data);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.updateVideos(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Video>> loader) {}
}