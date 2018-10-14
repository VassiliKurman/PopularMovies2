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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vkurman.popularmovies2.adapters.ReviewsAdapter;
import vkurman.popularmovies2.model.ResultMovieReview;
import vkurman.popularmovies2.model.ResultMovieReviews;
import vkurman.popularmovies2.retrofit.ApiUtils;
import vkurman.popularmovies2.utils.MoviesConstants;

/**
 * Activity that displays movie reviews
 */
public class MovieReviewsActivity extends AppCompatActivity {

    private static final String TAG = MovieReviewsActivity.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tv_reviews_title) TextView tvTitle;
    @BindView(R.id.rv_reviews) RecyclerView mRecyclerView;

    private ReviewsAdapter mAdapter;
    private long mMovieId;
    private String mMovieTitle;

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
            mMovieTitle = intent.getStringExtra(MoviesConstants.INTENT_EXTRA_MOVIE_TITLE);
            Log.d(TAG, "Retrieved movie id [ " + mMovieId + " ] and movie title: " + mMovieTitle);
        } else {
            closeOnError();
        }

        // Setting recycle view for reviews
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ReviewsAdapter(new ArrayList<ResultMovieReview>());
        mRecyclerView.setAdapter(mAdapter);

        // Retrieving api key
        final Map<String, String> reviewsData = new HashMap<>();
        reviewsData.put("api_key", getString(R.string.api_key));
        // Requesting for data
        ApiUtils.getTMDBService().getMovieReviews(mMovieId, reviewsData).enqueue(getReviewsMovieCallback());
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

    /**
     * Creates and returns reviews callback for retrofit enqueue method.
     *
     * @return - Callback<ResultMovieReviews>
     */
    private Callback<ResultMovieReviews> getReviewsMovieCallback() {
        return new Callback<ResultMovieReviews>() {
            @Override
            public void onResponse(Call<ResultMovieReviews> call, Response<ResultMovieReviews> response) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "Reviews retrieved: " + response.body().getResults().size());

                    mAdapter.updateReviews(response.body().getResults());
                    // Toolbar title
                    toolbar.setTitle(mMovieTitle);
                    Log.d(TAG, "data loaded from API");
                } else {
                    int statusCode  = response.code();
                    // handle request errors depending on status code
                    Log.d(TAG, "Error status code: " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<ResultMovieReviews> call, Throwable t) {
                showErrorMessage();
                Log.d(TAG, "error loading from API");

            }
        };
    }

    /**
     * Displays message when error occurs during request.
     */
    private void showErrorMessage() {
        Toast.makeText(this, "Error retrieving data from TMDB", Toast.LENGTH_SHORT).show();
    }
}