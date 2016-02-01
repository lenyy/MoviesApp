package com.example.pedro.popularmovies;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class MovieAdaptor extends CursorAdapter{

    private final String LOG_TAG = MovieAdaptor.class.getSimpleName();

    public MovieAdaptor(Context context, Cursor c, int flags) {
        super(context,c,flags);
    }

    @Override
    public View newView(Context context,Cursor cursor, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.movie_item,parent,false);
        return rootView;
    }


    @Override
    public void bindView (View view, Context context, Cursor cursor) {


            ImageView iconView = (ImageView) view.findViewById(R.id.movie_image);

            Uri imageUri = Uri.parse(context.getString(R.string.image_url)).buildUpon()
                    .appendPath(context.getString(R.string.image_size))
                    .appendEncodedPath(cursor.getString(MovieFragment.COL_MOVIE_IMAGE))
                    .build();

            Log.v(LOG_TAG, "Uri " + imageUri.toString());

            Log.v(LOG_TAG, "Image = " + cursor.getString(MovieFragment.COL_MOVIE_IMAGE));

            Picasso.with(context).load(imageUri).into(iconView);

    }
}
