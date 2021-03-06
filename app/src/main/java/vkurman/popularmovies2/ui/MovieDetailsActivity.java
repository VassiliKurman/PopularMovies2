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
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vkurman.popularmovies2.R;
import vkurman.popularmovies2.adapters.MovieCastAdapter;
import vkurman.popularmovies2.adapters.MovieCrewAdapter;
import vkurman.popularmovies2.adapters.RecommendationsMovieAdapter;
import vkurman.popularmovies2.adapters.VideosAdapter;
import vkurman.popularmovies2.listeners.ResultListener;
import vkurman.popularmovies2.loaders.VideosLoader;
import vkurman.popularmovies2.model.CastMovie;
import vkurman.popularmovies2.model.CreditsMovie;
import vkurman.popularmovies2.model.CrewMovie;
import vkurman.popularmovies2.model.Movie;
import vkurman.popularmovies2.model.MovieKeywords;
import vkurman.popularmovies2.model.MovieModel;
import vkurman.popularmovies2.model.RecommendationsMovieRequest;
import vkurman.popularmovies2.model.RecommendationsMovieResult;
import vkurman.popularmovies2.model.ResultMovieReview;
import vkurman.popularmovies2.model.ResultMovieReviews;
import vkurman.popularmovies2.model.Video;
import vkurman.popularmovies2.persistance.MoviesContract;
import vkurman.popularmovies2.retrofit.ApiUtils;
import vkurman.popularmovies2.utils.MovieUtils;
import vkurman.popularmovies2.utils.MoviesConstants;

/**
 * MovieDetailsActivity is displaying movie details and allows movie to add to favourites,
 * to read movie reviews and to watch movie trailers.
 *
 * Created by Vassili Kurman on 25/02/2018.
 * Version 2.0
 */
public class MovieDetailsActivity extends AppCompatActivity
        implements View.OnClickListener, LoaderManager.LoaderCallbacks<List<Video>>, ResultListener {

    // Binding views
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.iv_backdrop) ImageView ivBackdrop;
    @BindView(R.id.iv_poster) ImageView ivPoster;
    @BindView(R.id.iv_details_favourite) ImageView ivFavourite;
    @BindView(R.id.tv_details_title) TextView tvTitle;
    @BindView(R.id.tv_release_date) TextView tvReleaseDate;
    @BindView(R.id.tv_vote_average) TextView tvVoteAverage;
    @BindView(R.id.tv_overview) TextView tvOverview;
    @BindView(R.id.recyclerview_crew) RecyclerView mRecyclerViewCrew;
    @BindView(R.id.recyclerview_cast) RecyclerView mRecyclerViewCast;
    @BindView(R.id.cardview_social) CardView mCardViewReviews;
    @BindView(R.id.tv_movie_review_author) TextView mReviewAuthor;
    @BindView(R.id.tv_movie_review_content) TextView mReviewContent;
    @BindView(R.id.btn_reviews) Button btnReviews;
    @BindView(R.id.recyclerview_media) RecyclerView mRecyclerViewMedia;
    @BindView(R.id.recyclerview_recommendations) RecyclerView mRecyclerViewRecommendations;
    // Facts
    @BindView(R.id.movie_details_status_text) TextView tvStatus;
    @BindView(R.id.movie_details_release_information_text) TextView tvReleaseInformation;
    @BindView(R.id.movie_details_original_language_text) TextView tvOriginalLanguage;
    @BindView(R.id.movie_details_runtime_text) TextView tvRuntime;
    @BindView(R.id.movie_details_budget_text) TextView tvBudget;
    @BindView(R.id.movie_details_revenue_text) TextView tvRevenue;
    @BindView(R.id.movie_details_genres_text) TextView tvGenres;
    @BindView(R.id.movie_details_keywords_text) TextView tvKeywords;

    private final static String TAG = "MovieDetailsActivity";

    // Movie id
    private long mMovieId;
    // Movie title
    private String mMovieTitle;
    // VideosAdapter for Trailers RecycleView
    private VideosAdapter mTrailersAdapter;
    // MovieCrewAdapter for Crew RecycleView
    private MovieCrewAdapter mCrewAdapter;
    // MovieCrewAdapter for Crew RecycleView
    private MovieCastAdapter mCastAdapter;
    // RecommendationsMovieAdapter for Recommendations RecycleView
    private RecommendationsMovieAdapter mRecommendationsMovieAdapter;
    // Map of favourite movies
    private Map<Long, Long> favourites = new TreeMap<>();
    // Indicator that data changed for result intent
    private boolean dataChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
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
            mMovieId = intent.getLongExtra(MoviesConstants.INTENT_EXTRA_ID, -1L);
        } else {
            closeOnError();
        }

        dataChanged = false;

        // TODO clear bellow
