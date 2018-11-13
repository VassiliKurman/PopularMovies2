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
package vkurman.popularmovies2.ui;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vkurman.popularmovies2.R;
import vkurman.popularmovies2.adapters.RecommendationsTVShowAdapter;
import vkurman.popularmovies2.listeners.ResultListener;
import vkurman.popularmovies2.model.Crew;
import vkurman.popularmovies2.model.RecommendationsTVShowRequest;
import vkurman.popularmovies2.model.RecommendationsTVShowResult;
import vkurman.popularmovies2.model.ShowModel;
import vkurman.popularmovies2.model.TVContentRatings;
import vkurman.popularmovies2.model.TVKeywords;
import vkurman.popularmovies2.model.TVRating;
import vkurman.popularmovies2.retrofit.ApiUtils;
import vkurman.popularmovies2.utils.MovieUtils;
import vkurman.popularmovies2.utils.MoviesConstants;

/**
 * Displays details about tv show.
 * Created by Vassili Kurman on 02/10/2018.
 * Version 1.0
 */
public class ShowDetailsActivity extends AppCompatActivity implements ResultListener {

    private final static String TAG = ShowDetailsActivity.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.iv_show_backdrop) ImageView ivBackdrop;
    @BindView(R.id.iv_show_poster) ImageView ivPoster;
    @BindView(R.id.tv_show_details_title) TextView tvTitle;
    @BindView(R.id.tv_show_details_year) TextView tvYear;
    @BindView(R.id.tv_show_details_overview_text) TextView tvOverview;
    @BindView(R.id.tv_show_details_crew_text) TextView tvCrew;
    @BindView(R.id.recyclerview_show_details_cast) RecyclerView mRecyclerViewCast;
    @BindView(R.id.recyclerview_recommendations) RecyclerView mRecyclerViewRecommendations;
    // Facts
    @BindView(R.id.tv_show_details_status_text) TextView tvStatus;
    @BindView(R.id.tv_show_details_network_text) TextView tvNetwork;
    @BindView(R.id.tv_show_details_certification_text) TextView tvCertification;
    @BindView(R.id.tv_show_details_type_text) TextView tvType;
    @BindView(R.id.tv_show_details_original_language_text) TextView tvOriginalLanguage;
    @BindView(R.id.tv_show_details_runtime_text) TextView tvRuntime;
    @BindView(R.id.tv_show_details_genres_text) TextView tvGenres;
    @BindView(R.id.tv_show_details_keywords_text) TextView tvKeywords;

    private long showId;
    // RecommendationsMovieAdapter for Recommendations RecycleView
    private RecommendationsTVShowAdapter mRecommendationsTVShowAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);
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
            showId = intent.getLongExtra(MoviesConstants.INTENT_EXTRA_SHOW_ID, -1L);
        } else {
            closeOnError();
        }

        // Setting recycle view for recommendations
        mRecyclerViewRecommendations.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        mRecommendationsTVShowAdapter = new RecommendationsTVShowAdapter(new ArrayList<RecommendationsTVShowResult>(), this);
        mRecyclerViewRecommendations.setAdapter(mRecommendationsTVShowAdapter);

        // Retrieving api key
        final Map<String, String> data = new HashMap<>();
        data.put("api_key", getString(R.string.api_key));
        data.put("language", "en-US");
        ApiUtils.getTMDBService().getShow(showId, data).enqueue(getShowCallback());
        ApiUtils.getTMDBService().getTVShowRatings(showId, data).enqueue(getTVShowRatings());
        ApiUtils.getTMDBService().getTVShowKeywords(showId, data).enqueue(getTVShowKeywords());
        ApiUtils.getTMDBService().getTVShowRecommendations(showId, data).enqueue(getRecommendationsTVShowCallback());
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
     * Creates and returns movies callback for retrofit enqueue method.
     *
     * @return - Callback<ShowModel>
     */
    private Callback<ShowModel> getShowCallback() {
        return new Callback<ShowModel>() {
            @Override
            public void onResponse(Call<ShowModel> call, Response<ShowModel> response) {
                if(response.isSuccessful()) {
                    ShowModel showModel = response.body();
                    Log.d(TAG, "Show retrieved: " + showModel.getName());

                    // Setting title for Toolbar
                    toolbar.setTitle(showModel.getName());
                    // Setting details
                    tvTitle.setText(showModel.getName());
                    tvYear.setText(MovieUtils.formatYearSpanText(showModel.getFirstAirDate(), showModel.getLastAirDate()));
                    tvOverview.setText(showModel.getOverview());
                    if(showModel.getCreatedBy() != null && !showModel.getCreatedBy().isEmpty()) {
                        String crew = "";
                        for(Crew c: showModel.getCreatedBy()) {
                            crew = (crew.isEmpty()) ? c.getName() : crew + ", " + c.getName();
                        }
                        tvCrew.setText(crew);
                    }

                    // Facts
                    tvStatus.setText(showModel.getStatus());
                    tvNetwork.setText(MovieUtils.formatNetworksListToString(showModel.getNetworks()));
                    tvType.setText(showModel.getType());
                    tvOriginalLanguage.setText(showModel.getOriginalLanguage() != null
                            ? new Locale(showModel.getOriginalLanguage()).getDisplayLanguage()
                            : showModel.getOriginalLanguage());
                    tvRuntime.setText(MovieUtils.formatRuntimeListToString(showModel.getEpisodeRunTime()));
                    tvGenres.setText(MovieUtils.formatGenresListToString(showModel.getGenres()));

                    Picasso.get()
                            .load(MovieUtils.createFullBackdropPath(showModel.getBackdropPath()))
                            .placeholder(R.drawable.ic_image_area)
                            .error(R.drawable.ic_error_image)
                            .into(ivBackdrop);

                    Picasso.get()
                            .load(MovieUtils.createFullPosterPath(showModel.getPosterPath()))
                            .placeholder(R.drawable.ic_image_area)
                            .error(R.drawable.ic_error_image)
                            .into(ivPoster);
                } else {
                    int statusCode  = response.code();
                    // handle request errors depending on status code
                    Log.e(TAG, "Error status code: " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<ShowModel> call, Throwable t) {
                showErrorMessage();
                Log.e(TAG, "error loading from API");

            }
        };
    }

    /**
     * Creates and returns tv show ratings callback for retrofit enqueue method.
     *
     * @return - Callback<TVContentRatings>
     */
    private Callback<TVContentRatings> getTVShowRatings() {
        return new Callback<TVContentRatings>() {
            @Override
            public void onResponse(Call<TVContentRatings> call, Response<TVContentRatings> response) {
                if(response.isSuccessful()) {
                    TVContentRatings ratings = response.body();
                    Log.d(TAG, "TV Show ratings retrieved: " + ratings.getTVRating().size());

                    String ratingString = getString(R.string.text_tv_show_details_unspecified);
                    // Checking ratings
                    if(ratings.getTVRating().size() > 0) {
                        for(TVRating rating: ratings.getTVRating()) {
                            if(rating.getIso31661().equals(Locale.getDefault().getCountry())) {
                                ratingString = rating.getRating();
                            }
                        }
                    }
                    tvCertification.setText(ratingString);
                } else {
                    int statusCode  = response.code();
                    // handle request errors depending on status code
                    Log.e(TAG, "Error status code: " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<TVContentRatings> call, Throwable t) {
                showErrorMessage();
                Log.e(TAG, "error loading from API");

            }
        };
    }

    /**
     * Creates and returns tv show keywords callback for retrofit enqueue method.
     *
     * @return - Callback<TVKeywords>
     */
    private Callback<TVKeywords> getTVShowKeywords() {
        return new Callback<TVKeywords>() {
            @Override
            public void onResponse(Call<TVKeywords> call, Response<TVKeywords> response) {
                if(response.isSuccessful()) {
                    TVKeywords keywords = response.body();
                    Log.d(TAG, "TV Show keywords retrieved: " + keywords.getResults().size());

                    tvKeywords.setText(MovieUtils.formatKeywordsListToString(keywords.getResults()));
                } else {
                    int statusCode  = response.code();
                    // handle request errors depending on status code
                    Log.e(TAG, "Error status code: " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<TVKeywords> call, Throwable t) {
                showErrorMessage();
                Log.e(TAG, "error loading from API");

            }
        };
    }

    /**
     * Creates and returns RecommendationsMovieRequest callback for retrofit enqueue method.
     *
     * @return - Callback<RecommendationsMovieRequest>
     */
    private Callback<RecommendationsTVShowRequest> getRecommendationsTVShowCallback() {
        return new Callback<RecommendationsTVShowRequest>() {
            @Override
            public void onResponse(Call<RecommendationsTVShowRequest> call, Response<RecommendationsTVShowRequest> response) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "Recommendations retrieved: " + response.body().getResults().size());
                    mRecommendationsTVShowAdapter.updateData(response.body().getResults());
                    Log.d(TAG, " recommendations loaded from API");
                } else {
                    int statusCode  = response.code();
                    // handle request errors depending on status code
                    Log.d(TAG, "Error status code: " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<RecommendationsTVShowRequest> call, Throwable t) {
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

    /**
     * Closes and displays message when error occurs
     */
    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResultClick(long id, Bundle bundle) {
        String bundleExtra = bundle.getString(MoviesConstants.BUNDLE_EXTRA_TYPE);
        if(bundleExtra != null) {
            if (bundleExtra.equals(MoviesConstants.BUNDLE_EXTRA_TV_SHOW)) {
                Intent intent = new Intent(this, ShowDetailsActivity.class);
                intent.putExtra(MoviesConstants.INTENT_EXTRA_SHOW_ID, id);
                startActivity(intent);
            }
        }
    }
}