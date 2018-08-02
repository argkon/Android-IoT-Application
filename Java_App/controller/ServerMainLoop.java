package controller;

import buffer.Buffer;
import consumer.Consumer;
import controllerthreads.Command;
import controllerthreads.Controller;
import parameters.AppParameters;

import java.io.File;

public class ServerMainLoop {
    private String ip;
    private int port;
    private int count = 0;

    public ServerMainLoop(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void loop() {
        Console console = new Console();
        Controller controller = new Controller(ip, port);

        controller.init();
        controller.subscribeToAll();


        while (true) {
            console.printPrompt();
            Command command = console.readCommand();

            if (command == null) {
                System.out.println("Invalid command");
                System.out.println("Valid commands are: ");
                System.out.println("   1. FlashOn Time(sec) [Νumber of Εxecutions] [Εxecution Rate(sec)]");
                System.out.println("   2. FlashOff [Νumber of Εxecutions] [Εxecution Rate(sec)]");
                System.out.println("   3. MusicOn Time(sec) [Νumber of Εxecutions] [Εxecution Rate(sec)]");
                System.out.println("   4. MusicOff [Νumber of Εxecutions] [Εxecution Rate(sec)]");
                System.out.println("   5. Random Time(sec) [Min Execution Rate(sec)] [Max Execution Rate(sec)]");
                System.out.println("   6. ReadFiles [Startup file number to Read] [End-file number to Read]");
                System.out.println("   7. StopAll");
                System.out.println("   8. Exit \n");
            } else {
                controller.execute(command);
            }
        }
    }
}
