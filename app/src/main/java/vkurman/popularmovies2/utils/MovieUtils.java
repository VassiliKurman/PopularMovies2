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
package vkurman.popularmovies2.utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

/**
 * Project Popular Movies stage 2.
 * Created by Vassili Kurman on 06/03/2018.
 * Version 2.0
 */
public class MovieUtils {
    private static final String TAG = "MovieUtils";

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
     * Build and returns Uri for specified youtube trailer.
     *
     * @param trailerKey - String
     * @return Uri
     */
    public static Uri getYoutubeTrailerUri(String trailerKey) {
        String path = YOUTUBE_TRAILER_BASE_URL + trailerKey;

        Log.d(TAG, "Youtube trailer uri: " + path);

        return Uri.parse(path);
    }

    /**
     * Constructing and returning URL object to retrieve popular movies.
     *
     * @return URL
     */
    public static URL createPopularMovieUrl(String apiKey) {
        String path = API_BASE_URL + API_CATEGORY_POPULAR + API_URL_PARAMETER + apiKey;

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
    public static URL createTopRatedMovieUrl(String apiKey) {
        String path = API_BASE_URL + API_CATEGORY_TOP_RATED + API_URL_PARAMETER + apiKey;

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
     * @param movieId - String
     * @param apiKey - String
     * @return URL
     */
    public static URL createTrailersUrl(String movieId, String apiKey) {
        String path = API_BASE_URL + movieId + API_TRAILERS + API_URL_PARAMETER + apiKey;

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
     * @param movieId - String
     * @param apiKey - String
     * @return URL
     */
    public static URL createReviewsUrl(String movieId, String apiKey) {
        String path = API_BASE_URL + movieId + API_REVIEWS + API_URL_PARAMETER + apiKey;

        Log.d(TAG, "Reviews path: " + path);

        try {
            return new URL(path);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error building URL!");
        }

        return null;
    }

    /**
     * Creates and returns full path to movie poster.
     *
     * @param imagePath - partial path to the image retrieved from json
     * @return String
     */
    public static String createFullPosterPath(String imagePath) {
        if(imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        return IMAGE_BASE_URL + IMAGE_SIZE_W342 + imagePath;
    }

    /**
     * Creates and returns full path to movie poster.
     *
     * @param imagePath - partial path to the image retrieved from json
     * @return String
     */
    public static String createFullBackdropPath(String imagePath) {
        if(imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        return IMAGE_BASE_URL + IMAGE_SIZE_W780 + imagePath;
    }

    /**
     * Constructing and returning URL object to retrieve trailer image for movies from youtube.
     *
     * @param videoId - String
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

    /**
     * Constructing and returning String to display from - to years as text.
     *
     * @param startYear - String
     * @param endYear - String
     * @return String
     */
    public static String formatYearSpanText(String startYear, String endYear) {
        if(startYear == null || endYear == null) {
            return null;
        }

        return "("+startYear.substring(0, 4) + "-" + endYear.substring(0, 4)+")";
    }

    /**
     * Constructing and returning String to display from - to years as text.
     *
     * @param date - String
     * @return String
     */
    public static String formatYearText(String date) {
        if(date == null) {
            return null;
        }

        return "("+date.substring(0, 4) +")";
    }

    /**
     * Constructing and returning String representation of votes average in percents.
     *
     * @param voteAverage - Double
     * @return String
     */
    public static String formatPercentage(Double voteAverage) {
        return NumberFormat.getPercentInstance(Locale.getDefault()).format(voteAverage / 10);
    }

    /**
     * Constructing and returning String representation of date.
     *
     * @param date - String
     * @return String
     */
    public static String formatDate(String date) {
        try {
            Date parsedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date);
            return DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault()).format(parsedDate);
        } catch (ParseException e) {
            return date;
        }
    }
}
