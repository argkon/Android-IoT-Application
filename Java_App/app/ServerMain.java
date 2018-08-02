package app;

import controller.ServerMainLoop;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import parameters.AppParameters;


public class ServerMain {
    public static void main(String [] args) {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("src/main/resources/config.properties");
            prop.load(input); // load a properties file

            // get the property value and print it out
            AppParameters.minduration = Integer.parseInt(prop.getProperty("minduration"));
            AppParameters.maxduration = Integer.parseInt(prop.getProperty("maxduration"));
            AppParameters.threadpauseMinduration = Integer.parseInt(prop.getProperty("threadpauseMinduration"));
            AppParameters.threadpauseMaxduration = Integer.parseInt(prop.getProperty("threadpauseMaxduration"));
            AppParameters.ip = prop.getProperty("ip");
            AppParameters.port = Integer.parseInt(prop.getProperty("port"));
            AppParameters.qualityOfService = Integer.parseInt(prop.getProperty("qualityOfService"));
            AppParameters.versionNumber = prop.getProperty("versionNumber");
            AppParameters.storagedirectory = prop.getProperty("storagedirectory");
            AppParameters.trainingsetdirectory = prop.getProperty("trainingsetdirectory");
            AppParameters.strManyDirectories = prop.getProperty("strManyDirectories");
            AppParameters.cqThreshold = Double.parseDouble((prop.getProperty("cqThreshold")));
            AppParameters.k = Integer.parseInt((prop.getProperty("k")));


        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("                    controllerthreads.Controller " + AppParameters.versionNumber + " Version");
        System.out.println("------------------------------------------------------------------");

        System.out.println("MQTT IP is " + AppParameters.ip);
        System.out.println("MQTT PORT is " + AppParameters.port);
        System.out.println("\n\n");


        ServerMainLoop main = new ServerMainLoop(AppParameters.ip, AppParameters.port);
        main.loop();
    }
}