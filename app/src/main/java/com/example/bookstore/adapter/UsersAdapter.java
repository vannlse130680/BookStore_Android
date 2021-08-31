package com.example.bookstore.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookstore.CustomerServiceMessageActivity;
import com.example.bookstore.R;
import com.example.bookstore.model.Chat;
import com.example.bookstore.model.Users;
import com.example.bookstore.prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private Context mContext;
    private List<Users> mUser;
    String lastMessage;

    public UsersAdapter (Context mContext, List<Users> mUser) {
        this.mUser = mUser;
        this.mContext = mContext;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public TextView last_msg;

        public ViewHolder(View itemView){
            super(itemView);
            username = itemView.findViewById(R.id.username_list);
            last_msg = itemView.findViewById(R.id.last_msg);
        }
    }

    private void lastMessage(final String userid, final TextView last_msg) {
        lastMessage = "";
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(Prevalent.currentOnlineUser.getPhone()) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(Prevalent.currentOnlineUser.getPhone())) {
                        lastMessage = chat.getMessage();
                    }
                }

                switch (lastMessage) {
                    case "":
                        last_msg.setText("No message");
                    default:
                        last_msg.setText(lastMessage);
                        break;
                }
                lastMessage = "";
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Users user = mUser.get(position);
        holder.username.setText(user.getPhone());

        if (holder.last_msg.getText().equals("")) {
            holder.last_msg.setVisibility(View.GONE);
        }
        else {
            lastMessage(user.getPhone(),holder.last_msg);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CustomerServiceMessageActivity.class);
                intent.putExtra("userid", user.getPhone());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }
}
