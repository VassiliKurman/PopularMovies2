package vkurman.popularmovies2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
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

    @BindView(R.id.rv_movies) RecyclerView mRecyclerView;

    private MoviesAdapter mAdapter;
    private int sortingId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        // Binding views
        ButterKnife.bind(this);

        // use a grid layout manager
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(savedInstanceState != null) {
            sortingId = savedInstanceState.getInt(SORTING_KEY);
        }

        // specifying an adapter and passing empty list at start
        mAdapter = new MoviesAdapter(new ArrayList<Movie>(), this);
        mRecyclerView.setAdapter(mAdapter);

        // Setting loaders
        getSupportLoaderManager().initLoader(MoviesPopularLoader.ID, null, this).forceLoad();
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
            startActivity(intent);
        } else {
            Toast.makeText(this, "Movie not set!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        if (sortingId == -1){
            return true;
        }
        MenuItem item;
        switch (sortingId){
            case R.id.popular:
                item = menu.findItem(R.id.popular);
                item.setChecked(true);
                break;
            case R.id.top_rate:
                item = menu.findItem(R.id.top_rate);
                item.setChecked(true);
                break;
            case R.id.favourite:
                item = menu.findItem(R.id.favourite);
                item.setChecked(true);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popular:
                item.setChecked(true);
                sortingId = item.getItemId();
                if(getSupportLoaderManager().getLoader(MoviesPopularLoader.ID) == null) {
                    getSupportLoaderManager().initLoader(MoviesPopularLoader.ID, null, this).forceLoad();
                } else {
                    getSupportLoaderManager().getLoader(MoviesPopularLoader.ID).forceLoad();
                }
                return true;
            case R.id.top_rate:
                item.setChecked(true);
                sortingId = item.getItemId();
                if(getSupportLoaderManager().getLoader(MoviesRatedLoader.ID) == null) {
                    getSupportLoaderManager().initLoader(MoviesRatedLoader.ID, null, this).forceLoad();
                } else {
                    getSupportLoaderManager().getLoader(MoviesRatedLoader.ID).forceLoad();
                }
                return true;
            case R.id.favourite:
                item.setChecked(true);
                sortingId = item.getItemId();
                if(getSupportLoaderManager().getLoader(MoviesFavouriteLoader.ID) == null) {
                    getSupportLoaderManager().initLoader(MoviesFavouriteLoader.ID, null, this).forceLoad();
                } else {
                    getSupportLoaderManager().getLoader(MoviesFavouriteLoader.ID).forceLoad();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
            mAdapter.updateMovies(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {}
}