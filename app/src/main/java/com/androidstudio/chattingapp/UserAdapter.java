package com.androidstudio.chattingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewholder> {

     Context context;
    itemSelected Activity;
    FirebaseDatabase database;

     final ArrayList<UserDetailwithUrl> list;

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v= inflater.inflate(R.layout.chats_listlayout,parent,false);
        return  new viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, final int position) {
        SharedPreferences preftheme;
        preftheme=context.getSharedPreferences("theme",0);

        String theme=preftheme.getString("theme","red");

        switch (theme)
        { case "orange":

            holder.tvlastmessage.setTextColor(context.getResources().getColor(R.color.Orange));

            holder.time.setTextColor(context.getResources().getColor(R.color.Orange));
            break;

            case "blue":

              holder.tvlastmessage.setTextColor(context.getResources().getColor(R.color.blue));

                holder.time.setTextColor(context.getResources().getColor(R.color.blue));

            break;


            case "bluish":
                holder.tvlastmessage.setTextColor(context.getResources().getColor(R.color.bluish));

                holder.time.setTextColor(context.getResources().getColor(R.color.bluish));
            break;


        case "deepred":
            holder.tvlastmessage.setTextColor(context.getResources().getColor(R.color.deepred));

            holder.time.setTextColor(context.getResources().getColor(R.color.deepred));
        break;

        case "faintpink":
            holder.tvlastmessage.setTextColor(context.getResources().getColor(R.color.faintpink));

            holder.time.setTextColor(context.getResources().getColor(R.color.faintpink));

        break;

            case "darkblue":
                holder.tvlastmessage.setTextColor(context.getResources().getColor(R.color.darkblue));

                holder.time.setTextColor(context.getResources().getColor(R.color.darkblue));
        break;


        case "green":
            holder.tvlastmessage.setTextColor(context.getResources().getColor(R.color.green));

            holder.time.setTextColor(context.getResources().getColor(R.color.green));
        break;

        case "lightorange":
            holder.tvlastmessage.setTextColor(context.getResources().getColor(R.color.lightorange));

            holder.time.setTextColor(context.getResources().getColor(R.color.lightorange));
        break;

        case "lightred":
            holder.tvlastmessage.setTextColor(context.getResources().getColor(R.color.lightred));

            holder.time.setTextColor(context.getResources().getColor(R.color.lightred));
        break;


        case "mustard":
            holder.tvlastmessage.setTextColor(context.getResources().getColor(R.color.mustard));

            holder.time.setTextColor(context.getResources().getColor(R.color.mustard));
        break;

        case "pink":
            holder.tvlastmessage.setTextColor(context.getResources().getColor(R.color.pink));

            holder.time.setTextColor(context.getResources().getColor(R.color.pink));
        break;

        case "pureorange":
            holder.tvlastmessage.setTextColor(context.getResources().getColor(R.color.pureorange));

            holder.time.setTextColor(context.getResources().getColor(R.color.pureorange));
        break;

            case "purepink":
                holder.tvlastmessage.setTextColor(context.getResources().getColor(R.color.purepink));

                holder.time.setTextColor(context.getResources().getColor(R.color.purepink));
        break;

        case "purple":
            holder.tvlastmessage.setTextColor(context.getResources().getColor(R.color.purple));

            holder.time.setTextColor(context.getResources().getColor(R.color.purple));
        break;

        default:
            holder.tvlastmessage.setTextColor(context.getResources().getColor(R.color.red));

            holder.time.setTextColor(context.getResources().getColor(R.color.red));

        }

        database=FirebaseDatabase.getInstance();
        DatabaseReference dbreference=database.getReference();

        if(!(list.get(position).getTime().equals("null")))
        {
            holder.time.setText(list.get(position).getTime());
        }
        else
        {
            holder.time.setText("");
        }

        if(  !(list.get(position).getLastmessage().equals(" ")) &&!(list.get(position).getLastmessage().equals("null")) &&!(list.get(position).getLastmessage().equals("  ")) &&!(list.get(position).getLastmessage().equals("   "))
                &&!(list.get(position).getLastmessage().equals("    ")))
        {
            holder.tvlastmessage.setVisibility(View.VISIBLE);

            if(list.get(position).getLastmessage().length()>20) {
                holder.tvlastmessage.setText(list.get(position).getLastmessage().substring(0,20)+"..");
            }
            else
            {
                holder.tvlastmessage.setText(list.get(position).getLastmessage());
            }
            holder.ivImage.setVisibility(View.GONE);
        }
        else if(list.get(position).getLastmessage().equals(" "))
        {
            holder.ivImage.setVisibility(View.VISIBLE);
            holder.tvlastmessage.setVisibility(View.VISIBLE);
            holder.ivImage.setImageResource(R.drawable.image);

            holder.tvlastmessage.setText("Image");

        }

        else if(list.get(position).getLastmessage().equals("   "))
        {
            holder.ivImage.setVisibility(View.VISIBLE);
            holder.tvlastmessage.setVisibility(View.VISIBLE);
            holder.ivImage.setImageResource(R.drawable.gif);

            holder.tvlastmessage.setText("GIF");

        }
        else if(list.get(position).getLastmessage().equals("    "))
        {
            holder.ivImage.setVisibility(View.VISIBLE);
            holder.tvlastmessage.setVisibility(View.VISIBLE);
            holder.ivImage.setImageResource(R.drawable.gif);

            holder.tvlastmessage.setText("Sticker");

        }

        else if(list.get(position).getLastmessage().equals("  "))
        {
            holder.tvlastmessage.setVisibility(View.VISIBLE);
            holder.ivImage.setImageResource(R.drawable.video);
            holder.tvlastmessage.setText("Video");
            holder.ivImage.setVisibility(View.VISIBLE);

        }
        else{
          //  holder.tvlastmessage.setText("");
            holder.ivImage.setVisibility(View.GONE);
            holder.tvlastmessage.setVisibility(View.GONE);
            holder.tvUserName.setPadding(0,13,0,0);

        }
       // Log.d("asdf",list.get(position).getUrl());

        if(list.get(position).getUrl().equals("null")) {

            if(list.get(position).getGroupname()==null) {
                holder.iv.setImageResource(R.drawable.person);

//
                holder.iv.setClickable(false);

            }
            else
                holder.iv.setImageResource(R.drawable.group1);

        }

        else
        {
            holder.iv.setColorFilter(context.getResources().getColor(R.color.iOrange));
            Glide.with(context).load(list.get(position).getUrl()).into(holder.iv);
            holder.iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Activity.onImageSelected(holder.getAdapterPosition());
                }
            });
        }

