package com.androidstudio.chattingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(viewType == MSG_TYPE_RIGHT)
        {
            View v = LayoutInflater.from(context).inflate(R.layout.message_right,parent,false);
            return new ViewHolder(v);
        }
        else
        {
            View v = LayoutInflater.from(context).inflate(R.layout.message_left,parent,false);
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position)
    {
        holder.tvMessage.setText(messages.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(messages.get(position).getSender().equals(user.getPhoneNumber()))
        {
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }
    }
}
