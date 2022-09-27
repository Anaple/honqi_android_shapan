package com.example.myapplication.bean;

import android.widget.Button;

public class MapPoint {
    private int Id;
    private Button button;
    private int width;
    private int height;


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public MapPoint(int id, Button button, int width, int height) {
        Id = id;
        this.button = button;
        this.width = width;
        this.height = height;

    }

    public MapPoint() {
    }
}
