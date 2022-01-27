package com.example.kimhk.aoi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SelectMarkerViewAdapter extends RecyclerView.Adapter<SelectMarkerViewAdapter.ViewHolder> {

    private ArrayList<String> mData = null;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView, textView2;

        public ViewHolder(View selectMarkerViewAdapter) {
            super(selectMarkerViewAdapter);

            textView = itemView.findViewById(R.id.textView);
            textView2 = itemView.findViewById(R.id.textView2);
        }
    }

    SelectMarkerViewAdapter(ArrayList<String> list){
     mData = list;
    }

    @NonNull
    @Override
    public SelectMarkerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.selectmarkerview_item, parent, false);
        SelectMarkerViewAdapter.ViewHolder viewHolder = new SelectMarkerViewAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectMarkerViewAdapter.ViewHolder holder, int position) {
        String text = mData.get(position);
        String text2 = mData.get(position);

        holder.textView.setText(text);
        holder.textView2.setText(text2);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
