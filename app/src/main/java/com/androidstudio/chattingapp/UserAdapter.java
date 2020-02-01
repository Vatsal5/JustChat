package com.androidstudio.chattingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<UserDetail> {

    private final Context context;
    private ArrayList<UserDetail> list;
    public UserAdapter(@NonNull Context context, ArrayList<UserDetail> list) {
        super(context, R.layout.chats_list_layout,list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v= inflater.inflate(R.layout.chats_list_layout,parent,false);


        TextView tvUserName= v.findViewById(R.id.tv_username);
        tvUserName.setText(list.get(position).getuID());


        return v;
    }

}
