package consumer;

import buffer.Buffer;
import controllerthreads.Command;
import parameters.AppParameters;

import java.util.ArrayList;
import java.util.List;

public class Consumer implements Runnable {
    private Buffer buffer;

    private final List<Thread> threads = new ArrayList<>(); // Consumer Buffer with the running threads

    public Consumer(Buffer buffer) {
        this.buffer = buffer;
    }


    private void stopall() {
        synchronized (threads) {
            for (Thread t : threads) {
                try {
                    t.interrupt();
                } catch (Exception e) {
                    System.out.print(e.getMessage());
                }
            }
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (Exception e) {
                System.out.print(e.getMessage());
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                //System.out.println("consumer unblocked ... (waiting for command) ");

                Runnable r = buffer.get();

                if (r != null) {
                    Thread t = new Thread(r);
                    synchronized (threads) {
                        threads.add(t);
                    }
                    t.start();
                } else {
                    break;
                }
                Thread.sleep(AppParameters.threadPauseConsumer); // frequency sent notification
            } catch (InterruptedException e) {
                System.out.println("Consumer interrupted with EVENT: " + AppParameters.interrupt_event );
                switch(AppParameters.interrupt_event) {
                    case AppParameters.SHUTTING_DOWN: // exit
                        buffer.clear();
                        stopall();
                        return;
                    case AppParameters.STOPPING_ALL: // stopall
                        buffer.clear();
                        stopall();
                        break;
                }
            }
        }
    }

    public List<Thread> getThreads() {
        return threads;
    }
}
