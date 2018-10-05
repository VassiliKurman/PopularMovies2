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

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vkurman.popularmovies2.adapters.ReviewsAdapter;
import vkurman.popularmovies2.loaders.ReviewsLoader;
import vkurman.popularmovies2.model.Review;
import vkurman.popularmovies2.utils.MoviesConstants;

/**
 * Activity that displays movie reviews
 */
public class MovieReviewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Review>> {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tv_reviews_title) TextView tvTitle;
    @BindView(R.id.rv_reviews) RecyclerView mRecyclerView;

    private ReviewsAdapter mAdapter;
    private long mMovieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_reviews);
        // Binding views
        ButterKnife.bind(this);
        // Setting Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            ActionBar actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent != null) {
            mMovieId = intent.getLongExtra(MoviesConstants.INTENT_EXTRA_MOVIE_ID, -1L);
        } else {
            closeOnError();
        }
        // Toolbar title
        toolbar.setTitle("Reviews");

        // Setting recycle view for reviews
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ReviewsAdapter(new ArrayList<Review>());
        mRecyclerView.setAdapter(mAdapter);

        // Setting loaders
        getSupportLoaderManager().initLoader(ReviewsLoader.ID, null, this).forceLoad();
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
        return new ReviewsLoader(this, String.valueOf(mMovieId));
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