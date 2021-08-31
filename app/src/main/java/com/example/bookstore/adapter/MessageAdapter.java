package com.example.bookstore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookstore.R;
import com.example.bookstore.model.Chat;
import com.example.bookstore.prevalent.Prevalent;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context mContext;
    private List<Chat> mChat;

    public MessageAdapter (Context mContext, List<Chat> mchat) {
        this.mChat = mchat;
        this.mContext = mContext;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView showMessage;

        public ViewHolder(View itemView){
            super(itemView);
            showMessage = itemView.findViewById(R.id.show_message);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT)  {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        holder.showMessage.setText(chat.getMessage());
    }

    @Override
    public int getItemViewType(int position) {
        if (mChat.get(position).getSender().equals(Prevalent.currentOnlineUser.getPhone())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }
}
