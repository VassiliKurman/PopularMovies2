package vkurman.popularmovies2.persistance;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * MoviesContract - contract for database.
 *
 * Created by Vassili Kurman on 15/03/2018.
 * Version 1.0
 */
public class MoviesContract {

    // The authorithy is how app knows which content provider to use
    public static final String AUTHORITY  = "vkurman.popularmovies2";
    // The base content Uri
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    // path to access "movies" directory
    public static final String PATH_MOVIES  = "movies";

    public static final class MoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_POSTER = "moviePoster";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_PLOT_SYNOPSIS  ="plotSynopsis";
    }

}