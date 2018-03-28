package vkurman.popularmovies2.loaders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import vkurman.popularmovies2.R;
import vkurman.popularmovies2.model.Movie;
import vkurman.popularmovies2.utils.JsonUtils;
import vkurman.popularmovies2.utils.MovieUtils;

/**
 * MoviesLoader
 * Created by Vassili Kurman on 23/03/2018.
 * Version 1.0
 */
public class MoviesLoader extends AsyncTaskLoader<List<Movie>> {

    private int sortingId;

    public MoviesLoader(@NonNull Context context, @NonNull int sortingId) {
        super(context);
        this.sortingId = sortingId;
    }

    @Override
    public List<Movie> loadInBackground() {
        URL url = null;

        if(sortingId == -1 || sortingId == R.id.popular) {
            url = MovieUtils.createPopularMovieUrl();
        } else if(sortingId == R.id.top_rate) {
            url = MovieUtils.createTopRatedMovieUrl();
        }

        if(url == null) {
            return null;
        }

        try {
            String json = MovieUtils.getJsonResponseFromWeb(url);
            return JsonUtils.parseMovieJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
