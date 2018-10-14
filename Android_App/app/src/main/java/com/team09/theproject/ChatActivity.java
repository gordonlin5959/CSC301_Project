package com.team09.theproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.UserManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;

import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gordonlin on 2017-12-05.
 */

public class ChatActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Button sendButton;
    ListView chatListView;
    EditText inputline;
    Button refreshButton;

    ArrayList<Message> messages = new ArrayList<>();
    ChatListAdapter chatListAdapter;
    RequestQueue queue;

    int otheruserid;
    int newMessageCount = 0;
    String otherusername ="";




    private class ChatListAdapter extends ArrayAdapter<Message> {
        // since notificationsAdapter is a private class, all the arguments passed into the
        // super class's constructors are class variables from the MainActivity class.

        private ArrayList<Message> content;

        public ChatListAdapter(ArrayList<Message> content) {
            super(ChatActivity.this, 0, content);
            this.content = new ArrayList<Message>();
            this.content.addAll(content);
        }

        public View getView(int position, View convertView, ViewGroup parent){


            LayoutInflater inflater = (LayoutInflater)
                    ChatActivity.this.getSystemService(MainActivity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.chat_listview,null);

            TextView senderView = view.findViewById(R.id.Sender);
            TextView messagesView = view.findViewById(R.id.Message);

            String sender = content.get(position).getSender_name();
            String message = content.get(position).getMessage();


            senderView.setText(sender);
            messagesView.setText(message);

            if(content.get(position).isUser){
                senderView.setGravity(Gravity.RIGHT);
                messagesView.setGravity(Gravity.RIGHT);
            }


            return view;
        }

        public void update(ArrayList<Message> content){
            this.content.clear();
            this.content.addAll(content);
            this.notifyDataSetChanged();

        }
        @Override
        public int getCount() {
            return this.content.size();
        }


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendButton = findViewById(R.id.send);
        inputline = findViewById(R.id.input);
        chatListView = findViewById(R.id.Chat);
        refreshButton = findViewById(R.id.refresh);

        otheruserid = getIntent().getIntExtra("otheruserid", 0);

        chatListAdapter = new ChatListAdapter(messages);
        chatListView.setAdapter(chatListAdapter);

        queue = Volley.newRequestQueue(ChatActivity.this);
        String url ="https://csc301-group09.herokuapp.com/api/find-chat-by-users/";
        JSONObject jsobj = new JSONObject();

        try{
            jsobj.put("user1_id", Manager.getCurrentID());
            jsobj.put("user2_id",otheruserid);

        }catch(JSONException e){
            e.printStackTrace();
        }


        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsobj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response){

                        try{

                            JSONArray messagesArray = response.getJSONArray("messages");
                            for(int i = 0; i < messagesArray.length(); i ++){
                                JSONObject obj = messagesArray.getJSONObject(i);
                                String sender_name = (String) ((JSONObject) obj.get("user_from")).get("name");
                                int fromID = (int) ((JSONObject) obj.get("user_from")).get("id");

                                String message= (String) obj.get("message");


                                Message m = new Message(message,sender_name);
                                if(fromID == Manager.getCurrentID()){
                                    m.set_isUser(true);
                                    otherusername = sender_name;
                                }
                                messages.add(m);
                                chatListAdapter.update(messages);
                            }


                        }catch(JSONException e){
                            e.printStackTrace();
                        }



                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });


        queue.add(jsObjRequest);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newMessageCount ++;
                String url ="https://csc301-group09.herokuapp.com/api/chat/";
                JSONObject jsobj = new JSONObject();

                try{

                    jsobj.put("user_from_id", Manager.getCurrentID());
                    jsobj.put("user_to_id",otheruserid);
                    jsobj.put("message", inputline.getText().toString());

                }catch(JSONException e){
                    e.printStackTrace();
                }


                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url, jsobj, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response){
                                refreshButton.performClick();

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO Auto-generated method stub

                            }
                        });


                queue.add(jsObjRequest);


            }
        });


        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url ="https://csc301-group09.herokuapp.com/api/find-chat-by-users/";
                JSONObject jsobj = new JSONObject();

                try{
                    jsobj.put("user1_id", Manager.getCurrentID());
                    jsobj.put("user2_id",otheruserid);

                }catch(JSONException e){
                    e.printStackTrace();
                }


                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url, jsobj, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response){

                                try{
                                    messages.clear();

                                    JSONArray messagesArray = response.getJSONArray("messages");
                                    for(int i = 0; i < messagesArray.length(); i ++){
                                        JSONObject obj = messagesArray.getJSONObject(i);
                                        String sender_name = (String) ((JSONObject) obj.get("user_from")).get("name");
                                        int fromID = (int) ((JSONObject) obj.get("user_from")).get("id");

                                        String message= (String) obj.get("message");


                                        Message m = new Message(message,sender_name);
                                        if(fromID == Manager.getCurrentID()){
                                            m.set_isUser(true);
                                        }

                                        messages.add(m);
                                        chatListAdapter.update(messages);
                                    }



                                }catch(JSONException e){
                                    e.printStackTrace();
                                }



                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO Auto-generated method stub

                            }
                        });


                queue.add(jsObjRequest);

            }
        });



    }

    @Override
    //Listener function for the dropdown menu
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String s = (String) parent.getItemAtPosition(position);
        switch (s){
            case "Messages" : ;
                break;
            case "Home":{
                Intent startHomeIntent = new Intent(
                        ChatActivity.this, MainActivity.class);
                startActivity(startHomeIntent);
                finish();
                break;
            }
            case "Classrooms": ;
                break;
            case "Groups": ;
                break;
            case "Profile":{
                Intent startProfileIntent = new Intent(
                        ChatActivity.this, ProfileActivity.class);
                startActivity(startProfileIntent);
                finish();
            }
            case "Setting": ;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        String url = "https://csc301-group09.herokuapp.com/api/notification/";
        JSONObject jsobj = new JSONObject();

        try {
            jsobj.put("sender_id", Manager.getCurrentID());
            jsobj.put("receiver_id", otheruserid);
            jsobj.put("message", String.format("You have %d new messages from %s",newMessageCount, otherusername));
            jsobj.put("type", "Chat");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsobj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response){


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

        if(newMessageCount > 0){
            queue.add(jsObjRequest);
        }


        startActivity(new Intent(ChatActivity.this, MainActivity.class));
        finish();

    }
}
