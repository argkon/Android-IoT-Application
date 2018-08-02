package com.example.panos.controllerthreads;

import com.example.panos.parameters.AppParameters;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

// https://www.eclipse.org/paho/clients/java/

public class ControllerRunnable implements Runnable {
    private String ip;
    private String port;
    private Command cmd;
    private String topic;

    public ControllerRunnable(String ip, String port, Command cmd) {
        this.ip = ip;
        this.port = port;
        this.cmd = cmd;
    }

    public void run() {
        String content;
        String broker;
        String text_id = String.valueOf(Thread.currentThread().getId());
        String clientId = "controller_" + text_id;
        int qos = AppParameters.qualityOfService;

        MemoryPersistence persistence = new MemoryPersistence();

        topic = AppParameters.frequencyTopic;

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
    }
}