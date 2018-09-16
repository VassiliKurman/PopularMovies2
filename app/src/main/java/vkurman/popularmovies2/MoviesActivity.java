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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vkurman.popularmovies2.adapters.MoviesAdapter;
import vkurman.popularmovies2.loaders.MoviesFavouriteLoader;
import vkurman.popularmovies2.loaders.MoviesPopularLoader;
import vkurman.popularmovies2.loaders.MoviesRatedLoader;
import vkurman.popularmovies2.model.Movie;

/**
 * Project Popular Movies stage 2.
 * Created by Vassili Kurman on 25/02/2018.
 * Version 2.0
 */
public class MoviesActivity extends AppCompatActivity implements
        MoviesAdapter.MovieClickListener, LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String SORTING_KEY = "sort";
    private static final int REQUEST_CODE = 1001;

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView navigationView;

    private MoviesAdapter mAdapter;
    private int mMovieLoaderId;
    private int sortingId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        // Binding views
        ButterKnife.bind(this);
        // Setting Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            ActionBar actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // TODO
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });

        // use a grid layout manager
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(savedInstanceState != null) {
            sortingId = savedInstanceState.getInt(SORTING_KEY);
        }

        mMovieLoaderId = MoviesPopularLoader.ID;

        // specifying an adapter and passing empty list at start
        mAdapter = new MoviesAdapter(new ArrayList<Movie>(), this);
        mRecyclerView.setAdapter(mAdapter);

        // Setting loaders
        getSupportLoaderManager().initLoader(mMovieLoaderId, null, this).forceLoad();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SORTING_KEY, sortingId);
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
                Log.d("MovieActivity", "Result changed");
                mAdapter.notifyDataSetChanged();
                if(getSupportLoaderManager().getLoader(mMovieLoaderId) == null) {
                    getSupportLoaderManager().initLoader(mMovieLoaderId, null, this).forceLoad();
                } else {
                    getSupportLoaderManager().getLoader(mMovieLoaderId).forceLoad();
                }
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


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.popular:
//                item.setChecked(true);
//                sortingId = item.getItemId();
//                mMovieLoaderId = MoviesPopularLoader.ID;
//                if(getSupportLoaderManager().getLoader(mMovieLoaderId) == null) {
//                    getSupportLoaderManager().initLoader(mMovieLoaderId, null, this).forceLoad();
//                } else {
//                    getSupportLoaderManager().getLoader(mMovieLoaderId).forceLoad();
//                }
//                return true;
//            case R.id.top_rate:
//                item.setChecked(true);
//                sortingId = item.getItemId();
//                mMovieLoaderId = MoviesRatedLoader.ID;
//                if(getSupportLoaderManager().getLoader(mMovieLoaderId) == null) {
//                    getSupportLoaderManager().initLoader(mMovieLoaderId, null, this).forceLoad();
//                } else {
//                    getSupportLoaderManager().getLoader(mMovieLoaderId).forceLoad();
//                }
//                return true;
//            case R.id.favourite:
//                item.setChecked(true);
//                sortingId = item.getItemId();
//                mMovieLoaderId = MoviesFavouriteLoader.ID;
//                if(getSupportLoaderManager().getLoader(mMovieLoaderId) == null) {
//                    getSupportLoaderManager().initLoader(mMovieLoaderId, null, this).forceLoad();
//                } else {
//                    getSupportLoaderManager().getLoader(mMovieLoaderId).forceLoad();
//                }
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
        if(id == MoviesPopularLoader.ID) {
            return new MoviesPopularLoader(this);
        } else if(id == MoviesRatedLoader.ID) {
            return new MoviesRatedLoader(this);
        } else if(id == MoviesFavouriteLoader.ID) {
            return new MoviesFavouriteLoader(this);
        }
        return null;
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
            mAdapter.updateMovies(mRecyclerView.getContext(), data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {}
}