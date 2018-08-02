/*case 2:

case 3:
int startNumberFile = Integer.parseInt(tokens[1]);
int stopNumberFile = Integer.parseInt(tokens[2]);
if (startNumberFile < 0 || stopNumberFile < 0 || startNumberFile > stopNumberFile){
if (startNumberFile < 0) {System.out.println("The file number identifying the start of the scan is negative or zero !!");}
if (stopNumberFile < 0) {System.out.println("The file number identifying the end of the scan is negative or zero !!");}
if (startNumberFile > stopNumberFile) {System.out.println("Τhe file number that determines the start is greater than the file number that specifies the scanning end !!");}
}else {
return new Command(text, startNumberFile, stopNumberFile);
} */

package controller;

import controllerthreads.Command;
import parameters.AppParameters;

import java.util.Scanner;

    public class Console {
        private Scanner scanner = new Scanner(System.in);

        public static void printPrompt() {
            System.out.print("> ");
        }

        public Command readCommand() {
            String s = scanner.nextLine();
            if (s == null) {
                return null;
            }

            s = s.toUpperCase();

            if (s.startsWith("FLASHON")) {
                try {
                    String[] tokens = s.split(" ");
                    String text = tokens[0];
                    int t = Integer.parseInt(tokens[1]); //sec
                    t = t * 1000;  //sec -> msec
                    boolean random_flag = false;

                    switch (tokens.length) {
                        case 2:
                            if (t <= 0) {
                                System.out.println("The time is negative number or zero !!");
                            }
                            if (t < AppParameters.minduration || t > AppParameters.maxduration) {
                                System.out.println("Please select Time from interval [" + AppParameters.minduration / 1000 + ", " + AppParameters.maxduration / 1000 + "] sec!!");
                            } else {
                                return new Command(text, t);
                            }
                            break;
                        case 4:
                            int counter = Integer.parseInt(tokens[2]);
                            int sleeptime = Integer.parseInt(tokens[3]);
                            sleeptime = sleeptime * 1000;
                            if (t <= 0 || t < AppParameters.minduration || t > AppParameters.maxduration || counter <= 0 || sleeptime <= 0) {
                                if (t <= 0) {
                                    System.out.println("The time is negative number or zero !!");
                                }
                                if (t < AppParameters.minduration || t > AppParameters.maxduration) {
                                    System.out.println("Please select Time from interval [" + AppParameters.minduration / 1000 + ", " + AppParameters.maxduration / 1000 + "] sec!!");
                                }
                                if (counter <= 0) {
                                    System.out.println("Τhe number of times at which the current command is executed is negative number or zero !!");
                                }
                                if (sleeptime <= 0) {
                                    System.out.println("The frequency at which current command is executed is negative number or zero !!");
                                }
                            } else {
                                return new Command(text, t, counter, sleeptime, random_flag);
                            }
                    }
                } catch (Exception ex) {
                    return null;
                }
            }
            if (s.startsWith("FLASHOFF")) {
                String[] tokens = s.split(" ");

                try {
                    switch (tokens.length) {
                        case 1:
                            return new Command("FLASHOFF");
                        case 3:
                            int counter = Integer.parseInt(tokens[1]);
                            int sleeptime = Integer.parseInt(tokens[2]);
                            sleeptime = sleeptime * 1000;
                            if (counter <= 0 || sleeptime <= 0) {
                                System.out.println("Τhe number of times or the frequency at which the current command is executed is negative number or zero !!");
                            } else {
                                return new Command("FLASHOFF", counter, sleeptime, false);
                            }
                        default:
                            return null;
                    }
                } catch (Exception ex) {
                    return null;
                }
            }
            if (s.startsWith("MUSICON")) {
                try {
                    String[] tokens = s.split(" ");
                    String text = tokens[0];
                    int t = Integer.parseInt(tokens[1]); //sec
                    t = t * 1000; //msec
                    boolean random_flag = false;

                    switch (tokens.length) {
                        case 2:
                            if (t <= 0) {
                                System.out.println("The time is negative number or zero !!");
                            }
                            if (t < AppParameters.minduration || t > AppParameters.maxduration) {
                                System.out.println("Please select Time from interval [" + AppParameters.minduration / 1000 + ", " + AppParameters.maxduration / 1000 + "] sec!!");
                            } else {
                                return new Command(text, t);
                            }
                            break;
                        case 4:
                            int counter = Integer.parseInt(tokens[2]);
                            int sleeptime = Integer.parseInt(tokens[3]);
                            sleeptime = sleeptime * 1000;
                            if (t <= 0 || t < AppParameters.minduration || t > AppParameters.maxduration || counter <= 0 || sleeptime <= 0) {
                                if (t <= 0) {
                                    System.out.println("The time is negative number or zero !!");
                                }
                                if (t < AppParameters.minduration || t > AppParameters.maxduration) {
                                    System.out.println("Please select Time from interval [" + AppParameters.minduration / 1000 + ", " + AppParameters.maxduration / 1000 + "] sec!!");
                                }
                                if (counter <= 0) {
                                    System.out.println("Τhe number of times at which the current command is executed is negative number or zero !!");
                                }
                                if (sleeptime <= 0) {
                                    System.out.println("The frequency at which current command is executed is negative number or zero !!");
                                }
                            } else {
                                return new Command(text, t, counter, sleeptime, random_flag);
                            }
                    }
                } catch (Exception ex) {
                    return null;
                }
            }
            if (s.startsWith("MUSICOFF")) {
                String[] tokens = s.split(" ");

                try {
                    switch (tokens.length) {
                        case 1:
                            return new Command("MUSICOFF");
                        case 3:
                            int counter = Integer.parseInt(tokens[1]);
                            int sleeptime = Integer.parseInt(tokens[2]);
                            sleeptime = sleeptime * 1000;
                            if (counter <= 0 || sleeptime <= 0) {
                                System.out.println("Τhe number of times or the frequency at which the current command is executed is negative number or zero !!");
                            } else {
                                return new Command("MUSICOFF", counter, sleeptime, false);
                            }
                        default:
                            return null;
                    }
                } catch (Exception ex) {
                    return null;
                }
            }

            if (s.startsWith("RANDOM")) {
                try {
                    String[] tokens = s.split(" ");
                    String text = tokens[0];
                    int t = Integer.parseInt(tokens[1]); //sec
                    t = t * 1000; //sec -> msec
                    boolean random_flag = true;

                    switch (tokens.length) {
                        case 2:
                            if (t <= 0) {
                                System.out.println("The time is negative number or zero !!");
                            } else {
                                return new Command(text, t);
                            }
                            break;
                        case 4:
                            int min_execution_rate = Integer.parseInt(tokens[2]);
                            int max_execution_rate = Integer.parseInt(tokens[3]); //sec
                            min_execution_rate = min_execution_rate * 1000;
                            max_execution_rate = max_execution_rate * 1000;

                            if (t <= 0 || min_execution_rate < 0 || max_execution_rate < 0 || min_execution_rate > max_execution_rate) {
                                if (t <= 0) {
                                    System.out.println("The time is negative number or zero !!");
                                }
                                if (min_execution_rate < 0) {
                                    System.out.println("The min execution rate and max execution rate are negative numbers !!");
                                }
                                if (max_execution_rate < 0) {
                                    System.out.println("The max execution rate and max execution rate are negative numbers !!");
                                }
                                if (min_execution_rate > max_execution_rate) {
                                    System.out.println("Τhe min execution rate is fewer than max execution rate !!");
                                }
                            } else {
                                return new Command(text, t, min_execution_rate, max_execution_rate, random_flag);
                            }
                    }
                } catch (Exception ex) {
                    return null;
                }
            }

            if (s.startsWith("READFILES")){
                try {
                    String[] tokens = s.split(" ");
                    String text = tokens[0];
                    switch (tokens.length){
                        case 1:
                            AppParameters.startFileNumber = -1;
                            AppParameters.endFileNumber = -1;
                            return new Command(text);
                        case 3:
                            int startNumber = Integer.parseInt(tokens[1]);
                            int endNumber = Integer.parseInt(tokens[2]);
                            if (startNumber <= 0 || endNumber <= 0 || startNumber > endNumber) {
                                if(startNumber <= 0){
                                    System.out.println("The startup file number is not positive !!");
                                }
                                if(endNumber <= 0){
                                    System.out.println("The end-file number is not positive !!");
                                }
                                if(startNumber > endNumber){
                                    System.out.println("The startup file number is fewer than end-file number !!");
                                }
                            }else{
                                AppParameters.startFileNumber = startNumber - 1;
                                AppParameters.endFileNumber = endNumber;
                                return new Command(text, startNumber, endNumber, true);
                            }
                            break;
                    }
                }catch (Exception ex) {
                    return null;
                }
            }

            if (s.startsWith("EXIT")) {
                return new Command("EXIT");
            }

            if (s.startsWith("STOPALL")) {

                return new Command("STOPALL");
            }
            return null;
        }
    }