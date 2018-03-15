package vkurman.popularmovies2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import vkurman.popularmovies2.persistance.MoviesContract;
import vkurman.popularmovies2.persistance.MoviesDbHelper;

import static org.junit.Assert.*;

/**
 * Database test, which will execute on an Android device.
 *
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    /* Context used to perform operations on the database and create MoviesDbHelper */
    private final Context mContext = InstrumentationRegistry.getTargetContext();
    /* Class reference to help load the constructor on runtime */
    private final Class mDbHelperClass = MoviesDbHelper.class;

    /**
     * Because we annotate this method with the @Before annotation, this method will be called
     * before every single method with an @Test annotation. We want to start each test clean, so we
     * delete the database to do so.
     */
    @Before
    public void setUp() {
        deleteTheDatabase();
    }

    /**
     * This method tests that our database contains all of the tables that we think it should
     * contain.
     * @throws Exception in case the constructor hasn't been implemented yet
     */
    @Test
    public void create_database_test() throws Exception{


        /* Use reflection to try to run the correct constructor whenever implemented */
        SQLiteOpenHelper dbHelper =
                (SQLiteOpenHelper) mDbHelperClass.getConstructor(Context.class).newInstance(mContext);

        /* Use MoviesDbHelper to get access to a writable database */
        SQLiteDatabase database = dbHelper.getWritableDatabase();


        /* We think the database is open, let's verify that here */
        String databaseIsNotOpen = "The database should be open and isn't";
        assertEquals(databaseIsNotOpen,
                true,
                database.isOpen());

        /* This Cursor will contain the names of each table in our database */
        Cursor tableNameCursor = database.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='" +
                        MoviesContract.MoviesEntry.TABLE_NAME + "'",
                null);

        /*
         * If tableNameCursor.moveToFirst returns false from this query, it means the database
         * wasn't created properly. In actuality, it means that your database contains no tables.
         */
        String errorInCreatingDatabase =
                "Error: This means that the database has not been created correctly";
        assertTrue(errorInCreatingDatabase,
                tableNameCursor.moveToFirst());

        /* If this fails, it means that your database doesn't contain the expected table(s) */
        assertEquals("Error: Your database was created without the expected tables.",
                MoviesContract.MoviesEntry.TABLE_NAME, tableNameCursor.getString(0));

        /* Always close a cursor when you are done with it */
        tableNameCursor.close();
    }

    /**
     * This method tests inserting a single record into an empty table from a brand new database.
     * The purpose is to test that the database is working as expected
     * @throws Exception in case the constructor hasn't been implemented yet
     */
    @Test
    public void insert_single_record_test() throws Exception{

        /* Use reflection to try to run the correct constructor whenever implemented */
        SQLiteOpenHelper dbHelper =
                (SQLiteOpenHelper) mDbHelperClass.getConstructor(Context.class).newInstance(mContext);

        /* Use MoviesDbHelper to get access to a writable database */
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues testValues = new ContentValues();
        testValues.put(MoviesContract.MoviesEntry._ID, 122154);
        testValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER, "poster path");
        testValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE, "movie title");
        testValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, "2012-01-01");
        testValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, "7.1");
        testValues.put(MoviesContract.MoviesEntry.COLUMN_PLOT_SYNOPSIS, "plot synopsis some text");

        /* Insert ContentValues into database and get first row ID back */
        long firstRowId = database.insert(
                MoviesContract.MoviesEntry.TABLE_NAME,
                null,
                testValues);

        /* If the insert fails, database.insert returns -1 */
        assertNotEquals("Unable to insert into the database", -1, firstRowId);

        /*
         * Query the database and receive a Cursor. A Cursor is the primary way to interact with
         * a database in Android.
         */
        Cursor wCursor = database.query(
                /* Name of table on which to perform the query */
                MoviesContract.MoviesEntry.TABLE_NAME,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null,
                /* Values for "where" clause */
                null,
                /* Columns to group by */
                null,
                /* Columns to filter by row groups */
                null,
                /* Sort order to return in Cursor */
                null);

        /* Cursor.moveToFirst will return false if there are no records returned from your query */
        String emptyQueryError = "Error: No Records returned from movies query";
        assertTrue(emptyQueryError,
                wCursor.moveToFirst());

        /* Close cursor and database */
        wCursor.close();
        dbHelper.close();
    }

    /**
     * Tests that onUpgrade works by inserting 2 rows then calling onUpgrade and verifies that the
     * database has been successfully dropped and recreated by checking that the database is there
     * but empty
     * @throws Exception in case the constructor hasn't been implemented yet
     */
    @Test
    public void upgrade_database_test() throws Exception{

        /* Insert 2 rows before we upgrade to check that we dropped the database correctly */

        /* Use reflection to try to run the correct constructor whenever implemented */
        SQLiteOpenHelper dbHelper =
                (SQLiteOpenHelper) mDbHelperClass.getConstructor(Context.class).newInstance(mContext);

        /* Use MoviesDbHelper to get access to a writable database */
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues testValues = new ContentValues();
        testValues.put(MoviesContract.MoviesEntry._ID, 122154);
        testValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER, "poster path");
        testValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE, "movie title");
        testValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, "2012-01-01");
        testValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, "7.1");
        testValues.put(MoviesContract.MoviesEntry.COLUMN_PLOT_SYNOPSIS, "plot synopsis some text");

        ContentValues testValues2 = new ContentValues();
        testValues2.put(MoviesContract.MoviesEntry._ID, 122155);
        testValues2.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER, "poster path");
        testValues2.put(MoviesContract.MoviesEntry.COLUMN_TITLE, "movie title");
        testValues2.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, "2012-01-01");
        testValues2.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, "7.1");
        testValues2.put(MoviesContract.MoviesEntry.COLUMN_PLOT_SYNOPSIS, "plot synopsis some text");


        /* Insert ContentValues into database and get first row ID back */
        long firstRowId = database.insert(
                MoviesContract.MoviesEntry.TABLE_NAME,
                null,
                testValues);

        /* Insert ContentValues into database and get another row ID back */
        long secondRowId = database.insert(
                MoviesContract.MoviesEntry.TABLE_NAME,
                null,
                testValues2);

        dbHelper.onUpgrade(database, 0, 1);
        database = dbHelper.getReadableDatabase();

        /* This Cursor will contain the names of each table in our database */
        Cursor tableNameCursor = database.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='" +
                        MoviesContract.MoviesEntry.TABLE_NAME + "'",
                null);

        assertTrue(tableNameCursor.getCount() == 1);

        /*
         * Query the database and receive a Cursor. A Cursor is the primary way to interact with
         * a database in Android.
         */
        Cursor wCursor = database.query(
                /* Name of table on which to perform the query */
                MoviesContract.MoviesEntry.TABLE_NAME,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null,
                /* Values for "where" clause */
                null,
                /* Columns to group by */
                null,
                /* Columns to filter by row groups */
                null,
                /* Sort order to return in Cursor */
                null);

        /* Cursor.moveToFirst will return false if there are no records returned from your query */

        assertFalse("Database doesn't seem to have been dropped successfully when upgrading",
                wCursor.moveToFirst());

        tableNameCursor.close();
        database.close();
    }

    /**
     * Deletes the entire database.
     */
    void deleteTheDatabase(){
        try {
            /* Use reflection to get the database name from the db helper class */
            Field f = mDbHelperClass.getDeclaredField("DATABASE_NAME");
            f.setAccessible(true);
            mContext.deleteDatabase((String)f.get(null));
        }catch (NoSuchFieldException ex){
            fail("Make sure you have a member called DATABASE_NAME in the MoviesDbHelper");
        }catch (Exception ex){
            fail(ex.getMessage());
        }

    }
}
