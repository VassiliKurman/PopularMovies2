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
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vkurman.popularmovies2.R;
import vkurman.popularmovies2.adapters.KnownForAdapter;
import vkurman.popularmovies2.listeners.ResultListener;
import vkurman.popularmovies2.model.CastPersonCombinedCredits;
import vkurman.popularmovies2.model.KnownFor;
import vkurman.popularmovies2.model.PersonCombinedCredits;
import vkurman.popularmovies2.model.PersonModel;
import vkurman.popularmovies2.retrofit.ApiUtils;
import vkurman.popularmovies2.utils.MovieUtils;
import vkurman.popularmovies2.utils.MoviesConstants;

/**
 * Displays details about person.
 * Created by Vassili Kurman on 30/09/2018.
 * Version 1.0
 */
public class PersonDetailsActivity extends AppCompatActivity implements ResultListener {

    private final static String TAG = PersonDetailsActivity.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.poster_iv) ImageView ivPoster;
    @BindView(R.id.tv_details_name) TextView tvName;
    @BindView(R.id.tv_details_biography) TextView tvBiography;
    @BindView(R.id.tv_details_known_for_title) TextView tvKnownForTitle;
    @BindView(R.id.recyclerview_known_for) RecyclerView recyclerViewKnownFor;
    @BindView(R.id.table_details_acting) TableLayout tableDetailsActing;
    @BindView(R.id.tv_details_known_for_text) TextView tvKnownFor;
    @BindView(R.id.tv_details_gender) TextView tvGender;
    @BindView(R.id.tv_details_known_credits) TextView tvKnownCredits;
    @BindView(R.id.tv_details_birthday) TextView tvBirthday;
    @BindView(R.id.tv_details_place_of_birth) TextView tvPlaceOfBirth;
    @BindView(R.id.tv_details_official_site) TextView tvOfficialSite;
    @BindView(R.id.tv_details_also_known_as) TextView tvAlsoKnownAs;

    private long personId;
    private List<KnownFor> mKnownForList;

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

        Intent intent = getIntent();
        if (intent != null) {
            personId = intent.getLongExtra(MoviesConstants.INTENT_EXTRA_PERSON_ID, -1L);
            mKnownForList = intent.getParcelableArrayListExtra(MoviesConstants.INTENT_EXTRA_PERSON_KNOWN_FOR);
        } else {
            closeOnError();
        }

        // Setting LayoutManager
        if(mKnownForList != null && !mKnownForList.isEmpty()) {
            recyclerViewKnownFor.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            // specifying an adapter and passing empty list at start
            recyclerViewKnownFor.setAdapter(new KnownForAdapter(mKnownForList, this));
        } else {
            tvKnownForTitle.setVisibility(View.GONE);
            recyclerViewKnownFor.setVisibility(View.GONE);
        }

        // Retrieving api key
        final Map<String, String> data = new HashMap<>();
        data.put("api_key", getString(R.string.api_key));
        data.put("language", "en-US");
        // Requesting for person details
        ApiUtils.getTMDBService().getPerson(personId, data).enqueue(getPersonCallback());
        // Requesting for person credits
        ApiUtils.getTMDBService().getPersonCombinedCredits(personId, data).enqueue(getPersonCombinedCreditsCallback());
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
                    // Setting toolbar title
                    toolbar.setTitle(personModel.getName());
                    // Setting details
                    tvName.setText(personModel.getName());
                    tvBiography.setText(personModel.getBiography());
                    tvKnownFor.setText(personModel.getKnownForDepartment());
                    tvGender.setText(MovieUtils.genderFromInt(PersonDetailsActivity.this, personModel.getGender()));
                    tvBirthday.setText(personModel.getBirthday());
                    tvPlaceOfBirth.setText(personModel.getPlaceOfBirth());
                    tvOfficialSite.setText(personModel.getHomepage() == null ? getString(R.string.text_people_details_empty) : personModel.getHomepage());
                    tvAlsoKnownAs.setText(MovieUtils.formatListToString(personModel.getAlsoKnownAs()));

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
     * Creates and Callback<PersonCombinedCredits> callback for retrofit enqueue method.
     *
     * @return - Callback<PersonCombinedCredits>
     */
    private Callback<PersonCombinedCredits> getPersonCombinedCreditsCallback() {
        return new Callback<PersonCombinedCredits>() {
            @Override
            public void onResponse(Call<PersonCombinedCredits> call, Response<PersonCombinedCredits> response) {
                if(response.isSuccessful()) {
                    PersonCombinedCredits personCombinedCredits = response.body();
                    int castCredits = (personCombinedCredits.getCast() == null
                            || personCombinedCredits.getCast().isEmpty()) ? 0 : personCombinedCredits.getCast().size();
                    int crewCredits = (personCombinedCredits.getCrew() == null
                            || personCombinedCredits.getCrew().isEmpty()) ? 0 : personCombinedCredits.getCrew().size();
                    String total = Integer.toString(castCredits + crewCredits);
                    tvKnownCredits.setText(total);

                    List<CastPersonCombinedCredits> list = personCombinedCredits.getCast();
                    Collections.sort(list, new Comparator<CastPersonCombinedCredits>() {
                        @Override
                        public int compare(CastPersonCombinedCredits c1, CastPersonCombinedCredits c2) {
                            String date1 = (c1.getReleaseDate() == null || c1.getReleaseDate().isEmpty())
                                    ? c1.getFirstAirDate() : c1.getReleaseDate();
                            String date2 = (c2.getReleaseDate() == null || c2.getReleaseDate().isEmpty())
                                    ? c2.getFirstAirDate() : c2.getReleaseDate();

                            if(date1 == null || date2 == null) {
                                return -1;
                            }

                            return date1.compareTo(date2);
                        }
                    });
                    Collections.reverse(list);
                    for(CastPersonCombinedCredits cpcc: list) {
                        TableRow tr = new TableRow(PersonDetailsActivity.this);
                        TextView year = new TextView(PersonDetailsActivity.this);
                        year.setText(MovieUtils.formatYearTextWithoutBrackets(cpcc.getReleaseDate() == null
                                ? cpcc.getFirstAirDate()
                                : cpcc.getReleaseDate()));
                        tr.addView(year);

                        TextView dash = new TextView(PersonDetailsActivity.this);
                        dash.setText(" - ");
                        tr.addView(dash);

                        TextView title = new TextView(PersonDetailsActivity.this);
                        title.setText(cpcc.getTitle() == null ? cpcc.getName() : cpcc.getTitle());
                        tr.addView(title);
                        tableDetailsActing.addView(tr);
                    }
                } else {
                    int statusCode  = response.code();
                    // handle request errors depending on status code
                    Log.e(TAG, "Error status code: " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<PersonCombinedCredits> call, Throwable t) {
                showErrorMessage();
                Log.e(TAG, "error loading from API: " + t.getMessage());

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
        Intent intent = new Intent(PersonDetailsActivity.this, MovieDetailsActivity.class);
        intent.putExtra(MoviesConstants.INTENT_EXTRA_MOVIE_ID, id);
        startActivity(intent);
    }
}