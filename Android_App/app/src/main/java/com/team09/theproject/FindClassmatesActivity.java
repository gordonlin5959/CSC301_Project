package com.team09.theproject;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by gordo on 2017-11-09.
 */

public class FindClassmatesActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ImageView profilePicImageView;
    Toolbar toolBar;
    Spinner spinner;
    Button addFriendButton;
    Button nextButton;
    Button prevButton;
    ListView biographyListView;
    BiographyListViewAdapter biographyListViewAdapter1;

    //the list of potential matches for the user
    ArrayList<Profile> profiles;
    //the current profile being displayed
    ArrayList<Profile> currentProfile;
    //the counter to keep track of which profile is being displayed
    int profileCounter = 0;

    RequestQueue queue;


    private class BiographyListViewAdapter extends ArrayAdapter<Profile> {
        private ArrayList<Profile> content;
        public BiographyListViewAdapter(ArrayList<Profile> content) {
            super(FindClassmatesActivity.this, 0, content);
            this.content = new ArrayList<>();
            this.content.addAll(content);
        }
        public View getView(int position, View convertView, ViewGroup parent){

            String name = this.content.get(position).getName();
            int year = this.content.get(position).getYear();
            String bio = this.content.get(position).getBio();
            String matched_classes = this.content.get(position).getMatched_classes();

            LayoutInflater inflater = (LayoutInflater)
                    FindClassmatesActivity.this.getSystemService(MainActivity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.description_listview,null);
            TextView nameView = view.findViewById(R.id.Name);
            TextView yearView = view.findViewById(R.id.Year);
            TextView bioView = view.findViewById(R.id.Description);
            TextView matchedClassesView = view.findViewById(R.id.Matched_classes);

            matched_classes = "Matched Classes: " + matched_classes;
            matchedClassesView.setText(matched_classes);

            nameView.setText(name);

            switch(year){
                case 1: {
                    yearView.setText("First Year");
                    break;
                }
                case 2: {
                    yearView.setText("Second Year");
                    break;
                }
                case 3: {
                    yearView.setText("Third Year");
                    break;
                }
                case 4: {
                    yearView.setText("Fourth Year");
                    break;
                }
                case 5: {
                    yearView.setText("Fifth Year");
                    break;
                }
                default:{
                    yearView.setText("");
                    break;
                }

            }

            bioView.setText(bio);
            return view;
        }

        //gets called when the profile being displayed needs to be updated
        public void update(ArrayList<Profile> content){
            this.content.clear();
            this.content.addAll(content);
            this.notifyDataSetChanged();

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findclassmates);

        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //changes the profile to the next one in the profiles arraylist if possible
            public void onClick(View view) {

                profileCounter ++;
                //System.out.println(profileCounter);
                if(profileCounter >= profiles.size()){
                    //reached the last profile. Sends toast to user to notify this
                    Context context = getApplicationContext();
                    CharSequence text = "That's the last one!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    profileCounter = profiles.size()-1;
                }else{
                    //show the next profile in the list
                    currentProfile.clear();
                    currentProfile.add(profiles.get(profileCounter));
                    biographyListViewAdapter1.update(currentProfile);
                }
            }
        });

        prevButton = findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //changes the profile to the next one in the profiles arraylist if possible
            public void onClick(View view) {

                profileCounter --;
                //System.out.println(profileCounter);
                if(profileCounter < 0){
                    //reached the last profile. Sends toast to user to notify this
                    Context context = getApplicationContext();
                    CharSequence text = "That's the first one!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    profileCounter = 0;
                }else{
                    //show the next profile in the list
                    currentProfile.clear();
                    currentProfile.add(profiles.get(profileCounter));
                    biographyListViewAdapter1.update(currentProfile);

                }
            }
        });


        addFriendButton = findViewById(R.id.addFriendButton);
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                //still need to create a new friend request and send it to server


                //Builds a new alertDialog to let user input a message along with their friend request.

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FindClassmatesActivity.this);

                final EditText et = new EditText(FindClassmatesActivity.this);
                et.setHint(R.string.message);
                alertDialogBuilder.setView(et);

                // set dialog message
                alertDialogBuilder.setTitle(R.string.addFriendMessage);
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String url ="https://csc301-group09.herokuapp.com/api/friends/";
                        JSONObject body = new JSONObject();

                        try{
                            body.put("id", Manager.getCurrentID());
                            body.put("friendid", profiles.get(profileCounter).getUser_id());

                        }catch(JSONException e){
                            e.printStackTrace();
                        }

                        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                (Request.Method.POST, url, body, new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response){

                                        try{
                                            JSONObject f2 = response.getJSONObject("friend2");
                                            String name = (String) f2.get("name");
                                            Toast.makeText(FindClassmatesActivity.this,String.format("You have added %s",name),
                                                    Toast.LENGTH_SHORT).show();


                                            String url = "https://csc301-group09.herokuapp.com/api/notification/";
                                            JSONObject jsobj = new JSONObject();

                                            try {
                                                jsobj.put("sender_id", Manager.getCurrentID());
                                                jsobj.put("receiver_id", profiles.get(profileCounter).getUser_id());
                                                jsobj.put("message", et.getText().toString());
                                                jsobj.put("type", "Friend");

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


                                            queue.add(jsObjRequest);

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


        currentProfile = new ArrayList<>();
        profiles = new ArrayList<>();

        queue = Volley.newRequestQueue(FindClassmatesActivity.this);
        String url ="https://csc301-group09.herokuapp.com/api/find-matches/";
        JSONObject user = new JSONObject();


        try {
            user.put("user_id", Manager.getCurrentID());
        } catch(JSONException e) {

            e.printStackTrace();
        }


        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, user, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response){

                        try{
                            JSONArray matches = response.getJSONArray("matches");
                            for(int i = 0; i < matches.length(); i ++){
                                JSONObject obj = matches.getJSONObject(i);
                                String name = (String) obj.get("name");
                                int year = (int) obj.get("year");
                                String description = (String) obj.get("description");
                                JSONArray matched_classes = (JSONArray) obj.get("matched class");

                                String s = "";
                                for(int j = 0; j < matched_classes.length(); j++){
                                    s = matched_classes.get(j) + " " + s;
                                }

                                int id = (int) obj.get("matched_user_id");

                                Profile p = new Profile(name, year, description, id);
                                p.setMatched_classes(s);
                                profiles.add(p);

                            }
                            if(profiles.size() != 0){
                                currentProfile.add(profiles.get(0));
                            }else{
                                Toast.makeText(FindClassmatesActivity.this,String.format("You have no matches"),
                                        Toast.LENGTH_SHORT).show();
                                Intent startProfileIntent = new Intent(
                                        FindClassmatesActivity.this, MainActivity.class);
                                startActivity(startProfileIntent);
                                finish();

                            }


                            biographyListView = findViewById(R.id.description);
                            biographyListViewAdapter1 = new BiographyListViewAdapter(currentProfile);
                            biographyListView.setAdapter(biographyListViewAdapter1);

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


    @Override
    //Listener function for the dropdown menu
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String s = (String) parent.getItemAtPosition(position);
        switch (s){
            case "Messages" : ;
                break;
            case "Home":{
                Intent startHomeIntent = new Intent(
                        FindClassmatesActivity.this, MainActivity.class);
                startActivity(startHomeIntent);
                finish();
                break;
            }
            case "Groups":{
                Intent startGroupIntent = new Intent(
                        FindClassmatesActivity.this, GroupActivity.class);
                startActivity(startGroupIntent);
                finish();
                break;
            }
            case "Profile":{
                Intent startProfileIntent = new Intent(
                        FindClassmatesActivity.this, ProfileActivity.class);
                startActivity(startProfileIntent);
                finish();
                break;
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
        startActivity(new Intent(FindClassmatesActivity.this, MainActivity.class));
        finish();

    }
}
