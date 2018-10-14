package com.team09.theproject;

/**
 * Created by gordo on 2017-11-09.
 */

// A notification class that contains a sender, a message.

// Other information can be added on in the future to facilitate the implementation of the Notification center
// ListView Listener.


public class Notification{

    private String sender;
    private String message;
    private String notificationType;
    private int senderID;
    private int number;

    public Notification(String sender, String message, String notificationType, int senderID, int number){
        this.sender = sender;
        this.message = message;
        this.notificationType = notificationType;
        this.senderID = senderID;
        this.number = number;
    }

    public String getSender(){return this.sender;}

    public String getMessage(){return this.message;}

    public String getNotificationType(){return this.notificationType;}

    public int getSenderID(){return this.senderID;}

    public int getNumber(){return this.number;}

}


