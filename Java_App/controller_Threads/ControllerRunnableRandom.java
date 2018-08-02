package controllerthreads;

import controller.Console;
import parameters.AppParameters;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.List;
import java.util.Random;

public class ControllerRunnableRandom implements Runnable {
    private String ip;
    private String port;
    private int duration;
    private Command command;

    private String topic;

    private final List<Thread> threads;

    public ControllerRunnableRandom(String ip, String port, Command command, List<Thread> threads, int duration) {
        this.ip = ip;
        this.port = port;
        this.duration = duration;
        this.command = command;
        this.threads = threads;

    }

    public void run() {
        String topic;
        String content;
        String broker;
        String text_id = String.valueOf(Thread.currentThread().getId());
        String clientId = "controller_" + text_id;
        int qos = AppParameters.qualityOfService;

        MemoryPersistence persistence = new MemoryPersistence();

        long start = System.currentTimeMillis();


        Random randomGenerator = new Random();

        System.out.println("Random thread started !!!");

        AppParameters.flagFrequency = false;
        while (true) {

            long now = System.currentTimeMillis();

            long life = now - start;

            if (life > duration) {
                break;
            }

            int r = randomGenerator.nextInt(4);

            Command cmd = null;
            String cmdtext = null;

            long space = (long) AppParameters.maxduration - (long) AppParameters.minduration + 1;

            switch (r) {
                case 0:
                    cmdtext = "FLASHON";
                    long t1 = (long) (space * randomGenerator.nextDouble());
                    int random_time1 = (int) (t1 + AppParameters.minduration);
                    cmd = new Command(cmdtext, random_time1);
                    break;
                case 1:
                    cmdtext = "FLASHOFF";
                    cmd = new Command(cmdtext);
                    break;
                case 2:
                    cmdtext = "MUSICON";
                    long t2 = (long) (space * randomGenerator.nextDouble());
                    int random_time2 = (int) (t2 + AppParameters.minduration);
                    cmd = new Command(cmdtext, random_time2);
                    break;
                case 3:
                    cmdtext = "MUSICOFF";
                    cmd = new Command(cmdtext);
                    break;
            }

            if (cmd == null) {
                continue;
            }

            if (cmd.text.toUpperCase().startsWith("FLASH")) {
                topic = AppParameters.flashTopic;
            } else {
                topic = AppParameters.musicTopic;
            }

            if (cmd.t != 0) {
                content = cmd.text + " " + cmd.t;
            } else {
                content = cmd.text;
            }

            broker = "tcp://" + ip + ":" + port;

            try {
                MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);
                System.out.println("MQTT ID " + clientId + ": " + "Connecting to broker: " + broker);

                sampleClient.connect(connOpts);
                System.out.println("MQTT ID " + clientId + ": " + "Connected");
                System.out.println("MQTT ID " + clientId + ": " + "Publishing message: " + content);
                MqttMessage message = new MqttMessage(content.getBytes());
                message.setQos(qos);
                sampleClient.publish(topic, message);
                System.out.println("MQTT ID " + clientId + ": " + "Message published");
                sampleClient.disconnect();
                System.out.println("MQTT ID " + clientId + ": " + "Disconnected");
            } catch (MqttException me) {
                System.out.println("MQTT ID " + clientId + ": " + "reason " + me.getReasonCode());
                System.out.println("MQTT ID " + clientId + ": " + "msg " + me.getMessage());
                System.out.println("MQTT ID " + clientId + ": " + "loc " + me.getLocalizedMessage());
                System.out.println("MQTT ID " + clientId + ": " + "cause " + me.getCause());
                System.out.println("MQTT ID " + clientId + ": " + "excep " + me);
                me.printStackTrace();
            }

            if (!command.random_flag) {
                long pause_space = (long) AppParameters.threadpauseMaxduration - (long) AppParameters.threadpauseMinduration + 1;
                long fraction = (long) (pause_space * randomGenerator.nextDouble());
                int random_sleeptime = (int) (fraction + AppParameters.threadpauseMinduration);

                now = System.currentTimeMillis();
                try {
                    life = now + random_sleeptime - start;
                    if (!AppParameters.flagFrequency) {
                        if (life > duration) {
                            break;
                        } else {
                            System.out.println("Random thread will sleep for " + random_sleeptime + " ms \n");
                            Thread.sleep(random_sleeptime);
                        }
                    } else {
                        System.out.println("Random thread will sleep for " + AppParameters.threadPauseConsumer + " ms \n");
                        Thread.sleep(AppParameters.threadPauseConsumer);
                    }
                } catch (InterruptedException e) {
                    System.out.println("Thread ID: " + text_id + ", " + e.getMessage());
                    return;
                }
            } else {
                long pause_space = (long) command.max_execution_rate - (long) command.min_execution_rate + 1;
                long fraction = (long) (pause_space * randomGenerator.nextDouble());
                int random_sleeptime = (int) (fraction + command.min_execution_rate);

                now = System.currentTimeMillis();
                try {
                    life = now + random_sleeptime - start;
                    if (!AppParameters.flagFrequency) {
                        if (life > duration) {
                            break;
                        } else {
                            System.out.println("Random thread will sleep for " + random_sleeptime + " ms \n");
                            Thread.sleep(random_sleeptime);
                        }
                    } else {
                        life = now + AppParameters.threadPauseConsumer - start;
                        if (life > duration) {
                            break;
                        } else {
                            System.out.println("Random thread will sleep for " + AppParameters.threadPauseConsumer + " ms \n");
                            Thread.sleep(AppParameters.threadPauseConsumer);
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println("Thread ID: " + text_id + ", " + e.getMessage());
                    return;
                }
            }
        }

        synchronized (threads)

        {
            for (Thread t : threads) {
                if (t.getId() == Thread.currentThread().getId()) {
                    System.out.println("Random thread with ID " + t.getId() + " finished !!! \n");
                    threads.remove(t);
                    break;
                }
            }
        }
    }
}
