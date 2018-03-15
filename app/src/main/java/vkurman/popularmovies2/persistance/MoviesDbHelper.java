package vkurman.popularmovies2.persistance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * MoviesDbHelper
 * Created by Vassili Kurman on 15/03/2018.
 * Version 1.0
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_DATABASE = "CREATE TABLE " +
                MoviesContract.MoviesEntry.TABLE_NAME + " (" +
                MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY NOT NULL," +
                MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL," +
                MoviesContract.MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL," +
                MoviesContract.MoviesEntry.COLUMN_PLOT_SYNOPSIS + " TEXT NOT NULL" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // delete table and create new one in this app
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
