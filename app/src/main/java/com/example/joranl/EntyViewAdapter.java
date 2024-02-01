package com.example.joranl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class EntyViewAdapter extends RecyclerView.Adapter<EntyViewAdapter.ViewHolder> {

    List<CalendarEntry> allEntries;
    Context context;

    public EntyViewAdapter(List<CalendarEntry> allEntries, Context context) {
        this.allEntries = allEntries;
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return allEntries.get(position).getId();
    }

    @NonNull
    @Override
    public EntyViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.entry_view, parent, false);

        return new ViewHolder(view);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    public void onBindViewHolder(@NonNull EntyViewAdapter.ViewHolder holder, int position) {

        holder.entryTextView.setText(allEntries.get(position).getEntryText());

        if (position == 0 || !allEntries.get(position - 1).getEntryDate().equals(allEntries.get(position).getEntryDate())) {
            // Show date for the first item or if the date is different from the previous entry
            holder.entryDateTextView.setVisibility(View.VISIBLE);
            holder.entryDateTextView.setText(allEntries.get(position).getEntryDate());
        } else {
            // Hide date if it's the same as the previous entry
            holder.entryDateTextView.setVisibility(View.GONE);
        }

        if (allEntries.get(position).hasImage()) {
            byte[] bytes;
            try {
                bytes = readBytesFromFile(allEntries.get(position).getImgUri());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

            int desiredWidth = 200; // Set your desired width here
            int desiredHeight = 200;

            options.inSampleSize = calculateInSampleSize(options, desiredWidth, desiredHeight);
            options.inJustDecodeBounds = false;

            Bitmap resizedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

            Glide.with(context)
                    .load(resizedBitmap)
                    .centerCrop()
                    .into(holder.entryImageView);
        }
    }

    private byte[] readBytesFromFile(String imgUri) throws IOException {
        FileInputStream fileInputStream = context.openFileInput(imgUri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer,0,bytesRead);
        }

        fileInputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public int getItemCount() {
        return allEntries.size();
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
