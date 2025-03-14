package com.example.ooad_project.Events;

public class ParasiteInflictionEvent {

    int row;
    int col;
    int damage;

    public ParasiteInflictionEvent(int row, int col, int damage) {
        this.row = row;
        this.col = col;
        this.damage = damage;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getDamage() {
        return damage;
    }

}
