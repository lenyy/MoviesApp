package com.example.pedro.popularmovies.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.pedro.popularmovies.BuildConfig;
import com.example.pedro.popularmovies.R;
import com.example.pedro.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;


public class MovieService extends IntentService {

    private final String LOG_TAG = MovieService.class.getSimpleName();

    private final String MOVIEDATABASE_BASE_URL = "http://api.themoviedb.org/3";
    private final String DISCOVER = "discover";
    private final String MOVIE = "movie";
    private final String SORT_PARAM = "?sort_by";
    private final String APPID = "api_key";
    private final String TRAILER_URL = "videos";
    private final String REVIEW_URL = "reviews";

    //The order from which we get the info from the themoviedb.org
    private final int movieFetchOrder = 0;
    private final int trailerFetchOrder = 1;
    private final int reviewFetchOrder = 2;


    //its the sum of all fetch we need to make
    private final int numberOfFetchs = 3;


    private ArrayList<Integer> movieIds = new ArrayList<Integer>();

    private Vector<ContentValues> trailerContentValues = new Vector<>();

    private Vector<ContentValues> reviewContentValues = new Vector<>();

    public MovieService(){
        super("Movie Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int i;
        String movieJson;
        ArrayList<Uri> uris = new ArrayList<Uri>();
        ArrayList<Uri> reviewUri = new ArrayList<Uri>();
        ArrayList<Uri> trailerUri = new ArrayList<Uri>();
        ArrayList<StringBuffer> jsonStrings;
        for (i = 0; i < numberOfFetchs; i++) {
            Uri builtUri;
            switch (i) {
                case movieFetchOrder:
                    //Get the movies
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String sort = prefs.getString(this.getString(R.string.pref_movie_key), this.getString(R.string.pref_sort_popularity_label));
                    if (sort.equals("rating")) {
                        sort = "vote_average.desc";
                    } else {
                        sort = "popularity.desc";
                    }


                    builtUri = Uri.parse(MOVIEDATABASE_BASE_URL).buildUpon()
                            .appendEncodedPath(DISCOVER)
                            .appendEncodedPath(MOVIE)
                            .appendQueryParameter(SORT_PARAM, sort)
                            .appendQueryParameter(APPID, BuildConfig.OPEN_MOVIE_DATABASE_KEY)
                            .build();

                    uris.add(builtUri);
                    break;
                case trailerFetchOrder:
                    for (int j = 0; j < movieIds.size(); j++) {
                        String uri = MOVIEDATABASE_BASE_URL + "/" +
                                MOVIE + "/" +
                                Integer.toString(movieIds.get(j)) + "/" +
                                TRAILER_URL + "&" +
                                APPID + "=" +
                                BuildConfig.OPEN_MOVIE_DATABASE_KEY;
                        builtUri = Uri.parse(uri);

                        trailerUri.add(builtUri);

                    }
                    break;
                case reviewFetchOrder:
                    for(int j = 0; j < movieIds.size(); j++){
                        String uri = MOVIEDATABASE_BASE_URL + "/" +
                                MOVIE + "/" +
                                Integer.toString(movieIds.get(j)) + "/" +
                                REVIEW_URL + "&" +
                                APPID + "=" +
                                BuildConfig.OPEN_MOVIE_DATABASE_KEY;
                        builtUri = Uri.parse(uri);
                        reviewUri.add(builtUri);
                    }

            }

            if (!uris.isEmpty())
                jsonStrings = queryTheMovieDb(uris);
            else
            {
                if(! trailerUri.isEmpty()) {
                    jsonStrings = queryTheMovieDb(trailerUri);
                }
                else
                    jsonStrings = queryTheMovieDb(reviewUri);
            }

            try {
                switch (i) {
                    case movieFetchOrder:
                        getMoviesFromJson(jsonStrings.get(0).toString());
                        uris.clear();
                        break;
                    case trailerFetchOrder :
                        for (int j = 0; j < movieIds.size(); j++) {
                            if(jsonStrings != null) {
                                getTrailerFromJson(jsonStrings.get(j).toString(), movieIds.get(j));
                            }
                        }
                        trailerUri.clear();
                        break;
                    case reviewFetchOrder:
                        for (int j = 0; j < movieIds.size(); j++) {
                            if(jsonStrings != null) {
                                getReviewFromJson(jsonStrings.get(j).toString(), movieIds.get(j));

                            }
                        }
                        reviewUri.clear();
                        break;
                }
            }catch (JSONException e){
                Log.e(LOG_TAG, "ERROR : " + e );
            }
        }
    }


    public void getMoviesFromJson(String moviesJsonStr)
            throws JSONException {
        //These are the names of json objects/arrays that we need to extract
        final String OWM_RESULT = "results";
        final String OWM_ID = "id";
        final String OWN_PATH = "poster_path";
        final String OWM_TITLE = "original_title";
        final String OWM_OVERVIEW = "overview";
        final String OWM_RATING = "vote_average";
        final String OWM_RELEASE_DATE = "release_date";
        final String OWM_POPULARITY = "popularity";

        JSONObject movieJson = new JSONObject(moviesJsonStr);
        JSONArray resultArray = movieJson.getJSONArray(OWM_RESULT);

        Vector<ContentValues> cVVector = new Vector<ContentValues>(resultArray.length());

        for (int i = 0; i < resultArray.length(); i++) {

            //Get the json object representing a film
            JSONObject film = resultArray.getJSONObject(i);

            String path = film.getString(OWN_PATH);

            Double rating = film.getDouble(OWM_RATING);

            String releaseDate = film.getString(OWM_RELEASE_DATE);

            String title = film.getString(OWM_TITLE);

            String id = film.getString(OWM_ID);

            movieIds.add(Integer.parseInt(id));

            String overview = film.getString(OWM_OVERVIEW);

            Double popularity = film.getDouble(OWM_POPULARITY);

            ContentValues contentValues = new ContentValues();

            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_NAME, title);
            contentValues.put(MovieContract.MovieEntry.COLUMN_DESCRIPTION, overview);
            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, rating);
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
            contentValues.put(MovieContract.MovieEntry.COLUMN_IMAGE, path);
            contentValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);

