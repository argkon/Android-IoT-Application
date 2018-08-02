package classification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class TrainingSetLoader { // load Training Set

    public TrainingSet load(File file) {
        TrainingSet tsSet = new TrainingSet();

        //https://stackoverflow.com/questions/3663944/what-is-the-best-way-to-remove-the-first-element-from-an-array
        //https://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/

        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            br.readLine();
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] stringData = line.split(cvsSplitBy);
                double [] data = Arrays.stream(Arrays.copyOfRange(stringData, 1, stringData.length)).mapToDouble(Double::parseDouble).toArray();
                EntropyVector ev = new EntropyVector(data, 14);
                TrainingSetRow row = new TrainingSetRow(stringData[0], ev);
                tsSet.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tsSet; // Training Set
    }
}