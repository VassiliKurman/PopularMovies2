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
import vkurman.popularmovies2.model.RecommendationsMovieResult;
import vkurman.popularmovies2.utils.MovieUtils;
import vkurman.popularmovies2.utils.MoviesConstants;

/**
 * RecommendationsMovieAdapter
 * Created by Vassili Kurman on 13/11/2018.
 * Version 1.0
 */
public class RecommendationsMovieAdapter extends RecyclerView.Adapter<RecommendationsMovieAdapter.RecommendationViewHolder> {

    private List<RecommendationsMovieResult> mResults;
    private ResultListener mResultListener;

    public class RecommendationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.iv_list_recommendation_backdrop)
        ImageView posterImageView;
        @BindView(R.id.tv_list_recommendation_name)
        TextView posterTextView;

        RecommendationViewHolder(View itemView, ResultListener resultListener) {
            super(itemView);
            // Binding views
            ButterKnife.bind(this, itemView);
            mResultListener = resultListener;
            // Set separate click listeners to poster and favourite image
            posterImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mResults == null) {
                return;
            }
            int position = getAdapterPosition();
            if(position >= 0 && position < mResults.size()) {
                RecommendationsMovieResult result = mResults.get(position);
                Bundle bundle = new Bundle();
                bundle.putString(MoviesConstants.BUNDLE_EXTRA_TYPE, MoviesConstants.BUNDLE_EXTRA_MOVIE);
                mResultListener.onResultClick(result.getId(), bundle);
            }
        }
    }

    public RecommendationsMovieAdapter(List<RecommendationsMovieResult> results, ResultListener resultListener) {
        mResults = results;
        mResultListener = resultListener;
    }

    @NonNull
    @Override
    public RecommendationsMovieAdapter.RecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_recommendation_layout;

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new RecommendationsMovieAdapter.RecommendationViewHolder(view, mResultListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationsMovieAdapter.RecommendationViewHolder holder, int position) {
        if(position >= 0 && position < mResults.size()) {
            final RecommendationsMovieResult result = mResults.get(position);
            final long resultId = result.getId();

            String imagePath = MovieUtils.createFullPosterPath(result.getBackdropPath());

            holder.itemView.setTag(resultId);

            holder.posterTextView.setText(result.getTitle());

            Picasso.get()
                    .load(imagePath)
                    .placeholder(R.drawable.ic_image_area)
                    .error(R.drawable.ic_error_image)
                    .into(holder.posterImageView);
        }
    }

    @Override
    public int getItemCount() {
        return mResults == null ? 0 : mResults.size();
    }

    public void updateData(List<RecommendationsMovieResult> results) {
        mResults = results;
        notifyDataSetChanged();
    }
}