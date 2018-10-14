package com.team09.theproject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ryanro on 2017-11-08.
 */

class Schedule implements Serializable {

    private List<ScheduleEvent> events;
    private List<List<String>> courses;

    void addEvent(String name, Date date) {
        this.events.add(new ScheduleEvent(name, date));
    }

    List<ScheduleEvent> getEvents() {
        return this.events;
    }

    void addCourse(List<String> course) {
        this.courses.add(course);
    }

    void removeCourse(List<String> course) {
        this.courses.remove(course);
    }

    void removeAllCourses() {
        this.courses.clear();
    }

    List<List<String>> getCourses() {
        return this.courses;
    }

    List<String> getCommonCourses(Schedule otherSchedule) {
        List<String> commonCourses = new ArrayList<>();
        for (List<String> course:this.courses) {
            for (List<String> otherCourse:otherSchedule.getCourses()) {
                if (course.get(0).equals(otherCourse.get(0)) && course.get(2).equals(otherCourse.get(2))) {
                    if (course.get(1).equals(otherCourse.get(1))) {
                        commonCourses.add(course.get(0).concat(" ").concat(course.get(1)));
                    } else {
                        commonCourses.add(course.get(0));
                    }
                }
            }
        }
        return commonCourses;
    }
}
