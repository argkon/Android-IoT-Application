package classification;

public class EntropyVector {
    private double [] vector;
    private int size;

    public EntropyVector(double[] vector, int size) {
        this.vector = vector;
        this.size = size;
    }

    public double[] getVector() {
        return vector;
    }

    public void setVector(double[] vector) {
        this.vector = vector;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
