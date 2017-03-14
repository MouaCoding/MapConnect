package com.example.rhrn.RightHereRightNow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.example.rhrn.RightHereRightNow.firebase_entry.Messages;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;

import com.example.rhrn.RightHereRightNow.firebase_entry.Messages;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.google.android.gms.cast.Cast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

//import static com.example.rhrn.RightHereRightNow.firebaseEntry.Messages.STATUS_SENT;


/**
 * Created by Matt on 3/2/2017.
 */

public class ChatActivity extends MessageList implements View.OnClickListener,
        MessageSource.MessagesCallbacks{

    public static final String USER_EXTRA = "USER";
    public static final String TAG = "ChatActivity";
    public static final String RECEIVER_ID = "RECEIVER_ID";
    public ArrayList<Messages> mMessages;
    public ArrayList<User> mUsers;
    public MessagesAdapter mAdapter;
    public String mRecipient;
    public ListView mListView;
    public Date mLastMessageDate = new Date();
    public String mConvoId;
    public MessageSource.MessagesListener mListener;
    public Messages msg;
    public TextView receiverName;

    private DatabaseReference users;
    public int listener = 0;


    //User (Sender)
    public User sender;
    public FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_thread);

        receiverName = (TextView) findViewById(R.id.profile_name_chat);
        if(getIntent().getExtras()!=null)
            receiverName.setText(getIntent().getExtras().getString("ReceiverName"));
        mListView = (ListView)findViewById(R.id.message_list);
        sender = new User();
        mMessages = new ArrayList<>();
        mUsers = new ArrayList<>();
        mAdapter = new MessagesAdapter(mMessages);
        mListView.setAdapter(mAdapter);
        //setTitle(mRecipient);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ImageButton backButton = (ImageButton)findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish current activity when back button is pressed
                /*if(getIntent().getExtras() != null) {
                    Intent intent = new Intent(getApplicationContext(), MessageList.class);
                    intent.putExtra(ChatActivity.RECEIVER_ID, ChatActivity.RECEIVER_ID);
                    intent.putExtra("ReceiverName", "ReceiverName");
                    intent.putExtra("MessageContent", msg.getMessage());
                */
                finish();
                //}
            }
        });
        Button sendMessage = (Button)findViewById(R.id.send_message);
        sendMessage.setOnClickListener(this);

        FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
        mRecipient = fbuser.getUid();
        //getKeyFromFB();

        //Establish a link between two ids, this link will not only be unique
        String receiverKey="";
        Bundle extras = getIntent().getExtras();
        if(extras != null)
            receiverKey = extras.getString(RECEIVER_ID); // get the extra (receiver id) from previous activity
        //Log.d(receiverKey,"RKEY");
        String[] ids = {mRecipient, receiverKey};
        Arrays.sort(ids);
        mConvoId = ids[0]+ids[1];
        mListener = MessageSource.addMessagesListener(mConvoId, this);
    }

    public void onClick(View v) {
        EditText newMessageView = (EditText)findViewById(R.id.new_message);
        String newMessage = newMessageView.getText().toString();
        newMessageView.setText("");
        msg = new Messages();
        msg.setDate(new Date());
        msg.setMessage(newMessage);
        //set the sender to the sender's id
        msg.setSender(mRecipient);
        msg.setReceiver(getIntent().getExtras().getString(RECEIVER_ID));

        getKeyFromFB();
        /*//TODO: once we can query receiver, then change this.
        if(Objects.equals(mRecipient,"HALZm2q8cZdyfVsg9kWSkEvSYXg2"))
            msg.setReceiver("0wOKUffpCuMZIf575Q3NaXjW8UD3");
        else
            msg.setReceiver("HALZm2q8cZdyfVsg9kWSkEvSYXg2");
*/
    }

    @Override
    public void onMessageAdded(Messages message) {
        mMessages.add(message);
        mAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageSource.stop(mListener);
    }

    public class MessagesAdapter extends ArrayAdapter<Messages> {
        MessagesAdapter(ArrayList<Messages> messages){

            super(ChatActivity.this, R.layout.msg_item, R.id.msg, messages);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FirebaseUser fbuser = FirebaseAuth.getInstance().getCurrentUser();
            convertView = super.getView(position, convertView, parent);
            Messages message = getItem(position);
            TextView nameView = (TextView)convertView.findViewById(R.id.msg);
            nameView.setText(message.getMessage());
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)nameView.getLayoutParams();
            int sdk = android.os.Build.VERSION.SDK_INT;
            //if the sender matches the id of the sender, then bubble appears right side and green
            if ( message.getSender().equals(fbuser.getUid())){
                    nameView.setBackground(getDrawable(R.drawable.bubble_right_green));
                    layoutParams.gravity = Gravity.END;
            //else it appears left side and gray
                }else{
                    nameView.setBackground(getDrawable(R.drawable.bubble_left_gray));
                    layoutParams.gravity = Gravity.LEFT;
                }
            nameView.setLayoutParams(layoutParams);
            return convertView;
        }
    }

    public void getKeyFromFB()
    {
        user = FirebaseAuth.getInstance().getCurrentUser();
        users = FirebaseDatabase.getInstance().getReference("User");
        //found user and link the message to their sender id
        users.getRef().getRef().orderByChild("Sender ID").equalTo(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Messages m = userSnapshot.getValue(Messages.class);
                    String[] ids = {mRecipient, m.getReceiver()};
                    Arrays.sort(ids);
                    mConvoId = ids[0]+ids[1];
                    //Log.d(sender.id,"SENDER ID!");
                    //Log.d(key,"key !");
                }
                    MessageSource.saveMessage(msg, mConvoId);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }



    /*//This function is used to get all the list of users except the current user
    public void getAllUsers()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userKey = user.getUid();
        FirebaseDatabase.getInstance().getReference().child("User")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            User other = userSnapshot.getValue(User.class);
                            if (!TextUtils.equals(other.uid, userKey)) {
                                mUsers.add(other);
                                Log.d(other.id,"OTHER ID");
                                //TODO: find the person the current user wants to talk to.
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to retrieve the users.
                    }
                });
    } //getAllUsers()*/


}
