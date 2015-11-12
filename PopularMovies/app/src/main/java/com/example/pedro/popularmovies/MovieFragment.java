package com.example.pedro.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {

    private MovieAdaptor movieAdapter;

    private GridView view;

    private ArrayList<Movie> movieList;

    private String movieSort;


    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        if(savedInstance == null || !savedInstance.containsKey("movies")) {
            movieList = new ArrayList<Movie>();
        }
        else
        {
            movieList = savedInstance.getParcelableArrayList("movies");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.moviefragment_main, container, false);

        view = (GridView) rootView.findViewById(R.id.moviegridview);

        movieAdapter = new MovieAdaptor(getActivity(),movieList);

        view.setAdapter(movieAdapter);

        view.setOnItemClickListener((new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("Movie", movieAdapter.getItem(position));
                startActivity(intent);
            }
        }));

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies",movieList);
        super.onSaveInstanceState(outState);
    }



    public class FetchMovieTask extends AsyncTask <Void ,Void ,List<Movie>>{

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();


        public List<Movie> getMoviesFromJson(String moviesJsonStr)
                throws JSONException{
            //These are the names of json objets/arrays that we need to extract
            final String OWM_RESULT = "results";
            final String OWM_ID = "id";
            final String OWN_PATH = "poster_path";
            final String OWM_TITLE = "original_title";
            final String OWM_OVERVIEW = "overview";
            final String OWM_RATING = "vote_average";
            final String OWM_RELEASE_DATE = "release_date";

            JSONObject movieJson = new JSONObject(moviesJsonStr);
            JSONArray resultArray = movieJson.getJSONArray(OWM_RESULT);

            List<Movie> result = new ArrayList<Movie>();
            for (int i = 0; i< resultArray.length(); i++) {

                //Get the json object representing a film
                JSONObject film = resultArray.getJSONObject(i);



                Movie m = new Movie(film.getString(OWM_ID)
                        ,film.getString(OWN_PATH)
                        ,film.getString(OWM_TITLE)
                        ,film.getString(OWM_OVERVIEW)
                        ,film.getLong(OWM_RATING)
                        ,film.getString(OWM_RELEASE_DATE) );

                result.add(m);


            }

            return result;

        }

        @Override
        protected List<Movie> doInBackground(Void... params) {

            List<Movie> result = null;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJson = null;

            try {

                final String MOVIEDATABASE_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String APPID = "api_key";

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String sort = prefs.getString(getString(R.string.pref_movie_key), getString(R.string.pref_sort_popularity_label));
                if(sort.equals("rating")){
                    sort="vote_average.desc";
                }
                else
                {
                 sort="popularity.desc";
                }

                movieSort = sort;
                Uri builtUri = Uri.parse(MOVIEDATABASE_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sort)
                        .appendQueryParameter(APPID, BuildConfig.OPEN_MOVIE_DATABASE_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI = " + builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0) {
                    return null;
                }

                movieJson = buffer.toString();
                result = getMoviesFromJson(movieJson);
                Log.v(LOG_TAG,"Movie JSON String " + movieJson);
                return result;
            }catch (IOException e) {
                Log.v(LOG_TAG,"Error ",e);
            }catch (JSONException e){
                Log.v(LOG_TAG,"Json Error in  ",e);
            }finally {
                if(urlConnection != null)
                    urlConnection.disconnect();
                if(reader != null) {
                    try {
                        reader.close();

                    }catch (final IOException e) {
                        Log.v(LOG_TAG,"Error closing stream",e);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies != null) {
                movieList = (ArrayList<Movie>)movies;
                movieAdapter.clear();
                for(Movie m : movies) {
                    movieAdapter.add(m);
                }
            }
        }
    }



    public void updateMovies() {
        String sort = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_movie_key), getString(R.string.pref_sort_popularity_label));
        if( movieSort == null) {
            movieSort = sort;
        }
        if (movieList.isEmpty() || !movieSort.equals(sort)) {
            FetchMovieTask movies = new FetchMovieTask();
            movies.execute();
        }
    }
}
