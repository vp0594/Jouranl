package com.example.joranl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private final ArrayList<String> dayOfMonth;
    private final List<CalendarEntry> monthEntry;
    private final Context context;

    public CalendarAdapter(ArrayList<String> dayOfMonth, List<CalendarEntry> monthEntry, Context context) {
        this.dayOfMonth = dayOfMonth;
        this.monthEntry = monthEntry;
        this.context = context;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.095);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        holder.cardView.setVisibility(View.VISIBLE);
        String currentDay = dayOfMonth.get(position);
        holder.dayOfMonth.setText(currentDay);

        // Hide CardView for empty days
        if (dayOfMonth.get(position).equals("   ")) {
            holder.cardView.setVisibility(View.GONE);
        }


        // Load images for days with entries
        if (monthEntry != null && !monthEntry.isEmpty()) {
            for (CalendarEntry entry : monthEntry) {

                String[] monthYear = entry.getEntryDate().split(" ");

                if (monthYear[1].equals(dayOfMonth.get(position)) && entry.hasImage()) {
                    byte[] bytes;
                    try {
                        bytes = readBytesFromFile(entry.getImgUri());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Glide.with(context)
                            .asBitmap()
                            .load(bytes)
                            .apply(RequestOptions.overrideOf(200, 200))
                            .centerCrop()
                            .into(holder.dateBackgroundImageView);

                    break;
                }
            }

            // Set click listener for each day
            holder.itemView.setOnClickListener(v -> {

                if (!(holder.dateBackgroundImageView.getDrawable() == null)) {
                    // Open EntryActivity when a day with an image is clicked
                    Intent intent = new Intent(context, EntryActivity.class);
                    intent.putExtra("key", 1);

                    for (CalendarEntry entry : monthEntry) {

                        String[] monthYear = entry.getEntryDate().split(" ");

                        if (holder.dayOfMonth.getText().toString().equals(monthYear[1])) {
                            intent.putExtra("id", entry.getId());
                            // Inside an Activity
                            SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("from", 1);
                            editor.apply();

                            break;
                        }
                    }

                    context.startActivity(intent);
                }
            });
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

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {

        public final TextView dayOfMonth;
        public final ImageView dateBackgroundImageView;
        private final CardView cardView;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dayOfMonth = itemView.findViewById(R.id.cellDayText);
            dateBackgroundImageView = itemView.findViewById(R.id.backgroundImage);
            cardView = itemView.findViewById(R.id.calendarCellCardView);
        }
    }
}
