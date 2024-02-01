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

import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private final ArrayList<String> dayOfMonth;
    private final OnItemListener onItemListener;
    List<CalendarEntry> monthEntry;
    private Context context;

    public CalendarAdapter(ArrayList<String> dayOfMonth, OnItemListener onItemListener, List<CalendarEntry> monthEntry, Context context) {
        this.dayOfMonth = dayOfMonth;
        this.onItemListener = onItemListener;
        this.monthEntry = monthEntry;
        this.context = context;
    }


    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.085);
        return new CalendarViewHolder(view, onItemListener);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int w, int h) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > h || width > w) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= h && (halfWidth / inSampleSize) >= w) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        holder.dayOfMonth.setText(dayOfMonth.get(position));

        if (monthEntry != null && !monthEntry.isEmpty()) {
            for (CalendarEntry entry : monthEntry) {

                String[] monthYear = entry.getEntryDate().split(" ");

                if (monthYear[1].equals(dayOfMonth.get(position)) && entry.hasImage()) {
                    byte[] byt;
                    try {
                        byt = readBytesFromFile(entry.getImgUri());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;

                    BitmapFactory.decodeByteArray(byt, 0, byt.length, options);

                    int width = 50;
                    int height = 50;

                    options.inSampleSize = calculateInSampleSize(options, width, height);
                    options.inJustDecodeBounds = false;

                    Bitmap bitmap = BitmapFactory.decodeByteArray(byt, 0, byt.length, options);
                    holder.dateBackgroundImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    holder.dateBackgroundImageView.setImageBitmap(bitmap);

                    break;
                }
            }
        }
    }

    private byte[] readBytesFromFile(String name) throws IOException {
        FileInputStream fis = context.openFileInput(name);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = fis.read(buffer)) != -1) {
            bos.write(buffer, 0, bytesRead);
        }

        fis.close();
        return bos.toByteArray();
    }

    @Override
    public int getItemCount() {
        return dayOfMonth.size();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView dayOfMonth;
        public final ImageView dateBackgroundImageView;
        private final OnItemListener onItemListener;

        public CalendarViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            dayOfMonth = itemView.findViewById(R.id.cellDayText);
            dateBackgroundImageView=itemView.findViewById(R.id.backgroundImage);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition(), (String) dayOfMonth.getText());
        }
    }

    public interface OnItemListener {
        void onItemClick(int position, String dayText);
    }
}
