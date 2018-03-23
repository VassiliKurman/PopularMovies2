package vkurman.popularmovies2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vkurman.popularmovies2.adapters.VideosAdapter;
import vkurman.popularmovies2.loaders.VideosLoader;
import vkurman.popularmovies2.model.Movie;
import vkurman.popularmovies2.model.Video;

public class MovieVideosActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Video>> {

    @BindView(R.id.rv_videos) RecyclerView mRecyclerView;

    private VideosAdapter mAdapter;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_videos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Binding views
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        movie = intent.getParcelableExtra("movie");
        // Return from method if movie not set
        if(movie == null) {
            return;
        }

        setTitle(movie.getTitle());

        // Setting recycle view for reviews
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new VideosAdapter(new ArrayList<Video>());
        mRecyclerView.setAdapter(mAdapter);

        // Setting loaders
        getSupportLoaderManager().initLoader(0, null, this).forceLoad();
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

    @NonNull
    @Override
    public Loader<List<Video>> onCreateLoader(int id, @Nullable Bundle args) {
        return new VideosLoader(this, String.valueOf(movie.getMovieId()));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Video>> loader, List<Video> data) {
        if(data == null) {
            return;
        }

        if(mAdapter == null) {
            mAdapter = new VideosAdapter(data);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.updateVideos(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Video>> loader) {}
}