package classification;

public class Result {
    private String filename;
    private EntropyVector vector;
    private String category;

    public Result(String filename, EntropyVector vector, String category) {
        this.filename = filename;
        this.vector = vector;
        this.category = category;
    }

    public Result() {
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public EntropyVector getVector() {
        return vector;
    }

    public void setVector(EntropyVector vector) {
        this.vector = vector;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