//        movie = intent.getParcelableExtra("movie");
//        // Return from method if movie not set
//        if(movie == null) {
//            return;
//        }
//
//        Long movieId = movie.getMovieId();
//        String poster = movie.getMoviePoster();
//        String title = movie.getTitle();
//        String releaseDate = movie.getReleaseDate();
//        String voteAverage = movie.getVoteAverage();
//        String plotSynopsis = movie.getPlotSynopsis();
//
//        // Setting text to text views
//        tvTitle.setText(title);
//        tvReleaseDate.setText(releaseDate.substring(0, 4));
//        tvVoteAverage.setText(voteAverage);
//        tvPlotSynopsis.setText(plotSynopsis);
//        // Setting OnClickListener
//        btnReviews.setOnClickListener(this);
//        ivFavourite.setOnClickListener(this);
//
//        Picasso.get()
//                .load(MovieUtils.createFullBackdropPath(poster))
//                .placeholder(R.drawable.ic_image_area)
//                .error(R.drawable.ic_error_image)
//                .into(ivBackdrop);
//
//        Picasso.get()
//                .load(MovieUtils.createFullPosterPath(poster))
//                .placeholder(R.drawable.ic_image_area)
//                .error(R.drawable.ic_error_image)
//                .into(ivPoster);

        // TODO refactor bellow for favourite movies
