package controllerthreads;

import parameters.AppParameters;

public class Command {
    public final String text;
    public final int t;

    public boolean periodic;
    public int counter;
    public int sleeptime;
    public boolean random_flag = false;
    public int min_execution_rate = 0;
    public int max_execution_rate = 0;

    public Command(String text) {
        this.text = text;
        t = 0;
        periodic = false;
        counter = 0;
        sleeptime = 0;
    }

    public Command(String text, int t) {
        this.text = text;
        this.t = t;

        periodic = false;
        counter = 0;
        sleeptime = 0;
    }

    public Command(String text, int counter, int sleeptime, boolean readFiles) {
        this.text = text;
        this.t = 0;
        if(!readFiles) {
            this.periodic = true;
            this.counter = counter;
            if (AppParameters.flagFrequency) {
                this.sleeptime = AppParameters.threadPauseConsumer;
            } else {
                this.sleeptime = sleeptime;
            }
        }else{
            this.periodic = false;
            this.counter = 0;
            this.sleeptime = 0;
        }
    }

    public Command(String text, int t, int counter, int sleeptime, boolean random_flag) {
        this.text = text;
        this.t = t;
        this.random_flag = random_flag;
        if (!random_flag) {
            this.periodic = true;
            this.counter = counter;
            this.sleeptime = sleeptime;

        } else {
            this.periodic = false;
            this.counter = 0;
            this.sleeptime = 0;
            this.min_execution_rate = counter;
            this.max_execution_rate = sleeptime;
        }
    }

    @Override
    public String toString() {
        if (!AppParameters.flagFrequency) {
            return "Command{" +
                    "text = '" + text +
                    "', t = " + t +
                    " ms, periodic = " + periodic +
                    ", counter = " + counter +
                    ", execution rate (thread sleeptime) = " + sleeptime +
                    " ms}";
        } else {
            return "Command{" +
                    "text = '" + text +
                    "', t = " + t +
                    " ms, periodic = " + periodic +
                    ", counter = " + counter +
                    ", execution rate (thread sleeptime) = " + AppParameters.threadPauseConsumer +
                    " ms}";
        }
    }
}