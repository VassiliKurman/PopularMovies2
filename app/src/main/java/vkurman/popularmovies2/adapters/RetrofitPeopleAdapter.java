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
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vkurman.popularmovies2.R;
import vkurman.popularmovies2.listeners.ResultListener;
import vkurman.popularmovies2.model.ResultPerson;
import vkurman.popularmovies2.utils.MovieUtils;

/**
 * RetrofitPeopleAdapter
 * Created by Vassili Kurman on 23/09/2018.
 * Version 1.0
 */
public class RetrofitPeopleAdapter extends RecyclerView.Adapter<RetrofitPeopleAdapter.PersonViewHolder> {

    private List<ResultPerson> mResults;
    private ResultListener mResultListener;

    public class PersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.iv_list_poster_person)
        ImageView posterImageView;
        @BindView(R.id.tv_list_poster_person)
        TextView posterTextView;

        PersonViewHolder(View itemView, ResultListener resultListener) {
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
                ResultPerson result = mResults.get(position);
                mResultListener.onResultClick(result.getId());
            }
        }
    }

    public RetrofitPeopleAdapter(List<ResultPerson> posts, ResultListener resultListener) {
        mResults = posts;
        mResultListener = resultListener;
    }

    @NonNull
    @Override
    public RetrofitPeopleAdapter.PersonViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_person_layout;

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new RetrofitPeopleAdapter.PersonViewHolder(view, mResultListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        if(position >= 0 && position < mResults.size()) {
            final ResultPerson result = mResults.get(position);
            final long resultId = result.getId();

            String imagePath = MovieUtils.createFullIconPath(result.getProfilePath());

            holder.itemView.setTag(resultId);

            holder.posterTextView.setText(result.getName());

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

    public void updateData(List<ResultPerson> results) {
        mResults = results;
        notifyDataSetChanged();
    }
}