package com.example.pedro.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class PopularMoviesDbHelper extends SQLiteOpenHelper {

    private final String LOG_TAG = PopularMoviesDbHelper.class.getSimpleName();

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "movies.db";

    public PopularMoviesDbHelper(Context context){
        super(context , DATABASE_NAME , null ,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +

                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.MovieEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL," +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                MovieContract.MovieEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL," +
                MovieContract.MovieEntry.COLUMN_RATING + " REAL NOT NULL," +
                MovieContract.MovieEntry.COLUMN_IMAGE + " TEXT NOT NULL," +
                MovieContract.MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL," +
                " UNIQUE ( " + MovieContract.MovieEntry.COLUMN_MOVIE_ID +" ) ON CONFLICT REPLACE" +
                ");";


        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + MovieContract.FavouriteEntry.TABLE_NAME + " ("+

                MovieContract.FavouriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.FavouriteEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL," +
                " FOREIGN KEY (" + MovieContract.FavouriteEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME +  " (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") ON DELETE CASCADE);";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + MovieContract.TrailerEntry.TABLE_NAME + " (" +
                MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.TrailerEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL," +
                MovieContract.TrailerEntry.COLUMN_URL_KEY + " TEXT NOT NULL," +
                MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                " FOREIGN KEY (" + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") ON DELETE CASCADE);";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + MovieContract.ReviewEntry.TABLE_NAME + " (" +
                MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.ReviewEntry.COLUMN_AUTOR + " TEXT NOT NULL," +
                MovieContract.ReviewEntry.COLUMN_REVIEW + " TEXT NOT NULL," +
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                " FOREIGN KEY (" + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") ON DELETE CASCADE);";

        Log.v(LOG_TAG, "movie table sql = " + SQL_CREATE_MOVIE_TABLE);
        Log.v(LOG_TAG, "favourites table sql = " + SQL_CREATE_FAVORITE_TABLE);
        Log.v(LOG_TAG, "trailer table sql = " + SQL_CREATE_TRAILER_TABLE);
        Log.v(LOG_TAG, "review table sql = " + SQL_CREATE_REVIEW_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase,int oldVersion,int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavouriteEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
