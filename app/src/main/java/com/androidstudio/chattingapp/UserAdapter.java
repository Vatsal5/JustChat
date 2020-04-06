package com.androidstudio.chattingapp;

import android.content.Context;
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
import androidx.emoji.widget.EmojiTextView;
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

        database=FirebaseDatabase.getInstance();
        DatabaseReference dbreference=database.getReference();
        Log.d("tag",list.get(position).getUrl());

        if(!(list.get(position).getTime().equals("null")))
        {
            holder.time.setText(list.get(position).getTime());
        }
        else
        {
            holder.time.setText("");
        }

        if(!(list.get(position).getLastmessage().equals(" ")))
        {
            if(list.get(position).getLastmessage().length()>20) {
                holder.tvlastmessage.setText(list.get(position).getLastmessage().substring(0,20)+"..");
            }
            else
            {
                holder.tvlastmessage.setText(list.get(position).getLastmessage());
            }
            holder.ivImage.setVisibility(View.GONE);
        }
        if(list.get(position).getLastmessage().equals(" "))
        {
            holder.ivImage.setVisibility(View.VISIBLE);
            holder.tvlastmessage.setText("Image");
        }
        if(list.get(position).getLastmessage().equals("null"))
        {
            holder.ivImage.setVisibility(View.GONE);
            holder.tvlastmessage.setText(null);
        }

        if(list.get(position).getUrl().equals("null"))
        {
            holder.iv.setImageResource(R.drawable.person);
            holder.iv.setClickable(false);
        }
        else
        {
            Glide.with(context).load(list.get(position).getUrl()).into(holder.iv);
            holder.iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Activity.onImageSelected(position);
                }
            });
        }

        dbreference.child("UserStatus").child(Check(list.get(position).getPh_number())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    if (dataSnapshot.getValue(String.class).equals("online") || dataSnapshot.getValue(String.class).equals("typing")) {
                        holder.ivBackground.setBackgroundResource(R.drawable.orange);
                    }
                    else {
                        holder.ivBackground.setBackground(null);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(list.get(position).getuID().equals(""))
        {
            holder.tvUserName.setText(list.get(position).getPh_number());

        }
        else{
            holder.tvUserName.setText(list.get(position).getuID());

        }

        if(list.get(position).getMessagenum() > 2)
        {
            holder.tvMessageNum.setText(list.get(position).getMessagenum()-2+"");
            holder.tvMessageNum.setVisibility(View.VISIBLE);
        }
        else{
           // holder.tvMessageNum.setText(list.get(position).getMessagenum()-2+"");
            holder.tvMessageNum.setVisibility(View.GONE);
        }

        holder.innerConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity.onItemSelected(position);
            }
        });

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
        EmojiTextView tvlastmessage;
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
        if(!phone.substring(0,3).equals("+91"))
        {
            newPhone = "+91".concat(phone);
            return newPhone;
        }
        else
            return phone;

    }

}
