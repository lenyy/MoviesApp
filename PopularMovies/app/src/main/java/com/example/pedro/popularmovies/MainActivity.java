package com.example.pedro.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.pedro.popularmovies.service.MovieService;

public class MainActivity extends AppCompatActivity implements MovieFragment.Callback {

    public final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;
    //The current sorting of movies
    private String sorting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sorting = Utility.getPreferedSorting(this);

        if(findViewById(R.id.detail_movie_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp-land). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_movie_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }

        Intent intent = new Intent(this, MovieService.class);
        startService(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        String sort = Utility.getPreferedSorting(this);
        if(sort != null && !sort.equals(sorting)){
            MovieFragment movieFragment = (MovieFragment) getSupportFragmentManager().findFragmentById(R.id.gridMovies);
            if (movieFragment != null) {
                movieFragment.updateMovies();
                Log.v(LOG_TAG, "movies updatedddddddddddd");
            }
            sorting = sort;
        }

        Log.v(LOG_TAG, "OnRESUMEDDDDDDDDDDDDDDDDDDDDDDDD");
        Log.v(LOG_TAG, "Old sort : " + sorting);
        Log.v(LOG_TAG, "New sort : " + sort);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this,SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onItemSelected(Uri contentUri) {
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI,contentUri);

            DetailFragment df = new DetailFragment();
            df.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_movie_container, df, DETAILFRAGMENT_TAG)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
        Log.v(LOG_TAG, "Content URI : " + contentUri);
    }
}
