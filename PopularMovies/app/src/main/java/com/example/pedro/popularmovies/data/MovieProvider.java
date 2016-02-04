package com.example.pedro.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;



public class MovieProvider extends ContentProvider {

    private static final String LOG_TAG = MovieProvider.class.getSimpleName();

    private static final UriMatcher matcher = buildUriMatcher();
    private PopularMoviesDbHelper pmDbHelper;

    static final int EMPTY_CHECK = 1;
    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int FAVORITE = 200;
    static final int FAVORITE_WITH_ID = 201;
    static final int TRAILER = 300;
    static final int TRAILER_WITH_ID = 301;
    static final int REVIEW = 400;
    static final int REVIEW_WITH_ID = 401;


    private static final SQLiteQueryBuilder sFavoriteQueryBuilder;
    private static final SQLiteQueryBuilder sTrailerQueryBuilder;
    private static final SQLiteQueryBuilder sReviewQueryBuilder;

    static {
        sFavoriteQueryBuilder = new SQLiteQueryBuilder();
        sTrailerQueryBuilder = new SQLiteQueryBuilder();
        sReviewQueryBuilder = new SQLiteQueryBuilder();

        // this is a inner join which looks like
        // movie INNER JOINT favourites ON movie.movie_id = favourites.movie_id
        sFavoriteQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.FavouriteEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.FavouriteEntry.TABLE_NAME +
                        "." + MovieContract.FavouriteEntry.COLUMN_MOVIE_KEY
        );

