package com.androidstudio.chattingapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class gif_adapter extends RecyclerView.Adapter<gif_adapter.viewholder> {

    Context context;
    ArrayList<String> url;

    public gif_adapter(Context context, ArrayList<String> url) {
        this.context = context;
        this.url=url;
    }

    public class viewholder extends RecyclerView.ViewHolder
    {

        ImageView imageView;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.ivgif);
        }
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v= inflater.inflate(R.layout.gif_layout,parent,false);
        return  new gif_adapter.viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {

        Glide.with(context).load(Uri.parse(url.get(position))).into(holder.imageView);
    }



    @Override
    public int getItemCount() {
        return url.size();
    }
}
