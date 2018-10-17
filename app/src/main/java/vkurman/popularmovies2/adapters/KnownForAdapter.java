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
import vkurman.popularmovies2.model.KnownFor;
import vkurman.popularmovies2.utils.MovieUtils;

/**
 * KnownForAdapter
 * Created by Vassili Kurman on 14/10/2018.
 * Version 1.0
 */
public class KnownForAdapter extends RecyclerView.Adapter<KnownForAdapter.KnownForViewHolder> {

    private List<KnownFor> mKnownFor;
    private ResultListener mResultListener;

    public class KnownForViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.iv_list_poster_movie)
        ImageView mPersonPoster;

        KnownForViewHolder(View itemView, ResultListener resultListener) {
            super(itemView);
            // Binding views
            ButterKnife.bind(this, itemView);
            mResultListener = resultListener;
            // Set separate click listeners to poster
            mPersonPoster.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mKnownFor == null) {
                return;
            }
            int position = getAdapterPosition();
            if(position >= 0 && position < mKnownFor.size()) {
                KnownFor knownFor = mKnownFor.get(position);
                mResultListener.onResultClick(knownFor.getId(), null);
            }
        }
    }

    public KnownForAdapter(List<KnownFor> knownFor, ResultListener resultListener) {
        mKnownFor = knownFor;
        mResultListener = resultListener;
    }

    @NonNull
    @Override
    public KnownForViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_person_known_for_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new KnownForViewHolder(view, mResultListener);
    }

    @Override
    public void onBindViewHolder(@NonNull KnownForViewHolder holder, int position) {
        if(position >= 0 && position < mKnownFor.size()) {
            final KnownFor knownFor = mKnownFor.get(position);
            final long knownForId = knownFor.getId();

            String imagePath = MovieUtils.createFullPosterPath(knownFor.getPosterPath());

            holder.itemView.setTag(knownForId);

            Picasso.get()
                    .load(imagePath)
                    .placeholder(R.drawable.ic_image_area)
                    .error(R.drawable.ic_error_image)
                    .into(holder.mPersonPoster);
        }
    }

    @Override
    public int getItemCount() {
        return mKnownFor == null ? 0 : mKnownFor.size();
    }

    public void updateData(List<KnownFor> knownFor) {
        mKnownFor = knownFor;
        notifyDataSetChanged();
    }
}