        // this is a inner join which looks like
        // movie INNER JOINT trailers ON movie.movie_id = trailers.movie_id
        sTrailerQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.TrailerEntry.TABLE_NAME +
                        " ON " +  MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.TrailerEntry.TABLE_NAME +
                        "." + MovieContract.TrailerEntry.COLUMN_MOVIE_ID
        );

        // this is a inner join which looks like
        // movie INNER JOINT reviews ON movie.movie_id = reviews.movie_id
        sReviewQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.ReviewEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.ReviewEntry.TABLE_NAME +
                        "." + MovieContract.ReviewEntry.COLUMN_MOVIE_ID
        );

    }

    //favourites.movie_id = ?
    private static final String sFavouriteIdSelection =
            MovieContract.FavouriteEntry.TABLE_NAME +
                    "." + MovieContract.FavouriteEntry.COLUMN_MOVIE_KEY + " = ? ";
    //trailer.movie_id = ?
    private static final String sTrailerIdSelection =
            MovieContract.TrailerEntry.TABLE_NAME +
                    "." + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? ";
    //review.movie_id = ?
    private static final String sReviewIdSelection =
            MovieContract.ReviewEntry.TABLE_NAME +
                    "." + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ";

    //Get favorite by movie id
    private Cursor getFavouriteByMovieId(Uri uri, String[] projection, String sortOrder) {
        int movieId = MovieContract.FavouriteEntry.getIdFromUri(uri);

        return sFavoriteQueryBuilder.query(pmDbHelper.getReadableDatabase(),
                projection,
                sFavouriteIdSelection,
                new String[] {Integer.toString(movieId)},
                null,
                null,
                sortOrder
                );
    }

    private Cursor getTrailerByMovieId(Uri uri, String[] projection, String sortOrder) {
        int movieId = MovieContract.TrailerEntry.getMovieIdFromUri(uri);

        return sTrailerQueryBuilder.query(pmDbHelper.getReadableDatabase(),
                projection,
                sTrailerIdSelection,
                new String[] {Integer.toString(movieId)},
                null,
                null,
                sortOrder
                );
    }

    private Cursor getReviewByMovieId(Uri uri, String[] projection, String sortOrder) {
        int movieId = MovieContract.ReviewEntry.getMovieIdFromUri(uri);

        return sReviewQueryBuilder.query(pmDbHelper.getReadableDatabase(),
                projection,
                sTrailerIdSelection,
                new String[] {Integer.toString(movieId)},
                null,
                null,
                sortOrder
                );
    }

    private Cursor getFavourites(String[] projection,String sortOrder,String selection, String [] selectionArgs)
    {
        return sFavoriteQueryBuilder.query(pmDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }


    @Override
    public boolean onCreate() {
        pmDbHelper = new PopularMoviesDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        final int match = matcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_DIR_TYPE;
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.ITEM_DIR_TYPE;
            case FAVORITE:
                return MovieContract.FavouriteEntry.CONTENT_DIR_TYPE;
            case FAVORITE_WITH_ID:
                return MovieContract.FavouriteEntry.ITEM_DIR_TYPE;
            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_DIR_TYPE;
            case TRAILER_WITH_ID:
                return MovieContract.TrailerEntry.ITEM_DIR_TYPE;
            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_DIR_TYPE;
            case REVIEW_WITH_ID:
                return MovieContract.ReviewEntry.ITEM_DIR_TYPE;
            default: {
                throw new UnsupportedOperationException("Unknow Uri: " + uri);
            }
        }
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor queryCursor;
        switch (matcher.match(uri)) {
            case EMPTY_CHECK:{
                queryCursor = pmDbHelper.getReadableDatabase().rawQuery("SELECT * FROM " +
                        MovieContract.MovieEntry.TABLE_NAME, null);
                break;
            }
            case MOVIE: {
                queryCursor = pmDbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            }
            case MOVIE_WITH_ID: {
                queryCursor = pmDbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + "= ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case FAVORITE: {
                queryCursor = getFavourites(projection,sortOrder,selection,selectionArgs);
                break;
            }
            case FAVORITE_WITH_ID: {
                queryCursor = getFavouriteByMovieId(uri,projection,sortOrder);
                break;
            }
            case TRAILER: {
                queryCursor = pmDbHelper.getReadableDatabase().query(
                  MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TRAILER_WITH_ID : {
                queryCursor = getTrailerByMovieId(uri,projection,sortOrder);
                break;
            }
            case REVIEW : {
                queryCursor = pmDbHelper.getReadableDatabase().query(
                  MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REVIEW_WITH_ID : {
                queryCursor = getReviewByMovieId(uri,projection,sortOrder);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknow uri: " + uri);
            }
        }

        return queryCursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = pmDbHelper.getWritableDatabase();
        Uri returnUri;

        switch (matcher.match(uri)) {
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME,null,values);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            case FAVORITE: {
                long _id = db.insert(MovieContract.FavouriteEntry.TABLE_NAME,null,values);
                if(_id > 0)
                    returnUri = MovieContract.FavouriteEntry.buildFavoriteUri(_id);
                else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            case TRAILER : {
                long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME,null,values);
                if(_id > 0)
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                break;
            }
            case REVIEW : {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME,null,values);
                if(_id > 0)
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknow uri: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = pmDbHelper.getWritableDatabase();
        int numDeleted;
        switch (matcher.match(uri)) {
            case MOVIE: {
                numDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,selection,selectionArgs);

                //reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        MovieContract.MovieEntry.TABLE_NAME + "'");
                break;
            }
            case MOVIE_WITH_ID: {
                numDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                        MovieContract.MovieEntry._ID + " = ? ",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                //reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        MovieContract.MovieEntry.TABLE_NAME + "'");
                break;
            }
            case FAVORITE_WITH_ID: {
                numDeleted = db.delete(MovieContract.FavouriteEntry.TABLE_NAME,
                       MovieContract.FavouriteEntry.COLUMN_MOVIE_KEY + " = ? ",
                        new String[] {Integer.toString(MovieContract.FavouriteEntry.getIdFromUri(uri))});
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknow uri: " + uri);
            }
        }
        return numDeleted;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = pmDbHelper.getWritableDatabase();
        int returnCount;
        switch (matcher.match(uri)) {
            case MOVIE: {
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = -1;
                        try {
                            _id = db.insertOrThrow(MovieContract.MovieEntry.TABLE_NAME,null,value);
                        }catch (SQLiteConstraintException e) {
                            Log.w(LOG_TAG,"Attempting to insert " +
                                    value.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_NAME)
                            + " but value is already in database.");
                        }
                        if (_id != -1)
                            returnCount++;
                    }
                    if(returnCount > 0)
                        db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                if (returnCount > 0)
                    getContext().getContentResolver().notifyChange(uri,null);
                break;
            }
            case TRAILER : {
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = -1;
                        try {
                            _id = db.insertOrThrow(MovieContract.TrailerEntry.TABLE_NAME,null,value);
                        }catch (SQLiteConstraintException e) {
                            Log.w(LOG_TAG,"Attempting to insert " +
                                    value.getAsString(MovieContract.TrailerEntry.COLUMN_MOVIE_ID)
                                    + " but value is already in database.");
                        }
                        if (_id != -1)
                            returnCount++;
                    }
                    if(returnCount > 0)
                        db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                if (returnCount > 0)
                    getContext().getContentResolver().notifyChange(uri,null);
                break;
            }
            case REVIEW : {
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = -1;
                        try {
                            _id = db.insertOrThrow(MovieContract.ReviewEntry.TABLE_NAME,null,value);
                        }catch (SQLiteConstraintException e) {
                            Log.w(LOG_TAG,"Attempting to insert " +
                                    value.getAsString(MovieContract.ReviewEntry.COLUMN_MOVIE_ID)
                                    + " but value is already in database.");
                        }
                        if (_id != -1)
                            returnCount++;
                    }
                    if(returnCount > 0)
                        db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                if (returnCount > 0)
                    getContext().getContentResolver().notifyChange(uri,null);
                break;
            }
            default: {
                return super.bulkInsert(uri,values);
            }

        }

        return returnCount;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,String[] selectionArgs) {
        final SQLiteDatabase db = pmDbHelper.getWritableDatabase();
        int returnRows;
        switch (matcher.match(uri)) {
            case MOVIE: {
                returnRows = db.update(MovieContract.MovieEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }
            case MOVIE_WITH_ID: {
                returnRows = db.update(MovieContract.MovieEntry.TABLE_NAME,
                        contentValues,
                        MovieContract.MovieEntry._ID + " = ? ",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknow uri: " + uri);
            }
        }

        return returnRows;
    }


    static UriMatcher buildUriMatcher() {
        //The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        // 2) Use the addURI function to match each of the types.  Use the constants from
        // MovieContract to help define the types to the UriMatcher.
        matcher.addURI(authority,"raw query",EMPTY_CHECK);
        matcher.addURI(authority,MovieContract.PATH_MOVIE,MOVIE);
        matcher.addURI(authority,MovieContract.PATH_MOVIE + "/#",MOVIE_WITH_ID);
        matcher.addURI(authority,MovieContract.PATH_FAVORITES,FAVORITE);
        matcher.addURI(authority,MovieContract.PATH_FAVORITES + "/#",FAVORITE_WITH_ID);
        matcher.addURI(authority,MovieContract.PATH_TRAILERS,TRAILER);
        matcher.addURI(authority,MovieContract.PATH_TRAILERS + "/#", TRAILER_WITH_ID);
        matcher.addURI(authority,MovieContract.PATH_REVIEWS,REVIEW);
        matcher.addURI(authority,MovieContract.PATH_REVIEWS + "/#", REVIEW_WITH_ID);

        return matcher;
    }

}
