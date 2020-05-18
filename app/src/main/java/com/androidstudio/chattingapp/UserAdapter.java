package com.androidstudio.chattingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
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

            holder.tvMessageNum.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.Orange)));


            break;

            case "blue":

                holder.tvMessageNum.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.blue)));

            break;


            case "bluish":
                holder.tvMessageNum.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.bluish)));
            break;


        case "deepred":
            holder.tvMessageNum.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.deepred)));
        break;

        case "faintpink":
            holder.tvMessageNum.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.faintpink)));

        break;

            case "darkblue":
                holder.tvMessageNum.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.darkblue)));
        break;


        case "green":
            holder.tvMessageNum.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green)));
        break;

        case "lightorange":
            holder.tvMessageNum.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.lightorange)));
        break;

        case "lightred":
            holder.tvMessageNum.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.lightred)));
        break;


        case "mustard":
            holder.tvMessageNum.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.mustard)));
        break;

        case "pink":
            holder.tvMessageNum.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.pink)));
        break;

        case "pureorange":
            holder.tvMessageNum.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.pureorange)));
        break;

            case "purepink":
                holder.tvMessageNum.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.purepink)));
        break;

        case "purple":
            holder.tvMessageNum.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.purple)));
        break;

        default:
            holder.tvMessageNum.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.red)));

        }

        database=FirebaseDatabase.getInstance();

        if(!(list.get(position).getTime().equals("null")))
        {
            if(list.get(holder.getAdapterPosition()).getGroupname()!=null)
                holder.time.setText(list.get(holder.getAdapterPosition()).getTime().substring(0,5));
            else
                holder.time.setText(list.get(holder.getAdapterPosition()).getTime());
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
            holder.ivImage.setVisibility(View.GONE);
            holder.tvlastmessage.setVisibility(View.GONE);

        }
       // Log.d("asdf",list.get(position).getUrl());

        if(list.get(position).getUrl().equals("null")) {

            if(list.get(position).getGroupname()==null) {
                holder.iv.setImageResource(R.drawable.person);

//
                holder.iv.setClickable(false);

            }
            else
                holder.iv.setImageResource(R.drawable.group);

        }

        else
        {
            holder.iv.setColorFilter(context.getResources().getColor(R.color.iOrange));



            Glide.with(context).load(list.get(holder.getAdapterPosition()).getUrl()).into(holder.iv);
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
                holder.iv.setBackgroundResource(R.drawable.orange);
            else
                holder.iv.setBackgroundResource(R.drawable.white);        }

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
            holder.iv.setBackgroundResource(R.drawable.white);

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

       final ImageView ivStatus,ivImage;
       ImageView iv;
        TextView tvMessageNum,tvUserName,time;
        EmojiconTextView tvlastmessage;
        ConstraintLayout innerConstraintLayout;


        public viewholder(@NonNull View itemView) {
            super(itemView);

            time=itemView.findViewById(R.id.time);
            iv=itemView.findViewById(R.id.imageView);

            ivStatus = itemView.findViewById(R.id.ivStatus);
            ivImage = itemView.findViewById(R.id.ivImage);
          //  ivBackground = itemView.findViewById(R.id.ivBackground);
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


