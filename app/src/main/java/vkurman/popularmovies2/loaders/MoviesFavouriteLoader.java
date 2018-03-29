package vkurman.popularmovies2.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

import vkurman.popularmovies2.model.Movie;
import vkurman.popularmovies2.persistance.MoviesContract;

/**
 * MoviesFavouriteLoader
 * Created by Vassili Kurman on 29/03/2018.
 * Version 1.0
 */

public class MoviesFavouriteLoader extends AsyncTaskLoader<List<Movie>> {

    /**
     * Popular MoviesLoader id
     */
    public static final int ID = 103;

    private Context context;

    public MoviesFavouriteLoader(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public List<Movie> loadInBackground() {
        List<Movie> list = new ArrayList<>();
        try(Cursor cursor = context.getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null)
        ) {
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
}
