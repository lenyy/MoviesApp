package com.example.pedro.popularmovies;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utility {




    public static String yearOfRelease(String date) {
        String [] result = date.split("-");

        return result[0];
    }


    public static String prettyRating(Double rating) {
        String result = rating.toString();

        result += "/10";

        return result;
    }

    public static String getPreferedSorting(Context context)
    {
        SharedPreferences sharePref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharePref.getString(context.getString(R.string.pref_movie_key),
                context.getString(R.string.pref_sort_popularity_label));
    }
}
