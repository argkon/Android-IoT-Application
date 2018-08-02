package classification;

import buffer.Buffer;
import com.sun.org.apache.regexp.internal.RE;
import controllerthreads.Command;
import controllerthreads.ControllerRunnable;
import parameters.AppParameters;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.lang.Math;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ComputationalService {
    private Buffer buffer;

    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";

    //CSV file header
    private static final String FILE_HEADER = "File Name, Category, Success, Entropy Vector";

    private int samples = 0;
    private int successful = 0;
    private TrainingSetLoader tsLoader = new TrainingSetLoader();
    private EventSetLoader esLoader = new EventSetLoader();
    private ArrayList<Result> listofResults = new ArrayList<Result>();
    private boolean flagFile = false;
    private boolean flag = false;

    public ComputationalService(Buffer buffer) {

        this.buffer = buffer;
    }

    //https://examples.javacodegeeks.com/core-java/writeread-csv-files-in-java-example/
    public boolean CsvFileWriter(String fileName, ArrayList<Result> listofResults) {

        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(fileName);

            //Write the CSV file header
            fileWriter.append(FILE_HEADER.toString());

            //Add a new line separator after the header
            fileWriter.append(NEW_LINE_SEPARATOR);

            //Write a new Result object list to the CSV file
            for (Result r : listofResults) {
                fileWriter.append(String.valueOf(r.getFilename()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(r.getCategory());
                fileWriter.append(COMMA_DELIMITER);
                if (r.getFilename().contains(r.getCategory())) {
                    fileWriter.append("Yes");
                } else {
                    fileWriter.append("No");
                }
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(Arrays.toString(r.getVector().getVector()));
                fileWriter.append(NEW_LINE_SEPARATOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
                return true;
            } catch (IOException e) {
                System.out.println("\nError while flushing/closing fileWriter !!!");
                e.printStackTrace();
                return false;
            }
        }
    }


    public void execute(String storagedirectory, String trainingsetdirectory) throws Exception {
        Classifier classifier = new Classifier();

        //https://stackoverflow.com/questions/5694385/getting-the-filenames-of-all-files-in-a-folder
        File eventSetFolder = new File(storagedirectory);
        File[] listOfEventFiles = eventSetFolder.listFiles();


        File trainingSetFolder = new File(trainingsetdirectory);
        File[] listOfTrainingFiles = trainingSetFolder.listFiles();

        String csvFile;

        File folder = new File(AppParameters.strManyDirectories);
        File[] listOfFiles = folder.listFiles();

        if (!(folder.exists() && folder.isDirectory())) {

            try {
                boolean success = new File(AppParameters.strManyDirectories).mkdirs(); // Create a directory; all non-existent ancestor directories are automatically created!!
                if (!success) {
                    System.out.println("Directory creation failed !!");
                }
            } catch (Exception ex) {
                System.out.println("Error :" + ex.getMessage());
            }
        } else {
            flagFile = true;
        }

        System.out.println("\nTraining sets detected: ");

        for (File f : listOfTrainingFiles) {
            System.out.println(" + " + f.getName());
        }

        TrainingSet globalTrainingSet = new TrainingSet();

        for (File f : listOfTrainingFiles) {
            if (f.isFile()) {
                TrainingSet trainingSet = tsLoader.load(f);
                globalTrainingSet.addAll(trainingSet);
            }
        }

        System.out.println("\nPlease Waiting a few seconds...\n");

        if ((AppParameters.startFileNumber < 0 && AppParameters.endFileNumber < 0) || (AppParameters.startFileNumber > listOfEventFiles.length - 1)) {
            if (AppParameters.startFileNumber > listOfEventFiles.length - 1) {
                flag = true;
            }
            AppParameters.startFileNumber = 0;
            AppParameters.endFileNumber = listOfEventFiles.length;
        }else {
            if(AppParameters.endFileNumber > listOfEventFiles.length){
                AppParameters.endFileNumber = listOfEventFiles.length;
            }
        }

        int skipped = 0; // Skipped Samples
        for (int n = AppParameters.startFileNumber; n < AppParameters.endFileNumber; n++) {

            EventSet eventSet = esLoader.load(listOfEventFiles[n]);

            if (eventSet == null) { // Event Set is empty file
                skipped++;
                continue;
            }
            double[] entropyData = new double[eventSet.size()];

            for (int i = 0; i < eventSet.size(); i++) {
                double[] sensorData = new double[eventSet.get(i).size()];
                for (int j = 0; j < eventSet.get(i).size(); j++) {
                    sensorData[j] = eventSet.get(i).get(j);
                }
                entropyData[i] = Entropy.calculateEntropy(sensorData); // Entropy for each one of 14 channels
            }

            EntropyVector ev = new EntropyVector(entropyData, eventSet.size()); // create Entropy Vector

            Result result = classifier.classify(globalTrainingSet, ev, AppParameters.k);
            result.setFilename(listOfEventFiles[n].getName());
            listofResults.add(result);


            if (result.getFilename().contains(result.getCategory())) {
                successful++;
            }

            samples++; // valid samples

            Command c;
            if (result.getCategory().equals("EyesOpened")) {
                c = new Command("ON");
            } else {
                c = new Command("OFF");
            }

            Runnable runnable = new ControllerRunnable(AppParameters.ip, AppParameters.port + "", c);
            buffer.put(runnable);

            if (result.getFilename().contains(result.getCategory())) {
                System.out.println(samples + ". Classification Result: " + result.getCategory() + " | Event: " + result.getFilename().replaceAll("\\P{L}+", "").replace("csv", "") + " | Success: Yes");
            } else {
                System.out.println(samples + ". Classification Result: " + result.getCategory() + " | Event: " + result.getFilename().replaceAll("\\P{L}+", "").replace("csv", "") + " | Success: No");
            }
        }


        String strTemp;
        if (flagFile) {
            int count = listOfFiles.length + 1;
            strTemp = "/Results_" + String.valueOf(count) + ".csv";
            csvFile = AppParameters.strManyDirectories + strTemp;
        } else {
            strTemp = "/Results_1.csv";
            csvFile = AppParameters.strManyDirectories + strTemp;
        }

        boolean status = CsvFileWriter(csvFile, listofResults);

        System.out.println("\n\n---------------Comments---------------");

        int count = AppParameters.endFileNumber - AppParameters.startFileNumber;
        if (count == listOfEventFiles.length) {
            if(flag){
                System.out.println("* Startup and end-file mumber are out of bounds and it is decided to read all the available files !!");
            }else{
                System.out.println("* All CSV files read !!");
            }
        } else {
            System.out.println("* Number of CSV files read: " + " " + count + " !!");
        }

        System.out.println("* Are used the k = " + AppParameters.k + " nearest neighbors of each vector from the classification algorithm !!");
        System.out.println("* Skipping a line in each Event Set is done with cqThreshold = " + AppParameters.cqThreshold + " !!");

        if (AppParameters.flagNullFiles) {
            System.out.println("* There are some empty or omitted CSV files, which were ignored by the classification process !!");
        }

        if (status) {
            System.out.println("* Sussessful creative of " + strTemp.replace("/","") + " file in HistoryResults folder !!");
        } else {
            System.out.println("* Error in CsvFileWriter !!");
        }

        System.out.println("\n\n--------------Statistics--------------");
        System.out.println("* Successfully Categorized: " + successful);
        System.out.println("* Skipped Samples: " + skipped);
        System.out.println("* Valid Samples: " + samples);
        System.out.println("* Rate of Success: " + Math.round((successful / (double) samples * 100) * 100.0) / 100.0 + " %\n\n");
    }
}