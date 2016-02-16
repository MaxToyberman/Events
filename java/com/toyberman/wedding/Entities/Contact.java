package com.toyberman.wedding.Entities;

/**
 * Created by Toyberman Maxim on 14-Aug-15.
 */
public class Contact implements Comparable<Contact> {


    private String name;
    private String phone_number;
    private String status;
    private Boolean selected;


    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getName() {
        return name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status=status;
    }

    public Contact(String name, String phone_number) {
        setStatus("0");
        setSelected(false);
        String new_phone = "";
        this.name = name;

        for (int i = 0; i < phone_number.length(); i++) {
            char c = phone_number.charAt(i);
            if (Character.isDigit(c))
                new_phone += c;
        }
        this.phone_number = new_phone;
    }


    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", phone_number='" + phone_number + '\'' +
                '}';
    }

    @Override
    public int compareTo(Contact another) {
        return this.name.toLowerCase().compareTo(another.name.toLowerCase());
    }


}
