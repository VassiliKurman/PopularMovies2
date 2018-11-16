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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vkurman.popularmovies2.R;
import vkurman.popularmovies2.listeners.ResultListener;
import vkurman.popularmovies2.model.CastMovie;
import vkurman.popularmovies2.utils.MovieUtils;
import vkurman.popularmovies2.utils.MoviesConstants;

/**
 * MovieCastAdapter
 * Created by Vassili Kurman on 07/10/2018.
 * Version 1.0
 */
public class MovieCastAdapter extends RecyclerView.Adapter<MovieCastAdapter.MovieCastViewHolder> {

    private List<CastMovie> mCast;
    private ResultListener mResultListener;

    public class MovieCastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.iv_list_person_poster)
        ImageView mPersonPoster;
        @BindView(R.id.tv_list_person_name)
        TextView mPersonName;
        @BindView(R.id.tv_list_person_job)
        TextView mPersonJob;

        MovieCastViewHolder(View itemView, ResultListener resultListener) {
            super(itemView);
            // Binding views
            ButterKnife.bind(this, itemView);
            mResultListener = resultListener;
            // Set separate click listeners to poster
            mPersonPoster.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mCast == null) {
                return;
            }
            int position = getAdapterPosition();
            if(position >= 0 && position < mCast.size()) {
                CastMovie crew = mCast.get(position);
                Bundle bundle = new Bundle();
                bundle.putString(MoviesConstants.BUNDLE_EXTRA_TYPE, MoviesConstants.BUNDLE_EXTRA_PERSON);
                mResultListener.onResultClick(crew.getId(), bundle);
            }
        }
    }

    public MovieCastAdapter(List<CastMovie> cast, ResultListener resultListener) {
        mCast = cast;
        mResultListener = resultListener;
    }

    @NonNull
    @Override
    public MovieCastAdapter.MovieCastViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_movie_crew_layout;

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new MovieCastAdapter.MovieCastViewHolder(view, mResultListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieCastAdapter.MovieCastViewHolder holder, int position) {
        if(position >= 0 && position < mCast.size()) {
            final CastMovie crew = mCast.get(position);
            final long crewId = crew.getId();

            String imagePath = MovieUtils.createFullPosterPath(crew.getProfilePath());

            holder.itemView.setTag(crewId);

            holder.mPersonName.setText(crew.getName());
            holder.mPersonJob.setText(crew.getCharacter());

            Picasso.get()
                    .load(imagePath)
                    .placeholder(R.drawable.ic_image_area)
                    .error(R.drawable.ic_error_image)
                    .into(holder.mPersonPoster);
        }
    }

    @Override
    public int getItemCount() {
        return mCast == null ? 0 : mCast.size();
    }

    public void updateData(List<CastMovie> cast) {
        mCast = cast;
        notifyDataSetChanged();
    }
}