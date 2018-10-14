package com.team09.theproject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ryanro on 2017-11-22.
 */

class ScheduleEvent implements Serializable {

    private String name;
    private Date date;

    ScheduleEvent(String name, Date date) {
        this.name = name;
        this.date = date;
    }

    String getName() {
        return this.name;
    }

    Date getDate() {
        return this.date;
    }

}
