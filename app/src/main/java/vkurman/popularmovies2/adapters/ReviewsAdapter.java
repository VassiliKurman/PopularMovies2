package vkurman.popularmovies2.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    /**
     * Provides a reference to the views for each data item.
     */
    public static class ReviewsViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mAuthor;
        public TextView mContent;

        public ReviewsViewHolder(View view) {
            super(view);

            mAuthor = view.findViewById(R.id.tv_author);
            mContent = view.findViewById(R.id.tv_review);
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