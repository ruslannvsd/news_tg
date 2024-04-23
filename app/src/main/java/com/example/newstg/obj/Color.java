package com.example.newstg.obj;

public class Color {
    String name;
    int color;
    public Color(
            String name,
            int color
    ) {
        this.name = name;
        this.color = color;
    }
    public void setName(String name) { this.name = name; }
    public void setColor(int color) { this.color = color; }
    public String getName() { return name; }
    public int getColor() { return color; }
}
