package com.test.loremflickr.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.test.loremflickr.Constants;
import com.test.loremflickr.model.LoremFlickrImage;
import com.test.loremflickr.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {

    private final OnItemClickListener listener;
    private ArrayList<LoremFlickrImage> items = new ArrayList<>();

    ImageAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image_main, parent, false);
        return new ImageHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        holder.bind(items.get(position), listener, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void addItems(List<LoremFlickrImage> images) {
        int from = items.size();
        items.addAll(images);
        notifyItemRangeInserted(from, Constants.PER_PAGE);
    }

    void setItems(List<LoremFlickrImage> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    class ImageHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_view_item_main)
        ImageView imageView;
        @BindView(R.id.text_view_item_main_author)
        TextView authorText;

        ImageHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(LoremFlickrImage image, OnItemClickListener listener, int position) {
            authorText.setText(image.getOwner());
            Glide.with(itemView)
                    .load(image.getImage())
                    .placeholder(R.drawable.placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(imageView);

            itemView.setOnClickListener(v -> listener.onItemClick(image, position, itemView));
        }
    }

    interface OnItemClickListener {
        void onItemClick(LoremFlickrImage image, int position, View view);
    }
}
