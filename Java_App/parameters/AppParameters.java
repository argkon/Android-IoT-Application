package parameters;

public class AppParameters {
    public static final int SHUTTING_DOWN = 1; // exit
    public static final int STOPPING_ALL = 2; // stopall

    // read from property file Java App

    public static String ip; // default ip -> 127.0.0.1 (localhost)
    public static int port; // standard port for MQTT connections is 1883

    public static int minduration; // ms
    public static int maxduration;

    public static int threadpauseMinduration; // ms
    public static int threadpauseMaxduration;

    public static int threadPauseConsumer = 1000; // ms

    public static int qualityOfService; // QoS

    public static String versionNumber;

    public static boolean flagFrequency = false; // Change frequency from Android App

    public static int startFileNumber;
    public static int endFileNumber;

    public final static String musicTopic = "musictopic"; // topics
    public final static String flashTopic = "flashtopic";
    public final static String frequencyTopic = "frequencytopic";
    public final static String filesTopic = "filestopic";

    public static String clientId = "JavaSamplePublisher";

    public static double cqThreshold; // if (cqSensor < cgThreshod) -> Skip Row from Event Set

    public static int k; // k Nearest Neighbors for knn classification algorithm

    public static boolean flagNullFiles = false; // boolean for Empty Files

    public static volatile int interrupt_event = STOPPING_ALL;
    public static String storagedirectory;
    public static String trainingsetdirectory;
    public static String strManyDirectories;
}

