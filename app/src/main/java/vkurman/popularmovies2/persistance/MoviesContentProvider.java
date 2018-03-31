package vkurman.popularmovies2.persistance;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * MoviesContentProvider
 * Created by Vassili Kurman on 15/03/2018.
 * Version 1.0
 */

public class MoviesContentProvider extends ContentProvider {
    // constant for directory of movies
    public static final int MOVIES = 100;
    // constant for single movie
    public static final int MOVIE_WITH_ID = 101;
    // constant for movie id's column
    public static final int MOVIE_IDS = 102;
    // static UriMatcher
    public static final UriMatcher sUriMatcher = buildUriMatcher();

    private MoviesDbHelper mMoviesDbHelper;

    /**
     * Helper method to build UriMatcher
     *
     * @return UriMatcher
     */
    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // directory UriMatcher
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES, MOVIES);
        // single item UriMatcher
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES + "/#", MOVIE_WITH_ID);
        // items ids only UriMatcher
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES + "/#", MOVIE_IDS);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMoviesDbHelper = new MoviesDbHelper(context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mMoviesDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor returnCursor;
        switch (match){
            case MOVIES:
                returnCursor = db.query(MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};

                returnCursor = db.query(MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIE_IDS:
                String column = "movieId";
                String[] mProjection = new String[]{column};

                returnCursor = db.query(MoviesContract.MoviesEntry.TABLE_NAME,
                        mProjection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }
        // Notification URI on the Cursor
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                // directory
                return "vnd.android.cursor.dir" + "/" + MoviesContract.AUTHORITY + "/" + MoviesContract.PATH_MOVIES;
            case MOVIE_WITH_ID:
                // single item type
                return "vnd.android.cursor.item" + "/" + MoviesContract.AUTHORITY + "/" + MoviesContract.PATH_MOVIES;
            case MOVIE_IDS:
                // single item type
                return "vnd.android.cursor.item" + "/" + MoviesContract.AUTHORITY + "/" + MoviesContract.PATH_MOVIES;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case MOVIES:
                long id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, contentValues);
                if(id > 0) {
                    returnUri = ContentUris.withAppendedId(MoviesContract.MoviesEntry.CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert row into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }
        // notifying content resolver about change
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String whereClause, @Nullable String[] whereArgs) {
        final SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int deletedItems;
        switch (match){
            case MOVIES:
                deletedItems = db.delete(MoviesContract.MoviesEntry.TABLE_NAME,
                        whereClause,
                        whereArgs);
                break;
            case MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mWhereClause = MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + "=?";
                String[] mWhereArgs = new String[]{id};
                deletedItems = db.delete(MoviesContract.MoviesEntry.TABLE_NAME,
                        mWhereClause,
                        mWhereArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }
        // Notification about change
        getContext().getContentResolver().notifyChange(uri, null);

        return deletedItems;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        // not updating database in this app
        return 0;
    }
}