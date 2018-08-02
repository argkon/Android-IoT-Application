package sub;

import controllerthreads.Controller;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import parameters.AppParameters;

public class MqttSubscriber implements MqttCallback {

    private final Controller controller;

    public MqttSubscriber(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("INFO - connectionLost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String m = new String(message.getPayload());

        if (m.startsWith("frequency")) {
            int x = m.indexOf(" ") + 1;
            String param = m.substring(x);
            int frequency;

            try {
                frequency = Integer.parseInt(param.trim());
                System.out.println("\nINFO  -  messageArrived from Android App >>>>>>>>>>> frequency = " + frequency + "\n");
            } catch (Exception ex) {
                frequency = -1;
            }

            if (frequency > 0) {
                AppParameters.threadPauseConsumer = frequency * 1000; //ms
                AppParameters.flagFrequency = true; // change frequency
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}