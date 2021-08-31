package com.example.bookstore.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookstore.R;
import com.example.bookstore.adapter.UsersAdapter;
import com.example.bookstore.model.Chat;
import com.example.bookstore.model.Users;
import com.example.bookstore.prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;
    private List<Users> mUsers;

    String currentUser;
    DatabaseReference reference;
    private List<String> usersList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = view.findViewById(R.id.chat_recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        currentUser = Prevalent.currentOnlineUser.getPhone();
        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getSender().equals(currentUser)){
                        usersList.add(chat.getReceiver());
                    }
                    if (chat.getReceiver().equals(currentUser)){
                        usersList.add(chat.getSender());
                    }
                }
                readChat();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }

    private void readChat() {
        mUsers = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers.clear();
                ArrayList<String> arrTemp = new ArrayList<>();
                for (int i = 0; i < usersList.size();i++){
                    if (!arrTemp.contains(usersList.get(i))){
                        arrTemp.add(usersList.get(i));
                    }
                }
                usersList.clear();
                usersList.addAll(arrTemp);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users user = snapshot.getValue(Users.class);
                    for (String id : usersList) {
                        if (user.getPhone().equals(id)) {
                            mUsers.add(user);
                        }
                    }
                }
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Users user = snapshot.getValue(Users.class);
//
//                    for (String id : usersList) {
//                        if (user.getPhone().equals(id)){
//                            if (mUsers.size() != 0){
////                                for (Users user1 : mUsers) {
//                                for (int i = 0;i<mUsers.size();i++){
//                                    Users user1 = mUsers.get(i);
//                                    if (!user.getPhone().equals(user1.getPhone())){
//                                        mUsers.add(user);
//                                    }
//                                }
//                            } else {
//                                mUsers.add(user);
//                            }
//                        }
//                    }
//                }
                usersAdapter = new UsersAdapter(getContext(),mUsers);
                recyclerView.setAdapter(usersAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
