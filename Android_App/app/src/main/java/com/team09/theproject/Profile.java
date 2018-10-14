package com.team09.theproject;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

/**
 * Created by gordo on 2017-11-11.
 */

//Object for a user profile
public class Profile implements Serializable {

    private int user_id;
    private String name;
    private int year;
    private String bio;
    private String matched_classes;


    public Profile(String name, int year, String bio, int user_id){
        this.name = name;
        this.year = year;
        this.bio = bio;
        this.user_id = user_id;
    }

    public String getName(){
        return this.name;
    }

    public int getYear(){
        return this.year;
    }

    public String getBio(){
        return this.bio;
    }

    public int getUser_id(){return this.user_id;}

    public void setName(String name){
        this.name = name;
    }

    public void setYear(int year){
        this.year = year;
    }

    public void setBio(String bio){
        this.bio = bio;
    }

    public void setMatched_classes(String classes){this.matched_classes = classes;}

    public String getMatched_classes(){return this.matched_classes;}

}
