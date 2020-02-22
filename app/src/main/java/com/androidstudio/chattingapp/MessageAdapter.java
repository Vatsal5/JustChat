package com.androidstudio.chattingapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TXT_LEFT = 0;
    public static final int MSG_TXT_RIGHT = 1;
    public static final int MSG_IMG_LEFT = 2;
    public static final int MSG_IMG_RIGHT = 3;

    FirebaseUser user;

    Context context;
    ArrayList <MessageModel> messages;

    public MessageAdapter(Context context, ArrayList<MessageModel> messages) {
        this.context = context;
        this.messages = messages;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvMessage;
        ImageView ivDownload,ivImage,ivUpload;
        ImageView ivClose;
        ProgressBar progress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMessage = itemView.findViewById(R.id.tvMessage);
            ivDownload = itemView.findViewById(R.id.ivDownload);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivClose = itemView.findViewById(R.id.ivClose);
            progress = itemView.findViewById(R.id.progress);
            ivUpload = itemView.findViewById(R.id.ivUpload);
        }
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(viewType == MSG_IMG_LEFT)
        {
            View v = LayoutInflater.from(context).inflate(R.layout.image_left,parent,false);
            return new ViewHolder(v);
        }
        else if(viewType == MSG_IMG_RIGHT)
        {
            View v = LayoutInflater.from(context).inflate(R.layout.image_right,parent,false);
            return new ViewHolder(v);
        }
        else if(viewType == MSG_TXT_LEFT)
        {
            View v = LayoutInflater.from(context).inflate(R.layout.message_left,parent,false);
            return new ViewHolder(v);
        }
        else
        {
            View v = LayoutInflater.from(context).inflate(R.layout.message_right,parent,false);
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, final int position)
    {
        if(messages.get(position).getDownloaded()==0)
        {
            holder.ivUpload.setVisibility(View.GONE);
            holder.ivDownload.setVisibility(View.VISIBLE);
            holder.ivDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.ivDownload.setVisibility(View.GONE);
                    holder.progress.setVisibility(View.VISIBLE);
                    holder.ivClose.setVisibility(View.VISIBLE);

                    Glide.with(context).load(messages.get(position).getMessage()).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                            holder.progress.setVisibility(View.GONE);
                            holder.ivClose.setVisibility(View.GONE);
                            holder.ivDownload.setVisibility(View.VISIBLE);

                            Toast.makeText(context, "Could not download the image", Toast.LENGTH_LONG).show();

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            holder.ivImage.setImageDrawable(resource);

                            holder.ivDownload.setVisibility(View.GONE);
                            holder.progress.setVisibility(View.GONE);
                            holder.ivClose.setVisibility(View.GONE);

                            return false;
                        }
                    }).into(holder.ivImage);

                }
            });
        }
        else if(messages.get(position).getDownloaded() == 1)
        {
            holder.ivDownload.setVisibility(View.GONE);
            holder.ivClose.setVisibility(View.GONE);
            holder.progress.setVisibility(View.GONE);

        }
        else if(messages.get(position).getDownloaded() == 2)
        {
            holder.ivUpload.setVisibility(View.GONE);
            holder.ivDownload.setVisibility(View.GONE);
            holder.ivClose.setVisibility(View.VISIBLE);
            holder.progress.setVisibility(View.VISIBLE);
            holder.ivImage.setImageURI(messages.get(position).getUri());

            holder.ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.ivClose.setVisibility(View.GONE);
                    holder.progress.setVisibility(View.GONE);
                    holder.ivUpload.setVisibility(View.VISIBLE);
                }
            });

            holder.ivUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.ivUpload.setVisibility(View.GONE);
                    holder.ivClose.setVisibility(View.VISIBLE);
                    holder.progress.setVisibility(View.VISIBLE);
                }
            });
        }
        else if(messages.get(position).getDownloaded() == -1)
        {
            holder.tvMessage.setText(messages.get(position).getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(messages.get(position).getSender().equals(user.getPhoneNumber())) {
            if (messages.get(position).getType().equals("image")) {
                return MSG_IMG_RIGHT;
            } else if (messages.get(position).getType().equals("text")) {
                return MSG_TXT_RIGHT;
            }
        }
        else
        {
            if (messages.get(position).getType().equals("image")) {
                return MSG_IMG_LEFT;
            } else if (messages.get(position).getType().equals("text")) {
                return MSG_TXT_LEFT;
            }
        }
        return -1;
    }
}
