package com.example.pedro.popularmovies;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by pedro on 10/11/2015.
 */
public class MovieAdaptor extends ArrayAdapter<Movie>{
    private final String LOG_TAG = MovieAdaptor.class.getSimpleName();

    public MovieAdaptor(Activity context, List<Movie> movies) {
        super(context,0,movies);
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        Movie movie = getItem(position);


        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_item,parent,false);
        }

        ImageView iconView = (ImageView) convertView.findViewById(R.id.movie_image);
        Uri imageUri = Uri.parse(getContext().getString(R.string.image_url)).buildUpon()
                .appendPath(getContext().getString(R.string.image_size))
                .appendEncodedPath(movie.getImagePath())
                .build();

        Log.v(LOG_TAG, "Uri " + imageUri.toString());

        Picasso.with(getContext()).load(imageUri).into(iconView);

        return convertView;
    }
}