            cVVector.add(contentValues);
        }

        int inserted = 0;

        if (cVVector.size() > 0) {
            ContentValues[] cVArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cVArray);
            inserted = this.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cVArray);
        }

        Log.v(LOG_TAG, "Service Movie Completed. " + inserted + " inserted.");
    }

    public void getTrailerFromJson(String jsonTrailerString,int movieId)
                throws JSONException{
        //These are the names of json objects/arrays that we need to extract
        final String OWM_RESULT = "results";
        final String OWN_KEY = "key";
        final String OWN_NAME = "name";

        JSONObject movieJson = new JSONObject(jsonTrailerString);
        JSONArray resultArray = movieJson.getJSONArray(OWM_RESULT);

        for (int i = 0; i < resultArray.length(); i++)
        {
            JSONObject trailer = resultArray.getJSONObject(i);

            String url = trailer.getString(OWN_KEY);

            String name = trailer.getString(OWN_NAME);

            ContentValues c = new ContentValues();

            c.put(MovieContract.TrailerEntry.COLUMN_URL_KEY,url);
            c.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME,name);
            c.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID,movieId);

            trailerContentValues.add(c);
        }

        Log.v(LOG_TAG, "TRAILER Json response : " + jsonTrailerString);
    }


    public void getReviewFromJson(String jsonReviewString,int movieId)
            throws JSONException{

        //These are the names of json objects/arrays that we need to extract
        final String OWM_RESULT = "results";
        final String OWN_AUTHOR = "author";
        final String OWN_CONTENT = "content";

        JSONObject movieJson = new JSONObject(jsonReviewString);
        JSONArray resultArray = movieJson.getJSONArray(OWM_RESULT);

        for (int i = 0; i < resultArray.length(); i++)
        {
            JSONObject trailer = resultArray.getJSONObject(i);

            String author = trailer.getString(OWN_AUTHOR);

            String content = trailer.getString(OWN_CONTENT);

            ContentValues c = new ContentValues();

            c.put(MovieContract.ReviewEntry.COLUMN_AUTOR,author);
            c.put(MovieContract.ReviewEntry.COLUMN_REVIEW,content);
            c.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID,movieId);

            reviewContentValues.add(c);
        }

        Log.v(LOG_TAG, "REVIEW Json response : " + jsonReviewString);
    }

    public ArrayList<StringBuffer> queryTheMovieDb(ArrayList<Uri> uris) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        ArrayList<StringBuffer> buffer = new ArrayList<StringBuffer>();

        try {

            for(Uri uri : uris) {
                URL url = new URL(uri.toString());

                Log.v(LOG_TAG, "Built URI = " + uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer bufferLine = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line = reader.readLine()) != null) {
                    bufferLine.append(line + "\n");
                }

                if (bufferLine.length() == 0) {
                    return null;
                }
                buffer.add(bufferLine);

            }
            return buffer;
        } catch (IOException e) {
            Log.v(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            if (reader != null) {
                try {
                    reader.close();

                } catch (final IOException e) {
                    Log.v(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return null;
    }
}
