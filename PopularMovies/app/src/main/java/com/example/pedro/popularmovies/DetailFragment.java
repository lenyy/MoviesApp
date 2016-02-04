package com.example.pedro.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pedro.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;


public class DetailFragment extends android.support.v4.app.Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    //Loader id
    private int DETAIL_LOADER = 0;

    static final String DETAIL_URI = "URI";
    //Uri that contains the SQL query to be made
    private Uri mUri;

    //These constants corresponds to the SQL query response table names, used to get information on a certain column
    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_RATING = 1;
    static final int COL_MOVIE_DESCRIPTION = 2;
    static final int COL_MOVIE_POPULARITY = 3;
    static final int COL_MOVIE_NAME = 4;
    static final int COL_MOVIE_RELEASE_DATE = 5;
    static final int COL_MOVIE_IMAGE = 6;
    static final int COL_MOVIEID = 7;


    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_DESCRIPTION,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_MOVIE_NAME,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_IMAGE,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID
    };


    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if(arguments != null)
            mUri = arguments.getParcelable(DETAIL_URI);

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        DetailViewHolder viewHolder = new DetailViewHolder(rootView);
        rootView.setTag(viewHolder);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(mUri != null) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        Log.v(LOG_TAG, "in onLoadFinished");
        //Get the view holder associated with current view
        final DetailViewHolder holder = (DetailViewHolder) getView().getTag();
        if(data != null && data.moveToFirst() ){
            //Get and Set Movie name
            String name = data.getString(COL_MOVIE_NAME);
            holder.movieTitleView.setText(name);

            String releaseDate = data.getString(COL_MOVIE_RELEASE_DATE);
            releaseDate = Utility.yearOfRelease(releaseDate);
            holder.dateView.setText(releaseDate);

            Double rating = data.getDouble(COL_MOVIE_RATING);
            holder.userRatingView.setText(Utility.prettyRating(rating));

            String description = data.getString(COL_MOVIE_DESCRIPTION);
            holder.synopsisView.setText(description);

            holder.favoriteClick.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View view) {
                    favoriteClick(data.getInt(COL_MOVIEID),holder.favoriteClick);
                }
            });

            Uri imageUri = Uri.parse(getString(R.string.image_url)).buildUpon()
                    .appendPath(getString(R.string.image_size))
                    .appendEncodedPath(data.getString(COL_MOVIE_IMAGE))
                    .build();

            Picasso.with(getContext()).load(imageUri).into(holder.posterImageView);

            Log.v(LOG_TAG,"URI : " + imageUri);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){ }

    public void favoriteClick(Integer movieId,Button button) {
        Uri uri = MovieContract.FavouriteEntry.buildFavoriteWithId(movieId);
        Cursor cursor = getContext().getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );
        if(cursor.moveToFirst()) {
            getContext().getContentResolver().delete(uri,null,null);
            button.setText("Mark as Favorite");
        }
        else
        {
            uri = MovieContract.FavouriteEntry.CONTENT_URI;
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.FavouriteEntry.COLUMN_MOVIE_KEY, movieId);
            getContext().getContentResolver().insert(uri,contentValues);
            button.setText("Favorite!!");
        }
    }
}

