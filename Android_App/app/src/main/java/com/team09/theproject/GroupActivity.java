package com.team09.theproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class GroupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{


    Button myGroups;
    Button request;
    Button createGroup;
    ListView groupsListView;

    Groups selectedGroup;


    ArrayList<Groups> mygroupsList = new ArrayList<>();

    GroupsListAdapter GroupsListViewAdapter1;
    int clickCount = 0;

    ArrayList<Groups> groups = new ArrayList<Groups>();


    RequestQueue queue;

    private class GroupsListAdapter extends ArrayAdapter<Groups> {
        // since notificationsAdapter is a private class, all the arguments passed into the
        // super class's constructors are class variables from the MainActivity class.

        private ArrayList<Groups> content;

        public GroupsListAdapter(ArrayList<Groups> content) {
            super(GroupActivity.this, 0, content);
            this.content = new ArrayList<Groups>();
            this.content.addAll(content);
        }

        public View getView(int position, View convertView, ViewGroup parent){

            String groupName = this.content.get(position).getGroupName();
            String groupDescription = this.content.get(position).getDescription();

            LayoutInflater inflater = (LayoutInflater)
                    GroupActivity.this.getSystemService(MainActivity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.group_listview,null);

            TextView groupNameTextView = view.findViewById(R.id.GroupName);
            TextView groupDescriptionTextView = view.findViewById(R.id.GroupDescription);
            groupDescriptionTextView.setText(groupDescription);
            groupNameTextView.setText(groupName);

            return view;
        }

        public void update(ArrayList<Groups> content){
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
        setContentView(R.layout.activity_group);

        myGroups = findViewById(R.id.Filter);
        request = findViewById(R.id.Request);
        createGroup = findViewById(R.id.CreateGroup);
        groupsListView = findViewById(R.id.Groups);

        myGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCount ++;

                if(clickCount % 2 == 0){
                    GroupsListViewAdapter1.update(groups);
                }else{
                    GroupsListViewAdapter1.update(mygroupsList);
                }

            }
        });


        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });



        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                //still need to create a new friend request and send it to server


                //Builds a new alertDialog to let user input a message along with their friend request.

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GroupActivity.this);

                LinearLayout layout = new LinearLayout(GroupActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText et = new EditText(GroupActivity.this);
                final EditText et2 = new EditText(GroupActivity.this);
                et.setHint("Group Name");
                et2.setHint("Group Description");
                layout.addView(et);
                layout.addView(et2);
                alertDialogBuilder.setView(layout);


                // set dialog message
                alertDialogBuilder.setTitle("Create a New Group");
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String url ="https://csc301-group09.herokuapp.com/api/usergroups/";
                        JSONObject jsobj = new JSONObject();
                        String groupName = et.getText().toString();
                        String groupDescription = et2.getText().toString();

                        try{
                            jsobj.put("group_name", groupName);
                            jsobj.put("group_description", groupDescription);

                        }catch(JSONException e){
                            e.printStackTrace();
                        }


                        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                (Request.Method.POST, url, jsobj, new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response){

                                        try{

                                            int id = (int) response.get("group_id");
                                            String name = (String) response.get("group_name");
                                            String description  = (String) response.get("group_description");
                                            groups.add(new Groups(name, description, id));
                                            mygroupsList.add(new Groups(name, description, id));
                                            if(clickCount % 2 == 0){
                                                GroupsListViewAdapter1.update(groups);
                                            }else{
                                                GroupsListViewAdapter1.update(mygroupsList);
                                            }


                                            String url2 ="https://csc301-group09.herokuapp.com/api/add-member/";
                                            JSONObject jsobj2 = new JSONObject();
                                            int userid = Manager.getCurrentID();


                                            try{
                                                jsobj2.put("group_id", id);
                                                jsobj2.put("user_id", userid);

                                            }catch(JSONException e){
                                                e.printStackTrace();
                                            }


                                            JsonObjectRequest jsObjRequest2 = new JsonObjectRequest
                                                    (Request.Method.POST, url2, jsobj2, new Response.Listener<JSONObject>() {

                                                        @Override
                                                        public void onResponse(JSONObject response){

                                                        }
                                                    }, new Response.ErrorListener() {

                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            // TODO Auto-generated method stub

                                                        }
                                                    });


                                            queue.add(jsObjRequest2);

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

                alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        queue = Volley.newRequestQueue(GroupActivity.this);
        String url ="https://csc301-group09.herokuapp.com/api/usergroups/";
        JSONObject jsobj = new JSONObject();


        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, jsobj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response){

                        try{

                            JSONArray groupsArray = response.getJSONArray("groups");
                            for(int i = 0; i < groupsArray.length(); i ++){
                                JSONObject obj = groupsArray.getJSONObject(i);
                                String name = (String) obj.get("group_name");
                                int id = (int) obj.get("group_id");
                                String description = (String) obj.get("group_description");
                                JSONArray memebers = (JSONArray) obj.get("members");

                                Groups g = new Groups(name, description, id);
                                groups.add(g);
                            }
                            GroupsListViewAdapter1 = new GroupsListAdapter(groups);
                            groupsListView.setAdapter(GroupsListViewAdapter1);

                            String url2 ="https://csc301-group09.herokuapp.com/api/find-group-by-user/";
                            JSONObject body1 = new JSONObject();

                            try{
                                body1.put("user_id", Manager.getCurrentID());


                            }catch(JSONException e){
                                e.printStackTrace();
                            }

                            JsonObjectRequest jsObjRequest2 = new JsonObjectRequest
                                    (Request.Method.POST, url2, body1, new Response.Listener<JSONObject>() {

                                        @Override
                                        public void onResponse(JSONObject response){

                                            try{
                                                JSONArray jarray = response.getJSONArray("groups");

                                                for(int i = 0; i < jarray.length(); i ++){
                                                    JSONObject obj = jarray.getJSONObject(i);
                                                    String name = (String) obj.get("group_name");
                                                    int id = (int) obj.get("group_id");
                                                    String description = (String) obj.get("group_description");

                                                    Groups g = new Groups(name, description, id);

                                                    boolean found = false;
                                                    for(int j = 0; i < mygroupsList.size(); i++){
                                                        if(mygroupsList.get(j).getID() == g.getID()){
                                                            found = true;
                                                        }
                                                    }

                                                    if(! found){
                                                        mygroupsList.add(g);
                                                    }
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


                            queue.add(jsObjRequest2);

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

        groupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Groups item = (Groups) groupsListView.getItemAtPosition(position);
                selectedGroup = item;

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GroupActivity.this);

                LinearLayout layout = new LinearLayout(GroupActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final TextView tv = new TextView(GroupActivity.this);
                final TextView tv2 = new TextView(GroupActivity.this);

                tv.setText("Group Description:");
                tv2.setText(selectedGroup.getDescription());
                tv.setTextSize(18);
                tv2.setTextSize(15);
                layout.addView(tv);
                layout.addView(tv2);
                alertDialogBuilder.setView(layout);


                // set dialog message
                alertDialogBuilder.setTitle(selectedGroup.getGroupName());
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setPositiveButton("Join Group", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String url2 ="https://csc301-group09.herokuapp.com/api/add-member/";
                        JSONObject jsobj2 = new JSONObject();
                        int userid = Manager.getCurrentID();


                        try{
                            jsobj2.put("group_id", selectedGroup.getID());
                            jsobj2.put("user_id", userid);

                        }catch(JSONException e){
                            e.printStackTrace();
                        }


                        JsonObjectRequest jsObjRequest2 = new JsonObjectRequest
                                (Request.Method.POST, url2, jsobj2, new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response){

                                        Groups g = selectedGroup;
                                        boolean found = false;
                                        for(int j = 0; j < mygroupsList.size(); j++){
                                            if(mygroupsList.get(j).getID() == g.getID()){
                                                found = true;
                                            }
                                        }

                                        if(! found){
                                            mygroupsList.add(g);
                                        }

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

                alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

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
                        GroupActivity.this, MainActivity.class);
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
                        GroupActivity.this, ProfileActivity.class);
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
        startActivity(new Intent(GroupActivity.this, MainActivity.class));
        finish();

    }

}
