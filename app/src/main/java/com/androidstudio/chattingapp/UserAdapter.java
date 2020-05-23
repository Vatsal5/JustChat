package com.androidstudio.chattingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewholder> implements Filterable {

     Context context;
    itemSelected Activity;
    FirebaseDatabase database;
    String defaultvalue;

     ArrayList<UserDetailwithUrl> listFiltered;
     ArrayList<UserDetailwithUrl> list;

     SharedPreferences mode;

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

        holder.innerConstraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(listFiltered.get(holder.getAdapterPosition()).getGroupname()!=null)
                    Activity.onLongclick(listFiltered.get(holder.getAdapterPosition()).getGroupkey());
                return false;
            }
        });

        database=FirebaseDatabase.getInstance();

        if(listFiltered.get(holder.getAdapterPosition()).getGroupname()==null)
            defaultvalue = mode.getString("mode" + listFiltered.get(holder.getAdapterPosition()).getPh_number(), "null");
        else
            defaultvalue = mode.getString("mode" + listFiltered.get(holder.getAdapterPosition()).getGroupkey(), "null");

        if(!defaultvalue.equals("private") && !defaultvalue.equals("null")) {
            if (!(listFiltered.get(position).getTime().equals("null"))) {
                if (listFiltered.get(holder.getAdapterPosition()).getGroupname() != null)
                    holder.time.setText(listFiltered.get(holder.getAdapterPosition()).getTime().substring(0, 5));
                else
                    holder.time.setText(listFiltered.get(holder.getAdapterPosition()).getTime());
            } else {
                holder.time.setText("");
            }
        }
        else
        {
            holder.time.setText("");
        }


        if(!defaultvalue.equals("private") && !defaultvalue.equals("null")) {
            if (!(listFiltered.get(position).getLastmessage().equals(" ")) && !(listFiltered.get(position).getLastmessage().equals("null")) && !(listFiltered.get(position).getLastmessage().equals("  ")) && !(listFiltered.get(position).getLastmessage().equals("   "))
                    && !(listFiltered.get(position).getLastmessage().equals("    "))) {
                holder.tvlastmessage.setVisibility(View.VISIBLE);

                if (listFiltered.get(position).getLastmessage().length() > 20) {
                    holder.tvlastmessage.setText(listFiltered.get(position).getLastmessage().substring(0, 20) + "..");
                } else {
                    holder.tvlastmessage.setText(listFiltered.get(position).getLastmessage());
                }
                holder.ivImage.setVisibility(View.GONE);
            } else if (listFiltered.get(position).getLastmessage().equals(" ")) {
                holder.ivImage.setVisibility(View.VISIBLE);
                holder.tvlastmessage.setVisibility(View.VISIBLE);
                holder.ivImage.setImageResource(R.drawable.image);

                holder.tvlastmessage.setText("Image");

            } else if (listFiltered.get(position).getLastmessage().equals("   ")) {
                holder.ivImage.setVisibility(View.VISIBLE);
                holder.tvlastmessage.setVisibility(View.VISIBLE);
                holder.ivImage.setImageResource(R.drawable.gif);

                holder.tvlastmessage.setText("GIF");

            } else if (listFiltered.get(position).getLastmessage().equals("    ")) {
                holder.ivImage.setVisibility(View.VISIBLE);
                holder.tvlastmessage.setVisibility(View.VISIBLE);
                holder.ivImage.setImageResource(R.drawable.gif);

                holder.tvlastmessage.setText("Sticker");

            }else if (listFiltered.get(position).getLastmessage().equals("     ")) {
                holder.ivImage.setVisibility(View.VISIBLE);
                holder.tvlastmessage.setVisibility(View.VISIBLE);
                holder.ivImage.setImageResource(R.drawable.pdf);

                holder.tvlastmessage.setText("PDF");

            } else if (listFiltered.get(position).getLastmessage().equals("  ")) {
                holder.tvlastmessage.setVisibility(View.VISIBLE);
                holder.ivImage.setImageResource(R.drawable.video);
                holder.tvlastmessage.setText("Video");
                holder.ivImage.setVisibility(View.VISIBLE);

            }else{
                holder.ivImage.setVisibility(View.GONE);
                holder.tvlastmessage.setVisibility(View.GONE);
            }
        }
        else
        {
            holder.ivImage.setVisibility(View.GONE);
            holder.tvlastmessage.setVisibility(View.GONE);
        }
       // Log.d("asdf",listFiltered.get(position).getUrl());

        if(listFiltered.get(position).getUrl().equals("null")) {

            if(listFiltered.get(position).getGroupname()==null) {
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



            Glide.with(context).load(listFiltered.get(holder.getAdapterPosition()).getUrl()).into(holder.iv);
            holder.iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(listFiltered.get(holder.getAdapterPosition()).getGroupname()==null)
                        Activity.onImageSelected(listFiltered.get(holder.getAdapterPosition()).getPh_number());
                    else
                        Activity.onImageSelected(listFiltered.get(holder.getAdapterPosition()).getGroupkey());
                }
            });
        }

