package com.example.kolos;

public class PixelChange {
    private String id;
    private int x;
    private int y;
    private String color;

    public String getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getColor() {
        return color;
    }

    public PixelChange(String id, int x, int y, String color) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.color = color;
    }
}
