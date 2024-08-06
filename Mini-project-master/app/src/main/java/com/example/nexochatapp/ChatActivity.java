package com.example.nexochatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.nexochatapp.adapter.ChatRecyclerAdapter;
import com.example.nexochatapp.adapter.SearchUserRecyclerAdapter;
import com.example.nexochatapp.model.ChatMessageModel;
import com.example.nexochatapp.model.ChatRoomModel;
import com.example.nexochatapp.model.UserModel;
import com.example.nexochatapp.utils.AndroidUtil;
import com.example.nexochatapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {
    UserModel otherUser;
    EditText messageInput;
    ImageButton sendBtn;
    ImageButton backBtn;
    TextView otherUserName;
    RecyclerView recyclerView;
    ChatRecyclerAdapter adapter;
    String chatRoomId;
    ChatRoomModel chatRoomModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //get UserModel
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatRoomId = FirebaseUtil.getChatRoomId(FirebaseUtil.currentUserId(),otherUser.getUserId());


        messageInput = findViewById(R.id.chat_message_input);
        sendBtn = findViewById(R.id.button_send);
        backBtn = findViewById(R.id.button_back);
        otherUserName = findViewById(R.id.other_userName);
        recyclerView = findViewById(R.id.chat_recycler_view);

        backBtn.setOnClickListener((v)->{
            getOnBackPressedDispatcher().onBackPressed();
        });
        otherUserName.setText(otherUser.getUserName());

        sendBtn.setOnClickListener((v ->{
         String message = messageInput.getText().toString().trim();
         if(message.isEmpty())
             return;
         sendMessageToUser(message);


        }));

        getOrCreateChatRoomModel();
        setUpChatRecyclerView();
    }
    void setUpChatRecyclerView(){
        Query query = FirebaseUtil.getChatRoomMessageReference(chatRoomId)
                .orderBy("timeStamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();


        adapter = new ChatRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }
    void sendMessageToUser(String message){
        chatRoomModel.setLastMessageTimeStamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatRoomModel.setLastMessage(message);

        FirebaseUtil.getChatRoomReference(chatRoomId).set(chatRoomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message,FirebaseUtil.currentUserId(),Timestamp.now());
        FirebaseUtil.getChatRoomMessageReference(chatRoomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            messageInput.setText("");
                        }
                    }
                });
    }
    void getOrCreateChatRoomModel(){
        FirebaseUtil.getChatRoomReference(chatRoomId).get().addOnCompleteListener(task -> {
          if(task.isSuccessful()){
             chatRoomModel = task.getResult().toObject(ChatRoomModel.class);
             if(chatRoomModel==null){
                 chatRoomModel = new ChatRoomModel(
                         chatRoomId,
                         Arrays.asList(FirebaseUtil.currentUserId(),otherUser.getUserId()),
                         Timestamp.now(),"",""
                 );
                 FirebaseUtil.getChatRoomReference(chatRoomId).set(chatRoomModel);

             }
          }
        });
    }
}