package vkurman.popularmovies2.loaders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import vkurman.popularmovies2.model.Movie;
import vkurman.popularmovies2.utils.JsonUtils;
import vkurman.popularmovies2.utils.MovieUtils;

/**
 * MoviesRatedLoader is Loader for rated movies
 * Created by Vassili Kurman on 23/03/2018.
 * Version 1.0
 */
public class MoviesRatedLoader extends AsyncTaskLoader<List<Movie>> {

    /**
     * MoviesLoader id
     */
    public static final int ID = 102;

    public MoviesRatedLoader(@NonNull Context context) {
        super(context);
    }

    @Override
    public List<Movie> loadInBackground() {
        URL url = MovieUtils.createTopRatedMovieUrl();

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
