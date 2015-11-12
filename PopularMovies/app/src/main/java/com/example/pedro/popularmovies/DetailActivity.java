package com.example.pedro.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_frame,new DetailActivityFragment()).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
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

    public static class DetailActivityFragment extends Fragment {

        private final String LOG_TAG = DetailActivity.class.getSimpleName();

        private Movie movie;

        public DetailActivityFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            if(intent != null && intent.hasExtra("Movie"))
                movie = intent.getParcelableExtra("Movie");
            ((TextView) rootView.findViewById(R.id.movie_title)).setText(movie.getTitle());
            ((TextView) rootView.findViewById(R.id.synopsis_view)).setText(movie.getOverview());
            ((TextView) rootView.findViewById(R.id.date_view)).setText(movie.getReleaseDate());
            ((TextView) rootView.findViewById(R.id.user_rating_view)).setText(Float.toString(movie.getRating()));

            Uri imageUri = Uri.parse(getContext().getString(R.string.image_url)).buildUpon()
                    .appendPath(getContext().getString(R.string.image_size))
                    .appendEncodedPath(movie.getImagePath())
                    .build();

            Picasso.with(getContext()).load(imageUri).into(((ImageView) rootView.findViewById(R.id.poster_image)));

            return rootView;
        }
    }


}