//        if(list.get(position).getGroupname()==null) {
//
//            dbreference.child("UserStatus").child(Check(list.get(position).getPh_number())).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.getValue() != null) {
//                        if (dataSnapshot.getValue(String.class).equals("online") || dataSnapshot.getValue(String.class).substring(0, 6).equals("typing")) {
//                            holder.ivBackground.setBackgroundResource(R.drawable.orange);
//                        } else {
//                            holder.ivBackground.setBackground(null);
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }

        if(list.get(position).getStatus()!=null)
        {
            if(list.get(position).getStatus().equals("online"))
                holder.ivBackground.setBackgroundResource(R.drawable.orange);
            else
                holder.ivBackground.setBackground(null);
        }

        if(list.get(position).getuID().equals(""))
        {
            holder.tvUserName.setText(list.get(position).getPh_number());

        }
        else{
            holder.tvUserName.setText(list.get(position).getuID());

        }

        if(list.get(position).getGroupname()==null)
        {
            if(list.get(position).getMessagenum() > 2)
            {
                holder.tvMessageNum.setText(list.get(position).getMessagenum()-2+"");
                holder.tvMessageNum.setVisibility(View.VISIBLE);
            }
            else{
                // holder.tvMessageNum.setText(list.get(position).getMessagenum()-2+"");
                holder.tvMessageNum.setVisibility(View.GONE);
            }
        }
        else {
            if (list.get(position).getMessagenum() > 2) {
                holder.tvMessageNum.setText(list.get(position).getMessagenum()-2 + "");
                holder.tvMessageNum.setVisibility(View.VISIBLE);
            } else {
                // holder.tvMessageNum.setText(list.get(position).getMessagenum()-2+"");
                holder.tvMessageNum.setVisibility(View.GONE);
            }
        }

        holder.innerConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity.onItemSelected(holder.getAdapterPosition());
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface itemSelected
    {
        public void onItemSelected(int index);
        public void onImageSelected(int index);
    }

    public UserAdapter(@NonNull Context context, ArrayList<UserDetailwithUrl>list) {


        this.context = context;
        this.list = list;
        Activity = (itemSelected) context;
    }
    public  class viewholder extends RecyclerView.ViewHolder
    {

       final ImageView iv,ivStatus,ivImage,ivBackground;
        TextView tvMessageNum,tvUserName,time;
        EmojiconTextView tvlastmessage;
        ConstraintLayout innerConstraintLayout;


        public viewholder(@NonNull View itemView) {
            super(itemView);

            time=itemView.findViewById(R.id.time);
            iv=itemView.findViewById(R.id.imageView);

            ivStatus = itemView.findViewById(R.id.ivStatus);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivBackground = itemView.findViewById(R.id.ivBackground);
            tvlastmessage=itemView.findViewById(R.id.lastmessage);
            tvMessageNum = itemView.findViewById(R.id.tvMessageNum);
            tvUserName= itemView.findViewById(R.id.tv_username);
            innerConstraintLayout = itemView.findViewById(R.id.innerConstraintLayout);
        }
    }



    public String Check(String phone)
    {
        String newPhone;
//        if(!phone.substring(0,3).equals("+91"))
//        {
//            newPhone = "+91".concat(phone);
//            return newPhone;
//        }
//        else
            return phone;

    }

//    public static Drawable changeDrawableColor(Context context, int icon, int newColor) {
//        Drawable mDrawable = ContextCompat.getDrawable(context, icon).mutate();
//        mDrawable.setColorFilter(new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_IN));
//        return mDrawable;
//    }

    }


