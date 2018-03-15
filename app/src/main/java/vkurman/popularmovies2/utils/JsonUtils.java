package vkurman.popularmovies2.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vkurman.popularmovies2.model.Movie;

/**
 * Project Popular Movies stage 2.
 * Created by Vassili Kurman on 06/03/2018.
 * Version 2.0
 */
public class JsonUtils {
    private static final String TAG = "JsonUtils";

    // Results Json name
    private static final String JSON_RESULTS = "results";

    // Movie fields Json names
    private static final String JSON_MOVIE_ID = "movieId";
    private static final String JSON_TITLE = "title";
    private static final String JSON_VOTE_AVERAGE = "vote_average";
    private static final String JSON_POSTER_PATH = "poster_path";
    private static final String JSON_RELEASE_DATE = "release_date";
    private static final String JSON_PLOT_SYNOPSIS = "overview";

    /**
     * Fetches and returns list of movies from json string.
     *
     * @param json - string in json format
     * @return List<Movie>
     */
    public static List<Movie> parseMovieJson(String json) {

        final List<Movie> movies = new ArrayList<>();

        try {
            // Parsing json string to json object
            JSONObject jsonObject = new JSONObject(json);
            // Getting json array results from json object
            JSONArray resultsArray = jsonObject.optJSONArray(JSON_RESULTS);
            if(resultsArray.length() > 0) {
                Log.d(TAG, "Objects in json results array: " + resultsArray.length());
                for(int i = 0; i < resultsArray.length(); i++) {
                    JSONObject movieJsonObject = resultsArray.optJSONObject(i);
                    // Getting individual values from json object
                    int movieId = movieJsonObject.optInt(JSON_MOVIE_ID);
                    String title = movieJsonObject.optString(JSON_TITLE);
                    String poster = movieJsonObject.optString(JSON_POSTER_PATH);
                    String vote = String.valueOf(movieJsonObject.optDouble(JSON_VOTE_AVERAGE));
                    String released = movieJsonObject.optString(JSON_RELEASE_DATE);
                    String synopsis = movieJsonObject.optString(JSON_PLOT_SYNOPSIS);

                    movies.add(new Movie(movieId, poster, title, released, vote, synopsis));
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parse Movie Json: " + e);
        }

        return movies;
    }
}
