package com.example.joranl;

import android.content.Context;
import android.content.Intent;
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
    private final OnItemListener onItemListener;
    List<CalendarEntry> monthEntry;
    private final Context context;

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
        layoutParams.height = (int) (parent.getHeight() * 0.095);
        return new CalendarViewHolder(view, onItemListener);
    }


    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        holder.cardView.setVisibility(View.VISIBLE);
        String currentDay = dayOfMonth.get(position);
        holder.dayOfMonth.setText(currentDay);

        if (dayOfMonth.get(position).equals("   ")){
            holder.cardView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {

            if (!(holder.dateBackgroundImageView.getDrawable() == null)) {

                Intent intent = new Intent(context, EntryActivity.class);
                intent.putExtra("key", 1);

                for (CalendarEntry entry : monthEntry) {

                    String[] monthYear = entry.getEntryDate().split(" ");

                    if (holder.dayOfMonth.getText().toString().equals(monthYear[1])) {
                        intent.putExtra("id", entry.getId());
                        break;
                    }
                }

                context.startActivity(intent);

            }
        });

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
        private final CardView cardView;

        public CalendarViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            dayOfMonth = itemView.findViewById(R.id.cellDayText);
            dateBackgroundImageView = itemView.findViewById(R.id.backgroundImage);
            cardView = itemView.findViewById(R.id.calendarCellCardView);
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
