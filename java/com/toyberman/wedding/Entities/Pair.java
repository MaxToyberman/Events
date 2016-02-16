package com.toyberman.wedding.Entities;

/**
 * Created by Maxim Toyberman on 9/8/15.
 */
public class Pair {
    /*
        this class represents a key value pair

     */

    String parameter_name;
    String Parameter_value;



    public Pair(String parameter_name, String getParameter_value) {
        this.parameter_name = parameter_name;
        this.Parameter_value = getParameter_value;
    }

    public String getParameter_name() {
        return parameter_name;
    }

    public String getGetParameter_value() {
        return Parameter_value;
    }



}
