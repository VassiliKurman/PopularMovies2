package vkurman.popularmovies2.persistance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import vkurman.popularmovies2.model.Movie;

/**
 * MoviesPersistenceManager
 * Created by Vassili Kurman on 15/03/2018.
 * Version 1.0
 */

public class MoviesPersistenceManager {

    /**
     * The volatile keyword ensures that multiple threads
     * handle the <code>manager</code> variable correctly when it is being
     * initialised to the <code>MoviesPersistenceManager</code> instance.
     * In Java 1.4 and earlier the volatile keyword is improper
     * synchronised for double-checked locking
     */
    private volatile static MoviesPersistenceManager manager;
    private MoviesDbHelper dbHelper;
    private SQLiteDatabase mDB;
    private boolean isWritableDB;

    private MoviesPersistenceManager(Context context) {
        // creating instance of MoviesDbHelper
        this.dbHelper = new MoviesDbHelper(context);
        this.mDB = dbHelper.getWritableDatabase();
        isWritableDB = false;
    }

    /**
     * This method is checking if the instance of current class exists
     * in the system and returns ever the instance that has been created
     * earlier or the newly created instance.
     *
     * @return MoviesPersistenceManager object
     */
    public static MoviesPersistenceManager getInstance(Context context) {
        // Double-checked locking with synchronisation
        if(manager == null) {
            synchronized (MoviesPersistenceManager.class) {
                if(manager == null) {
                    manager = new MoviesPersistenceManager(context);
                }
            }
        }
        // Under either circumstance this returns the instance
        return manager;
    }

    /**
     * Returns true if database is writable, otherwise false;
     *
     * @return boolean
     */
    public boolean isWritableDB() {
        return isWritableDB;
    }

    /**
     * Switches db to readable mode.
     */
    public void switchToWritableDb() {
        if(!isWritableDB) {
            mDB = dbHelper.getWritableDatabase();
            isWritableDB = true;
        }
    }

    /**
     * Switches database to readable mode.
     */
    public void switchToReadableDb() {
        if(isWritableDB) {
            mDB = dbHelper.getReadableDatabase();
            isWritableDB = false;
        }
    }

    /**
     * Retrieves all favourite movies saved in local database
     *
     * @return List<Movie>
     */
    public List<Movie> getFavouriteMovies() {
        List<Movie> list = new ArrayList<>();

        try(Cursor cursor =  mDB.query(
                MoviesContract.MoviesEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                MoviesContract.MoviesEntry._ID
        )) {
            while (cursor.moveToNext()) {
                Integer id = cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry._ID));
                String poster = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER));
                String title = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE));
                String release = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE));
                String vote = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE));
                String plot = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_PLOT_SYNOPSIS));
                list.add(new Movie(id, poster, title, release, vote, plot));
            }
        }

        return list;
    }

    /**
     * Retrieves all id's from favourite movies saved in local database
     *
     * @return Map<Integer, Integer>
     */
    public Map<Long, Long> getFavouriteMovieIds() {
        Map<Long, Long> map = new TreeMap<>();

        try(Cursor cursor =  mDB.query(
                MoviesContract.MoviesEntry.TABLE_NAME,
                new String[] {MoviesContract.MoviesEntry._ID},
                null,
                null,
                null,
                null,
                MoviesContract.MoviesEntry._ID
        )) {
            while (cursor.moveToNext()) {
                Long id = cursor.getLong(cursor.getColumnIndex(MoviesContract.MoviesEntry._ID));
                map.put(id, id);
            }
        }

        return map;
    }

//    /**
//     * Adds new favourite movie to local database
//     *
//     * @param movie
//     * @return long
//     */
//    public long addNewFavouriteMovie(Movie movie) {
//        if(movie == null) {
//            return -1L;
//        }
//
//        ContentValues cv = new ContentValues();
//        cv.put(MoviesContract.MoviesEntry._ID, movie.getMovieId());
//        cv.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER, movie.getMoviePoster());
//        cv.put(MoviesContract.MoviesEntry.COLUMN_TITLE, movie.getTitle());
//        cv.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
//        cv.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
//        cv.put(MoviesContract.MoviesEntry.COLUMN_PLOT_SYNOPSIS, movie.getPlotSynopsis());
//
//        return mDB.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, cv);
//    }

//    /**
//     * Removes favourite movie from local database.
//     *
//     * @param id
//     * @return long - movie id
//     */
//    public boolean removeFavouriteMovie(long id) {
//        if(id < 0) {
//            return false;
//        }
//
//        return mDB.delete(MoviesContract.MoviesEntry.TABLE_NAME,
//                MoviesContract.MoviesEntry._ID + "="+id, null) > 0;
//    }
}
