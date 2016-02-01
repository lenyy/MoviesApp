package com.example.pedro.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.pedro.popularmovies.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = MovieFragment.class.getSimpleName();

    private MovieAdaptor movieAdapter;
    private GridView view;
    private Uri mUri;
    private String movieSort;

    private static final int CURSOR_LOADER_ID = 0;

    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_NAME = 1;
    static final int COL_MOVIE_RELEASE_DATE = 2;
    static final int COL_MOVIEID = 3;
    static final int COL_MOVIE_DESCRIPTION = 4;
    static final int COL_MOVIE_RATING = 5;
    static final int COL_MOVIE_IMAGE = 6;
    static final int COL_MOVIE_POPULARITY = 7;



    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_NAME,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_DESCRIPTION,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_IMAGE,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
    };
    private static final String[] MOVIE_FAVOURITE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." +
                    MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_NAME,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.TABLE_NAME + "." +
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_DESCRIPTION,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_IMAGE,
            MovieContract.MovieEntry.COLUMN_POPULARITY
    };


    public MovieFragment() {
    }

    public interface Callback {

        void onItemSelected(Uri uri);
    }


    @Override
    public void onActivityCreated(Bundle savedInstance) {
        getLoaderManager().initLoader(CURSOR_LOADER_ID,null,this);
        super.onActivityCreated(savedInstance);
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.moviefragment_main, container, false);

        view = (GridView) rootView.findViewById(R.id.moviegridview);

        movieAdapter = new MovieAdaptor(getContext(),null ,0);

        view.setAdapter(movieAdapter);

        view.setOnItemClickListener((new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(MovieContract.MovieEntry.buildMovieUriWithId(cursor.getInt(COL_MOVIEID)));
                }
            }
        }));

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }



    public CursorLoader onCreateLoader(int id, Bundle args) {
        Uri movieUri = MovieContract.MovieEntry.CONTENT_URI;
        String sortOrder;
        String order = Utility.getPreferedSorting(getContext());
        String [] columns;
        if(order.equals(getContext().getString(R.string.pref_sort_popularity_label))) {
             sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
             columns = MOVIE_COLUMNS;
        }
        else
        {
            if(order.equals(getContext().getString(R.string.pref_sort_rating_label))) {
                sortOrder = MovieContract.MovieEntry.COLUMN_RATING + " DESC";
                columns = MOVIE_COLUMNS;
            }
            else
            {
                movieUri = MovieContract.FavouriteEntry.CONTENT_URI;
                sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
                columns = MOVIE_FAVOURITE_COLUMNS;
            }
        }

        Log.v(LOG_TAG, "New loader with uri : " + movieUri);
        return new CursorLoader(getActivity(),
                movieUri,
                columns,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        movieAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        movieAdapter.swapCursor(null);
    }



    public void updateMovies() {
        Log.v(LOG_TAG, "UPDATING MOVIESSSSSSS");
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }
}

