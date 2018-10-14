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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vkurman.popularmovies2.R;
import vkurman.popularmovies2.model.ResultMovieReview;

/**
 * ReviewsListAdapter is adapter for movie reviews recycler view
 * Created by Vassili Kurman on 23/03/2018.
 * Version 1.0
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {

    private List<ResultMovieReview> reviews;

    /**
     * Provides a reference to the views for each data item.
     */
    class ReviewsViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @BindView(R.id.tv_author) TextView mAuthor;
        @BindView(R.id.tv_review) TextView mContent;
        @BindView(R.id.btn_expand) Button mExpandableButton;

        ReviewsViewHolder(View view) {
            super(view);
            // Binding views
            ButterKnife.bind(this, itemView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReviewsAdapter(List<ResultMovieReview> reviews) {
        this.reviews = reviews;
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                   int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_review_layout, parent, false);
        return new ReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsViewHolder holder, int position) {
        if(position >= 0 && position < reviews.size()) {
            final ResultMovieReview review = reviews.get(position);
            Log.d(ReviewsAdapter.class.getSimpleName(), "Review id: " + review.getId());
            Log.d(ReviewsAdapter.class.getSimpleName(), "Review author: " + review.getAuthor());
            Log.d(ReviewsAdapter.class.getSimpleName(), "Review content: " + review.getContent());

            holder.mAuthor.setText(review.getAuthor());
            holder.mContent.setText(review.getContent());
            final int maxLines = 5;
            final ReviewsViewHolder mHolder = holder;
            if(mHolder.mContent.length() > maxLines) {
                mHolder.mExpandableButton.setVisibility(View.VISIBLE);
            } else {
                mHolder.mExpandableButton.setVisibility(View.GONE);
            }
            mHolder.mContent.setMaxLines(maxLines);

            mHolder.mExpandableButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mHolder.mContent.getMaxLines() == maxLines) {
                        mHolder.mContent.setMaxLines(mHolder.mContent.length());
                        mHolder.mExpandableButton.setText(R.string.text_collapse);
                    } else {
                        mHolder.mContent.setMaxLines(maxLines);
                        mHolder.mExpandableButton.setText(R.string.text_expand);
                    }
                }
            });
        }
    }

    // Return the size of list (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return reviews == null ? 0 : reviews.size();
    }

    /**
     * Updates reviews.
     *
     * @param reviews - List<ResultMovieReview>
     */
    public void updateReviews(List<ResultMovieReview> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }
}