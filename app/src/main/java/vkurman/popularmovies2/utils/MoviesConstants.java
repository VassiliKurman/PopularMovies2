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

/**
 * MoviesConstants
 * Created by Vassili Kurman on 22/09/2018.
 * Version 1.0
 */
public class MoviesConstants {
    private MoviesConstants(){}

    // Navigation drawer movies
    public static final String PATH_SELECTION_DISCOVER_MOVIES = "movie";
    public static final String PATH_SELECTION_DISCOVER_TV_SHOWS = "tv";

    // Navigation drawer movies
    public static final String PATH_SELECTION_MOVIES_NOW_PLAYING = "now_playing";
    public static final String PATH_SELECTION_MOVIES_POPULAR = "popular";
    public static final String PATH_SELECTION_MOVIES_TOP_RATED = "top_rated";
    public static final String PATH_SELECTION_MOVIES_UPCOMING = "upcoming";

    // Navigation drawer movies
    /**
     * Get a list of the current popular TV shows on TMDb. This list updates daily.
     */
    public static final String PATH_SELECTION_TV_SHOWS_POPULAR = "popular";
    /**
     * Get a list of the top rated TV shows on TMDb.
     */
    public static final String PATH_SELECTION_TV_SHOWS_TOP_RATED = "top_rated";
    /**
     * TV show that has an episode with an air date in the next 7 days.
     */
    public static final String PATH_SELECTION_TV_SHOWS_ON_TV = "on_the_air";
    /**
     * Get a list of TV shows that are airing today.
     */
    public static final String PATH_SELECTION_TV_SHOWS_AIRING_TODAY = "airing_today";

    // Navigation drawer people
    public static final String PATH_SELECTION_PEOPLE_PUPOLAR = "popular";

    // Intent extras
    public static final String INTENT_EXTRA_MOVIE_ID = "movieId";
    public static final String INTENT_EXTRA_SHOW_ID = "showId";
    public static final String INTENT_EXTRA_PERSON_ID = "personId";
    public static final String INTENT_EXTRA_MOVIE_TITLE = "movieTitle";
    public static final String INTENT_EXTRA_PERSON_KNOWN_FOR = "knownFor";
}