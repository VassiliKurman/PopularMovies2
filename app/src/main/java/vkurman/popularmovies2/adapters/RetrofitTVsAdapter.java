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
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vkurman.popularmovies2.R;
import vkurman.popularmovies2.listeners.ResultListener;
import vkurman.popularmovies2.model.ResultTV;
import vkurman.popularmovies2.utils.MovieUtils;

/**
 * RetrofitTVsAdapter
 * Created by Vassili Kurman on 23/09/2018.
 * Version 1.0
 */
public class RetrofitTVsAdapter extends RecyclerView.Adapter<RetrofitTVsAdapter.TVsViewHolder> {

    private List<ResultTV> mResults;
    private ResultListener mResultListener;

    public class TVsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.iv_list_poster) ImageView posterImageView;

        TVsViewHolder(View itemView, ResultListener resultListener) {
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
                ResultTV result = mResults.get(position);
                mResultListener.onResultClick(result.getId());
            }
        }
    }

    public RetrofitTVsAdapter(List<ResultTV> data, ResultListener resultListener) {
        mResults = data;
        mResultListener = resultListener;
    }

    @NonNull
    @Override
    public RetrofitTVsAdapter.TVsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_layout;

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new TVsViewHolder(view, mResultListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TVsViewHolder holder, int position) {
        if(position >= 0 && position < mResults.size()) {
            final ResultTV result = mResults.get(position);
            final long resultId = result.getId();

            String imagePath = MovieUtils.createFullIconPath(result.getPosterPath());

            holder.itemView.setTag(resultId);

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

    public void updateData(List<ResultTV> results) {
        mResults = results;
        notifyDataSetChanged();
    }
}