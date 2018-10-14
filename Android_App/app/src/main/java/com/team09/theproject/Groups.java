package com.team09.theproject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Created by shaya on 11/13/2017.
 */

public class Groups implements Serializable{
    private String groupName;
    private String description;
    private int id;



    public Groups(String groupName, String description, int id){
        this.groupName = groupName;
        this.description = description;
        this.id = id;
    }

    public String getGroupName(){
        return this.groupName;
    }

    public String getDescription(){
        return this.description;
    }

    public int getID(){return this.id;}

    public String toString(){return this.getDescription() + " " + this.getGroupName();}


}
