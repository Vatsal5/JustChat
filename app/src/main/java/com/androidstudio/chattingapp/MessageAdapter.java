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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
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
    ArrayList<MessageModel> messages;

    public interface ImageSelected
    {
        public void showImage(int index);
        public void downloadImage(int index);
    }

    ImageSelected Activity;

    public MessageAdapter(Context context, ArrayList<MessageModel> messages) {
        this.context = context;
        this.messages = messages;
        Activity = (ImageSelected) context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        ImageView ivDownload, ivImage, ivUpload;
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
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_IMG_LEFT) {
            View v = LayoutInflater.from(context).inflate(R.layout.image_left, parent, false);
            return new ViewHolder(v);
        } else if (viewType == MSG_IMG_RIGHT) {
            View v = LayoutInflater.from(context).inflate(R.layout.image_right, parent, false);
            return new ViewHolder(v);
        } else if (viewType == MSG_TXT_LEFT) {
            View v = LayoutInflater.from(context).inflate(R.layout.message_left, parent, false);
            return new ViewHolder(v);
        } else {
            View v = LayoutInflater.from(context).inflate(R.layout.message_right, parent, false);
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, final int position) {

        if(holder.ivImage!=null) {
            holder.ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Activity.showImage(position);
                }
            });
        }

        if (messages.get(position).getDownloaded() == 0)   //image is received but yet to be downloaded
        {
            holder.ivClose.setVisibility(View.VISIBLE);
            holder.progress.setVisibility(View.VISIBLE);
            holder.ivImage.setImageResource(0);

            Activity.downloadImage(position);

        }
        else if (messages.get(position).getDownloaded() == 1) // image is sent or downloaded successfully
        {
            holder.ivDownload.setVisibility(View.GONE);
            holder.ivClose.setVisibility(View.GONE);
            holder.progress.setVisibility(View.GONE);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.error(R.drawable.error);

            Glide.with(context).setDefaultRequestOptions(requestOptions).load(messages.get(position).getMessage()).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    holder.ivImage.setClickable(false);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(holder.ivImage);

        } else if (messages.get(position).getDownloaded() == 2) // when sender sends the image
        {

            holder.ivClose.setVisibility(View.VISIBLE);
            holder.progress.setVisibility(View.VISIBLE);

            Glide.with(context).load(messages.get(position).getMessage()).into(holder.ivImage);
        }

        else if(messages.get(position).getDownloaded() == -1) // if message is a text message
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
