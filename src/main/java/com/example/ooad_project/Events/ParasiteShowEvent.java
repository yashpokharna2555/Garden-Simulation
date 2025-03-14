package com.example.ooad_project.Events;

import com.example.ooad_project.Parasite.Parasite;

public class ParasiteShowEvent {

    private final Parasite parasite;
    private final int row;
    private final int column;


    public ParasiteShowEvent(Parasite parasite, int row, int column) {
        this.parasite = parasite;
        this.row = row;
        this.column = column;
    }

    public Parasite getParasite() {
        return parasite;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

}
