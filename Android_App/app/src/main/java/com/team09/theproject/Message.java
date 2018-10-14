package com.team09.theproject;

/**
 * Created by gordonlin on 2017-12-05.
 */

public class Message {

    private String message;
    private String sender_name;
    public boolean isUser;

    public Message( String message, String sender_name){

        this.message = message;
        this.sender_name = sender_name;
    }



    public String getMessage(){return this.message;}

    public String getSender_name(){return  this.sender_name;}

    public void set_isUser(boolean b){this.isUser = b;}

}
