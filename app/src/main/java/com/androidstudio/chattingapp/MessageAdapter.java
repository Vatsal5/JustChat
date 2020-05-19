package com.androidstudio.chattingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TXT_LEFT = 0;
    public static final int MSG_TXT_RIGHT = 1;
    public static final int MSG_IMG_LEFT = 2;
    public static final int MSG_IMG_RIGHT = 3;
    public static final int MSG_VIDEO_LEFT = 6;
    public static final int MSG_VIDEO_RIGHT = 5;
    public static final int DATE = 4;
    public static final int TYPING = 7;
    public static final int GRP_MSG_LEFT = 8;
    public static final int GRP_VIDEO_LEFT = 9;
    public static final int GRP_IMAGE_LEFT = 10;
    public static final int UNREAD = 11;
    public static final int GIF_RIGHT = 12;
    public static final int GIF_LEFT = 13;
    public static final int GIF_LEFT_GRP = 14;
    public static final int STICK_RIGHT = 15;
    public static final int STICK_LEFT = 16;
    public static final int STICK_LEFT_GRP = 17;
    public static final int MSG_GRP_INFO = 18;

    FirebaseUser user;
    Context context;
    ArrayList<MessageModel> messages;
    SharedPreferences pref,preftheme;

    public interface ImageSelected
    {
        public void showImage(int index);
        public void downloadImage(int index);
        public void sentTextMessage(int index);
        public void sendImage(int index);
        public void SendVideo(int index);
        public void Downloadvideo(int index);
        public void showVideo(int index);
        public void Onlongclick(int index);
        public void OnFileDeleted(int index);
        public void sendGIF(int index);
        public void downloadGIF(int index);
        public void sendSticker(int index);
        public void downloadSticker(int index);
        public void UrlClicked(int index);
    }

    static ImageSelected Activity;

    public MessageAdapter(Context context, ArrayList<MessageModel> messages) {
        this.context = context;
        this.messages = messages;
        Activity = (ImageSelected) context;
        pref = context.getSharedPreferences("Names",0);
        preftheme=context.getSharedPreferences("theme",0);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime,tvDate,tvSender,tvError,tvGroupInfo;
        EmojiconTextView tvMessage;
        ImageView ivImage,ivPlay,ivProfile,ivTyping,ivSeen,ivGIF;
        ProgressBar progress;
        LinearLayout llMesageLeft,llTyping,llDownload;
        ConstraintLayout llMessageRight;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMessage = itemView.findViewById(R.id.tvMessage);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            progress = itemView.findViewById(R.id.progress);
            tvTime = itemView.findViewById(R.id.tvTime);
            llMessageRight = itemView.findViewById(R.id.llMessageRight);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivPlay = itemView.findViewById(R.id.ivPlay);
            ivTyping = itemView.findViewById(R.id.ivTyping);
            tvSender = itemView.findViewById(R.id.tvSender);
            llDownload = itemView.findViewById(R.id.llDownload);
            llMesageLeft = itemView.findViewById(R.id.llMessageLeft);
            llTyping = itemView.findViewById(R.id.llTyping);
            tvError = itemView.findViewById(R.id.tvError);
            ivSeen = itemView.findViewById(R.id.ivSeen);
            ivGIF = itemView.findViewById(R.id.ivGIF);
            tvGroupInfo = itemView.findViewById(R.id.tvGroupInfo);
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
        }
        else if (viewType == DATE) {
            View v = LayoutInflater.from(context).inflate(R.layout.date_layout, parent, false);
            return new ViewHolder(v);
        }
        else if (viewType == MSG_VIDEO_LEFT) {
            View v = LayoutInflater.from(context).inflate(R.layout.video_left, parent, false);
            return new ViewHolder(v);
        }
        else if (viewType == MSG_VIDEO_RIGHT) {
            View v = LayoutInflater.from(context).inflate(R.layout.video_right, parent, false);
            return new ViewHolder(v);
        }
        else if (viewType == TYPING) {
            View v = LayoutInflater.from(context).inflate(R.layout.typing_layout, parent, false);
            return new ViewHolder(v);
        }
        else if (viewType == GRP_IMAGE_LEFT) {
            View v = LayoutInflater.from(context).inflate(R.layout.image_left2, parent, false);
            return new ViewHolder(v);
        }
        else if (viewType == GRP_MSG_LEFT) {
            View v = LayoutInflater.from(context).inflate(R.layout.message_left2, parent, false);
            return new ViewHolder(v);
        }
        else if (viewType == GRP_VIDEO_LEFT) {
            View v = LayoutInflater.from(context).inflate(R.layout.video_left2, parent, false);
            return new ViewHolder(v);
        }else if(viewType == UNREAD){
            View v = LayoutInflater.from(context).inflate(R.layout.unreadmessages, parent, false);
            return new ViewHolder(v);
        }else if(viewType == GIF_RIGHT){
            View v = LayoutInflater.from(context).inflate(R.layout.gif_right, parent, false);
            return new ViewHolder(v);
        }else if(viewType == GIF_LEFT){
            View v = LayoutInflater.from(context).inflate(R.layout.gif_left, parent, false);
            return new ViewHolder(v);
        }else if(viewType == GIF_LEFT_GRP){
            View v = LayoutInflater.from(context).inflate(R.layout.gif_left2, parent, false);
            return new ViewHolder(v);
        }else if(viewType == STICK_RIGHT){
            View v = LayoutInflater.from(context).inflate(R.layout.sticker_right, parent, false);
            return new ViewHolder(v);
        }else if(viewType == STICK_LEFT){
            View v = LayoutInflater.from(context).inflate(R.layout.sticker_left, parent, false);
            return new ViewHolder(v);
        }else if(viewType == STICK_LEFT_GRP){
            View v = LayoutInflater.from(context).inflate(R.layout.sticker_left2, parent, false);
            return new ViewHolder(v);
        }else if(viewType == MSG_GRP_INFO){
            View v = LayoutInflater.from(context).inflate(R.layout.group_info_layout, parent, false);
            return new ViewHolder(v);
        }
        else {
            View v = LayoutInflater.from(context).inflate(R.layout.message_right, parent, false);
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, final int position) {

        if(holder.tvGroupInfo!=null)
            holder.tvGroupInfo.setText(messages.get(holder.getAdapterPosition()).getMessage());

        if(holder.tvError!=null)
            holder.tvError.setVisibility(View.GONE);

        if(holder.tvSender!=null)
        {
            if(pref.getString(messages.get(holder.getAdapterPosition()).getSender(),"null").equals("null"))
                holder.tvSender.setText(messages.get(holder.getAdapterPosition()).getSender());
            else
                holder.tvSender.setText(pref.getString(messages.get(holder.getAdapterPosition()).getSender(),"null"));
        }

       if( holder.ivImage!=null) {
           holder.ivImage.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View v) {
                   Activity.Onlongclick(holder.getAdapterPosition());
                   return false;
               }
           });
       }


        if( holder.tvMessage!=null) {

            holder.tvMessage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Activity.Onlongclick(holder.getAdapterPosition());
                    return false;
                }
            });

            holder.tvMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Activity.UrlClicked(holder.getAdapterPosition());
                }
            });
        }

        if(holder.ivProfile!=null) {
            if ((messages.get(holder.getAdapterPosition()).getGroupKey().equals("null"))  ) {
                // holder.ivProfile.setVisibility(View.VISIBLE);

                if(!ApplicationClass.url.equals("null"))
                    Glide.with(context.getApplicationContext()).load(ApplicationClass.url).into(holder.ivProfile);
                else
                    Glide.with(context.getApplicationContext()).load(R.drawable.person).into(holder.ivProfile);
            }
            else{
                FirebaseDatabase.getInstance().getReference("users").child(messages.get(holder.getAdapterPosition()).getSender()).child("profile")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.getValue(String.class)!=null)
                                    Glide.with(context).load(dataSnapshot.getValue(String.class)).into(holder.ivProfile);
                                else
                                    holder.ivProfile.setImageResource(R.drawable.person);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            }
        }

        if(holder.ivTyping!=null)
        {
            Glide.with(context).load(R.drawable.typing1).into(holder.ivTyping);
            setBackground(holder.llTyping);
        }


        if(messages.get(holder.getAdapterPosition()).getDownloaded()==300) //when sender sends sticker
        {

            String message = messages.get(holder.getAdapterPosition()).getMessage();

            holder.progress.setVisibility(View.VISIBLE);
            holder.ivSeen.setVisibility(View.GONE);

            Glide.with(context).load(message.substring(0,message.lastIndexOf(" "))).into(holder.ivImage);
            Activity.sendSticker(holder.getAdapterPosition());
        }

        if(messages.get(holder.getAdapterPosition()).getDownloaded()==301) // when request has been sent to listener to upload sticker
        {
            String message = messages.get(holder.getAdapterPosition()).getMessage();

            holder.progress.setVisibility(View.VISIBLE);
            holder.ivSeen.setVisibility(View.GONE);

            Glide.with(context).load(message.substring(0,message.lastIndexOf(" "))).into(holder.ivImage);
        }

        if(messages.get(holder.getAdapterPosition()).getDownloaded()==302) //when sticker is sent or downloaded successfully
        {
            String message = messages.get(holder.getAdapterPosition()).getMessage();
            holder.progress.setVisibility(View.GONE);

            RequestOptions options = new RequestOptions();
            options.diskCacheStrategy(DiskCacheStrategy.NONE);
            options.skipMemoryCache(true);

            if(!message.equals("null")) {
                Glide.with(context).load(message.substring(0,message.lastIndexOf(" "))).apply(options).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        holder.ivImage.setImageResource(0);
                        holder.tvError.setVisibility(View.VISIBLE);

                        if (holder.getAdapterPosition() != -1) {

                            if(!messages.get(holder.getAdapterPosition()).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
                                setBackground(holder.tvError);
                            else
                                holder.tvError.setBackgroundResource(R.drawable.background_right);

                            Activity.OnFileDeleted(holder.getAdapterPosition());
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {


                        return false;
                    }
                }).into(holder.ivImage);
            }
            else
            {
                holder.ivImage.setImageResource(0);
                holder.tvError.setVisibility(View.VISIBLE);

                if(!messages.get(holder.getAdapterPosition()).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
                    setBackground(holder.tvError);
                else
                    holder.tvError.setBackgroundResource(R.drawable.background_right);
            }

                if(messages.get(holder.getAdapterPosition()).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
                    holder.ivSeen.setVisibility(View.GONE);
        }

        else if(messages.get(holder.getAdapterPosition()).getDownloaded()==303) // when sticker is received and yet to be downloaded
        {
            holder.ivImage.setImageResource(0);
            holder.progress.setVisibility(View.VISIBLE);

            Activity.downloadSticker(holder.getAdapterPosition());
        }

        else if(messages.get(holder.getAdapterPosition()).getDownloaded()==304) // when request has been sent to download sticker
        {
            holder.progress.setVisibility(View.VISIBLE);
            holder.ivImage.setImageResource(0);
        }

        else if(messages.get(holder.getAdapterPosition()).getDownloaded()==305  || messages.get(holder.getAdapterPosition()).getDownloaded()==306) //when sticker has been "seen"
        {
            String message = messages.get(holder.getAdapterPosition()).getMessage();
            holder.progress.setVisibility(View.GONE);

            if(messages.get(holder.getAdapterPosition()).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
                holder.ivSeen.setVisibility(View.VISIBLE);

            if(messages.get(holder.getAdapterPosition()).getDownloaded()==306)
                holder.ivSeen.setColorFilter(context.getResources().getColor(R.color.red));
            else
                holder.ivSeen.setColorFilter(context.getResources().getColor(R.color.white));

            RequestOptions options = new RequestOptions();
            options.diskCacheStrategy(DiskCacheStrategy.NONE);
            options.skipMemoryCache(true);

            if(!message.equals("null")) {
                Glide.with(context).load(message.substring(0,message.lastIndexOf(" "))).apply(options).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        holder.ivImage.setImageResource(0);
                        holder.tvError.setVisibility(View.VISIBLE);

                        if (holder.getAdapterPosition() != -1) {

                            if(!messages.get(holder.getAdapterPosition()).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
                                setBackground(holder.tvError);
                            else
                                holder.tvError.setBackgroundResource(R.drawable.background_right);

                            Activity.OnFileDeleted(holder.getAdapterPosition());
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {


                        return false;
                    }
                }).into(holder.ivImage);
            }
            else
            {
                holder.ivImage.setImageResource(0);
                holder.tvError.setVisibility(View.VISIBLE);

                if(!messages.get(holder.getAdapterPosition()).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
                    setBackground(holder.tvError);
                else
                    holder.tvError.setBackgroundResource(R.drawable.background_right);
            }

        }


        if(messages.get(holder.getAdapterPosition()).getDownloaded()==200) // when sender sends gif
        {
            String message = messages.get(holder.getAdapterPosition()).getMessage();
//            Glide.with(context).load(message.substring(0,message.lastIndexOf(" "))).into(holder.ivImage);

            holder.ivImage.setImageURI(Uri.parse(message.substring(0,message.lastIndexOf(" "))));

            holder.progress.setVisibility(View.VISIBLE);
            holder.ivGIF.setVisibility(View.GONE);
            holder.ivImage.setBackgroundResource(R.drawable.orange2);
            holder.tvError.setVisibility(View.GONE);
            holder.ivSeen.setVisibility(View.GONE);

            Activity.sendGIF(holder.getAdapterPosition());
        }

        else if(messages.get(holder.getAdapterPosition()).getDownloaded()==201) // when request has been sent to upload gif
        {
            String message = messages.get(holder.getAdapterPosition()).getMessage();
//            Glide.with(context).load(message.substring(0,message.lastIndexOf(" "))).into(holder.ivImage);

            holder.ivImage.setImageURI(Uri.parse(message.substring(0,message.lastIndexOf(" "))));

            holder.progress.setVisibility(View.VISIBLE);
            holder.ivImage.setBackgroundResource(R.drawable.orange2);
            holder.ivGIF.setVisibility(View.GONE);
            holder.tvError.setVisibility(View.GONE);
            holder.ivSeen.setVisibility(View.GONE);
        }

        else if(messages.get(holder.getAdapterPosition()).getDownloaded()==202) // when gif has been sent or downloaded successfully
        {
            String message = messages.get(holder.getAdapterPosition()).getMessage();
            holder.progress.setVisibility(View.GONE);

            if(holder.llDownload!=null)
                holder.llDownload.setVisibility(View.GONE);

            if (messages.get(holder.getAdapterPosition()).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                holder.ivImage.setBackgroundResource(R.drawable.background_right);
                holder.ivSeen.setVisibility(View.GONE);
            }

            else {
                setBackground(holder.ivImage);
                setBackground(holder.ivGIF);
            }

            RequestOptions options = new RequestOptions();
            options.diskCacheStrategy(DiskCacheStrategy.NONE);
            options.skipMemoryCache(true);

            if(!message.equals("null")) {
//                Glide.with(context).load(messages.get(holder.getAdapterPosition()).getMessage()).apply(options).addListener(new RequestListener<Drawable>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//
//                        holder.ivGIF.setVisibility(View.GONE);
//                        holder.tvError.setVisibility(View.VISIBLE);
//
//                        if(holder.getAdapterPosition()!=-1)
//                            if(!messages.get(holder.getAdapterPosition()).getMessage().equals("null"))
//                                Activity.OnFileDeleted(holder.getAdapterPosition());
//
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        holder.ivGIF.setVisibility(View.VISIBLE);
//                        holder.tvError.setVisibility(View.GONE);
//
//                        return false;
//                    }
//                }).into(holder.ivImage);

                holder.ivImage.setImageURI(Uri.parse(message.substring(0,message.lastIndexOf(" "))));
                if(holder.ivImage.getDrawable()==null)
                {
                    holder.ivImage.setImageURI(null);
                    holder.ivGIF.setVisibility(View.GONE);
                        holder.tvError.setVisibility(View.VISIBLE);

                        if(holder.getAdapterPosition()!=-1)
                            if(!messages.get(holder.getAdapterPosition()).getMessage().equals("null"))
                                Activity.OnFileDeleted(holder.getAdapterPosition());
                }
                else
                {
                    holder.ivGIF.setVisibility(View.VISIBLE);
                    holder.tvError.setVisibility(View.GONE);
                }

            }
            else
            {
                holder.ivImage.setImageURI(null);
                holder.ivGIF.setVisibility(View.GONE);
                holder.tvError.setVisibility(View.VISIBLE);
            }

        }

        else if(messages.get(holder.getAdapterPosition()).getDownloaded()==203) // when gif is received and yet to be downloaded
        {
                holder.ivImage.setImageResource(0);
                setBackground(holder.ivImage);
                holder.progress.setVisibility(View.GONE);
                holder.ivGIF.setVisibility(View.GONE);
                holder.llDownload.setVisibility(View.VISIBLE);
                holder.tvError.setVisibility(View.GONE);

                holder.llDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.progress.setVisibility(View.VISIBLE);
                        holder.llDownload.setVisibility(View.GONE);
                        Activity.downloadGIF(holder.getAdapterPosition());
                    }
                });
        }

        else if(messages.get(holder.getAdapterPosition()).getDownloaded()==204) // when request has been sent to download gif
        {
            holder.progress.setVisibility(View.VISIBLE);
            holder.llDownload.setVisibility(View.GONE);
            holder.ivGIF.setVisibility(View.GONE);
            holder.ivImage.setImageResource(0);
            setBackground(holder.ivImage);
            holder.tvError.setVisibility(View.GONE);
        }

        else if(messages.get(holder.getAdapterPosition()).getDownloaded()==205  || messages.get(holder.getAdapterPosition()).getDownloaded()==206) //when gif has been "seen"
        {
            String message = messages.get(holder.getAdapterPosition()).getMessage();
            holder.progress.setVisibility(View.GONE);

            if(holder.llDownload!=null)
                holder.llDownload.setVisibility(View.GONE);

            holder.ivImage.setBackgroundResource(R.drawable.background_right);
            holder.ivSeen.setVisibility(View.VISIBLE);

            if(messages.get(holder.getAdapterPosition()).getDownloaded()==206)
                holder.ivSeen.setColorFilter(context.getResources().getColor(R.color.red));
            else
                holder.ivSeen.setColorFilter(context.getResources().getColor(R.color.white));

            RequestOptions options = new RequestOptions();
            options.diskCacheStrategy(DiskCacheStrategy.NONE);
            options.skipMemoryCache(true);

            if(!message.equals("null")) {

                holder.ivImage.setImageURI(Uri.parse(message.substring(0,message.lastIndexOf(" "))));
                if(holder.ivImage.getDrawable()==null)
                {
                    holder.ivImage.setImageURI(null);
                    holder.ivGIF.setVisibility(View.GONE);
                    holder.tvError.setVisibility(View.VISIBLE);

                    if(holder.getAdapterPosition()!=-1)
                        if(!messages.get(holder.getAdapterPosition()).getMessage().equals("null"))
                            Activity.OnFileDeleted(holder.getAdapterPosition());
                }
                else
                {
                    holder.ivGIF.setVisibility(View.VISIBLE);
                    holder.tvError.setVisibility(View.GONE);
                }

            }
            else
            {
                holder.ivImage.setImageURI(null);
                holder.ivGIF.setVisibility(View.GONE);
                holder.tvError.setVisibility(View.VISIBLE);
            }


        }



        else if (messages.get(holder.getAdapterPosition()).getDownloaded() == 0)   //image is received but yet to be downloaded
        {
            holder.progress.setVisibility(View.GONE);
            holder.ivImage.setImageResource(0);
            setBackground(holder.ivImage);
            holder.tvError.setVisibility(View.GONE);
            holder.llDownload.setVisibility(View.VISIBLE);

            holder.llDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.progress.setVisibility(View.VISIBLE);
                    holder.llDownload.setVisibility(View.GONE);
                    Activity.downloadImage(holder.getAdapterPosition());
                }
            });

            holder.ivImage.setClickable(false);

        } else if (messages.get(holder.getAdapterPosition()).getDownloaded() == 1) // image is sent or downloaded successfully
        {
            holder.progress.setVisibility(View.GONE);
            holder.ivImage.setClickable(false);

            holder.tvError.setVisibility(View.GONE);

            if(holder.llDownload!=null)
                holder.llDownload.setVisibility(View.GONE);

            RequestOptions options = new RequestOptions();
            options.diskCacheStrategy(DiskCacheStrategy.NONE);
            options.skipMemoryCache(true);

            if(!messages.get(holder.getAdapterPosition()).getMessage().equals("null")) {
                Glide.with(context).load(messages.get(holder.getAdapterPosition()).getMessage()).apply(options).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.ivImage.setClickable(false);
                        holder.ivImage.setImageURI(null);
                        holder.tvError.setVisibility(View.VISIBLE);

                        if(holder.getAdapterPosition()!=-1)
                            if(!messages.get(holder.getAdapterPosition()).getMessage().equals("null"))
                                Activity.OnFileDeleted(holder.getAdapterPosition());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(holder.ivImage);
            }
            else
            {
                holder.ivImage.setImageURI(null);
                holder.tvError.setVisibility(View.VISIBLE);
            }


                if (messages.get(holder.getAdapterPosition()).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                    holder.ivImage.setBackgroundResource(R.drawable.background_right);
                    holder.ivSeen.setVisibility(View.GONE);
                }
                else
                    setBackground(holder.ivImage);

        }else if(messages.get(holder.getAdapterPosition()).getDownloaded()==5 || messages.get(holder.getAdapterPosition()).getDownloaded()==6) // when image bas been "seen"
        {
            holder.progress.setVisibility(View.GONE);
            holder.ivImage.setClickable(false);

            holder.tvError.setVisibility(View.GONE);

            if(holder.llDownload!=null)
                holder.llDownload.setVisibility(View.GONE);

            RequestOptions options = new RequestOptions();
            options.diskCacheStrategy(DiskCacheStrategy.NONE);
            options.skipMemoryCache(true);

            if(!messages.get(holder.getAdapterPosition()).getMessage().equals("null")) {
                Glide.with(context).load(messages.get(holder.getAdapterPosition()).getMessage()).apply(options).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.ivImage.setClickable(false);
                        holder.ivImage.setImageURI(null);
                        holder.tvError.setVisibility(View.VISIBLE);

                        if(holder.getAdapterPosition()!=-1)
                            if(!messages.get(holder.getAdapterPosition()).getMessage().equals("null"))
                                Activity.OnFileDeleted(holder.getAdapterPosition());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(holder.ivImage);
            }
            else
            {
                holder.ivImage.setImageURI(null);
                holder.tvError.setVisibility(View.VISIBLE);
            }
            holder.ivImage.setBackgroundResource(R.drawable.background_right);
            holder.ivSeen.setVisibility(View.VISIBLE);

            if(messages.get(holder.getAdapterPosition()).getDownloaded()==6){
                holder.ivSeen.setColorFilter(context.getResources().getColor(R.color.red));
            }
            else
                holder.ivSeen.setColorFilter(context.getResources().getColor(R.color.white));
        }
        else if (messages.get(holder.getAdapterPosition()).getDownloaded() == 2) // when sender sends the image
        {

            holder.progress.setVisibility(View.VISIBLE);
            holder.ivImage.setBackgroundResource(R.drawable.orange2);
            holder.tvError.setVisibility(View.GONE);
            holder.ivSeen.setVisibility(View.GONE);


            Glide.with(context.getApplicationContext()).load(messages.get(holder.getAdapterPosition()).getMessage()).into(holder.ivImage);
             Activity.sendImage(holder.getAdapterPosition());
        }
        else if (messages.get(holder.getAdapterPosition()).getDownloaded() == 3) // when request has been sent to upload image
        {

            holder.progress.setVisibility(View.VISIBLE);
            holder.ivImage.setBackgroundResource(R.drawable.orange2);
            holder.tvError.setVisibility(View.GONE);
            holder.ivSeen.setVisibility(View.GONE);

            Glide.with(context.getApplicationContext()).load(messages.get(holder.getAdapterPosition()).getMessage()).into(holder.ivImage);
        }else if(messages.get(holder.getAdapterPosition()).getDownloaded() == 4) // when request has been sent to download image
        {
            holder.progress.setVisibility(View.VISIBLE);
            holder.llDownload.setVisibility(View.GONE);
            holder.ivImage.setImageResource(0);
            setBackground(holder.ivImage);
            holder.ivImage.setClickable(false);
            holder.tvError.setVisibility(View.GONE);
        }
        else if (messages.get(holder.getAdapterPosition()).getDownloaded() == -2) // when text message is being sent
        {
            if(checkUrl(messages.get(holder.getAdapterPosition()).getMessage())) {
                holder.tvMessage.setTextColor(context.getResources().getColor(R.color.red));
                holder.tvMessage.setTypeface(holder.tvMessage.getTypeface(), Typeface.BOLD_ITALIC);
            }
            else {
                holder.tvMessage.setTextColor(Color.WHITE);
                holder.tvMessage.setTypeface(holder.tvMessage.getTypeface(), Typeface.NORMAL);
            }

            holder.tvMessage.setText(messages.get(holder.getAdapterPosition()).getMessage());
            holder.llMessageRight.setBackgroundResource(R.drawable.orange2);
            holder.ivSeen.setVisibility(View.GONE);
            Activity.sentTextMessage(holder.getAdapterPosition());
        } else if (messages.get(holder.getAdapterPosition()).getDownloaded() == -3) // when request has been sent to listener
        {

            if(checkUrl(messages.get(holder.getAdapterPosition()).getMessage())) {
                holder.tvMessage.setTextColor(context.getResources().getColor(R.color.red));
                holder.tvMessage.setTypeface(holder.tvMessage.getTypeface(), Typeface.BOLD_ITALIC);
            }
            else {
                holder.tvMessage.setTextColor(Color.WHITE);
                holder.tvMessage.setTypeface(holder.tvMessage.getTypeface(), Typeface.NORMAL);
            }

            holder.tvMessage.setText(messages.get(holder.getAdapterPosition()).getMessage());

            if (holder.llMessageRight != null) {
                holder.llMessageRight.setBackgroundResource(R.drawable.orange2);
                holder.ivSeen.setVisibility(View.GONE);
            }
        } else if (messages.get(holder.getAdapterPosition()).getDownloaded() == -1) // if text message is sent or received successfully
        {

            if(checkUrl(messages.get(holder.getAdapterPosition()).getMessage())) {
                holder.tvMessage.setTextColor(context.getResources().getColor(R.color.red));
                holder.tvMessage.setTypeface(holder.tvMessage.getTypeface(), Typeface.BOLD_ITALIC);
            }
            else {
                holder.tvMessage.setTextColor(Color.WHITE);
                holder.tvMessage.setTypeface(holder.tvMessage.getTypeface(), Typeface.NORMAL);
            }

            holder.tvMessage.setText(messages.get(holder.getAdapterPosition()).getMessage());

            if (holder.llMessageRight != null) {
                holder.llMessageRight.setBackgroundResource(R.drawable.background_right);
                holder.ivSeen.setVisibility(View.GONE);
            }

            if(holder.llMesageLeft!=null)
                setBackground(holder.llMesageLeft);
        }
        else if(messages.get(holder.getAdapterPosition()).getDownloaded()==-4 || messages.get(holder.getAdapterPosition()).getDownloaded()==-5)  //when text message has been "seen"
        {

            if(checkUrl(messages.get(holder.getAdapterPosition()).getMessage())) {
                holder.tvMessage.setTextColor(context.getResources().getColor(R.color.red));
                holder.tvMessage.setTypeface(holder.tvMessage.getTypeface(), Typeface.BOLD_ITALIC);
            }
            else {
                holder.tvMessage.setTextColor(Color.WHITE);
                holder.tvMessage.setTypeface(holder.tvMessage.getTypeface(), Typeface.NORMAL);
            }

            holder.tvMessage.setText(messages.get(holder.getAdapterPosition()).getMessage());
            holder.llMessageRight.setBackgroundResource(R.drawable.background_right);
            holder.ivSeen.setVisibility(View.VISIBLE);

            if(messages.get(holder.getAdapterPosition()).getDownloaded()==-5)
                holder.ivSeen.setColorFilter(context.getResources().getColor(R.color.red));
            else
                holder.ivSeen.setColorFilter(context.getResources().getColor(R.color.white));
        }
        else if(messages.get(holder.getAdapterPosition()).getDownloaded()==100) // when sender sends video
        {
            Glide.with(context).load(messages.get(holder.getAdapterPosition()).getMessage()).into(holder.ivImage);
            holder.progress.setVisibility(View.VISIBLE);
            holder.ivPlay.setVisibility(View.GONE);
            holder.ivImage.setBackgroundResource(R.drawable.orange2);
            holder.tvError.setVisibility(View.GONE);
            holder.ivSeen.setVisibility(View.GONE);

            holder.ivImage.setClickable(false);

            Activity.SendVideo(holder.getAdapterPosition());
        }
        else if(messages.get(holder.getAdapterPosition()).getDownloaded()==103) // when request has been sent to upload video
        {
            Glide.with(context).load(messages.get(holder.getAdapterPosition()).getMessage()).into(holder.ivImage);
            holder.progress.setVisibility(View.VISIBLE);
            holder.ivImage.setBackgroundResource(R.drawable.orange2);
            holder.ivPlay.setVisibility(View.GONE);
            holder.tvError.setVisibility(View.GONE);
            holder.ivSeen.setVisibility(View.GONE);

            holder.ivImage.setClickable(false);
        }
        else if(messages.get(holder.getAdapterPosition()).getDownloaded()==101) // when video is received  and yet to be downloaded
        {
            holder.ivImage.setImageResource(0);
            setBackground(holder.ivImage);
            holder.progress.setVisibility(View.GONE);
            holder.ivPlay.setVisibility(View.GONE);
            holder.llDownload.setVisibility(View.VISIBLE);
            holder.tvError.setVisibility(View.GONE);

            holder.llDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.progress.setVisibility(View.VISIBLE);
                    holder.llDownload.setVisibility(View.GONE);
                    Activity.Downloadvideo(holder.getAdapterPosition());
                }
            });

            holder.ivImage.setClickable(false);
        }
        else if(messages.get(holder.getAdapterPosition()).getDownloaded()==102) // when video is sent or downloaded successfully
        {
            holder.progress.setVisibility(View.GONE);
            holder.ivImage.setClickable(false);

            if(holder.llDownload!=null)
                holder.llDownload.setVisibility(View.GONE);

            if (messages.get(holder.getAdapterPosition()).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                holder.ivImage.setBackgroundResource(R.drawable.background_right);
                holder.ivSeen.setVisibility(View.GONE);
            }

            else {
                setBackground(holder.ivImage);
                setBackground(holder.ivPlay);
            }

            RequestOptions options = new RequestOptions();
            options.diskCacheStrategy(DiskCacheStrategy.NONE);
            options.skipMemoryCache(true);

            if(!messages.get(holder.getAdapterPosition()).getMessage().equals("null")) {
                Glide.with(context).load(messages.get(holder.getAdapterPosition()).getMessage()).apply(options).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        holder.ivImage.setImageURI(null);
                        holder.ivPlay.setVisibility(View.GONE);
                        holder.tvError.setVisibility(View.VISIBLE);

                        if(holder.getAdapterPosition()!=-1)
                            if(!messages.get(holder.getAdapterPosition()).getMessage().equals("null"))
                            Activity.OnFileDeleted(holder.getAdapterPosition());

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.ivPlay.setVisibility(View.VISIBLE);
                        holder.tvError.setVisibility(View.GONE);

                        holder.ivPlay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Activity.showVideo(holder.getAdapterPosition());
                            }
                        });
                        return false;
                    }
                }).into(holder.ivImage);
            }
            else
            {
                holder.ivImage.setImageURI(null);
                holder.ivPlay.setVisibility(View.GONE);
                holder.tvError.setVisibility(View.VISIBLE);
            }


        }
        else if(messages.get(holder.getAdapterPosition()).getDownloaded()==105  || messages.get(holder.getAdapterPosition()).getDownloaded()==106) //when video has been "seen"
        {
            holder.progress.setVisibility(View.GONE);
            holder.ivImage.setClickable(false);

            if(holder.llDownload!=null)
                holder.llDownload.setVisibility(View.GONE);

                holder.ivImage.setBackgroundResource(R.drawable.background_right);
                holder.ivSeen.setVisibility(View.VISIBLE);

                if(messages.get(holder.getAdapterPosition()).getDownloaded()==106)
                    holder.ivSeen.setColorFilter(context.getResources().getColor(R.color.red));
                else
                    holder.ivSeen.setColorFilter(context.getResources().getColor(R.color.white));

            RequestOptions options = new RequestOptions();
            options.diskCacheStrategy(DiskCacheStrategy.NONE);
            options.skipMemoryCache(true);

            if(!messages.get(holder.getAdapterPosition()).getMessage().equals("null")) {
                Glide.with(context).load(messages.get(holder.getAdapterPosition()).getMessage()).apply(options).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        holder.ivImage.setImageURI(null);
                        holder.ivPlay.setVisibility(View.GONE);
                        holder.tvError.setVisibility(View.VISIBLE);

                        if(holder.getAdapterPosition()!=-1)
                            if(!messages.get(holder.getAdapterPosition()).getMessage().equals("null"))
                                Activity.OnFileDeleted(holder.getAdapterPosition());

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.ivPlay.setVisibility(View.VISIBLE);
                        holder.tvError.setVisibility(View.GONE);

                        holder.ivPlay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Activity.showVideo(holder.getAdapterPosition());
                            }
                        });
                        return false;
                    }
                }).into(holder.ivImage);
            }
            else
            {
                holder.ivImage.setImageURI(null);
                holder.ivPlay.setVisibility(View.GONE);
                holder.tvError.setVisibility(View.VISIBLE);
            }


        }
        else if(messages.get(holder.getAdapterPosition()).getDownloaded() == 104) // when request has been sent to download video
        {
            holder.progress.setVisibility(View.VISIBLE);
            holder.llDownload.setVisibility(View.GONE);
            holder.ivPlay.setVisibility(View.GONE);
            holder.ivImage.setImageResource(0);
            setBackground(holder.ivImage);
            holder.ivImage.setClickable(false);
            holder.tvError.setVisibility(View.GONE);
        }

        if(holder.ivImage!=null)
            holder.ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Activity.showImage(holder.getAdapterPosition());
                }
            });


        if (holder.tvTime != null) {
            holder.tvTime.setText(messages.get(holder.getAdapterPosition()).getTime().substring(0, 5));
            if(messages.get(holder.getAdapterPosition()).getType().equals("sticker")){
                if(!messages.get(holder.getAdapterPosition()).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
                    setBackground(holder.tvTime);
                else
                    holder.tvTime.setBackgroundResource(R.drawable.background_right);
            }
            else
            {
                holder.tvTime.setBackground(null);
            }
        }

        if (holder.tvDate != null) {

            long millis = System.currentTimeMillis();
            java.sql.Date date = new java.sql.Date(millis);

            if(messages.get(holder.getAdapterPosition()).getDate().equals(date.toString()))
                holder.tvDate.setText("Today");
            else
                holder.tvDate.setText(newDate(messages.get(holder.getAdapterPosition()).getDate()));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {

        user = FirebaseAuth.getInstance().getCurrentUser();

        if(messages.get(position).getType().equals("grpinfo"))
        {
            return MSG_GRP_INFO;
        }

        if(messages.get(position).getType().equals("unread"))
        {
            return UNREAD;
        }

        if(messages.get(position).getType().equals("typing"))
        {
            return TYPING;
        }

        if(messages.get(position).getType().equals("Date"))
        {
            return DATE;
        }

        if(messages.get(position).getSender().equals(user.getPhoneNumber())) {
            switch (messages.get(position).getType()) {
                case "image":
                    return MSG_IMG_RIGHT;
                case "text":
                    return MSG_TXT_RIGHT;
                case "video":
                    return MSG_VIDEO_RIGHT;
                case "gif":
                    return GIF_RIGHT;
                case "sticker":
                    return STICK_RIGHT;
            }
        }

        if(messages.get(position).getGroupKey().equals("null")) {
            if (!messages.get(position).getSender().equals(user.getPhoneNumber()) && !messages.get(position).getSender().equals("null")) {
                switch (messages.get(position).getType()) {
                    case "image":
                        return MSG_IMG_LEFT;
                    case "text":
                        return MSG_TXT_LEFT;
                    case "video":
                        return MSG_VIDEO_LEFT;
                    case "gif":
                        return GIF_LEFT;
                    case "sticker":
                        return STICK_LEFT;
                }
            }
        }
        else
        {
            if (!messages.get(position).getSender().equals(user.getPhoneNumber()) && !messages.get(position).getSender().equals("null")) {
                switch (messages.get(position).getType()) {
                    case "image":
                        return GRP_IMAGE_LEFT;
                    case "text":
                        return GRP_MSG_LEFT;
                    case "video":
                        return GRP_VIDEO_LEFT;
                    case "gif":
                        return GIF_LEFT_GRP;
                    case "sticker":
                        return STICK_LEFT_GRP;
                }
            }
        }
        return -1;
    }

    @Override
    public long getItemId(int position) {

        return messages.get(position).getId();
    }

    public String newDate(String date)
    {
        String newDate;

        newDate = date.substring(8,10);

        switch (date.substring(5,7))
        {
            case "01":
                newDate = newDate+" January ";
                break;

            case "02":
                newDate = newDate+" February ";
                break;

            case "03":
                newDate = newDate+" March ";
                break;

            case "04":
                newDate = newDate+" April ";
                break;

            case "05":
                newDate = newDate+" May ";
                break;

            case "06":
                newDate = newDate+" June ";
                break;

            case "07":
                newDate = newDate+" July ";
                break;

            case "08":
                newDate = newDate+" August ";
                break;

            case "09":
                newDate = newDate+" September ";
                break;

            case "10":
                newDate = newDate+" October ";
                break;

            case "11":
                newDate = newDate+" November ";
                break;

            case "12":
                newDate = newDate+" December ";
                break;
        }

        newDate = newDate+date.substring(0,4);

        return newDate;
    }

    public void setBackground(View view)
    {
        String theme=preftheme.getString("theme","red");

        switch (theme)
        {
            case "orange":

                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#d6514a")));

                break;

            case "blue":

                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#283470")));

                break;


            case "bluish":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#292f3b")));
                break;


            case "deepred":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#e24a3c")));
                break;

            case "faintpink":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#f25c65")));

                break;

            case "darkblue":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#2b3050")));
                break;


            case "green":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#6ebd52")));
                break;

            case "lightorange":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#f2a37a")));

                break;

            case "lightred":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#e9776c")));
                break;


            case "mustard":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#eba54d")));
                break;

            case "pink":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#e91e63")));
                break;

            case "pureorange":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#ff5722")));
                break;

            case "purepink":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#f57268")));
                break;

            case "purple":
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#49264e")));
                break;

            default:
                ViewCompat.setBackgroundTintList(view,ColorStateList.valueOf(Color.parseColor("#d6514a")));
        }
    }

    public Boolean checkUrl(String url)
    {
        try {
            new URL(url).toURI();
            return true;
        }

        // If there was an Exception
        // while creating URL object
        catch (Exception e) {
        }

        return false;
    }

}
