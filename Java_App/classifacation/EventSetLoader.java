package classification;

import parameters.AppParameters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class EventSetLoader {
    public EventSet load(File file) { // read Event Set
        EventSet evSet = new EventSet();

        for(int i=0;i<14;i++)
        {
            evSet.add(i, new EventColumn());
        }

        String line = "";
        String cvsSplitBy = ",";

        // Read event set from a file
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            br.readLine();

            while ((line = br.readLine()) != null) {   // Use comma as separator
                boolean skipRow = false;
                String[] stringData = line.split(cvsSplitBy);

                for (int i = 14; i < stringData.length - 2; i++) {
                    if (Double.parseDouble(stringData[i].trim()) < AppParameters.cqThreshold) { // skip Row
                        skipRow = true;
                        break;
                    }
                }

                if (!skipRow) {
                    double[] data = Arrays.stream(Arrays.copyOfRange(stringData, 0, 14)).mapToDouble(Double::parseDouble).toArray();

                    for (int i = 0; i < evSet.size(); i++) {
                        evSet.get(i).add(data[i]);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (evSet.get(0).size() == 0) {
            AppParameters.flagNullFiles = true; // boolean flag for NullFiles -> true
            return null;
        } else {
            return evSet;
        }
    }
}