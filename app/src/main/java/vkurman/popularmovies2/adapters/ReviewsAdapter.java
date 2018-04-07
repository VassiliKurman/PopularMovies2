package vkurman.popularmovies2.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;

import vkurman.popularmovies2.R;
import vkurman.popularmovies2.model.Review;

/**
 * ReviewsListAdapter
 * Created by Vassili Kurman on 23/03/2018.
 * Version 1.0
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {

    private List<Review> reviews;
    private int mExpandedPosition = -1;

    /**
     * Provides a reference to the views for each data item.
     */
    public static class ReviewsViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mAuthor;
        public TextView mContent;
        public Button mExpandableButton;

        public ReviewsViewHolder(View view) {
            super(view);

            mAuthor = view.findViewById(R.id.tv_author);
            mContent = view.findViewById(R.id.tv_review);
            mExpandableButton = view.findViewById(R.id.btn_expand);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReviewsAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReviewsAdapter.ReviewsViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_review_layout;

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new ReviewsAdapter.ReviewsViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ReviewsViewHolder holder, int position) {
        if(position >= 0 && position < reviews.size()) {
            final Review review = reviews.get(position);

            if(review == null) {
                return;
            }
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
     * @param reviews
     */
    public void updateReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }
}