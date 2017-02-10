package com.mac.training.firebaseproject;

import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.id.list;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference myRef;
    Button btSave;
    Button btList;
    Button btDelete;
    EditText editName;
    EditText editEmail;
    ArrayList<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userList = new ArrayList<User>();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Guillermo");
        btSave = (Button) findViewById(R.id.btnSave);
        btList = (Button) findViewById(R.id.btnList);
        btDelete = (Button) findViewById(R.id.btnDelete);
        editName = (EditText) findViewById(R.id.editName);
        editEmail = (EditText) findViewById(R.id.editEmail);



        btSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                writeNewUser(editName.getText().toString(),editEmail.getText().toString());
            }
        });

        btList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,ListActivity.class);
                i.putParcelableArrayListExtra("list",userList);
                startActivity(i);
            }
        });

        btDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                removeUser();
            }
        });

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("TAG", "onChildAdded:" + dataSnapshot.getKey());
                // A new comment has been added, add it to the displayed list
                User user = dataSnapshot.getValue(User.class);
                userList.add(user);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("TAG", "onChildChanged:" + dataSnapshot.getKey());
                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                User newComment = dataSnapshot.getValue(User.class);
                String commentKey = dataSnapshot.getKey();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("TAG", "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();
                userList.clear();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("TAG", "onChildMoved:" + dataSnapshot.getKey());
                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                User movedComment = dataSnapshot.getValue(User.class);
                String commentKey = dataSnapshot.getKey();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "postComments:onCancelled", databaseError.toException());
            }
        };
        myRef.child("user").addChildEventListener(childEventListener);
        }

    public void writeNewUser(String name, String email) {
        User user = new User(name, email);
        String key = myRef.push().getKey();
        user.setUserId(key);
        myRef.child("user").child(key).setValue(user);
    }

    private void removeUser() {
        myRef.removeValue();
    }

}
