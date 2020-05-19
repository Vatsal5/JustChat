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

    public static final int GIF = 0;
    public static final int STICK = 1;

    Context context;
    ArrayList<String> url;
    ItemSelected Activity;

    public interface ItemSelected
    {
        public void ImageClicked(int index);
        public void StickerClicked(int index);
    }

    public gif_adapter(Context context, ArrayList<String> url) {
        this.context = context;
        this.url=url;
        Activity = (ItemSelected) context;
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

        if(viewType == GIF) {
            final View v = inflater.inflate(R.layout.gif_layout, parent, false);
            return new gif_adapter.viewholder(v);
        }
        else
        {
            final View v = inflater.inflate(R.layout.sticker_layout, parent, false);
            return new gif_adapter.viewholder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, int position) {

        Glide.with(context.getApplicationContext()).load(Uri.parse(url.get(position).substring(1))).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(url.get(holder.getAdapterPosition()).substring(0,1).equals("g"))
                    Activity.ImageClicked(holder.getAdapterPosition());
                else
                    Activity.StickerClicked(holder.getAdapterPosition());
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return url.size();
    }

    @Override
    public int getItemViewType(int position) {

        if(url.get(position).substring(0,1).equals("s"))
            return STICK;
        else
            return GIF;

    }
}
