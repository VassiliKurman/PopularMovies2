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
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vkurman.popularmovies2.adapters.MoviesAdapter;
import vkurman.popularmovies2.adapters.RetrofitMoviesAdapter;
import vkurman.popularmovies2.adapters.RetrofitPeopleAdapter;
import vkurman.popularmovies2.adapters.RetrofitTVsAdapter;
import vkurman.popularmovies2.listeners.ResultListener;
import vkurman.popularmovies2.loaders.MoviesFavouriteLoader;
import vkurman.popularmovies2.model.Movie;
import vkurman.popularmovies2.model.MoviesQueryResponse;
import vkurman.popularmovies2.model.PeopleQueryResponse;
import vkurman.popularmovies2.model.ResultMovie;
import vkurman.popularmovies2.model.TVQueryResponse;
import vkurman.popularmovies2.retrofit.ApiUtils;
import vkurman.popularmovies2.retrofit.TMDBService;
import vkurman.popularmovies2.utils.MoviesConstants;

/**
 * Project Popular Movies stage 2.
 * Created by Vassili Kurman on 25/02/2018.
 * Version 2.0
 */
public class MoviesActivity extends AppCompatActivity implements
        MoviesAdapter.MovieClickListener, LoaderManager.LoaderCallbacks<List<Movie>>, ResultListener {

    private static final String TAG = MoviesActivity.class.getSimpleName();

    private static final int REQUEST_CODE = 1001;

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private RecyclerView.Adapter mAdapter;
    private TMDBService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        // Binding views
        ButterKnife.bind(this);
        // Setting Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            ActionBar actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        mService = ApiUtils.getTMDBService();

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        switch (menuItem.getItemId()) {
                            case R.id.navigation_drawer_discover_movies:
                                discoverData(R.id.navigation_drawer_discover_movies);
                                return true;
                            case R.id.navigation_drawer_discover_tv_shows:
                                discoverData(R.id.navigation_drawer_discover_tv_shows);
                                return true;
                            case R.id.navigation_drawer_movies_now_playing:
                                loadData(R.id.navigation_drawer_movies_now_playing);
                                return true;
                            case R.id.navigation_drawer_movies_popular:
                                loadData(R.id.navigation_drawer_movies_popular);
                                return true;
                            case R.id.navigation_drawer_movies_top_rated:
                                loadData(R.id.navigation_drawer_movies_top_rated);
                                return true;
                            case R.id.navigation_drawer_movies_upcoming:
                                loadData(R.id.navigation_drawer_movies_upcoming);
                                return true;
                            case R.id.navigation_drawer_movies_favourites:
                                loadData(R.id.navigation_drawer_movies_favourites);
                                return true;
                            case R.id.navigation_drawer_tv_shows_popular:
                                loadData(R.id.navigation_drawer_tv_shows_popular);
                                return true;
                            case R.id.navigation_drawer_tv_shows_top_rated:
                                loadData(R.id.navigation_drawer_tv_shows_top_rated);
                                return true;
                            case R.id.navigation_drawer_tv_shows_on_tv:
                                loadData(R.id.navigation_drawer_tv_shows_on_tv);
                                return true;
                            case R.id.navigation_drawer_tv_shows_airing_today:
                                loadData(R.id.navigation_drawer_tv_shows_airing_today);
                                return true;
                            case R.id.navigation_drawer_people_popular:
                                loadData(R.id.navigation_drawer_people_popular);
                                return true;
                        }
                        return true;
                    }
                });
        // use a grid layout manager
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specifying an adapter and passing empty list at start
        mAdapter = new RetrofitMoviesAdapter(new ArrayList<ResultMovie>(0), this);
        mRecyclerView.setAdapter(mAdapter);
        // Loading data
        loadData(R.id.navigation_drawer_movies_popular);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * This is where callback is received from
     * {@link MoviesAdapter.MovieClickListener}
     *
     * This callback is invoked when item in the list is clicked.
     *
     * @param movie - Sensor in the list of the item that was clicked.
     */
    @Override
    public void onMovieClicked(Movie movie) {
        if(movie != null) {
            Intent intent = new Intent(MoviesActivity.this, MovieDetailsActivity.class);
            intent.putExtra("movie", movie);
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            Toast.makeText(this, "Movie not set!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE) {
            if(resultCode == 1) {
                // TODO change
                Log.d("MovieActivity", "ResultMovie changed");
//                mAdapter.notifyDataSetChanged();
//                if(getSupportLoaderManager().getLoader(mMovieLoaderId) == null) {
//                    getSupportLoaderManager().initLoader(mMovieLoaderId, null, this).forceLoad();
//                } else {
//                    getSupportLoaderManager().getLoader(mMovieLoaderId).forceLoad();
//                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
        return new MoviesFavouriteLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {
        if(data == null) {
            return;
        }

        if(mAdapter == null) {
            mAdapter = new MoviesAdapter(data, this);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            if(mAdapter instanceof MoviesAdapter) {
                ((MoviesAdapter) mAdapter).updateData(mRecyclerView.getContext(), data);
            } else {
                mAdapter = new MoviesAdapter(data,this);
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {}

    public void discoverData(int selection) {
        // Retrieving api key
        final Map<String, String> data = new HashMap<>();
        data.put("api_key", getString(R.string.api_key));
        data.put("language", "en-US");
        // TODO other parameters
        data.put("page", "1");
        // Loading data depending on selection
        switch (selection) {
            case R.id.navigation_drawer_discover_movies:
                toolbar.setTitle(R.string.toolbar_title_discover_movies);
                mService.discoverMovies(MoviesConstants.PATH_SELECTION_DISCOVER_MOVIES, data).enqueue(getMoviesCallback());
                break;
            case R.id.navigation_drawer_discover_tv_shows:
                toolbar.setTitle(R.string.toolbar_title_discover_tv_shows);
                mService.discoverTVs(MoviesConstants.PATH_SELECTION_DISCOVER_TV_SHOWS, data).enqueue(getTVCallback());
                break;
        }
    }

    public void loadData(int selection) {
        // Retrieving api key
        final Map<String, String> data = new HashMap<>();
        data.put("api_key", getString(R.string.api_key));
        data.put("language", "en-US");
        data.put("page", "1");
        // Loading data depending on selection
        switch (selection) {
            case R.id.navigation_drawer_movies_now_playing :
                toolbar.setTitle(R.string.toolbar_title_movies_now_playing);
                mService.getMovies(MoviesConstants.PATH_SELECTION_MOVIES_NOW_PLAYING, data).enqueue(getMoviesCallback());
                break;
            case R.id.navigation_drawer_movies_popular :
                toolbar.setTitle(R.string.toolbar_title_movies_popular);
                mService.getMovies(MoviesConstants.PATH_SELECTION_MOVIES_POPULAR, data).enqueue(getMoviesCallback());
                break;
            case R.id.navigation_drawer_movies_top_rated :
                toolbar.setTitle(R.string.toolbar_title_movies_top_rated);
                mService.getMovies(MoviesConstants.PATH_SELECTION_MOVIES_TOP_RATED, data).enqueue(getMoviesCallback());
                break;
            case R.id.navigation_drawer_movies_upcoming :
                toolbar.setTitle(R.string.toolbar_title_movies_upcoming);
                mService.getMovies(MoviesConstants.PATH_SELECTION_MOVIES_UPCOMING, data).enqueue(getMoviesCallback());
                break;
            case R.id.navigation_drawer_movies_favourites :
                toolbar.setTitle(R.string.toolbar_title_movies_favourites);
                if(getSupportLoaderManager().getLoader(MoviesFavouriteLoader.ID) != null) {
                    getSupportLoaderManager().getLoader(MoviesFavouriteLoader.ID).forceLoad();
                } else {
                    getSupportLoaderManager().initLoader(MoviesFavouriteLoader.ID, null, this).forceLoad();
                }
                break;
            case R.id.navigation_drawer_tv_shows_popular :
                toolbar.setTitle(R.string.toolbar_title_tv_shows_popular);
                mService.getTV(MoviesConstants.PATH_SELECTION_TV_SHOWS_POPULAR, data).enqueue(getTVCallback());
                break;
            case R.id.navigation_drawer_tv_shows_top_rated :
                toolbar.setTitle(R.string.toolbar_title_tv_shows_top_rated);
                mService.getTV(MoviesConstants.PATH_SELECTION_TV_SHOWS_TOP_RATED, data).enqueue(getTVCallback());
                break;
            case R.id.navigation_drawer_tv_shows_on_tv :
                toolbar.setTitle(R.string.toolbar_title_tv_shows_on_tv);
                mService.getTV(MoviesConstants.PATH_SELECTION_TV_SHOWS_ON_TV, data).enqueue(getTVCallback());
                break;
            case R.id.navigation_drawer_tv_shows_airing_today :
                toolbar.setTitle(R.string.toolbar_title_tv_shows_airing_today);
                mService.getTV(MoviesConstants.PATH_SELECTION_TV_SHOWS_AIRING_TODAY, data).enqueue(getTVCallback());
                break;
            case R.id.navigation_drawer_people_popular :
                toolbar.setTitle(R.string.toolbar_title_people_popular);
                mService.getPeople(MoviesConstants.PATH_SELECTION_PEOPLE_PUPOLAR, data).enqueue(getPeopleCallback());
                break;

        }
    }

    /**
     * Creates and returns movies callback for retrofit enqueue method.
     *
     * @return - Callback<MoviesQueryResponse>
     */
    private Callback<MoviesQueryResponse> getMoviesCallback() {
        return new Callback<MoviesQueryResponse>() {
            @Override
            public void onResponse(Call<MoviesQueryResponse> call, Response<MoviesQueryResponse> response) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "Movies retrieved: " + response.body().getResults().size());
                    if(mAdapter instanceof RetrofitMoviesAdapter) {
                        ((RetrofitMoviesAdapter) mAdapter).updateData(response.body().getResults());
                        Log.d(TAG, "posts loaded from API");
                    }else {
                        mAdapter = new RetrofitMoviesAdapter(response.body().getResults(), MoviesActivity.this);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                } else {
                    int statusCode  = response.code();
                    // handle request errors depending on status code
                    Log.d(TAG, "Error status code: " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<MoviesQueryResponse> call, Throwable t) {
                showErrorMessage();
                Log.d("MoviesActivity", "error loading from API");

            }
        };
    }

    /**
     * Creates and returns movies callback for retrofit enqueue method.
     *
     * @return - Callback<TVQueryResponse>
     */
    private Callback<TVQueryResponse> getTVCallback() {
        return new Callback<TVQueryResponse>() {
            @Override
            public void onResponse(Call<TVQueryResponse> call, Response<TVQueryResponse> response) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "TVs retrieved: " + response.body().getResults().size());
                    if(mAdapter instanceof RetrofitTVsAdapter) {
                        ((RetrofitTVsAdapter) mAdapter).updateData(response.body().getResults());
                        Log.d(TAG, "posts loaded from API");
                    } else {
                        mAdapter = new RetrofitTVsAdapter(response.body().getResults(), MoviesActivity.this);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                } else {
                    int statusCode  = response.code();
                    // handle request errors depending on status code
                    Log.d(TAG, "Error status code: " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<TVQueryResponse> call, Throwable t) {
                showErrorMessage();
                Log.d("MoviesActivity", "error loading from API");

            }
        };
    }

    /**
     * Creates and returns movies callback for retrofit enqueue method.
     *
     * @return - Callback<PeopleQueryResponse>
     */
    private Callback<PeopleQueryResponse> getPeopleCallback() {
        return new Callback<PeopleQueryResponse>() {
            @Override
            public void onResponse(Call<PeopleQueryResponse> call, Response<PeopleQueryResponse> response) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "People retrieved: " + response.body().getResults().size());
                    if(mAdapter instanceof RetrofitPeopleAdapter) {
                        ((RetrofitPeopleAdapter) mAdapter).updateData(response.body().getResults());
                        Log.d(TAG, "posts loaded from API");
                    }else {
                        mAdapter = new RetrofitPeopleAdapter(response.body().getResults(), MoviesActivity.this);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                } else {
                    int statusCode  = response.code();
                    // handle request errors depending on status code
                    Log.d(TAG, "Error status code: " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<PeopleQueryResponse> call, Throwable t) {
                showErrorMessage();
                Log.d("MoviesActivity", "error loading from API");

            }
        };
    }

    private void showErrorMessage() {
        Toast.makeText(this, "Error retrieving data from TMDB", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResultClick(long id) {
        Toast.makeText(MoviesActivity.this, "Post id is" + id, Toast.LENGTH_SHORT).show();
    }
}