package vkurman.popularmovies2.utils;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import vkurman.popularmovies2.model.Movie;

/**
 * Project Popular Movies stage 2.
 * Created by Vassili Kurman on 06/03/2018.
 * Version 2.0
 */
public class MovieUtils {
    private static final String TAG = "MovieUtils";

    // TODO enter TheMovieDB API key here
    private static final String THEMOVIEDB_API_KEY = "";

    private static final String API_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String API_CATEGORY_POPULAR = "popular";
    private static final String API_CATEGORY_TOP_RATED = "top_rated";
    private static final String API_URL_PARAMETER = "?api_key=";

    private static final String API_TRAILERS = "/videos";
    private static final String API_REVIEWS = "/reviews";

    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    // Available image sizes
    private static final String IMAGE_SIZE_W92 = "w92";
    private static final String IMAGE_SIZE_W154 = "w154";
    private static final String IMAGE_SIZE_W185 = "w185";
    private static final String IMAGE_SIZE_W342 = "w342";
    private static final String IMAGE_SIZE_W500 = "w500";
    private static final String IMAGE_SIZE_W780 = "w780";
    private static final String IMAGE_SIZE_ORIGINAL = "original";

    private static final String IMAGE_SIZE_DEFAULT = IMAGE_SIZE_W185;

    // Youtube
    private static final String YOUTUBE_IMAGE_BASE_URL = "http://img.youtube.com/vi/";
    private static final String YOUTUBE_IMAGE_FILE = "/0.jpg";
    private static final String YOUTUBE_TRAILER_BASE_URL = "https://www.youtube.com/watch?v=";

    /**
     * Returns response from specified URL in json format.
     *
     * @param url - url to specific query
     * @return String - response from server in json format
     * @throws IOException - throws when can't open url connection
     */
    public static String getJsonResponseFromWeb(URL url) throws IOException {
        if(url != null) {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = urlConnection.getInputStream();
                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                if(scanner.hasNext()) {
                    return scanner.next();
                }
            } finally {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    /**
     * Constructing and returning URL object to retrieve popular movies.
     *
     * @return URL
     */
    public static URL createPopularMovieUrl() {
        String path = API_BASE_URL + API_CATEGORY_POPULAR + API_URL_PARAMETER + THEMOVIEDB_API_KEY;

        Log.d(TAG, "Path: " + path);

        try {
            return new URL(path);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error building URL!");
        }

        return null;
    }

    /**
     * Constructing and returning URL object to retrieve top rated movies.
     *
     * @return URL
     */
    public static URL createTopRatedMovieUrl() {
        String path = API_BASE_URL + API_CATEGORY_TOP_RATED + API_URL_PARAMETER + THEMOVIEDB_API_KEY;

        Log.d(TAG, "Top rated movies path: " + path);

        try {
            return new URL(path);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error building URL!");
        }

        return null;
    }

    /**
     * Constructing and returning URL object to retrieve trailers for movies.
     *
     * @param movieId
     * @return URL
     */
    public static URL createTrailersUrl(String movieId) {
        String path = API_BASE_URL + movieId + API_TRAILERS + API_URL_PARAMETER + THEMOVIEDB_API_KEY;

        Log.d(TAG, "Trailers path: " + path);

        try {
            return new URL(path);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error building URL!");
        }

        return null;
    }

    /**
     * Constructing and returning URL object to retrieve reviews for movies.
     *
     * @param movieId
     * @return URL
     */
    public static URL createReviewsUrl(String movieId) {
        String path = API_BASE_URL + movieId + API_REVIEWS + API_URL_PARAMETER + THEMOVIEDB_API_KEY;

        Log.d(TAG, "Reviews path: " + path);

        try {
            return new URL(path);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error building URL!");
        }

        return null;
    }

    /**
     * Parses json string anf returns list of movies.
     *
     * @param json - response from server in json format
     * @return List<Movie>
     */
    public static List<Movie> parseJson(String json) {
        return JsonUtils.parseMovieJson(json);
    }

    /**
     * Creates and returns full path to movie poster.
     *
     * @param imagePath - partial path to the image retrieved from json
     * @return String
     */
    public static String createFullIconPath(String imagePath) {
        if(imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        return IMAGE_BASE_URL + IMAGE_SIZE_DEFAULT + imagePath;
    }

    /**
     * Constructing and returning URL object to retrieve trailer image for movies from youtube.
     *
     * @param videoId
     * @return String
     */
    public static String createYoutubeTrailerImageUrl(String videoId) {
        if(videoId == null || videoId.isEmpty()) {
            return null;
        }

        String path = YOUTUBE_IMAGE_BASE_URL + videoId + YOUTUBE_IMAGE_FILE;
        Log.d("MovieUtils", "Youtube path: " + path);
        return path;
    }
}
