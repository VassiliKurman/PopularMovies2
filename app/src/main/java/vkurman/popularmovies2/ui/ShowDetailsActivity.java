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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vkurman.popularmovies2.R;
import vkurman.popularmovies2.model.Crew;
import vkurman.popularmovies2.model.ShowModel;
import vkurman.popularmovies2.retrofit.ApiUtils;
import vkurman.popularmovies2.utils.MovieUtils;
import vkurman.popularmovies2.utils.MoviesConstants;

/**
 * Displays details about tv show.
 * Created by Vassili Kurman on 02/10/2018.
 * Version 1.0
 */
public class ShowDetailsActivity extends AppCompatActivity {

    private final static String TAG = ShowDetailsActivity.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.iv_backdrop) ImageView ivBackdrop;
    @BindView(R.id.iv_poster) ImageView ivPoster;
    @BindView(R.id.tv_details_title) TextView tvTitle;
    @BindView(R.id.tv_details_year) TextView tvYear;
    @BindView(R.id.tv_details_overview_text) TextView tvOverview;
    @BindView(R.id.tv_details_crew_title) TextView tvCrewTitle;
    @BindView(R.id.tv_details_crew_text) TextView tvCrew;

    private long showId;

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

        // Retrieving api key
        final Map<String, String> data = new HashMap<>();
        data.put("api_key", getString(R.string.api_key));
        data.put("language", "en-US");
        ApiUtils.getTMDBService().getShow(showId, data).enqueue(getShowCallback());
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
                        // TODO create horizontal RecyclerView
                        tvCrewTitle.setVisibility(View.VISIBLE);
                        String crew = "";
                        for(Crew c: showModel.getCreatedBy()) {
                            crew = (crew.isEmpty()) ? c.getName() : crew + ", " + c.getName();
                        }
                        tvCrew.setText(crew);
                        tvCrew.setVisibility(View.VISIBLE);
                    }

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
}