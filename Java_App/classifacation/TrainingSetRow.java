package classification;

public class TrainingSetRow {
    private String eventName;
    private EntropyVector vector;

    public TrainingSetRow(String eventName, EntropyVector vector) {
        this.eventName = eventName;
        this.vector = vector;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public EntropyVector getEntropyVector() {
        return vector;
    }

    public void setVector(EntropyVector vector) {
        this.vector = vector;
    }
}
