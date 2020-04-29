package com.androidstudio.chattingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends ArrayAdapter<UserDetailWithStatus> {



        private final Context context;
        CircleImageView iv;
        ImageView ivSelected;
        FirebaseDatabase database;
        DatabaseReference reference;
        private ArrayList<UserDetailWithStatus> list;

        public interface itemSelected
        {
            public void onItemSelected(int index);
        }

        itemSelected Activity;

        public FriendsAdapter(@NonNull Context context, ArrayList<UserDetailWithStatus>list) {
            super(context, R.layout.friends_list_layout,list);

            this.context = context;
            this.list = list;
            Activity = (itemSelected) context;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View v= inflater.inflate(R.layout.friends_list_layout,parent,false);
            iv=v.findViewById(R.id.imageView);
            database=FirebaseDatabase.getInstance();
            reference=database.getReference();
            TextView tvStatus= v.findViewById(R.id.tvstatus);
            ivSelected=v.findViewById(R.id.ivSelected);
           // Log.d("tag",list.get(position).getUrl());
            SharedPreferences preftheme;
            preftheme=context.getSharedPreferences("theme",0);

            String theme=preftheme.getString("theme","red");

            switch (theme)
            { case "orange":

                tvStatus.setTextColor(context.getResources().getColor(R.color.Orange));
                break;

                case "blue":

                    tvStatus.setTextColor(context.getResources().getColor(R.color.blue));

                    break;


                case "bluish":
                    tvStatus.setTextColor(context.getResources().getColor(R.color.bluish));
                    break;


                case "deepred":
                    tvStatus.setTextColor(context.getResources().getColor(R.color.deepred));
                    break;

                case "faintpink":
                    tvStatus.setTextColor(context.getResources().getColor(R.color.faintpink));

                    break;

                case "darkblue":
                    tvStatus.setTextColor(context.getResources().getColor(R.color.darkblue));
                    break;


                case "green":
                    tvStatus.setTextColor(context.getResources().getColor(R.color.green));
                    break;

                case "lightorange":
                    tvStatus.setTextColor(context.getResources().getColor(R.color.lightorange));
                    break;

                case "lightred":
                    tvStatus.setTextColor(context.getResources().getColor(R.color.lightred));
                    break;


                case "mustard":
                    tvStatus.setTextColor(context.getResources().getColor(R.color.mustard));
                    break;

                case "pink":
                    tvStatus.setTextColor(context.getResources().getColor(R.color.pink));

                    break;

                case "pureorange":
                    tvStatus.setTextColor(context.getResources().getColor(R.color.pureorange));
                    break;

                case "purepink":
                    tvStatus.setTextColor(context.getResources().getColor(R.color.purepink));
                    break;

                case "purple":
                    tvStatus.setTextColor(context.getResources().getColor(R.color.purple));
                    break;

                default:
                    tvStatus.setTextColor(context.getResources().getColor(R.color.red));

            }

            if(list.get(position).getSelected()==1)
            {
                ivSelected.setVisibility(View.VISIBLE);
            }
            if(list.get(position).getUrl().equals("null"))
            {
                iv.setImageResource(R.drawable.person);
                String theme1=preftheme.getString("theme","red");
                switch (theme1) {
                    case "orange":

                        iv.setColorFilter(context.getResources().getColor(R.color.Orange));
                        break;

                    case "blue":

                        iv.setColorFilter(context.getResources().getColor(R.color.blue));

                        break;


                    case "bluish":
                        iv.setColorFilter(context.getResources().getColor(R.color.bluish));
                        break;


                    case "deepred":
                        iv.setColorFilter(context.getResources().getColor(R.color.deepred));
                        break;

                    case "faintpink":
                        iv.setColorFilter(context.getResources().getColor(R.color.faintpink));

                        break;

                    case "darkblue":
                        iv.setColorFilter(context.getResources().getColor(R.color.darkblue));
                        break;


                    case "green":
                        iv.setColorFilter(context.getResources().getColor(R.color.green));
                        break;

                    case "lightorange":
                        iv.setColorFilter(context.getResources().getColor(R.color.lightorange));
                        break;

                    case "lightred":
                        iv.setColorFilter(context.getResources().getColor(R.color.lightred));
                        break;


                    case "mustard":
                        iv.setColorFilter(context.getResources().getColor(R.color.mustard));
                        break;

                    case "pink":
                        iv.setColorFilter(context.getResources().getColor(R.color.pink));
                        break;

                    case "pureorange":
                        iv.setColorFilter(context.getResources().getColor(R.color.pureorange));
                        break;

                    case "purepink":
                        iv.setColorFilter(context.getResources().getColor(R.color.purepink));
                        break;

                    case "purple":
                        iv.setColorFilter(context.getResources().getColor(R.color.purple));
                        break;

                    default:
                        iv.setColorFilter(context.getResources().getColor(R.color.red));
                }
            }
            else
            {
                Glide.with(context).load(list.get(position).getUrl()).into(iv);}



            TextView tvUserName= v.findViewById(R.id.tv_username);


            if((list.get(position).getuID().equals("")))
            tvUserName.setText(list.get(position).getPh_number());
            else
                tvUserName.setText(list.get(position).getuID());
            if(list.get(position).getStatus().equals(""))
            {
                tvStatus.setVisibility(View.GONE);
                tvUserName.setPadding(0,22,0,0);
            }
            else
            tvStatus.setText(list.get(position).getStatus());

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Activity.onItemSelected(position);

                }
            });

            return v;
        }

    }

