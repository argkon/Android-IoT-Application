package com.example.panos.parameters;


public class AppParameters {

    public static String ip = "198.41.30.241"; // default ip -> public MQTT broker iot.eclipse.org
    public static int port = 1883; // standard port for MQTT connections

    public static int frequency = 1;
    public final static int poll = 1000; //ms

    public static int durationOn = 0; // if durationOn = 0 ms -> activation of alerts (indefinite)
                                      // if durationOn != 0 ms -> activation of alerts for time durationOn

    public static boolean toastUpConnection = false;

    public static boolean flagSub = false;  // has been done successfully MQTT subscribe in the past

    public static String clientId = "JavaSampleSubscriber";

    public final static String musicTopic = "musictopic"; // topics
    public final static String flashTopic = "flashtopic";
    public final static String frequencyTopic = "frequencytopic";
    public final static String filesTopic = "filestopic";

    public final static String validIP = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    public final static String validFREQUENCY = "[1-9]";

    public final static int qualityOfService = 2; // QoS
}