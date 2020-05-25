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
import com.google.firebase.auth.FirebaseAuth;
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

        long millis = System.currentTimeMillis();
        java.sql.Date date1 = new java.sql.Date(millis);

        String lastTime = listFiltered.get(holder.getAdapterPosition()).getTime();
        String lastTimesubstring;
        String lastdate;

        if(!lastTime.equals("null")) {
            lastTimesubstring = lastTime.substring(0, lastTime.lastIndexOf(" "));
            lastdate = lastTime.substring(lastTime.lastIndexOf(" ")+1);
        }
        else {
            lastTimesubstring = "null";
            lastdate = "null";
        }


        if(!defaultvalue.equals("private") && !defaultvalue.equals("null")) {
            if (!(lastTimesubstring.equals("null"))) {
                if(lastdate.equals(date1.toString())) {
                    if (listFiltered.get(holder.getAdapterPosition()).getGroupname() != null)
                        holder.time.setText(lastTimesubstring.substring(0, 5));
                    else
                        holder.time.setText(lastTimesubstring);
                }
                else{
                    holder.time.setText(newDate(lastdate));
                }
            } else {
                holder.time.setText("");
            }
        }
        else
        {
            holder.time.setText("");
        }


        String lastmessage = listFiltered.get(holder.getAdapterPosition()).getLastmessage();
        String lastmessagesubstring;
        String sender;

        if(!lastmessage.equals("null")) {
            lastmessagesubstring = lastmessage.substring(0, lastmessage.lastIndexOf(" "));
            sender = lastmessage.substring(lastmessage.lastIndexOf(" ")+1);
        }
        else {
            lastmessagesubstring = "null";
            sender = "null";
        }


        if(!defaultvalue.equals("private") && !defaultvalue.equals("null")) {

            if(!sender.equals("null") && !sender.equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
                holder.ivSend.setVisibility(View.VISIBLE);
            else
                holder.ivSend.setVisibility(View.GONE);

            if (!(lastmessagesubstring.equals(" ")) && !(lastmessagesubstring.equals("null")) && !(lastmessagesubstring.equals("  ")) && !(lastmessagesubstring.equals("   "))
                    && !(lastmessagesubstring.equals("    ")) && !(lastmessagesubstring.equals("     "))) {
                holder.tvlastmessage.setVisibility(View.VISIBLE);

                if (lastmessagesubstring.length() > 20) {
                    holder.tvlastmessage.setText(lastmessagesubstring.substring(0, 20) + "..");
                } else {
                    holder.tvlastmessage.setText(lastmessagesubstring);
                }
                holder.ivImage.setVisibility(View.GONE);
            } else if (lastmessagesubstring.equals(" ")) {
                holder.ivImage.setVisibility(View.VISIBLE);
                holder.tvlastmessage.setVisibility(View.VISIBLE);
                holder.ivImage.setImageResource(R.drawable.image);

                holder.tvlastmessage.setText("Image");

            } else if (lastmessagesubstring.equals("   ")) {
                holder.ivImage.setVisibility(View.VISIBLE);
                holder.tvlastmessage.setVisibility(View.VISIBLE);
                holder.ivImage.setImageResource(R.drawable.gif);

                holder.tvlastmessage.setText("GIF");

            } else if (lastmessagesubstring.equals("    ")) {
                holder.ivImage.setVisibility(View.VISIBLE);
                holder.tvlastmessage.setVisibility(View.VISIBLE);
                holder.ivImage.setImageResource(R.drawable.gif);

                holder.tvlastmessage.setText("Sticker");

            }else if (lastmessagesubstring.equals("     ")) {
                holder.ivImage.setVisibility(View.VISIBLE);
                holder.tvlastmessage.setVisibility(View.VISIBLE);
                holder.ivImage.setImageResource(R.drawable.pdf);

                holder.tvlastmessage.setText("PDF");

            } else if (lastmessagesubstring.equals("  ")) {
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

        if(listFiltered.get(holder.getAdapterPosition()).getUrl().equals("null")) {

            if(listFiltered.get(holder.getAdapterPosition()).getGroupname()==null) {
                holder.iv.setImageResource(R.drawable.person);

//
                holder.iv.setClickable(false);

            }
            else {
                holder.iv.setImageResource(R.drawable.group);
            }

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

        if(listFiltered.get(holder.getAdapterPosition()).getStatus()!=null)
        {
            if(listFiltered.get(holder.getAdapterPosition()).getStatus().equals("online"))
                holder.ivBackground.setBackgroundResource(R.drawable.orange);
            else
                holder.ivBackground.setBackgroundResource(R.drawable.white);        }

        if(listFiltered.get(holder.getAdapterPosition()).getuID().equals(""))
        {
            holder.tvUserName.setText(listFiltered.get(holder.getAdapterPosition()).getPh_number());

        }
        else{
            holder.tvUserName.setText(listFiltered.get(holder.getAdapterPosition()).getuID());

        }

        if(listFiltered.get(holder.getAdapterPosition()).getGroupname()==null)
        {
            if(listFiltered.get(holder.getAdapterPosition()).getMessagenum() > 2)
            {
                holder.tvMessageNum.setText(listFiltered.get(holder.getAdapterPosition()).getMessagenum()-2+"");
                holder.tvMessageNum.setVisibility(View.VISIBLE);
            }
            else{
                // holder.tvMessageNum.setText(listFiltered.get(position).getMessagenum()-2+"");
                holder.tvMessageNum.setVisibility(View.GONE);
            }
        }
        else {
            holder.ivBackground.setBackgroundResource(R.drawable.white);

        if (listFiltered.get(holder.getAdapterPosition()).getMessagenum() > 2) {
                holder.tvMessageNum.setText(listFiltered.get(holder.getAdapterPosition()).getMessagenum()-2 + "");
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
       ImageView iv,ivBackground,ivSend;
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
            ivSend = itemView.findViewById(R.id.ivSend);
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

    public String newDate(String date)
    {
        String newDate;

        newDate = date.substring(8,10);

        switch (date.substring(5,7))
        {
            case "01":
                newDate = newDate+"/01/";
                break;

            case "02":
                newDate = newDate+"/02/";
                break;

            case "03":
                newDate = newDate+"/03/";
                break;

            case "04":
                newDate = newDate+"/04/";
                break;

            case "05":
                newDate = newDate+"/05/";
                break;

            case "06":
                newDate = newDate+"/06/";
                break;

            case "07":
                newDate = newDate+"/07/";
                break;

            case "08":
                newDate = newDate+"/08/";
                break;

            case "09":
                newDate = newDate+"/09/";
                break;

            case "10":
                newDate = newDate+"/10/";
                break;

            case "11":
                newDate = newDate+"/11/";
                break;

            case "12":
                newDate = newDate+"/12/";
                break;
        }

        newDate = newDate+date.substring(2,4);

        return newDate;
    }

    }


