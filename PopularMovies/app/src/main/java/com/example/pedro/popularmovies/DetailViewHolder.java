package com.example.pedro.popularmovies;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by pedro on 22/12/2015.
 */
public class DetailViewHolder {

    public final ImageView posterImageView;
    public final TextView movieTitleView;
    public final TextView synopsisView;
    public final TextView dateView;
    public final TextView userRatingView;
    public final Button favoriteClick;


    public DetailViewHolder(View view) {
        posterImageView = (ImageView) view.findViewById(R.id.poster_image);
        movieTitleView = (TextView) view.findViewById(R.id.movie_title);
        synopsisView = (TextView) view.findViewById(R.id.synopsis_view);
        dateView = (TextView) view.findViewById(R.id.date_view);
        userRatingView = (TextView) view.findViewById(R.id.user_rating_view);
        favoriteClick = (Button) view.findViewById(R.id.favorite_button);
    }

}