//        loadFavourites();
//
//        if (favourites.get(movieId) != null) {
//            ivFavourite.setImageResource(R.drawable.ic_heart);
//        } else {
//            ivFavourite.setImageResource(R.drawable.ic_heart_outline);
//        }
//
//        setTitle(getString(R.string.activity_title_movie_details));

        // Setting recycle view for crew
        mRecyclerViewCrew.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        mCrewAdapter = new MovieCrewAdapter(new ArrayList<CrewMovie>(), this);
        mRecyclerViewCrew.setAdapter(mCrewAdapter);

        // Setting recycle view for cast
        mRecyclerViewCast.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        mCastAdapter = new MovieCastAdapter(new ArrayList<CastMovie>(), this);
        mRecyclerViewCast.setAdapter(mCastAdapter);

        // Setting recycle view for recommendations
        mRecyclerViewRecommendations.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        mRecommendationsMovieAdapter = new RecommendationsMovieAdapter(new ArrayList<RecommendationsMovieResult>(), this);
        mRecyclerViewRecommendations.setAdapter(mRecommendationsMovieAdapter);

        // Retrieving api key
        final Map<String, String> data = new HashMap<>();
        data.put("api_key", getString(R.string.api_key));
        data.put("language", "en-US");
        ApiUtils.getTMDBService().getMovie(mMovieId, data).enqueue(getMovieCallback());
        ApiUtils.getTMDBService().getMovieCredits(mMovieId, data).enqueue(getCreditsMovieCallback());
        ApiUtils.getTMDBService().getMovieReviews(mMovieId, data).enqueue(getReviewsMovieCallback());
        ApiUtils.getTMDBService().getMovieRecommendations(mMovieId, data).enqueue(getRecommendationsMovieCallback());
        ApiUtils.getTMDBService().getMovieKeywords(mMovieId, data).enqueue(getMovieKeywords());

        // Setting recycle view for trailers
        mRecyclerViewMedia.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        mTrailersAdapter = new VideosAdapter(new ArrayList<Video>());
        mRecyclerViewMedia.setAdapter(mTrailersAdapter);

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
        cursor.close();
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
            if(mMovieId >= 0) {
                Intent intent = new Intent(MovieDetailsActivity.this, ReviewsActivity.class);
                intent.putExtra(MoviesConstants.INTENT_EXTRA_ID, mMovieId);
                intent.putExtra(MoviesConstants.INTENT_EXTRA_TITLE, mMovieTitle);
                intent.putExtra(MoviesConstants.INTENT_EXTRA_TYPE, MoviesConstants.INTENT_EXTRA_TYPE_MOVIE);
                startActivity(intent);
            }
        } else if(view == ivFavourite) {
            // TODO
            Toast.makeText(this, "Favourite clicked", Toast.LENGTH_SHORT).show();
            // Sending message to adapter that image for favourite movie is clicked
//            favouriteClicked(view, movie);
        }
    }

    @NonNull
    @Override
    public Loader<List<Video>> onCreateLoader(int id, @Nullable Bundle args) {
        return new VideosLoader(this, String.valueOf(mMovieId));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Video>> loader, List<Video> data) {
        if(data == null) {
            return;
        }

        if(mTrailersAdapter == null) {
            mTrailersAdapter = new VideosAdapter(data);
            mRecyclerViewMedia.setAdapter(mTrailersAdapter);
        } else {
            mTrailersAdapter.updateVideos(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Video>> loader) {}

    /**
     * Creates and returns movies callback for retrofit enqueue method.
     *
     * @return - Callback<MovieModel>
     */
    private Callback<MovieModel> getMovieCallback() {
        return new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                if(response.isSuccessful()) {
                    MovieModel movieModel = response.body();
                    Log.d(TAG, "Movie retrieved: " + movieModel.getTitle());

                    // Setting title for Toolbar
                    toolbar.setTitle(movieModel.getTitle());
                    // Setting title
                    mMovieTitle = movieModel.getTitle();

                    // Setting details
                    tvTitle.setText(movieModel.getTitle());
                    tvReleaseDate.setText(MovieUtils.formatYearTextWithBrackets(movieModel.getReleaseDate()));
                    tvVoteAverage.setText(MovieUtils.formatPercentage(movieModel.getVoteAverage()));
                    tvOverview.setText(movieModel.getOverview());
                    // Setting OnClickListener
                    btnReviews.setOnClickListener(MovieDetailsActivity.this);
                    ivFavourite.setOnClickListener(MovieDetailsActivity.this);

                    // Facts
                    tvStatus.setText(movieModel.getStatus());
                    tvReleaseInformation.setText(MovieUtils.formatDate(movieModel.getReleaseDate()));
                    tvOriginalLanguage.setText(movieModel.getOriginalLanguage() != null
                            ? new Locale(movieModel.getOriginalLanguage()).getDisplayLanguage()
                            : movieModel.getOriginalLanguage());
                    tvRuntime.setText(MovieUtils.formatRuntimeToString(movieModel.getRuntime()));
                    tvBudget.setText(MovieUtils.formatBudgetToString(movieModel.getBudget()));
                    tvRevenue.setText(MovieUtils.formatBudgetToString(movieModel.getRevenue()));
                    tvGenres.setText(MovieUtils.formatGenresListToString(movieModel.getGenres()));

                    Picasso.get()
                            .load(MovieUtils.createFullBackdropPath(movieModel.getBackdropPath()))
                            .placeholder(R.drawable.ic_image_area)
                            .error(R.drawable.ic_error_image)
                            .into(ivBackdrop);

                    Picasso.get()
                            .load(MovieUtils.createFullPosterPath(movieModel.getPosterPath()))
                            .placeholder(R.drawable.ic_image_area)
                            .error(R.drawable.ic_error_image)
                            .into(ivPoster);

                    // Loading trailers
                    getSupportLoaderManager().initLoader(VideosLoader.ID, null, MovieDetailsActivity.this).forceLoad();
                } else {
                    int statusCode  = response.code();
                    // handle request errors depending on status code
                    Log.e(TAG, "Error status code: " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {
                showErrorMessage();
                Log.e(TAG, "error loading from API");

            }
        };
    }

    /**
     * Creates and returns credits callback for retrofit enqueue method.
     *
     * @return - Callback<CreditsMovie>
     */
    private Callback<CreditsMovie> getCreditsMovieCallback() {
        return new Callback<CreditsMovie>() {
            @Override
            public void onResponse(Call<CreditsMovie> call, Response<CreditsMovie> response) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "Crew retrieved: " + response.body().getCrew().size());
                    Log.d(TAG, "Cast retrieved: " + response.body().getCast().size());
                    // Removing incomplete crew members
                    List<CrewMovie> crew = new ArrayList<>();
                    for(CrewMovie c: response.body().getCrew()) {
                        if(c.getProfilePath() != null) {
                            crew.add(c);
                        }
                    }
                    // Setting crew
                    mCrewAdapter.updateData(crew);
                    // Setting cast
                    mCastAdapter.updateData(response.body().getCast());
                    Log.d(TAG, "data loaded from API");
                } else {
                    int statusCode  = response.code();
                    // handle request errors depending on status code
                    Log.d(TAG, "Error status code: " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<CreditsMovie> call, Throwable t) {
                showErrorMessage();
                Log.d(TAG, "error loading from API");

            }
        };
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
                    if(response.body().getResults().size() > 0) {
                        ResultMovieReview review = response.body().getResults().get(0);
                        mReviewAuthor.setText(String.format("%s %s", getString(R.string.text_movie_details_reviews_by), review.getAuthor()));
                        mReviewContent.setText(review.getContent());
                        Log.d(TAG, "data loaded from API");
                    } else {
                        mCardViewReviews.setVisibility(View.GONE);
                        btnReviews.setEnabled(false);
                    }
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
     * Creates and returns RecommendationsMovieRequest callback for retrofit enqueue method.
     *
     * @return - Callback<RecommendationsMovieRequest>
     */
    private Callback<RecommendationsMovieRequest> getRecommendationsMovieCallback() {
        return new Callback<RecommendationsMovieRequest>() {
            @Override
            public void onResponse(Call<RecommendationsMovieRequest> call, Response<RecommendationsMovieRequest> response) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "Recommendations retrieved: " + response.body().getResults().size());
                    mRecommendationsMovieAdapter.updateData(response.body().getResults());
                    Log.d(TAG, " recommendations loaded from API");
                } else {
                    int statusCode  = response.code();
                    // handle request errors depending on status code
                    Log.d(TAG, "Error status code: " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<RecommendationsMovieRequest> call, Throwable t) {
                showErrorMessage();
                Log.d(TAG, "error loading from API");

            }
        };
    }

    /**
     * Creates and returns keywords callback for retrofit enqueue method.
     *
     * @return - Callback<MovieKeywords>
     */
    private Callback<MovieKeywords> getMovieKeywords() {
        return new Callback<MovieKeywords>() {
            @Override
            public void onResponse(Call<MovieKeywords> call, Response<MovieKeywords> response) {
                if(response.isSuccessful()) {
                    MovieKeywords keywords = response.body();
                    Log.d(TAG, "Movie keywords retrieved: " + keywords.getResults().size());

                    tvKeywords.setText(MovieUtils.formatKeywordsListToString(keywords.getResults()));
                } else {
                    int statusCode  = response.code();
                    // handle request errors depending on status code
                    Log.e(TAG, "Error status code: " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<MovieKeywords> call, Throwable t) {
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

    @Override
    public void onResultClick(long id, Bundle bundle) {
        String bundleExtra = bundle.getString(MoviesConstants.BUNDLE_EXTRA_TYPE);
        if(bundleExtra != null) {
            if (bundleExtra.equals(MoviesConstants.BUNDLE_EXTRA_MOVIE)) {
                Intent intent = new Intent(this, MovieDetailsActivity.class);
                intent.putExtra(MoviesConstants.INTENT_EXTRA_ID, id);
                startActivity(intent);
            } else if (bundleExtra.equals(MoviesConstants.BUNDLE_EXTRA_PERSON)) {
                Intent intent = new Intent(this, PersonDetailsActivity.class);
                intent.putExtra(MoviesConstants.INTENT_EXTRA_PERSON_ID, id);
                startActivity(intent);
            }
        }
    }
}