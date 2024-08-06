package com.example.nexochatapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.net.Uri;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nexochatapp.ChatActivity;
import com.example.nexochatapp.R;
import com.example.nexochatapp.model.ChatRoomModel;
import com.example.nexochatapp.model.UserModel;
import com.example.nexochatapp.utils.FirebaseUtil;
import com.example.nexochatapp.utils.AndroidUtil;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoomModel,RecentChatRecyclerAdapter.ChatRoomModelViewHolder> {
    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int position, @NonNull ChatRoomModel model) {
        FirebaseUtil.getOtherUserFromChatRoom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            boolean lastMsgSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());

                            UserModel otherUserModel = task.getResult().toObject(UserModel.class);
                            holder.userNameText.setText(otherUserModel.getUserName());
                            if(lastMsgSentByMe) {
                                holder.lastMessageText.setText("You:" + model.getLastMessage());
                            }
                            else {
                                holder.lastMessageText.setText(model.getLastMessage());
                            }
                                holder.lastMessageTime.setText(FirebaseUtil.timeStampToString(model.getLastMessageTimeStamp()));

                            holder.itemView.setOnClickListener(v -> {
                                //navigate to chat activity
                                Intent intent = new Intent(context, ChatActivity.class);
                                AndroidUtil.passUserModelAsIntent(intent,otherUserModel);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            });
                        }
                });


    }

    @NonNull
    @Override
    public ChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler,parent,false);
        return new ChatRoomModelViewHolder(view);
    }

    static class ChatRoomModelViewHolder extends RecyclerView.ViewHolder{
        TextView userNameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;


        public ChatRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_messsage_text);
            lastMessageTime = itemView.findViewById(R.id.last_messsage_time_stamp);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
