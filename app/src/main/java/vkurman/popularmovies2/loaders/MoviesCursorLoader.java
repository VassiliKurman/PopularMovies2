package vkurman.popularmovies2.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import vkurman.popularmovies2.persistance.MoviesContract;

/**
 * MoviesCursorLoader CursorLoader from ContentProvider
 * Created by Vassili Kurman on 23/03/2018.
 * Version 1.0
 */

public class MoviesCursorLoader extends AsyncTaskLoader<Cursor> {
    // TODO load data from ContentProvider and return Cursor
    private Context context;
    private Cursor mMovieData;

    public MoviesCursorLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (mMovieData != null) {
            deliverResult(mMovieData);
        } else {
            forceLoad();
        }
    }

    @Override
    public Cursor loadInBackground() {
        try {
            return context.getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deliverResult(Cursor data) {
        mMovieData = data;
        super.deliverResult(data);
    }
}