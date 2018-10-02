/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vkurman.popularmovies2.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import vkurman.popularmovies2.R;
import vkurman.popularmovies2.listeners.ResultListener;
import vkurman.popularmovies2.model.ResultMovie;
import vkurman.popularmovies2.persistance.MoviesContract;
import vkurman.popularmovies2.utils.MovieUtils;

/**
 * RetrofitMoviesAdapter
 * Created by Vassili Kurman on 19/09/2018.
 * Version 1.0
 */
public class RetrofitMoviesAdapter extends RecyclerView.Adapter<RetrofitMoviesAdapter.MoviesViewHolder> {

    private List<ResultMovie> mResults;
    private Map<Long, Long> favourites;
    private ResultListener mResultListener;

    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.iv_list_poster_movie) ImageView posterImageView;
        @BindView(R.id.iv_favourite) ImageView favouriteImageView;

        MoviesViewHolder(View itemView, ResultListener resultListener) {
            super(itemView);
            // Binding views
            ButterKnife.bind(this, itemView);
            mResultListener = resultListener;
            // Set separate click listeners to poster and favourite image
            posterImageView.setOnClickListener(this);
            favouriteImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mResults == null) {
                return;
            }
            int position = getAdapterPosition();
            if(position >= 0 && position < mResults.size()) {
                ResultMovie result = mResults.get(position);
                if(view.getId() == R.id.iv_favourite) {
                    // Sending message to adapter that image for favourite movie is clicked
                    favouriteClicked(view, result);
                } else {
                    mResultListener.onResultClick(result.getId());
                }
            }
        }
    }

    public RetrofitMoviesAdapter(List<ResultMovie> posts, ResultListener resultListener) {
        mResults = posts;
        mResultListener = resultListener;
        favourites = new TreeMap<>();
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_movie_layout;

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new MoviesViewHolder(view, mResultListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {
        if(position >= 0 && position < mResults.size()) {
            final ResultMovie result = mResults.get(position);
            final long resultId = result.getId();

            String imagePath = MovieUtils.createFullPosterPath(result.getPosterPath());

            holder.itemView.setTag(resultId);

            Context context = holder.posterImageView.getContext();
            Picasso.get()
                    .load(imagePath)
                    .placeholder(R.drawable.ic_image_area)
                    .error(R.drawable.ic_error_image)
                    .into(holder.posterImageView);

            // Making sure that favourites not null
            if(favourites == null) {
                loadFavouriteMovieIds(context);
            }

            if (favourites.get(resultId) != null) {
                holder.favouriteImageView.setImageResource(R.drawable.ic_heart);
            } else {
                holder.favouriteImageView.setImageResource(R.drawable.ic_heart_outline);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mResults == null ? 0 : mResults.size();
    }

    public void updateData(List<ResultMovie> results) {
        mResults = results;
        notifyDataSetChanged();
    }

    private void favouriteClicked(View view, ResultMovie result) {
        if(favourites == null) {
            loadFavouriteMovieIds(view.getContext());
        }

        if(view.getId() == R.id.iv_favourite) {
            if(favourites.containsKey(result.getId())) {
                Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
                uri = uri.buildUpon().build();

                String where = MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(result.getId())};

                AppCompatActivity context = (AppCompatActivity) view.getContext();
                context.getContentResolver().delete(uri, where, selectionArgs);

                ((ImageView)view).setImageResource(R.drawable.ic_heart_outline);
                // Removing from favourites map
                favourites.remove(result.getId());
                // Displaying toast
                Toast.makeText(context, "Removed from favourites!", Toast.LENGTH_SHORT).show();
            } else {

                long id = result.getId();
                ContentValues cv = new ContentValues();
                cv.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, result.getId());
                cv.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_POSTER, result.getPosterPath());
                cv.put(MoviesContract.MoviesEntry.COLUMN_TITLE, result.getTitle());
                cv.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, result.getReleaseDate());
                cv.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, result.getVoteAverage());
                cv.put(MoviesContract.MoviesEntry.COLUMN_PLOT_SYNOPSIS, result.getOverview());

                view.getContext().getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, cv);

                ((ImageView)view).setImageResource(R.drawable.ic_heart);

                // Adding to favourites map
                favourites.put(id, id);
                // Displaying toast
                Toast.makeText(view.getContext(), "Added to favourites!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Loading Favourite movies from content provider
     * @param context - Context
     */
    private void loadFavouriteMovieIds(Context context) {
        if(favourites != null) {
            favourites.clear();
        }

        String[] projection = new String[] {MoviesContract.MoviesEntry.COLUMN_MOVIE_ID};
        Cursor cursor = context.getContentResolver().query(
                MoviesContract.MoviesEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID));
            favourites.put(id, id);
        }
        cursor.close();
    }
}