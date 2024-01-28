package com.example.joranl;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private final ArrayList<String> dayOfMonth;
    private final OnItemListener onItemListener;

    public CalendarAdapter(ArrayList<String> dayOfMonth, OnItemListener onItemListener) {
        this.dayOfMonth = dayOfMonth;
        this.onItemListener = onItemListener;
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

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        holder.dayOfMonth.setText(dayOfMonth.get(position));
        if(Objects.equals(dayOfMonth.get(position), "3")){
            holder.dateBackgroundImageView.setBackgroundResource(R.drawable.img);
        }if(Objects.equals(dayOfMonth.get(position), "2")){
        }
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
