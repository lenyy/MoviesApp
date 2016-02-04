package com.example.pedro.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.pedro.popularmovies.app";

     public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movies";
    public static final String PATH_FAVORITES = "favorites";
    public static final String PATH_TRAILERS = "trailers";
    public static final String PATH_REVIEWS = "reviews";
    public static final String EMPTY_QUERY = "raw query";


    public static final class MovieEntry implements BaseColumns {
        //Table name
        public static final String TABLE_NAME = "movies";

        //Table Columns
        public static final String _ID = "_id";
        public static final String COLUMN_MOVIE_NAME = "name";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_IMAGE = "image";
        //The popularity of each movie
        public static final String COLUMN_POPULARITY = "popularity";
        //The id of the movie
        public static final String COLUMN_MOVIE_ID = "movie_id";

        //Create Content Uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        //Create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +TABLE_NAME;
        //Create cursor of base type item for single entry
        public static final String ITEM_DIR_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        //For building Uri's on insertion
        public static Uri buildMovieUri(long id) {
            return  ContentUris.withAppendedId(CONTENT_URI,id);
        }

        // This one is different from the above since this is used to query data with a movie ID
        public static Uri buildMovieUriWithId(int id) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
        }

        public static Uri buildEmptyQuery(){
            return BASE_CONTENT_URI.buildUpon().appendPath(EMPTY_QUERY).build();
        }

    }

    public static final class FavouriteEntry implements BaseColumns {
        //Table name
        public static final String TABLE_NAME = "favorites";

        //Column with foreign key to movie table
        public static final String COLUMN_MOVIE_KEY = "movie_id";

        //Create Content Uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();
        //Create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        //Create cursor of base type item for single entry
        public static final String ITEM_DIR_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static Uri buildFavoriteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static int getIdFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }

        public static Uri buildFavoriteWithId(int id) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
        }
    }

    public static final class TrailerEntry implements BaseColumns {

        public static final String TABLE_NAME = "trailers";

        public static final String COLUMN_MOVIE_ID = "id";

        public static final String COLUMN_URL_KEY = "url_key";

        public static final String COLUMN_TRAILER_NAME = "trailer_name";


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final String ITEM_DIR_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static Uri buildTrailerWithId(int id) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
        }

        public static int getMovieIdFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
    }


    public static final class ReviewEntry implements BaseColumns {

        public static final String TABLE_NAME = "reviews";

        public static final String COLUMN_MOVIE_ID = "id";

        public static final String COLUMN_AUTOR = "author";

        public static final String COLUMN_REVIEW = "review";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final String ITEM_DIR_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static Uri buildReviewWithId(int id) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
        }

        public static int getMovieIdFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
    }
}
