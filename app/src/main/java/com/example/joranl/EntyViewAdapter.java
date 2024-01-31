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
        holder.entryTextView.setText(allEntries.get(position).getEntryText());
        holder.entryDateTextView.setText(allEntries.get(position).getEntryDate());
        if (allEntries.get(position).hasImage()) {
            byte[] bytes;
            try {
                bytes = readBytesFromFile(allEntries.get(position).getImgUri());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            holder.entryImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.entryImageView.setImageBitmap(bitmap);
        }
    }

    private byte[] readBytesFromFile(String imgUri) throws IOException {
        FileInputStream fileInputStream = context.openFileInput(imgUri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = fileInputStream.read(buffer)) != -1){
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
