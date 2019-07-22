package com.test.loremflickr.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.test.loremflickr.model.LoremFlickrImage;
import com.test.loremflickr.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {

    private final OnItemClickListener listener;
    private List<LoremFlickrImage> items = new ArrayList<>();

    public ImageAdapter(OnItemClickListener listener) {
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
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(LoremFlickrImage image) {
        items.add(image);
        notifyItemInserted(items.size() - 1);
    }

    public class ImageHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_view_item_main)
        ImageView imageView;
        @BindView(R.id.text_view_item_main_author)
        TextView authorText;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(LoremFlickrImage image, OnItemClickListener listener) {
            authorText.setText(image.getOwner());
            Glide.with(itemView)
                    .load(image.getImage())
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);

            itemView.setOnClickListener(v -> listener.onItemClick(image));
        }
    }

    interface OnItemClickListener {
        void onItemClick(LoremFlickrImage image);
    }
}
