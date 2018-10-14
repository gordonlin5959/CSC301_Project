package com.team09.theproject;

/**
 * Created by shayan on 11/2/2017.
 *
 * Updated by ryanro on 11/9/2017.
 */

class Manager {

    private static int currentID = -1;

    private Manager() {}

    static void setCurrentID(int ID){
        currentID = ID;
    }

    static int getCurrentID(){
        return currentID;
    }

}