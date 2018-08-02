package controllerthreads;

import controller.Console;
import parameters.AppParameters;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.List;

// https://www.eclipse.org/paho/clients/java/

public class ControllerRunnable implements Runnable {
    private String ip;
    private String port;
    private Command cmd;

    private String topic;

    private final List<Thread> threads;

    public ControllerRunnable(String ip, String port, Command cmd) {
        this.ip = ip;
        this.port = port;
        this.cmd = cmd;
        threads = null;
    }

    public ControllerRunnable(String ip, String port, Command cmd, List<Thread> threads) {
        this.ip = ip;
        this.port = port;
        this.cmd = cmd;
        this.threads = threads;
    }

    public void run() {
        String topic;
        String content;
        String broker;
        String clientId;
        String text_id;
        int qos = AppParameters.qualityOfService;

        MemoryPersistence persistence = new MemoryPersistence();

        if (threads != null) {

            text_id = String.valueOf(Thread.currentThread().getId());
            clientId = "controller_" + text_id;

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

            if (!cmd.periodic) {
                try {
                    MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
                    MqttConnectOptions connOpts = new MqttConnectOptions();
                    connOpts.setCleanSession(true);
                    System.out.println("\nMQTT ID " + clientId + ": " + "Connecting to broker: " + broker);
                    sampleClient.connect(connOpts);
                    System.out.println("MQTT ID " + clientId + ": " + "Connected");
                    System.out.println("MQTT ID " + clientId + ": " + "Publishing message: " + content);
                    MqttMessage message = new MqttMessage(content.getBytes());
                    message.setQos(qos);
                    sampleClient.publish(topic, message);
                    System.out.println("MQTT ID " + clientId + ": " + "Message published");
                    sampleClient.disconnect();
                    System.out.println("MQTT ID " + clientId + ": " + "Disconnected \n");
                } catch (MqttException me) {
                    System.out.println("MQTT ID " + clientId + ": " + "reason " + me.getReasonCode());
                    System.out.println("MQTT ID " + clientId + ": " + "msg " + me.getMessage());
                    System.out.println("MQTT ID " + clientId + ": " + "loc " + me.getLocalizedMessage());
                    System.out.println("MQTT ID " + clientId + ": " + "cause " + me.getCause());
                    System.out.println("MQTT ID " + clientId + ": " + "excep " + me);
                    me.printStackTrace();
                }
            } else {
                AppParameters.flagFrequency = false;
                for (int i = 0; i < cmd.counter; i++) {
                    try {
                        MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
                        MqttConnectOptions connOpts = new MqttConnectOptions();
                        connOpts.setCleanSession(true);
                        System.out.println("\nMQTT ID " + clientId + ": " + "Connecting to broker: " + broker);
                        sampleClient.connect(connOpts);
                        System.out.println("MQTT ID " + clientId + ": " + "Connected");
                        System.out.println("MQTT ID " + clientId + ": " + "Publishing message: " + content);
                        MqttMessage message = new MqttMessage(content.getBytes());
                        message.setQos(qos);
                        sampleClient.publish(topic, message);
                        System.out.println("MQTT ID " + clientId + ": " + "Message published");
                        sampleClient.disconnect();
                        System.out.println("MQTT ID " + clientId + ": " + "Disconnected \n");

                        if (AppParameters.flagFrequency) {
                            Thread.sleep(AppParameters.threadPauseConsumer);
                        } else {
                            Thread.sleep(cmd.sleeptime);
                        }
                    } catch (InterruptedException e) {
                        System.out.println("Thread ID: " + text_id + ", " + e.getMessage());
                        System.out.println("INTERRUPTED *****************************");
                        return;
                    } catch (MqttException me) {
                        System.out.println("MQTT ID " + clientId + ": " + "reason " + me.getReasonCode());
                        System.out.println("MQTT ID " + clientId + ": " + "msg " + me.getMessage());
                        System.out.println("MQTT ID " + clientId + ": " + "loc " + me.getLocalizedMessage());
                        System.out.println("MQTT ID " + clientId + ": " + "cause " + me.getCause());
                        System.out.println("MQTT ID " + clientId + ": " + "excep " + me);
                        me.printStackTrace();
                        break;
                    }
                }
            }

            synchronized (threads) {
                for (Thread t : threads) {
                    if (t.getId() == Thread.currentThread().getId()) {
                        threads.remove(t);
                        break;
                    }
                }
                if (cmd.periodic) {
                    System.out.println("periodic thread for command: " + cmd + " exitted \n");
                    Console.printPrompt();
                } else {
                    System.out.println("aperiodic thread for command: " + cmd + " exitted \n");
                    Console.printPrompt();
                }
            }
        }
        else {
            clientId = "controller_1";
            //topic = AppParameters.musicTopic;

            topic = AppParameters.filesTopic;

            /*if(cmd.text.equals("ON")){
                content = "MUSICON 0";
            }else{
                content = "MUSICOFF";
            }*/

            broker = "tcp://" + ip + ":" + port;
            content = cmd.text;

            try {
                MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);
                sampleClient.connect(connOpts);
                MqttMessage message = new MqttMessage(content.getBytes());
                message.setQos(qos);
                sampleClient.publish(topic, message);

                System.out.println("ControllerRunnable: publishing " + message);

                //sampleClient.publish(topic, message);

                sampleClient.disconnect();
            } catch (MqttException me) {
                me.printStackTrace();
            }
        }
    }
}