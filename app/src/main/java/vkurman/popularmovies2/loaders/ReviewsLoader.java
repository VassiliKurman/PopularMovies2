package vkurman.popularmovies2.loaders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import vkurman.popularmovies2.model.Review;
import vkurman.popularmovies2.utils.JsonUtils;
import vkurman.popularmovies2.utils.MovieUtils;

/**
 * ReviewsLoader
 * Created by Vassili Kurman on 23/03/2018.
 * Version 1.0
 */

public class ReviewsLoader extends AsyncTaskLoader<List<Review>> {

    private String movieId;

    public ReviewsLoader(@NonNull Context context, @NonNull String movieId) {
        super(context);
        this.movieId = movieId;
    }

    @Override
    public List<Review> loadInBackground() {
        if(movieId == null || movieId.isEmpty()) {
            return null;
        }

        URL url = MovieUtils.createReviewsUrl(movieId);
        try {
            String json = MovieUtils.getResponseFromTheMovieDB(url);
            return JsonUtils.parseReviewsJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}