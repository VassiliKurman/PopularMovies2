package vkurman.popularmovies2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import vkurman.popularmovies2.utils.MovieUtils;

/**
 * Project Popular Movies stage 2.
 * Created by Vassili Kurman on 25/02/2018.
 * Version 1.0
 */
public class MovieDetailsActivity extends AppCompatActivity {

    // Binding views
    @BindView(R.id.poster_iv) ImageView ivMoviePoster;
    @BindView(R.id.title_tv) TextView tvTitle;
    @BindView(R.id.release_date_tv) TextView tvReleaseDate;
    @BindView(R.id.vote_average_tv) TextView tvVoteAverage;
    @BindView(R.id.plot_synopsis_tv) TextView tvPlotSynopsis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        String poster = intent.getStringExtra(getString(R.string.extra_poster));
        String title = intent.getStringExtra(getString(R.string.extra_title));
        String releaseDate = intent.getStringExtra(getString(R.string.extra_release_date));
        String voteAverage = intent.getStringExtra(getString(R.string.extra_vote_average));
        String plotSynopsis = intent.getStringExtra(getString(R.string.extra_plot_synopsis));

        // Setting text to text views
        tvTitle.setText(title);
        tvReleaseDate.setText(releaseDate.substring(0, 4));
        tvVoteAverage.setText(voteAverage);
        tvPlotSynopsis.setText(plotSynopsis);

        Picasso.with(this)
                .load(MovieUtils.createFullIconPath(poster))
                .placeholder(R.drawable.ic_image_area)
                .error(R.drawable.ic_error_image)
                .into(ivMoviePoster);

        setTitle(getString(R.string.activity_title_movie_details));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }
}