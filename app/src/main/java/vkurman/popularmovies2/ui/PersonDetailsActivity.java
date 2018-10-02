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
import vkurman.popularmovies2.model.PersonModel;
import vkurman.popularmovies2.retrofit.ApiUtils;
import vkurman.popularmovies2.retrofit.TMDBService;
import vkurman.popularmovies2.utils.MovieUtils;
import vkurman.popularmovies2.utils.MoviesConstants;

/**
 * Displays details about person.
 * Created by Vassili Kurman on 30/09/2018.
 * Version 1.0
 */
public class PersonDetailsActivity extends AppCompatActivity {

    private final static String TAG = PersonDetailsActivity.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.poster_iv) ImageView ivPoster;
    @BindView(R.id.tv_details_name) TextView tvName;
    @BindView(R.id.tv_details_birthday) TextView tvBirthday;
    @BindView(R.id.tv_details_place_of_birth) TextView tvPlaceOfBirth;
    @BindView(R.id.tv_details_biography) TextView tvBiography;

    private TMDBService mService;
    private long personId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details);
        // Binding views
        ButterKnife.bind(this);
        // Setting Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            ActionBar actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        mService = ApiUtils.getTMDBService();

        Intent intent = getIntent();
        if (intent != null) {
            personId = intent.getLongExtra(MoviesConstants.INTENT_EXTRA_PERSON_ID, -1L);
        } else {
            closeOnError();
        }

        // Retrieving api key
        final Map<String, String> data = new HashMap<>();
        data.put("api_key", getString(R.string.api_key));
        data.put("language", "en-US");
        mService.getPerson(personId, data).enqueue(getPersonCallback());
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
     * Creates and Callback<PersonModel> callback for retrofit enqueue method.
     *
     * @return - Callback<PersonModel>
     */
    private Callback<PersonModel> getPersonCallback() {
        return new Callback<PersonModel>() {
            @Override
            public void onResponse(Call<PersonModel> call, Response<PersonModel> response) {
                if(response.isSuccessful()) {
                    PersonModel personModel = response.body();
                    Log.d(TAG, "Person retrieved: " + personModel.getName());
                    tvName.setText(personModel.getName());
                    tvBiography.setText(personModel.getBiography());
                    tvBirthday.setText(personModel.getBirthday());
                    tvPlaceOfBirth.setText(personModel.getPlaceOfBirth());

                    Picasso.get()
                            .load(MovieUtils.createFullPosterPath(personModel.getProfilePath()))
                            .placeholder(R.drawable.ic_image_area)
                            .error(R.drawable.ic_error_image)
                            .into(ivPoster);
                } else {
                    int statusCode  = response.code();
                    // handle request errors depending on status code
                    Log.d(TAG, "Error status code: " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<PersonModel> call, Throwable t) {
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
}