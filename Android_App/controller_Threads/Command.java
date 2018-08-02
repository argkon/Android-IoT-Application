package com.example.panos.controllerthreads;

public class Command {
    public final String text;
    public final int t;

    public boolean periodic;
    public int counter;
    public int sleeptime;

    public Command(String text, int t) { // Command for frequency to the Java App
        this.text = text;
        this.t = t;

        periodic = false;
        counter = 0;
        sleeptime = 0;
    }
}
