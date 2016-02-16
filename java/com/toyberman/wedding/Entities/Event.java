package com.toyberman.wedding.Entities;

/**
 * Created by maximtoyberman on 05/11/2015.
 */
public class Event {

    String eventName;
    String eventiD;

    public Event(String eventName,String eventId){


        this.eventName=eventName;
        this.eventiD=eventId;
    }


    public String getEventName() {
        return eventName;
    }

    public String getEventiD() {
        return eventiD;
    }
}
