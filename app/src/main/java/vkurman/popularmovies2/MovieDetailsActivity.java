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
import android.support.v7.widget.GridLayoutManager;
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
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vkurman.popularmovies2.adapters.MovieCrewAdapter;
import vkurman.popularmovies2.adapters.RetrofitPeopleAdapter;
import vkurman.popularmovies2.adapters.VideosAdapter;
import vkurman.popularmovies2.listeners.ResultListener;
import vkurman.popularmovies2.loaders.VideosLoader;
import vkurman.popularmovies2.model.CreditsMovie;
import vkurman.popularmovies2.model.CrewMovie;
import vkurman.popularmovies2.model.Movie;
import vkurman.popularmovies2.model.MovieModel;
import vkurman.popularmovies2.model.PeopleQueryResponse;
import vkurman.popularmovies2.model.Video;
import vkurman.popularmovies2.persistance.MoviesContract;
import vkurman.popularmovies2.retrofit.ApiUtils;
import vkurman.popularmovies2.ui.PersonDetailsActivity;
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
    @BindView(R.id.rv_videos) RecyclerView mRecyclerViewTrailers;
    @BindView(R.id.recyclerview_crew) RecyclerView mRecyclerViewCrew;
    @BindView(R.id.btn_reviews) Button btnReviews;

    private final static String TAG = "MovieDetailsActivity";

    // Reference to Movie object
//    private Movie movie;
    private long movieId;
    // VideosAdapter for Trailers RecycleView
    private VideosAdapter mTrailersAdapter;
    // MovieCrewAdapter for Crew RecycleView
    private MovieCrewAdapter mCrewAdapter;
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
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            ActionBar actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent != null) {
            movieId = intent.getLongExtra(MoviesConstants.INTENT_EXTRA_MOVIE_ID, -1L);
        } else {
            closeOnError();
        }

        dataChanged = false;

        // Retrieving api key
        final Map<String, String> data = new HashMap<>();
        data.put("api_key", getString(R.string.api_key));
        data.put("language", "en-US");
        ApiUtils.getTMDBService().getMovie(movieId, data).enqueue(getMovieCallback());

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
        // Loading data
        // Retrieving api key
        final Map<String, String> creditsData = new HashMap<>();
        creditsData.put("api_key", getString(R.string.api_key));
        ApiUtils.getTMDBService().getMovieCredits(movieId, creditsData).enqueue(getCreditsMovieCallback());

        // Setting recycle view for trailers
        mRecyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        mTrailersAdapter = new VideosAdapter(new ArrayList<Video>());
        mRecyclerViewTrailers.setAdapter(mTrailersAdapter);

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

    // TODO refactor for reviews
    @Override
    public void onClick(View view) {
        if(view == btnReviews) {
            if(movieId >= 0) {
                Intent intent = new Intent(MovieDetailsActivity.this, MovieReviewsActivity.class);
                intent.putExtra(MoviesConstants.INTENT_EXTRA_MOVIE_ID, movieId);
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
        return new VideosLoader(this, String.valueOf(movieId));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Video>> loader, List<Video> data) {
        if(data == null) {
            return;
        }

        if(mTrailersAdapter == null) {
            mTrailersAdapter = new VideosAdapter(data);
            mRecyclerViewTrailers.setAdapter(mTrailersAdapter);
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
                    // Setting details
                    tvTitle.setText(movieModel.getTitle());
                    tvReleaseDate.setText(MovieUtils.formatYearText(movieModel.getReleaseDate()));
                    tvVoteAverage.setText(MovieUtils.formatPercentage(movieModel.getVoteAverage()));
                    tvOverview.setText(movieModel.getOverview());
                    // Setting OnClickListener
                    btnReviews.setOnClickListener(MovieDetailsActivity.this);
                    ivFavourite.setOnClickListener(MovieDetailsActivity.this);

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

                    mCrewAdapter.updateData(response.body().getCrew());
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
    public void onResultClick(long id) {
        Intent intent = new Intent(this, PersonDetailsActivity.class);
        intent.putExtra(MoviesConstants.INTENT_EXTRA_PERSON_ID, id);
        startActivity(intent);
    }
}