package producer;

import buffer.Buffer;
import classification.ComputationalService;
import parameters.AppParameters;

import java.util.ArrayList;
import java.util.List;

public class Producer implements Runnable {
    private Buffer buffer;

    private final List<Thread> threads = new ArrayList<>();

    public Producer(Buffer buffer) {
        this.buffer = buffer;
    }


    @Override
    public void run() {
        try {
            String storagedirectory = AppParameters.storagedirectory;
            String trainingsetdirectory = AppParameters.trainingsetdirectory;

            ComputationalService service = new ComputationalService(buffer); // create ComputationalService Object
            service.execute(storagedirectory, trainingsetdirectory);
        }catch (Exception ex){

        }
    }
}