//        if(listFiltered.get(position).getGroupname()==null) {
//
//            dbreference.child("UserStatus").child(Check(listFiltered.get(position).getPh_number())).addValueEventListener(new ValueEventListener() {
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

        if(listFiltered.get(position).getStatus()!=null)
        {
            if(listFiltered.get(position).getStatus().equals("online"))
                holder.ivBackground.setBackgroundResource(R.drawable.orange);
            else
                holder.ivBackground.setBackgroundResource(R.drawable.white);        }

        if(listFiltered.get(position).getuID().equals(""))
        {
            holder.tvUserName.setText(listFiltered.get(position).getPh_number());

        }
        else{
            holder.tvUserName.setText(listFiltered.get(position).getuID());

        }

        if(listFiltered.get(position).getGroupname()==null)
        {
            if(listFiltered.get(position).getMessagenum() > 2)
            {
                holder.tvMessageNum.setText(listFiltered.get(position).getMessagenum()-2+"");
                holder.tvMessageNum.setVisibility(View.VISIBLE);
            }
            else{
                // holder.tvMessageNum.setText(listFiltered.get(position).getMessagenum()-2+"");
                holder.tvMessageNum.setVisibility(View.GONE);
            }
        }
        else {
            holder.ivBackground.setBackgroundResource(R.drawable.white);

        if (listFiltered.get(position).getMessagenum() > 2) {
                holder.tvMessageNum.setText(listFiltered.get(position).getMessagenum()-2 + "");
                holder.tvMessageNum.setVisibility(View.VISIBLE);
            } else {
                // holder.tvMessageNum.setText(listFiltered.get(position).getMessagenum()-2+"");
                holder.tvMessageNum.setVisibility(View.GONE);
            }
        }

        holder.innerConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listFiltered.get(holder.getAdapterPosition()).getGroupname()==null)
                    Activity.onItemSelected(listFiltered.get(holder.getAdapterPosition()).getPh_number());
                else
                    Activity.onItemSelected(listFiltered.get(holder.getAdapterPosition()).getGroupkey());
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return listFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();

                ArrayList<UserDetailwithUrl> filtered = new ArrayList<>();

                if (query.isEmpty()) {
                    filtered = list;
                } else {
                    for (UserDetailwithUrl user : list) {
                        if (user.getuID().toLowerCase().contains(query.toLowerCase()) ||(user.getGroupname()==null && user.getPh_number().contains(query))) {
                            filtered.add(user);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.count = filtered.size();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                listFiltered = (ArrayList<UserDetailwithUrl>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface itemSelected
    {
        public void onItemSelected(String key);
        public void onImageSelected(String key);
        public void onLongclick(String key);
    }

    public UserAdapter(@NonNull Context context, ArrayList<UserDetailwithUrl>list) {


        this.context = context;
        this.listFiltered = list;
        this.list = list;
        Activity = (itemSelected) context;

        mode = context.getSharedPreferences("Mode", 0);
    }
    public  class viewholder extends RecyclerView.ViewHolder
    {

       final ImageView ivStatus,ivImage;
       ImageView iv,ivBackground;
        TextView tvMessageNum,tvUserName,time;
        EmojiconTextView tvlastmessage;
        ConstraintLayout innerConstraintLayout;


        public viewholder(@NonNull View itemView) {
            super(itemView);

            time=itemView.findViewById(R.id.time);
            iv=itemView.findViewById(R.id.imageView);

            ivStatus = itemView.findViewById(R.id.ivStatus);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivBackground = itemView.findViewById(R.id.ivback);
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


