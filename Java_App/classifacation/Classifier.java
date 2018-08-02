package classification;

import java.util.*;

public class Classifier {
    public Result classify(TrainingSet tsSet, EntropyVector ev, int k) {
        String category = knn(ev.getVector(), tsSet, k);

        Result res = new Result();
        res.setCategory(category);
        res.setVector(ev);
        return res;
    }

    //https://stackoverflow.com/questions/34622466/euclidean-distance-between-2-vectors-implementation
    private double euclideanDistance(double[] a, double[] b) {
        double diff_square_sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            diff_square_sum += (a[i] - b[i]) * (a[i] - b[i]);
        }
        return Math.sqrt(diff_square_sum);
    }

    private class TrainingNode {
        public double distance;
        public String evName;
    }

    private String knn(double[] x, TrainingSet tsSet, int k)
    {
        ArrayList<TrainingNode> list = new ArrayList<TrainingNode>();

        for(TrainingSetRow row : tsSet){
            TrainingNode currentNode = new TrainingNode();
            currentNode.distance = euclideanDistance(row.getEntropyVector().getVector(), x);
            currentNode.evName = row.getEventName();
            list.add(currentNode);
        }

        Collections.sort(list, (a, b) -> Double.valueOf(a.distance).compareTo(b.distance)); // ArrayList ordered by distance attribute

        int eyesOpened = 0;
        int eyesClosed = 0;
        double eyesOpenedWeight = 0.0;
        double eyesClosedWeight = 0.0;

        for (int i = 0; i < list.size() && i < k; i++) { // categorization decision
            if ((list.get(i).evName).equals("EyesOpened")) {
                eyesOpened++;
                eyesOpenedWeight += 1.0 / list.get(i).distance;
            } else {
                eyesClosed++;
                eyesClosedWeight += 1.0 / list.get(i).distance;
            }
        }

        if ((eyesClosedWeight * (float) eyesClosed) > (eyesOpenedWeight * (float) eyesOpened)) {
            return "EyesClosed";
        } else {
            return "EyesOpened";
        }
    }
}