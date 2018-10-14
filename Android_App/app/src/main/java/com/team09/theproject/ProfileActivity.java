package com.team09.theproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by gordo on 2017-11-11.
 */

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    Profile profile;
    EditText nameEditText;
    EditText yearEditText;
    EditText bioEditText;
    ImageView profilePic;
    Spinner spinner;
    Button saveButton;
    Button takePicture;
    Button choosePicture;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameEditText = findViewById(R.id.name);
        yearEditText = findViewById(R.id.year);
        bioEditText = findViewById(R.id.biography);
        profilePic = findViewById(R.id.profilePic);





        queue = Volley.newRequestQueue(ProfileActivity.this);
        String url ="https://csc301-group09.herokuapp.com/api/profiles-by-id/";
        JSONObject user = new JSONObject();

        try{
            user.put("id", Manager.getCurrentID());
        }catch(JSONException e){
            e.printStackTrace();
        }


        // Initialize a new JsonArrayRequest instance
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, user, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response){

                        try{
                            JSONObject user = (JSONObject) response.get("user");
                            String name = (String) user.get("name");
                            int year = (int) response.get("year");
                            String description = (String) response.get("description");


                            nameEditText.setText(name);
                            yearEditText.setText(Integer.toString(year));
                            bioEditText.setText(description);


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


        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                int year = Integer.valueOf(yearEditText.getText().toString());
                String description  = bioEditText.getText().toString();



                Toast.makeText(ProfileActivity.this, "Saved.",Toast.LENGTH_SHORT).show();

                String url ="https://csc301-group09.herokuapp.com/api/profiles/";
                JSONObject user = new JSONObject();

                try{
                    user.put("id", Manager.getCurrentID());
                    user.put("name", name);
                    user.put("description", description);
                    user.put("year", year);
                }catch(JSONException e){
                    e.printStackTrace();
                }


                // Initialize a new JsonArrayRequest instance
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url, user, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response){

                                try{
                                    JSONObject user = (JSONObject) response.get("user");
                                    String name = (String) user.get("name");
                                    int year = (int) response.get("year");
                                    String description = (String) response.get("description");


                                    nameEditText.setText(name);
                                    yearEditText.setText(Integer.toString(year));
                                    bioEditText.setText(description);


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
            case "Find Classmates":{
                Intent startFindClassmateIntent = new Intent(
                        ProfileActivity.this, FindClassmatesActivity.class);
                startActivity(startFindClassmateIntent);
                finish();
                break;
            }

            case "Groups":{
                Intent startGroupIntent = new Intent(
                        ProfileActivity.this, GroupActivity.class);
                startActivity(startGroupIntent);
                finish();
                break;
            }
            case "Home": {
                Intent startHomeIntent = new Intent(
                        ProfileActivity.this, MainActivity.class);
                startActivity(startHomeIntent);
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
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        finish();

    }
}
