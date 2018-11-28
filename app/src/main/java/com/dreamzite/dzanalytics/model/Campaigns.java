package com.dreamzite.dzanalytics.model;

import java.io.Serializable;

public class Campaigns implements Serializable {

    private int id;
    private String name;

    public Campaigns(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Campaigns(){
        this.id = -1;
        this.name = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
