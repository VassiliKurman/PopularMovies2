package vkurman.popularmovies2.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import vkurman.popularmovies2.R;
import vkurman.popularmovies2.model.Video;

/**
 * VideosAdapter
 * Created by Vassili Kurman on 23/03/2018.
 * Version 1.0
 */
public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoViewHolder> {

    private List<Video> videos;

    /**
     * Provides a reference to the views for each data item.
     */
    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView ivImage;

        public VideoViewHolder(View view) {
            super(view);

            ivImage = view.findViewById(R.id.iv_video);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public VideosAdapter(List<Video> videos) {
        this.videos = videos;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public VideosAdapter.VideoViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_video_layout;

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new VideosAdapter.VideoViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(VideosAdapter.VideoViewHolder holder, int position) {
        if(position >= 0 && position < videos.size()) {
            final Video video = videos.get(position);

            if(video == null) {
                return;
            }

            holder.ivImage.setBackgroundResource(R.drawable.ic_error_image);
        }
    }

    // Return the size of list (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return videos == null ? 0 : videos.size();
    }

    public void updateVideos(List<Video> videos) {
        this.videos = videos;
        notifyDataSetChanged();
    }
}
