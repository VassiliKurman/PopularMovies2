package vkurman.popularmovies2.persistance;

import android.provider.BaseColumns;

/**
 * MoviesContract
 * Created by Vassili Kurman on 15/03/2018.
 * Version 1.0
 */

public class MoviesContract {

    public static final class MoviesEntry implements BaseColumns {
        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_POSTER = "moviePoster";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_PLOT_SYNOPSIS  ="plotSynopsis";
    }

}