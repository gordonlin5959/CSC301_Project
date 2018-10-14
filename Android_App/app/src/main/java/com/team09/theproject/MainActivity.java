package com.team09.theproject;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.content.Intent;
import android.widget.Button;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

// TODO
// make a listener to the ListView elements;


// This is the home screen
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Toolbar toolBar;
    Spinner spinner;
    Button findClassmatesButton;
    ListView notificationsListView;
    //data to be used in notificationsAdapter.
    ArrayList<Notification> notifications = new ArrayList<>();
    ArrayList<Notification> friends = new ArrayList<>();
    RequestQueue queue;
    notificationsAdapter nadapter;
    TextView noti;
    Button friendsButton;
    int clickCounter = 0;


    private class notificationsAdapter extends ArrayAdapter<Notification> {
        // since notificationsAdapter is a private class, all the arguments passed into the
        // super class's constructors are class variables from the MainActivity class.

        public ArrayList<Notification> content;

        public notificationsAdapter(ArrayList<Notification> content) {
            super(MainActivity.this, 0, notifications);
            this.content = new ArrayList<>();
            this.content.addAll(content);
        }
        public View getView(int position, View convertView, ViewGroup parent){
            //for each message, find the sender and message and then input those content into the
            // scrolling list
            String sender = content.get(position).getSender();
            String message = content.get(position).getMessage();
            String type = content.get(position).getNotificationType();
            int number = content.get(position).getNumber();

            if(number == -1){

            }else {
                sender = String.format("%s   (%s Request)", sender, type);
            }

            LayoutInflater inflater = (LayoutInflater)
                    MainActivity.this.getSystemService(MainActivity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.notifications_listview,null);
            TextView top = view.findViewById(R.id.Top);
            TextView bottom = view.findViewById(R.id.Bottom);
            top.setText(sender);
            bottom.setText(message);
            return view;
        }

        public void update(ArrayList<Notification> content){
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
        setContentView(R.layout.activity_main);
        //disables the app name from showing up in the tool bar
        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //connects the findClassmatesButton to a listener
        findClassmatesButton = findViewById(R.id.findClassmateButton);
        findClassmatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FindClassmatesActivity.class));
            }
        });

        spinner = findViewById(R.id.spinner);
        //The items that go into the dropdown menu
        String[] items = new String[]{"Menu", "Find Classmates",
                "Groups", "Profile", "Upload Schedule"};
        //Populating the dropdown menu with the items declared above
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
        //adds a listener to the dropdown menu to react to clicks
        spinner.setOnItemSelectedListener(this);

        notificationsListView = findViewById(R.id.notifications);
        noti = findViewById(R.id.notificationsTitle);
        friendsButton = findViewById(R.id.FriendsButton);


        nadapter = new notificationsAdapter(notifications);
        notificationsListView.setAdapter(nadapter);
        int notiSize = notifications.size();
        noti.setText("Notifications (" + notiSize + ")");

        queue = Volley.newRequestQueue(MainActivity.this);
        String url ="https://csc301-group09.herokuapp.com/api/find-notification-by-user/";
        JSONObject jsobj = new JSONObject();

        try{
            jsobj.put("user_id", Manager.getCurrentID());

        }catch(JSONException e){
            e.printStackTrace();
        }


        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsobj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response){

                        try{

                            JSONArray notificationsArray = response.getJSONArray("notifications");
                            for(int i = 0; i < notificationsArray.length(); i ++){
                                JSONObject obj = notificationsArray.getJSONObject(i);
                                String sender_name = (String) ((JSONObject) obj.get("notification_sender")).get("name");
                                int senderID = (int) ((JSONObject) obj.get("notification_sender")).get("id");

                                String message = (String) obj.get("message");
                                String type = (String) obj.get("type");
                                int number = (int) obj.get("number");


                                Notification n = new Notification(sender_name,message,type,senderID, number);

                                notifications.add(n);

                                nadapter.update(notifications);
                                int notiSize = notifications.size();
                                noti.setText("Notifications (" + notiSize + ")");
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




        notificationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                System.out.println(position);
                System.out.println(nadapter.content.size());
                final Notification item = (Notification) nadapter.content.get(position);
                if(item.getNumber() == -1){

                    Intent startChatIntent = new Intent(
                            MainActivity.this, ChatActivity.class);
                    startChatIntent.putExtra("otheruserid", item.getSenderID());
                    startActivity(startChatIntent);
                    finish();

                } else if (item.getNotificationType().equals( "Chat")) {
                    Intent startChatIntent = new Intent(
                            MainActivity.this, ChatActivity.class);
                    startChatIntent.putExtra("otheruserid", item.getSenderID());
                    startActivity(startChatIntent);

                    String url ="https://csc301-group09.herokuapp.com/api/notification/";
                    JSONObject jsobj = new JSONObject();

                    try{
                        jsobj.put("number", Integer.toString(item.getNumber()));

                    }catch(JSONException e){
                        e.printStackTrace();
                    }

                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                            (Request.Method.PUT, url, jsobj, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response){

                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO Auto-generated method stub

                                }
                            });

                    queue.add(jsObjRequest);
                    finish();

                }else if(item.getNotificationType().equals("Friend")){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                    LinearLayout layout = new LinearLayout(MainActivity.this);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final TextView tv = new TextView(MainActivity.this);
                    final TextView tv2 = new TextView(MainActivity.this);

                    tv.setText(item.getSender());
                    tv2.setText(item.getMessage());
                    tv.setTextSize(18);
                    tv2.setTextSize(15);
                    layout.addView(tv);
                    layout.addView(tv2);
                    alertDialogBuilder.setView(layout);


                    // set dialog message
                    alertDialogBuilder.setTitle("Friend Request");
                    alertDialogBuilder.setCancelable(true);
                    alertDialogBuilder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            String url2 ="https://csc301-group09.herokuapp.com/api/friends/";
                            JSONObject jsobj2 = new JSONObject();
                            int userid = Manager.getCurrentID();
                            int friendid = item.getSenderID();


                            try{
                                jsobj2.put("id", userid);
                                jsobj2.put("friendid", friendid);

                            }catch(JSONException e){
                                e.printStackTrace();
                            }


                            JsonObjectRequest jsObjRequest2 = new JsonObjectRequest
                                    (Request.Method.POST, url2, jsobj2, new Response.Listener<JSONObject>() {

                                        @Override
                                        public void onResponse(JSONObject response){

                                            for(int i = 0; i < notifications.size(); i++){
                                                if(notifications.get(i).getNumber() == item.getNumber()){
                                                    notifications.remove(i);
                                                }
                                            }
                                            nadapter.update(notifications);
                                            int notiSize = notifications.size();
                                            noti.setText("Notifications (" + notiSize + ")");

                                            String url ="https://csc301-group09.herokuapp.com/api/notification/";
                                            JSONObject jsobj = new JSONObject();

                                            try{
                                                jsobj.put("number", Integer.toString(item.getNumber()));

                                            }catch(JSONException e){
                                                e.printStackTrace();
                                            }

                                            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                                    (Request.Method.PUT, url, jsobj, new Response.Listener<JSONObject>() {

                                                        @Override
                                                        public void onResponse(JSONObject response){

                                                        }
                                                    }, new Response.ErrorListener() {

                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            // TODO Auto-generated method stub

                                                        }
                                                    });

                                            queue.add(jsObjRequest);
                                        }
                                    }, new Response.ErrorListener() {

                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // TODO Auto-generated method stub

                                        }
                                    });


                            queue.add(jsObjRequest2);

                        }


                    });

                    alertDialogBuilder.setNegativeButton("Reject",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            String url2 ="https://csc301-group09.herokuapp.com/api/friends/";
                            JSONObject jsobj2 = new JSONObject();
                            int userid = Manager.getCurrentID();
                            int friendid = item.getSenderID();


                            try{
                                jsobj2.put("id", userid);
                                jsobj2.put("friendid", friendid);

                            }catch(JSONException e){
                                e.printStackTrace();
                            }


                            JsonObjectRequest jsObjRequest2 = new JsonObjectRequest
                                    (Request.Method.PUT, url2, jsobj2, new Response.Listener<JSONObject>() {

                                        @Override
                                        public void onResponse(JSONObject response){

                                            for(int i = 0; i < notifications.size(); i++){
                                                if(notifications.get(i).getNumber() == item.getNumber()){
                                                    notifications.remove(i);
                                                }
                                            }
                                            nadapter.update(notifications);
                                            int notiSize = notifications.size();
                                            noti.setText("Notifications (" + notiSize + ")");

                                            String url ="https://csc301-group09.herokuapp.com/api/notification/";
                                            JSONObject jsobj = new JSONObject();

                                            try{
                                                jsobj.put("number", Integer.toString(item.getNumber()));

                                            }catch(JSONException e){
                                                e.printStackTrace();
                                            }

                                            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                                    (Request.Method.PUT, url, jsobj, new Response.Listener<JSONObject>() {

                                                        @Override
                                                        public void onResponse(JSONObject response){

                                                        }
                                                    }, new Response.ErrorListener() {

                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            // TODO Auto-generated method stub

                                                        }
                                                    });

                                            queue.add(jsObjRequest);
                                        }
                                    }, new Response.ErrorListener() {

                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // TODO Auto-generated method stub

                                        }
                                    });


                            queue.add(jsObjRequest2);
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

            }
        });

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCounter ++;

                noti.setText("Friends");
                if (clickCounter % 2 == 0) {
                    nadapter.update(notifications);
                    int notiSize = notifications.size();
                    noti.setText("Notifications (" + notiSize + ")");

                } else {

                    String url = "https://csc301-group09.herokuapp.com/api/find-friends/";
                    JSONObject jsobj = new JSONObject();

                    try {
                        jsobj.put("id", Manager.getCurrentID());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                            (Request.Method.POST, url, jsobj, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response){
                                    friends.clear();
                                    try{

                                        JSONArray notificationsArray = response.getJSONArray("friends");
                                        for(int i = 0; i < notificationsArray.length(); i ++){
                                            JSONObject obj = notificationsArray.getJSONObject(i);
                                            String sender_name = (String) obj.get("name");
                                            int senderID = (int) obj.get("id");



                                            Notification n = new Notification(sender_name,"","",senderID, -1);

                                            friends.add(n);

                                            nadapter.update(friends);
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
            }
        });



    }


    @Override
    //Listener function for the dropdown menu
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String s = (String) parent.getItemAtPosition(position);
        switch (s){

            case "Find Classmates":{
                Intent startFindClassmateIntent = new Intent(
                    MainActivity.this, FindClassmatesActivity.class);
                startActivity(startFindClassmateIntent);

                break;
            }

            case "Groups": {
                Intent groupsIntent = new Intent(
                        MainActivity.this, GroupActivity.class);
                startActivity(groupsIntent);
                finish();
                break;
            }
            case "Profile": {
                Intent startProfileIntent = new Intent(
                        MainActivity.this, ProfileActivity.class);
                startActivity(startProfileIntent);
                finish();
                break;
            }

            case "Upload Schedule":{
                Intent startSchedule = new Intent(
                        MainActivity.this, UploadScheduleActivity.class);
                startActivity(startSchedule);
                finish();
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
