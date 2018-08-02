package controllerthreads;

import buffer.Buffer;
import classification.Classifier;
import consumer.Consumer;
import controller.Console;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import parameters.AppParameters;
import producer.Producer;
import sub.MqttSubscriber;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    private String ip;
    private String port;

    private MqttClient sampleClient = null;
    private MqttConnectOptions connOpts = new MqttConnectOptions();
    private MqttSubscriber sub = new MqttSubscriber(this);
    private MemoryPersistence persistence = new MemoryPersistence();
    private String[] topics;
    private String broker;
    private String clientId;

    private Buffer buffer = new Buffer();
    private Producer prod = new Producer(buffer);
    private Consumer cons = new Consumer(buffer);
    private Thread prodThread = null;
    private Thread consThread = new Thread(cons);
    private List<Thread> threads;
    private Classifier classifier = new Classifier();

    public Controller(String ip, int port) {
        this.ip = ip;
        this.port = String.valueOf(port);
    }

    public void execute(Command command) {
        if (command.text.equals("EXIT")) {
            AppParameters.interrupt_event = AppParameters.SHUTTING_DOWN;

            consThread.interrupt();
            try {
                System.out.println("Controller: Waiting for consumer to shutdown all threads ... ");
                consThread.join();

                unsubscribeFromAll();

                System.out.println("Controller: Proper shutdown. Bye bye");
                System.exit(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (command.text.equals("READFILES")) {
            //if (prodThread != null) {
            //    prodThread.interrupt();
            //
            //}
            prodThread = new Thread(prod);
            prodThread.start();
            try {
                prodThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.println("Main thread unblocked");

        } else {
            if (command.text.startsWith("STOPALL")) {
                System.out.println("interrupt consumer!!!");
                consThread.interrupt();
            } else {
                if (!command.text.startsWith("RANDOM")) {
                    Runnable runnable = new ControllerRunnable(ip, port, command, threads);
                    buffer.put(runnable); // Produce
                } else {
                    int duration = command.t;
                    Runnable runnable = new ControllerRunnableRandom(ip, port, command, threads, duration);
                    buffer.put(runnable); // Produce
                }
            }
        }
    }


    public void init() {
        topics = new String[]{AppParameters.frequencyTopic};

        broker = String.format("tcp://%s:%d", AppParameters.ip, AppParameters.port);
        clientId = AppParameters.clientId;

        threads = cons.getThreads();
        consThread.start();
    }

    public String subscribeToAll() {
        try {
            broker = String.format("tcp://%s:%d", AppParameters.ip, AppParameters.port);
            sampleClient = new MqttClient(broker, clientId, persistence);

            connOpts.setCleanSession(true);
            sampleClient.setCallback(sub);
            sampleClient.connect(connOpts);
            sampleClient.subscribe(topics);
            return "OK";
        } catch (MqttException me) {
            sampleClient = null;
            return me.getMessage();
        }
    }

    public String unsubscribeFromAll() {
        if (sampleClient != null) {
            try {
                sampleClient.unsubscribe(topics);
                sampleClient.disconnect();
                sampleClient.close();
                sampleClient = null;
                return "OK";
            } catch (MqttException me) {
                return me.getMessage();
            }
        } else {
            return "OK";
        }
    }
}
