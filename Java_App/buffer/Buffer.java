package buffer;

import controllerthreads.Command;
import parameters.AppParameters;

import java.util.ArrayList;

public class Buffer {
    private ArrayList<Runnable> storage = new ArrayList<>(); // Storage ArrayList <-> Buffer


    public synchronized void put(Runnable r) { // put a Runnable into the Buffer
        storage.add(r);
        this.notify();
    }

    public synchronized Runnable get() throws InterruptedException { // get a Runnable from the Buffer
        while (storage.isEmpty()) {
            this.wait();
        }

        Runnable temp = storage.remove(0);  // remove the first item from Buffer
        return temp;
    }

    public synchronized void clear() {
        storage.clear(); // clear Buffer
    }
}
