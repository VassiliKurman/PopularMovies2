package vkurman.popularmovies2.loaders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import vkurman.popularmovies2.model.Video;
import vkurman.popularmovies2.utils.JsonUtils;
import vkurman.popularmovies2.utils.MovieUtils;

/**
 * VideosLoader
 * Created by Vassili Kurman on 23/03/2018.
 * Version 1.0
 */

public class VideosLoader extends AsyncTaskLoader<List<Video>> {

    /**
     * ReviewsLoader id
     */
    public static final int ID = 105;

    private String movieId;

    public VideosLoader(@NonNull Context context, @NonNull String movieId) {
        super(context);
        this.movieId = movieId;
    }

    @Override
    public List<Video> loadInBackground() {
        if(movieId == null || movieId.isEmpty()) {
            return null;
        }

        URL url = MovieUtils.createTrailersUrl(movieId);
        try {
            String json = MovieUtils.getJsonResponseFromWeb(url);
            Log.d("VideosLoader", "Json: " + json);
            return JsonUtils.parseVideosJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}