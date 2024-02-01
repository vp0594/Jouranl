package com.example.joranl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class EntyViewAdapter extends RecyclerView.Adapter<EntyViewAdapter.ViewHolder> {

    List<EntryWithBItMap> entryWithBItMaps;
    Context context;

    public EntyViewAdapter(List<EntryWithBItMap> entryWithBItMaps, Context context) {
        this.entryWithBItMaps = entryWithBItMaps;
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return entryWithBItMaps.get(position).getEntry().getId();
    }

    @NonNull
    @Override
    public EntyViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.entry_view, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull EntyViewAdapter.ViewHolder holder, int position) {
        holder.entryTextView.setText(entryWithBItMaps.get(position).getEntry().getEntryText());

        if (position == 0 || !entryWithBItMaps.get(position-1).getEntry().getEntryDate().equals(entryWithBItMaps.get(position).getEntry().getEntryDate())) {
            // Show date for the first item or if the date is different from the previous entry
            holder.entryDateTextView.setVisibility(View.VISIBLE);
            holder.entryDateTextView.setText(entryWithBItMaps.get(position).getEntry().getEntryDate());
        } else {
            // Hide date if it's the same as the previous entry
            holder.entryDateTextView.setVisibility(View.GONE);
        }

        if(entryWithBItMaps.get(position).getEntry().hasImage()){
            Glide.with(context)
                    .load(entryWithBItMaps.get(position).getBitmap())
                    .centerCrop()
                    .into(holder.entryImageView);
        }
    }

    @Override
    public int getItemCount() {
        return entryWithBItMaps.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView entryDateTextView, entryTextView;
        public final ImageView entryImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            entryImageView = itemView.findViewById(R.id.entryImageView);
            entryDateTextView = itemView.findViewById(R.id.dateTextView);
            entryTextView = itemView.findViewById(R.id.entryTextView);
        }
    }
}
